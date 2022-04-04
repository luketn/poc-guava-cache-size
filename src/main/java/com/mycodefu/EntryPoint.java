package com.mycodefu;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class EntryPoint {
    private record CacheValue(String key){}

    private static final LoadingCache<String, CacheValue> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .recordStats()
            .build(
                CacheLoader.from(s -> new CacheValue(s))
            );

    public static void main(String[] args) throws ExecutionException {
        for (int i = 0; i < 10_000; i++) {
            String key = "Hi there %d!".formatted(i);
            CacheValue cacheValue = cache.get(key);
            System.out.println(cacheValue);

            for (int j = 9999; j > 0; j--) {
                cache.get(key);
            }
        }

        System.out.println(cache.stats());
    }
}
