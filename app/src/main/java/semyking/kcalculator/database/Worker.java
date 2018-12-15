package semyking.kcalculator.database;

import android.os.AsyncTask;
import android.os.Looper;
import java.util.concurrent.Executor;

public class Worker {
    private static Worker instance;
    private static final Object sLock = new Object();
    private Executor executor = AsyncTask.THREAD_POOL_EXECUTOR;

    public void shutdown(boolean z) {
    }

    private Worker() {
    }

    public static Worker getInstance() {
        Worker worker;
        synchronized (sLock) {
            if (instance == null) {
                instance = new Worker();
            }
            worker = instance;
        }
        return worker;
    }

    public void registerTask(Runnable runnable) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            this.executor.execute(runnable);
        } else {
            runnable.run();
        }
    }
}
