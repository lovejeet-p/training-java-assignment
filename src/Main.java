import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.File;
import java.util.*;

public class Main {

    public static Map<Integer, List<String>> queryIdToKeywordsMap = new HashMap<>();
    public  static Queue<String> taskQueue = new LinkedList<>();
    public static Map<String, Map<String, List<Integer>>> queryWordCache = new HashMap<>();
    public static List<String> textFilePaths = new ArrayList<>();
    private static int queryIdCounter = 1;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Consumer consumer= new Consumer();
        Thread thread = new Thread(consumer);
        thread.start();
        String folderPath = "../files";
        File folder = new File(folderPath);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".txt")) {

                        textFilePaths.add(file.getAbsolutePath());
                    }
                }
            }
            String[] textFilePathArray = textFilePaths.toArray(new String[0]);
            for (String path : textFilePathArray) {
                System.out.println(path);
            }
        } else {
            System.out.println("The specified path is not a folder or does not exist.");
        }
        while (true) {
            System.out.println("Menu:");
            System.out.println("1 -> Query keywords");
            System.out.println("2 -> Query ID");
            System.out.println("3 -> Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    queryKeywords(scanner);
                    break;
                case 2:
                    queryById(scanner);
                    break;
                case 3:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void queryKeywords(Scanner scanner) {
        System.out.print("Enter keywords (space-separated): ");
        String keywords = scanner.nextLine();
        int queryId = queryIdCounter++;
        String[] keywordArray = keywords.split(" ");
        List<String> keywordList = new ArrayList<>();
        for (String keyword : keywordArray) {
            taskQueue.add(keyword);
            keywordList.add(keyword);
        }

        queryIdToKeywordsMap.put(queryId, keywordList);

        System.out.println("Query ID: " + queryId);
    }

    private static void queryById(Scanner scanner) {
        System.out.print("Enter Query ID: ");
        int queryId = scanner.nextInt();
        scanner.nextLine();
        if (!queryIdToKeywordsMap.containsKey(queryId)){
            System.out.println("Query id doesn't exist");
            return;
        }
        List<String> result = queryIdToKeywordsMap.get(queryId);
        boolean queryComplete = true;
        for (String word : result) {
            if (!queryWordCache.containsKey(word) || queryWordCache.get(word).size() != textFilePaths.size()) {
                queryComplete = false;
                break;
            }
        }

        if (queryComplete) {
            for (String word : result) {
                Map<String, List<Integer>> fileLinesMap = queryWordCache.get(word);
                System.out.println("Word: " + word);
                for (Map.Entry<String, List<Integer>> entry : fileLinesMap.entrySet()) {
                    String file = entry.getKey();
                    List<Integer> lines = entry.getValue();
                    System.out.println("  File: " + file);
                    System.out.println("  Lines: " + lines);
                }
            }
        } else {
            System.out.println("Query not complete.");
        }
    }
}
