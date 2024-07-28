import java.util.Collection;
import java.util.concurrent.ExecutorService;

public class BatchSearcher implements Runnable {
    private Collection<String> batch;
    private ExecutorService fileExecutorService;

    BatchSearcher(Collection<String> batch, ExecutorService fileExecutorService) {
        this.batch = batch;
        this.fileExecutorService = fileExecutorService;
    }

    @Override
    public void run() {

    }
}
