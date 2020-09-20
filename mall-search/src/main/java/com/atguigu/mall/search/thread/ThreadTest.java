package com.atguigu.mall.search.thread;

import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.*;

public class ThreadTest {

    public static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main.. start");

        //没返回值
        CompletableFuture.runAsync(() -> {
            System.out.println("Current Thread: " + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("Result: " + i);
        }, executor).handle((result, thr) -> {
            if (result != null) {
                return 100;
            }
            if (thr != null) {
                return 0;
            }
            return 0;
        });

//        //可以返回结果
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("Current Thread: " + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("Result: " + i);
//            return i;
//        }, executor).whenComplete((result,exception)->{
//            //whenComplete: 虽然能监听异常，但是出现异常后无法返回数据
//            System.out.println("Success, result:"+ result+"exception: "+exception);  //Success, result: 5, exception: null
//        }).exceptionally(throwable -> {
//            return 10;          //exceptionally: whenComplete 有异常的时候会返回10
//        });
//        Integer i = future.get();

        /*
         * 线程串行化
         * */
        //thenRun: 不能获取结果
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("Current Thread: " + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("Result: " + i);
            return i;
        }, executor).thenRunAsync(() -> {
            System.out.println("Task 2");
        }, executor);

        //thenAccept: 能接受上一步结果，但无返回值
        CompletableFuture<Void> future2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("Current Thread: " + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("Result: " + i);
            return i;
        }, executor).thenAcceptAsync((result) -> {
            System.out.println("Task 2" + result);
        }, executor);

        //thenApply: 能接受上一步结果，且有返回值
        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
            System.out.println("Current Thread: " + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("Result: " + i);
            return i;
        }, executor).thenApplyAsync((result) -> {
            System.out.println("Task 2" + result);
            return "10" + result;
        }, executor);
        String s = future3.get();


        System.out.println("main.. end");
    }


//    public static ExecutorService service = Executors.newFixedThreadPool(10);
//
//    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        System.out.println("main... start...");
//        /*
//         * 1. 继承Thread
//         * 2. 实现Runnable
//         * 3. 实现Callable接口 + FutureTask (可以返回结果，可以处理异常)
//         * 4. 线程池
//         *       给线程池直接提交任务。
//         *       1. 创建
//         *           1） Executors
//         *           2)
//         *
//         *
//         * 在业务代码中，一般直接使用线程池
//         *
//         * 区别：
//         *   1，2不能得到返回值，3可以获取返回值
//         *   1，2，3都不能控制资源
//         *   4可以控制资源，性能稳定。
//         * */
//
//
////        // 1. 继承Thread
////        Thread thread = new Thread01();
////        thread.start();
////
////        // 2. 实现Runnable
////        Runnable01 runnable01 = new Runnable01();
////        new Thread(runnable01).start();
////
////        // 3. 实现Callable接口 + FutureTask
////        FutureTask<Integer> futureTask = new FutureTask<>(new Callable01());
////        new Thread(futureTask).start();
////        //阻塞等待整个线程执行完成，再获取返回结果
////        Integer integer = futureTask.get();
////        System.out.println(integer);
//
//        // 4. 线程池【ExecutorService】
//        //当前系统吃中有一两个，每个异步任务，提交给线程池让他自己执行
//        /*
//         public ThreadPoolExecutor(int var1, int var2, long var3, TimeUnit var5, BlockingQueue<Runnable> var6, ThreadFactory var7, RejectedExecutionHandler var8) {
//        * 七大参数
//        *    this.acc = System.getSecurityManager() == null ? null : AccessController.getContext();
//             this.corePoolSize = var1;    核心线程数（一直存在，除非设置了（allowCoreThreadTimeOut）），线程池创建好就准备就绪的线程数量，等待接收异步任务去执行
//             this.maximumPoolSize = var2; 最大线程数量；控制资源并发
//             this.workQueue = var6;                      阻塞队列，如果任务有很多，就会将目前多地任务放在队列里面
//                                                        只要有线程空闲了，就会去队列里取出新的任务继续执行
//                                                        默认是Integer的最大值。可能导致内存沾满
//              this.keepAliveTime = var5.toNanos(var3);   存活时间,如果当前的线程数量大于core数量
//                                                        释放空闲的线程(超过core的数量)，只要线程空闲大于指定的keepAlivetime
//              this.threadFactory = var7;                线程的创建工厂
//              this.handler = var8;                      如果队列满了，按照指定的拒绝策略拒绝执行任务
//                                                        RejectedExecutionHandler有多种实现类，默认使用AbortPolicy() 丢弃
//          工作顺序
//            1） 线程池穿件，准备好core数量的核心线程，准备接收任务
//            1.1  core满了，九江再进来的任务放入阻塞队列中，空闲的core就会自己去阻塞队列获取任务执行
//            1.2 阻塞队列满了，就直接开新县城执行，最大只能开到maximumPoolSize指定的数量
//            1.3 max也满了，就用handler拒绝任务
//            1.4 max都执行完成，有很多空闲，在指定的时间keepAliveTime以后释放线程多余的线程
//
//           一个线程池， core 7 max 20   queue 50  100b并发进来怎么分配
//                  7个会立即执行，50个会进入队列，再13个线程执行，剩下的30个使用拒绝策略
//                  如果不想抛弃，则使用CallRunsPolicy
//        * */
//        //多种创建方法:
//        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 200, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<>(100000), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy);
//        Executors.newCachedThreadPool();     // core为0，所有线程都可回收
//        Executors.newFixedThreadPool();      // 固定的core线程数量，没有max，都不可回收
//        Executors.newScheduledThreadPool()   // 定时任务的线程池
//        Executors.newSingleThreadExecutor()  //单线程的线程池，有序冲队列中获取任务执行任务
//
//        service.execute(new Runnable01());
//
//        System.out.println("main... end...");
//    }
//
//    public static class Thread01 extends Thread {
//        @Override
//        public void run() {
//            System.out.println("Current Thread: " + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("Result: " + i);
//        }
//    }
//
//    public static class Runnable01 implements Runnable {
//        @Override
//        public void run() {
//            System.out.println("Current Thread: " + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("Result: " + i);
//        }
//    }
//
//    public static class Callable01 implements Callable {
//        @Override
//        public Object call() throws Exception {
//            System.out.println("Current Thread: " + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("Result: " + i);
//            return i;
//        }
//    }


}
