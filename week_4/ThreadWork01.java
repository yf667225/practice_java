package week4;

public class ThreadWork01 {
    public static String result;
    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        Thread subThread = new Thread(() -> {
            ThreadWork01.result = "ThreadWork01";
        }, "subThread");

        subThread.start();
        subThread.join();
        System.out.println("结果为"+result);
        long endTime = System.currentTimeMillis();
        System.out.println("耗时："+(endTime-startTime));
    }
}
