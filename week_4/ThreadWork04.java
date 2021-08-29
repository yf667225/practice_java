package week4;


import java.util.concurrent.CountDownLatch;

public class ThreadWork04 {
    public static void main(String[] args) throws InterruptedException {
        //使用Java并发包concurrent中的CountDownLatch
        CountDownLatch countDownLatch = new CountDownLatch(5);
        for(int i=0;i<5;i++){
            new Thread(new readNum(i,countDownLatch)).start();
        }

        countDownLatch.await();//注意跟CyclicBarrier不同，这里在主线程await
        System.out.println("------ThreadWork04执行结束------------");
    }

    static class readNum implements Runnable{

        private int id;
        private CountDownLatch latch;
        public readNum(int id,CountDownLatch latch){
            this.id = id;
            this.latch = latch;
        }

        @Override
        public void run() {
            synchronized (this){
                System.out.println("id:"+id+","+Thread.currentThread().getName());
                System.out.println("线程组任务"+id+"结束，其他任务继续");
                latch.countDown();
            }
        }
    }
}
