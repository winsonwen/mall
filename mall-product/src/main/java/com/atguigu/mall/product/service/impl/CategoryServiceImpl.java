package com.atguigu.mall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.mall.product.service.CategoryBrandRelationService;
import com.atguigu.mall.product.vo.Catelog2Vo;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.mall.product.dao.CategoryDao;
import com.atguigu.mall.product.entity.CategoryEntity;
import com.atguigu.mall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

//    private Map<String, Object>  cache = new HashMap<>();

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redisson;

    @Override
    public List<CategoryEntity> listWithTree() {

        //1. find out all categories
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);

        // 2. assemble categories into a tree structure
        //2.1  find out all first-class category------parent_id=0
        List<CategoryEntity> level1Menus = categoryEntities.stream().filter((categoryEntity) -> {
            return categoryEntity.getParentCid() == 0;
        }).map((menu) -> {
            menu.setChildren(getChildren(menu, categoryEntities));
            return menu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 1. 检查当前删除菜单，是否被别的地方引用

        //逻辑删除
        baseMapper.deleteBatchIds(asList);


    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> path = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, path);
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[path.size()]);
    }


    /*
     * 级联更新所有关联的数据
     * */
    //多项操作
    @Caching(evict = {
            @CacheEvict(value="category", key = "'getLevel1Categorys'"),  //删除指定分区
            @CacheEvict(value="category", key = "'getCatelogJson'")
    })
    //删除指定分区下的所有缓存
    @CacheEvict(value="category", allEntries = true)
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }


    /**
 * 1. 代表当前方法的结果需要缓存，如果缓存中有，方法就不调用，如果缓存中没有，会调用方法，最后将方法的结果放入缓存
 * 2. 每一个需要缓存的数据我们都来指定要放到哪个名字的缓存【缓存分区（按照业务类型）】
 * 3. 可以写成数组把数据放在不同的分区
 * 4. 默认行为
 *      1）如果缓存中有，方法就不调用
 *      2）key默认自动生成： 缓存名字::SimpleKey []（自动生成的key值）
 *      3）缓存的value的值：默认使用JDK序列化机制，将序列化后的数据存到redis
 *      4）默认ttl时间为 -1  永不过期
 *
 *     自定义：
 *          1） 指定生成的缓存使用的key: key属性指定接受一个SpEl
 *          2） 指定缓存数据的存活时间ttl：  spring.cache.redis.time-to-live=360000  单位为毫秒
 *          3） 存的数据已json格式
 *                  CacheAutoConfiguration:
 */
//    @Cacheable(value={"category"}, key = "'Level1Categorys'")
    @Cacheable(value="category", key = "#root.method.name",sync=true)
    @Override
    public List<CategoryEntity> getLevel1Categorys() {

        //parent_cia==0 就是一级分类
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;
    }

    @Cacheable(value="category", key = "#root.method.name",sync = true)  //sync = true:local lock
    @Override
    public Map<String, List<Catelog2Vo>> getCatelogJson() {
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        //1. 查出所有1级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
        //2. 封装数据
        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> {
            return k.getCatId().toString();
        }, v -> {
            //1. 每一个一级分类，查出这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            //2. 封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    //1.查出二级分类的三级分类封装成vo
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());
                    if (level3Catelog != null) {
                        List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
                            //2.封装成指定格式
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(collect);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));

        return parent_cid;
    }

    //TODO 产生堆外内存溢出：OutOfDirectMemoryError
    //1. SpringBoot 2.0 以后默认使用lettuce作为操作redis的客户端。它使用netty进行通信
    //2. lettuce的bug导致netty推外内存溢出
    //解决方案：不能只去调大堆外内存
    //  1. 升级lettuce客户端
    //  2. 切换使用jedis
    // lettuce, jedis操作redis的底层客户端，Spring再次封装redisTemplate.
//    @Override
    public Map<String, List<Catelog2Vo>> getCatelogJson2() {
        //1.adding cache logic, the data in cache is json String
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (StringUtils.isEmpty(catalogJSON)) {
            //2. data not in cache
            Map<String, List<Catelog2Vo>> catelogJsonFromDb = getCatelogJsonFromDbWithRedisLock();
            String s = JSON.toJSONString(catelogJsonFromDb);
            redisTemplate.opsForValue().set("catalogJson", s, 1, TimeUnit.DAYS);
            return catelogJsonFromDb;
        }
        Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });

        return result;
    }


/*
* 缓存里面的数据如何和数据库保持一致
* 缓存数据一致性
* 1） 双写模式
* 2） 失效模式
* */
    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDbWithRedissonLock() {

        RLock lock = redisson.getLock("catalogJson-lock");
        lock.lock();

        Map<String, List<Catelog2Vo>> dataFromDb;
        try {
            dataFromDb = getDataFromDb();
        } finally {
            lock.unlock();
        }

        return dataFromDb;


    }


    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDbWithRedisLock() {
        String uuid = UUID.randomUUID().toString();
        //原子加锁
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);

        if (lock) {
            Map<String, List<Catelog2Vo>> dataFromDb;
            try {
                dataFromDb = getDataFromDb();
            } finally {
                //原子解锁
                //lua脚本解锁
                String script = "if redis.call('get',KEYS[1]) == ARGV[1] then  return redis.call('del',KEYS[1]) else return 0 end";
                //删除锁
                Long lock1 = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"),
                        uuid);
            }
//            String lockerValue = redisTemplate.opsForValue().get("lock");
//            if(uuid.equals(lockerValue)){
//                redisTemplate.delete("lock");
//            }

            return dataFromDb;
        } else {
            // 自旋方式
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatelogJsonFromDbWithRedisLock();
        }

    }

    public Map<String, List<Catelog2Vo>> getDataFromDb() {
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        //1. 查出所有1级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
        //2. 封装数据
        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> {
            return k.getCatId().toString();
        }, v -> {
            //1. 每一个一级分类，查出这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            //2. 封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    //1.查出二级分类的三级分类封装成vo
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());
                    if (level3Catelog != null) {
                        List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
                            //2.封装成指定格式
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(collect);
                    }


                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));

        return parent_cid;
    }


    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDb() {
        // local cache
//        Map<String,  List<Catelog2Vo>> catalogJson = (Map<String, List<Catelog2Vo>>) cache.get("catalogJson");
//       检测是否有缓存，有则直接返回数据，没有则先把数据放入缓存，再返回
//        if ( cache.get("catalogJson")==null){
//            cache.put("catalogJson",parent_cid);
//            return parent_cid;
//        }
//         return parent_cid;

        //
        /*
         * 1. 优化：将数据库的多次查询变为一次
         * 2. 优化：抽取源baseMapper方法为 getParent_cid(Long parentCid)  选中代码部分右键 -- Refactor -- Extract Method
         * */

        //只要是通一把锁，就能锁住需要这个锁的所有线程
        //1. synchronized(this)   SpringBoot所有组件在容器中都是单例的

        //TODO 本地锁 synchronized (this): 只适合当前进程，不适合分布式
        synchronized (this) {
            //得到锁以后，应该再去缓存中确定一次，如果没有菜需要继续查询
            String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
            if (StringUtils.isEmpty(catalogJSON)) {
                //如果缓存不为null直接返回
                Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
                });
                return result;
            }

//-------------------------------------------------------------------------------------------
            List<CategoryEntity> selectList = baseMapper.selectList(null);

            //1. 查出所有1级分类
            List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
            //2. 封装数据
            Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> {
                return k.getCatId().toString();
            }, v -> {
                //1. 每一个一级分类，查出这个一级分类的二级分类
                List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
                //2. 封装上面的结果
                List<Catelog2Vo> catelog2Vos = null;
                if (categoryEntities != null) {
                    catelog2Vos = categoryEntities.stream().map(l2 -> {
                        Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                        //1.查出二级分类的三级分类封装成vo
                        List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());
                        if (level3Catelog != null) {
                            List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
                                //2.封装成指定格式
                                Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                                return catelog3Vo;
                            }).collect(Collectors.toList());
                            catelog2Vo.setCatalog3List(collect);
                        }


                        return catelog2Vo;
                    }).collect(Collectors.toList());
                }
                return catelog2Vos;
            }));

            //本地锁
            String s = JSON.toJSONString(parent_cid);
            redisTemplate.opsForValue().set("catalogJson", s, 1, TimeUnit.DAYS);


            return parent_cid;
        }

    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        List<CategoryEntity> collect = selectList.stream().filter(item -> {
            return item.getParentCid() == parent_cid;
        }).collect(Collectors.toList());
        return collect;
        //return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", parentCid));
    }

    private List<Long> findParentPath(Long catelogId, List<Long> path) {
        // 收集当前结点
        path.add(catelogId);
        CategoryEntity categoryEntity = this.getById(catelogId);
        if (categoryEntity.getParentCid() != 0) {
            findParentPath(categoryEntity.getParentCid(), path);
        }
        return path;
    }


    //递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            //1、找到子菜单
            categoryEntity.setChildren(getChildren(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            //2、菜单的排序
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return children;
    }


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }


}