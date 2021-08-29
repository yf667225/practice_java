package week4;
import java.util.concurrent.ArrayBlockingQueue;
import static java.lang.Thread.sleep;

public class ThreadWork05 {
    //使用同步数据结构
    static ArrayBlockingQueue<String> queue;

    public static void main(String[] args) throws InterruptedException {

        queue = new ArrayBlockingQueue<>(1);
        new Thread(new RunnableTask(queue)).start();
        System.out.println(queue.take());
    }

    static class RunnableTask implements Runnable {

        ArrayBlockingQueue<String> queue;

        public RunnableTask(ArrayBlockingQueue<String> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            queue.offer("ThreadWork05");
        }
    }

}
