import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FileSearcher implements Runnable {

    private Collection<String> batch;
    private String path;
    private final Map<String, List<Integer>> searchResults;
    private final Predicate<String> SEARCHED_WORDS_FILTER = (word) ->
        Main.wordQueryCache.containsKey(word) && !Main.wordQueryCache.get(word).containsKey(path);

    FileSearcher(Collection<String> batch, String path) {
        this.batch = batch;
        this.path = path;
        this.searchResults = new HashMap<>();
        batch.forEach(word -> searchResults.put(word, new ArrayList<>()));
    }

    @Override
    public void run() {
        synchronized (Main.wordQueryCache) {
            batch = batch.stream().filter(SEARCHED_WORDS_FILTER).collect(Collectors.toList());
        }
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(path);
        searchWords(inputStream);
    }

    void searchWords(InputStream inputStream) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            int lineNumber = 0;
            String line;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                for (String word : batch) {
                    if (line.contains(word)) {
                        searchResults.get(word).add(lineNumber);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while reading from file: " + path);
            e.printStackTrace();
        } finally {
            updateCache();
        }
    }

    synchronized private void updateCache() {
        batch.forEach(word -> {
            Main.wordQueryCache.get(word).put(path, searchResults.get(word));
        });
    }
}
