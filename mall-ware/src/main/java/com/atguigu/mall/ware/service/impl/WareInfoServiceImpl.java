package com.atguigu.mall.ware.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.mall.ware.dao.WareInfoDao;
import com.atguigu.mall.ware.entity.WareInfoEntity;
import com.atguigu.mall.ware.service.WareInfoService;

import javax.annotation.Resource;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

//    @Resource
//    private MemberFeign memberFeign;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.eq("id", key).or()
                    .like("name", key).or()
                    .like("address", key).or()
                    .like("areacode", key);
        }
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

//    @Override
//    public FareVO getFare(Long attrId) {
//        FareVO fareVO = new FareVO();
//        R r = memberFeign.addrInfo(attrId);
//        MemberAddressVO data = r.getData("memberReceiveAddress", new TypeReference<MemberAddressVO>() {});
//        if (!ObjectUtils.isEmpty(data)) {
//            String phone = data.getPhone();
//            String substring = phone.substring(phone.length() - 1);
//            fareVO.setFare(new BigDecimal(substring));
//            fareVO.setMemberAddressVO(data);
//            return fareVO;
//        }
//        return null;
//    }

}