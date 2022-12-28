package vng.training.w3;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;
import test.Sizeof;
import vng.training.w3.predictive.IPredictiveText;
import vng.training.w3.predictive.TriePredictive;
import vng.training.w3.searcher.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class Main {

    private static record PerformanceProfile(String name, WordSearcher searcher) {
    }

    public static void main(String[] args) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        List<Blog> blogs = new ArrayList<>();

        System.out.println("Reading blogs...");
        {
            File cache = new File("cache_blog.csv");
            File csv;
            if (cache.exists()) {
                System.out.println("Reading from cache...");
                csv = cache;
            } else {
                System.out.println("Reading from csv...");
                csv = new File("blogtext.csv");
            }
            blogs = Utils.readCsv(csv, Blog.class);

            for (int i = 0; i < blogs.size(); i++) {
                int j = i + 1;
                while (j < blogs.size()) {
                    if (blogs.get(i).getId().equals(blogs.get(j).getId())) {
                        blogs.get(i).setText(blogs.get(i).getText() + blogs.get(j).getText());
                        blogs.remove(j);
                    } else {
                        break;
                    }
                }
            }

            if (!cache.exists()) {
                System.out.println("Writing cache...");
                Utils.writeCsv(cache, blogs);
            }
        }

        System.out.println("Reading words...");
        List<String> words = blogs.parallelStream().map(blog -> blog.getText().split(" ")).flatMap(Arrays::stream)
                .map(String::trim).distinct().toList();

        int functionType = 0;

        Scanner scanner = new Scanner(System.in);
        while (functionType != 3) {
            System.out.println("Choose function:");
            System.out.println("1. Search word");
            System.out.println("2. Predictive text");
            System.out.println("3. Exit");
            functionType = scanner.nextInt();
            scanner.nextLine();

            switch (functionType) {
                case 1 -> {
                    startSearchers(words);
                    return;
                }
                case 2 -> {
                    startPredictiveText(words);
                    return;
                }
                default -> System.out.println("Invalid function type");
            }
        }

    }

    private static long getUsedMemory() {
        System.gc();
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    private static void startSearchers(List<String> words) {
        System.out.println("Creating searchers...");

        System.out.println("Creating simple searcher...");
        long before = getUsedMemory();
        PerformanceProfile simpleProfile = new PerformanceProfile("Simple", new SimpleWordSearcher(words));
        long after = getUsedMemory();
//        System.out.println(GraphLayout.parseInstance(simpleProfile.searcher).totalSize());
        System.out.println("Simple searcher created, memory used: " + (after - before));

        System.out.println("Creating sorted searcher...");
        before = getUsedMemory();
        PerformanceProfile sortedProfile = new PerformanceProfile("Sorted", new SortedWordSearcher(words));
        after = getUsedMemory();
//        System.out.println(GraphLayout.parseInstance(sortedProfile.searcher).totalSize());
        System.out.println("Sorted searcher created, memory used: " + (after - before));

        System.out.println("Creating bloom filter searcher...");
        before = getUsedMemory();
        PerformanceProfile bloomFilterProfile = new PerformanceProfile("Bloom Filter", new BloomFilterWordSearcher(words));
        after = getUsedMemory();
//        System.out.println(GraphLayout.parseInstance(bloomFilterProfile.searcher).totalSize());
        System.out.println("Bloom filter searcher created, memory used: " + (after - before));

        System.out.println("Creating hash set searcher...");
        before = getUsedMemory();
        PerformanceProfile hashSetProfile = new PerformanceProfile("Hash", new HashWordSearcher(words));
        after = getUsedMemory();
//        System.out.println(GraphLayout.parseInstance(hashSetProfile.searcher).totalSize());
        System.out.println("Hash set searcher created, memory used: " + (after - before));

        System.out.println("Creating trie searcher...");
        before = getUsedMemory();
        PerformanceProfile trieProfile = new PerformanceProfile("Trie", new TrieWordSearcher(words));
        after = getUsedMemory();
//        System.out.println(GraphLayout.parseInstance(trieProfile.searcher).totalSize());
        System.out.println("Trie searcher created, memory used: " + (after - before));

        List<PerformanceProfile> profiles = List.of(simpleProfile, sortedProfile, bloomFilterProfile, hashSetProfile, trieProfile);

        Executor executor = Executors.newFixedThreadPool(profiles.size());
        CompletionService<?> completionService =
                new ExecutorCompletionService<>(executor);

        System.out.println("Reading queries...");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter a word to search: ");
            String word = scanner.nextLine();
            if (word.equals("exit")) {
                break;
            }

            System.out.println("Searching...");
            List<? extends Future<?>> futures = profiles.stream().map(profile -> completionService.submit(() -> {
                long start = System.nanoTime();
                boolean contains = profile.searcher.contains(word);
                long end = System.nanoTime();
                System.out.printf("%s: %s (%d ms, %d ns)%n", profile.name, contains, (end - start) / 1_000_000, end - start);
                return null;
            })).toList();

            int received = 0;
            while (received < futures.size()) {
                try {
                    completionService.take();
                    received++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Done!");
        }
    }

    private static void startPredictiveText(List<String> words) throws IOException {
        System.out.println("Creating predictive text...");
        IPredictiveText predictiveText = new TriePredictive(words);

        // Setup terminal and screen layers
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();

        // Create panel to hold components
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(2));

        TextBox[] results = new TextBox[5];

        panel.addComponent(new Label("Input"));
        new TextBox().setTextChangeListener((s, b) -> {
            for (TextBox result : results) {
                result.setText("");
            }
            List<String> predictions = predictiveText.predict(s, 5);
            for (int i = 0; i < results.length; i++) {
                results[i].setText(i < predictions.size() ? predictions.get(i) : "");
            }
        }).addTo(panel);

        panel.addComponent(new Label("Predictive"));
        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));

        for (int i = 0; i < results.length; i++) {
            panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
            results[i] = new TextBox();
            results[i].setEnabled(false);
            panel.addComponent(results[i]);
        }

        // Create window to hold the panel
        BasicWindow window = new BasicWindow();
        window.setComponent(panel);

        // Create gui and start gui
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
        gui.addWindowAndWait(window);

        /*System.out.println("Reading queries...");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter a prefix to search: ");
            String prefix = scanner.nextLine();
            if (prefix.equals("exit")) {
                break;
            }

            System.out.println("Searching...");
            long start = System.nanoTime();
            List<String> results = predictiveText.predict(prefix, 5);
            long end = System.nanoTime();
            System.out.printf("Found %d results (%d ms, %d ns)%n", results.size(), (end - start) / 1_000_000, end - start);
            System.out.printf("Results: %s%n", results.stream().collect(Collectors.joining(", ")));
            System.out.println("Done!");
        }*/
    }

}
