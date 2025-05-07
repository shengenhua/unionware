package com.unionware.printer;

public abstract class ReadThread extends Thread {
    //无意义
    private final Object lock = new Object();
    //标志线程阻塞情况
    private boolean pause = true;

    //标志线程是否结束
    private boolean isRun = false;

    public boolean isRun() {
        return isRun;
    }

    /**
     * 设置线程是否阻塞
     */
    public void pauseThread() {
        if (pause) return;
        this.pause = true;
    }

    protected abstract void read();

    /**
     * 调用该方法实现恢复线程的运行
     */
    public void resumeThread() {
        if (!pause) return;
        this.pause = false;
        synchronized (lock) {
            //唤醒线程
            lock.notify();
        }
    }

    /**
     * 这个方法只能在run 方法中实现，不然会阻塞主线程，导致页面无响应
     */
    private void onPause() {
        synchronized (lock) {
            try {
                //线程 等待/阻塞
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void run() {
        isRun = true;
        try {
            while (true) {
                if (pause) {
                    //线程 阻塞/等待
                    onPause();
                }
                read();
            }
        } catch (Exception e) {
            e.printStackTrace();
            //中断将线程退出
            isRun = false;
        }
    }
}
