package vng.training.w3;

import net.jpountz.xxhash.StreamingXXHash32;
import net.jpountz.xxhash.StreamingXXHash64;
import net.jpountz.xxhash.XXHashFactory;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static org.apache.commons.lang3.RandomStringUtils.randomAscii;

public class Main {

    private static void doXXH32(ByteBuffer buffer) {
        xxHash32.update(buffer.array(), 0, buffer.array().length);
//        System.out.println("XXH32: " + xxHash32.getValue());
    }

    private static void doXXH64(ByteBuffer buffer) {
        xxHash64.update(buffer.array(), 0, buffer.array().length);
//        System.out.println("XXH64: " + xxHash64.getValue());
    }

    private static final MessageDigest md;
    private static final XXHashFactory factory = XXHashFactory.fastestInstance();
    private static final StreamingXXHash32 xxHash32 = factory.newStreamingHash32(new Random().nextInt());
    private static final StreamingXXHash64 xxHash64 = factory.newStreamingHash64(new Random().nextLong());

    static {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static void doMD5(ByteBuffer buffer) {
        byte[] hash = md.digest(buffer.array());
//        System.out.println("MD5: " + hash);
    }

    private static class PerformanceProfile {

        private final String name;
        private final Consumer<ByteBuffer> hashFunction;
        private long counter;

        public PerformanceProfile(String name, Consumer<ByteBuffer> hashFunction, int counter) {
            this.name = name;
            this.hashFunction = hashFunction;
            this.counter = counter;
        }

        public String getName() {
            return name;
        }

        public long getCounter() {
            return counter;
        }

        public void random() {
            String ascii = randomAscii(100);
            ByteBuffer buffer = ByteBuffer.wrap(ascii.getBytes());
            hashFunction.accept(buffer);
            counter++;
        }

    }

    private static AtomicBoolean running = new AtomicBoolean(true);

    public static void main(String[] args) throws NoSuchAlgorithmException {
        List<PerformanceProfile> profiles = List.of(
                new PerformanceProfile("XXH32", Main::doXXH32, 0),
                new PerformanceProfile("XXH64", Main::doXXH64, 0),
                new PerformanceProfile("MD5", Main::doMD5, 0)
        );

        //Test
        for (PerformanceProfile profile : profiles) {
            profile.random();
        }

        Executor executor = Executors.newFixedThreadPool(profiles.size());
        CompletionService<PerformanceProfile> completionService =
                new ExecutorCompletionService<>(executor);

        List<Future<PerformanceProfile>> futures = profiles.stream().map(profile -> completionService.submit(() -> {
            while (running.get()) {
                profile.random();
            }
            return profile;
        })).toList();

        long end = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(15);
        long start = System.currentTimeMillis();
        int lastMinute = 0;
        List<Integer> checkpoints = List.of(1, 5, 15, 30);
        while (System.currentTimeMillis() < end) {
            int currentMinute = (int) TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - start);
            if (currentMinute != lastMinute) {
                lastMinute = currentMinute;
                System.out.println("Minute: " + currentMinute);
                if (checkpoints.contains(currentMinute)) {
                    for (PerformanceProfile profile : profiles) {
                        System.out.println(profile.getName() + ": " + profile.getCounter());
                    }
                }
            }
        }
        running.set(false);
        int received = 0;
        while (received < futures.size()) {
            try {
                PerformanceProfile profile = completionService.take().get();
                System.out.println(profile.getName() + ": " + profile.getCounter());
                received++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
