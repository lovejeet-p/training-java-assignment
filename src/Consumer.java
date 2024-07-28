import java.util.ArrayList;
import java.util.concurrent.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

class Consumer implements Runnable {
    private BlockingQueue<String> queue;
    private static int BATCH_SIZE = 8;
    private static int NUM_BATCH_THREADS = 8;
    private static int NUM_FILE_THREADS = 8;
    private ExecutorService batchExecutorService;
    private ExecutorService fileExecutorService;

    public Consumer(BlockingQueue<String> queue) {
        this.queue = queue;
        this.fileExecutorService = Executors.newFixedThreadPool(NUM_FILE_THREADS);
        BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>(NUM_BATCH_THREADS);
        this.batchExecutorService = new ThreadPoolExecutor(
                NUM_BATCH_THREADS,
                NUM_BATCH_THREADS,
                0L, TimeUnit.MILLISECONDS,
                blockingQueue,
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    @Override
    public void run() {
        while (true) {
            try {
                ArrayList<String> wordBatch = new ArrayList<>();
                for(int i = 0; i < min(BATCH_SIZE, queue.size()); i++) {
                    String word = queue.take();
                    wordBatch.add(word);
                    System.out.println("Consumed: " + word);
                }

                BatchSearcher batchSearcher = new BatchSearcher(wordBatch, fileExecutorService);
                batchExecutorService.execute(batchSearcher);
                // It is a runnable, create a thread pool in this class and ddd to a pool here

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Consumer interrupted");
            } finally {
                batchExecutorService.shutdown();
                try {
                    if (!batchExecutorService.awaitTermination(60, TimeUnit.SECONDS)) {
                        batchExecutorService.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    batchExecutorService.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
