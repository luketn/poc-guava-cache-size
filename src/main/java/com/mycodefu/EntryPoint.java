package com.mycodefu;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.openjdk.jol.info.ClassData;
import org.openjdk.jol.info.GraphLayout;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class EntryPoint {
    private record CacheValue(String key, String data){
        public CacheValue(String key){
            this(key, randomData());
        }

        private static String randomData() {
            Random random = new Random();
            char[] bigDataString = new char[random.nextInt(3000, 10000)];
            char a = 'a';
            for (int i = 0; i < bigDataString.length; i++) {
                bigDataString[i] = (char) (a + (i % 26));
            }
            return new String(bigDataString);
        }
    }

    private static final LoadingCache<String, CacheValue> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .recordStats()
            .build(
                CacheLoader.from(CacheValue::new)
            );

    public static void main(String[] args) throws ExecutionException {
        System.setProperty("jol.magicFieldOffset","true");

        for (int i = 0; i < 10_000; i++) {
            String key = "Hi there %d!".formatted(i);
            CacheValue cacheValue = cache.get(key);
            System.out.println(cacheValue);

            for (int j = 9999; j > 0; j--) {
                cache.get(key);
            }
        }

        System.out.println(cache.stats());

        CacheValue value = cache.get("Hi there %d!".formatted(5000));
        GraphLayout graphLayout = GraphLayout.parseInstance(value);
        long sizeBytes = graphLayout.totalSize();
        System.out.println("Size in bytes object: %d".formatted(sizeBytes));

        long sizeMB = GraphLayout.parseInstance(cache).totalSize() / 1024 / 1024;
        System.out.println("Cache size in MB: %d".formatted(sizeMB));
    }
}
