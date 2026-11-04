package org.embeddedt.modernfix.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.function.Function;

public class CacheUtil {
   public static <K, V> LoadingCache<K, V> simpleCacheForLambda(final Function<K, V> function, long maxSize) {
      return CacheBuilder.newBuilder().maximumSize(maxSize).build(new CacheLoader<K, V>() {
         public V load(K key) throws Exception {
            return function.apply(key);
         }
      });
   }
}
