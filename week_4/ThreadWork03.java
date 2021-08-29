package week4;


import java.util.concurrent.Semaphore;

public class ThreadWork03 {
    static String result;

    public static void main(String[] args) throws Exception {
        Semaphore semaphore = new Semaphore(0);
        new Thread(new RunnableTask(semaphore)).start();
        semaphore.acquire();
        System.out.println(result);
    }

    static class RunnableTask implements Runnable{
        Semaphore semaphore;

        public RunnableTask(Semaphore semaphore) {
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            try {

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            result = "ThreadWork03";
            semaphore.release();
        }
    }
}
