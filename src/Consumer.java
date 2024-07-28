import java.util.ArrayList;
import java.util.concurrent.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

class Consumer implements Runnable {
    private static int BATCH_SIZE = 8;
    private static int NUM_BATCH_THREADS = 8;
    private static int NUM_FILE_THREADS = 8;
    private ExecutorService batchExecutorService;
    private ExecutorService fileExecutorService;

    public Consumer() {
        this.fileExecutorService = Executors.newFixedThreadPool(NUM_FILE_THREADS, DaemonThreadFactory.instance);
        BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>(NUM_BATCH_THREADS);
        this.batchExecutorService = new ThreadPoolExecutor(
                NUM_BATCH_THREADS,
                NUM_BATCH_THREADS,
                0L, TimeUnit.MILLISECONDS,
                blockingQueue,
                DaemonThreadFactory.instance,
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    @Override
    public void run() {
        while (true) {
            ArrayList<String> wordBatch = new ArrayList<>();
            int taskSize=Main.taskQueue.size();

            for(int i = 0; i < min(BATCH_SIZE, taskSize); i++) {
                String word = Main.taskQueue.poll();
                wordBatch.add(word);
            }


            if(wordBatch.size() == 0) {
                continue;
            }

            BatchSearcher batchSearcher = new BatchSearcher(wordBatch, fileExecutorService);
            batchExecutorService.execute(batchSearcher);
        }
    }
}
