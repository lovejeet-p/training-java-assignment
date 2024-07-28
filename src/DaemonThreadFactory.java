import java.util.concurrent.ThreadFactory;

public class DaemonThreadFactory implements ThreadFactory {

    public final static ThreadFactory instance =
        new DaemonThreadFactory();

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    }
}