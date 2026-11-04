package org.embeddedt.modernfix.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.embeddedt.modernfix.ModernFix;
import org.embeddedt.modernfix.platform.ModernFixPlatformHooks;

public class LRUMap<K, V> extends Object2ObjectLinkedOpenHashMap<K, V> {
   private Set<K> permanentEntries = Set.of();

   public LRUMap(Map<K, V> map) {
      super(map);
   }

   public V put(K k, V v) {
      return (V)this.putAndMoveToLast(k, v);
   }

   public V get(Object k) {
      return (V)this.getAndMoveToLast(k);
   }

   public void setPermanentEntries(Set<K> permanentEntries) {
      this.permanentEntries = permanentEntries;
   }

   public void dropEntriesToMeetSize(int size) {
      int expectedQuota = size;
      size += this.permanentEntries.size();
      int prevSize = this.size();
      if (this.size() > size) {
         ObjectBidirectionalIterator<Entry<K, V>> iterator = this.entrySet().iterator();

         while (this.size() > size && iterator.hasNext()) {
            Entry<K, V> entry = (Entry<K, V>)iterator.next();
            K key = entry.getKey();
            if (key != null && !this.permanentEntries.contains(key)) {
               iterator.remove();
            }
         }

         this.trim(this.size() + expectedQuota);
         if (ModernFixPlatformHooks.INSTANCE.isDevEnv()) {
            ModernFix.LOGGER.warn("Trimmed map from {} to {} entries", prevSize, size);
         }
      }
   }
}
