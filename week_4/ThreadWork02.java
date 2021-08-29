package week4;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadWork02 {
    static String result;

    public static void main(String[] args) {
        Lock reentrantLock = new ReentrantLock();
        Condition notComplete = reentrantLock.newCondition();
        try {
            reentrantLock.lock();
            new Thread(new RunnableTask<>(reentrantLock, notComplete)).start();
            // 释放锁，等待再次获取锁。
            notComplete.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            reentrantLock.unlock();
        }
        System.out.println(result);

    }

    static class RunnableTask<L extends Lock, T extends Condition> implements Runnable {

        L lock;
        T condition;

        public RunnableTask(L lock, T condition) {
            this.lock = lock;
            this.condition = condition;
        }

        @Override
        public void run() {
            try {
                lock.lock();
                // pretend to compute
                Thread.sleep(1000);
                result = "ThreadWork02";
                // Wakes up one waiting thread.
                condition.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

    }
}
