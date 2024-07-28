import java.util.Collection;
import java.util.concurrent.ExecutorService;

public class BatchSearcher implements Runnable {

    private final Collection<String> batch;
    private final ExecutorService executorService;

    BatchSearcher(Collection<String> batch, ExecutorService executorService) {
        this.batch = batch;
        this.executorService = executorService;
    }

    @Override
    public void run() {
        Main.files.forEach(file -> executorService.submit(new FileSearcher(batch, file)));
    }
}
