package unionware.base.room;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Author: sheng
 * Date:2024/10/8
 */
public class ThreadTask {
    public static <T> T get(Callable<T> task) throws ExecutionException, InterruptedException, TimeoutException {
        FutureTask<T> futureTask = new FutureTask<>(task);
        new Thread(futureTask).start();
        return futureTask.get(5000, TimeUnit.MILLISECONDS);
    }

    public static <T> T getTwo(Callable<T> task)  {
        try {
            return get(task);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void getVoid(Runnable runnable) {
        Callable<Void> task = () -> {
            runnable.run();
            return null;
        };

        FutureTask<Void> futureTask = new FutureTask<>(task);
        new Thread(futureTask).start();
        try {
            futureTask.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void start(Runnable runnable) {
        new Thread(runnable).start();
    }
}
