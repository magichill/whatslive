package com.letv.whatslive.common.utils;


import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 线程分发器
 * <p/>
 * <p/>
 * <p/>
 */
public class ThreadDistribution {


    private static ThreadDistribution instance = null;

    private final ExecutorService executor;

    private final AtomicBoolean closed = new AtomicBoolean(false);

    private ThreadDistribution(int initCount) {
        executor = new ThreadPoolExecutor(initCount, 20, 5, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10000000),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    synchronized public static ThreadDistribution getInstance(int initCount) {
        if (instance == null) {
            instance = new ThreadDistribution(initCount);
        }
        return instance;
    }

    synchronized public static ThreadDistribution getInstance() {
        if (instance == null) {
            instance = new ThreadDistribution(10);
        }
        return instance;
    }

    /**
     * 获取新的线程
     *
     * @param work
     * @return
     */
    public void doWork(Runnable work) {
        if (closed.get())
            return;

        if (work == null) {
            throw new NullPointerException();
        }
        try {
            executor.execute(work);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> void submit(Callable<T> task) {
        if (closed.get())
            return;

        if (task == null) {
            throw new NullPointerException();
        }
        try {
            executor.submit(task);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Future<String> submit4Futrue(Callable<String> task) {
        Future<String> future = null;
        if (closed.get())
            return null;

        if (task == null) {
            throw new NullPointerException();
        }
        try {
            future = executor.submit(task);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return future;
    }

    public void shutdown() {
        if (closed.compareAndSet(false, true)) {
            executor.shutdownNow();
        }
    }
}
