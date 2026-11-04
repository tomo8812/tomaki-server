package org.embeddedt.modernfix.dynamicresources;

import com.mojang.blaze3d.audio.SoundBuffer;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.resources.ResourceLocation;
import org.embeddedt.modernfix.ModernFix;
import org.jetbrains.annotations.NotNull;

public class DynamicSoundHelpers {
   private static final long SOUND_EVICTION_DELAY = TimeUnit.SECONDS.toNanos(30L);
   private static final boolean debugDynamicSoundLoading = Boolean.getBoolean("modernfix.debugDynamicSoundLoading");

   public static final class Cache extends AbstractMap<ResourceLocation, CompletableFuture<SoundBuffer>> {
      private final Object2ObjectLinkedOpenHashMap<ResourceLocation, DynamicSoundHelpers.Cache.Entry> store = new Object2ObjectLinkedOpenHashMap();

      public Cache(Map<ResourceLocation, CompletableFuture<SoundBuffer>> otherMap) {
         this.putAll(otherMap);
      }

      private void checkExpired() {
         long ts = System.nanoTime();
         ObjectBidirectionalIterator<it.unimi.dsi.fastutil.objects.Object2ObjectMap.Entry<ResourceLocation, DynamicSoundHelpers.Cache.Entry>> iter = this.store
            .object2ObjectEntrySet()
            .fastIterator();

         while (iter.hasNext()) {
            it.unimi.dsi.fastutil.objects.Object2ObjectMap.Entry<ResourceLocation, DynamicSoundHelpers.Cache.Entry> entry = (it.unimi.dsi.fastutil.objects.Object2ObjectMap.Entry<ResourceLocation, DynamicSoundHelpers.Cache.Entry>)iter.next();
            if (!((DynamicSoundHelpers.Cache.Entry)entry.getValue()).isExpired(ts)) {
               break;
            }

            if (DynamicSoundHelpers.debugDynamicSoundLoading) {
               ModernFix.LOGGER
                  .warn("Evicted sound {} with duration {} ms", entry.getKey(), ((DynamicSoundHelpers.Cache.Entry)entry.getValue()).getDuration() / 1000000L);
            }

            ((DynamicSoundHelpers.Cache.Entry)entry.getValue()).discard();
            iter.remove();
         }
      }

      public CompletableFuture<SoundBuffer> get(Object key) {
         if (key instanceof ResourceLocation rl) {
            DynamicSoundHelpers.Cache.Entry entry = (DynamicSoundHelpers.Cache.Entry)this.store.getAndMoveToLast(rl);
            CompletableFuture<SoundBuffer> result = entry != null ? entry.getBuffer() : null;
            this.checkExpired();
            return result;
         } else {
            return null;
         }
      }

      public CompletableFuture<SoundBuffer> put(ResourceLocation key, CompletableFuture<SoundBuffer> value) {
         DynamicSoundHelpers.Cache.Entry entry = new DynamicSoundHelpers.Cache.Entry(value);
         if (DynamicSoundHelpers.debugDynamicSoundLoading) {
            ModernFix.LOGGER.info("Loaded sound {}", key);
         }

         DynamicSoundHelpers.Cache.Entry previousEntry = (DynamicSoundHelpers.Cache.Entry)this.store.putAndMoveToLast(key, entry);
         return previousEntry != null ? previousEntry.getBuffer() : null;
      }

      @NotNull
      @Override
      public Set<Entry<ResourceLocation, CompletableFuture<SoundBuffer>>> entrySet() {
         return new DynamicSoundHelpers.Cache.EntrySet();
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Object put(Object param1, Object nullx) {
         return this.put((ResourceLocation)var1, (CompletableFuture<SoundBuffer>)nullx);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Object get(Object param1) {
         return this.get(var1);
      }

      private static class Entry {
         private final CompletableFuture<SoundBuffer> buffer;
         private long lastAccessTime;

         private Entry(CompletableFuture<SoundBuffer> buffer) {
            this.buffer = buffer;
            this.lastAccessTime = System.nanoTime();
         }

         public CompletableFuture<SoundBuffer> getBuffer() {
            this.lastAccessTime = System.nanoTime();
            return this.buffer;
         }

         public long getDuration() {
            SoundBuffer buf = this.buffer.getNow(null);
            return buf == null ? 0L : ((DynamicSoundHelpers.SoundBufAccess)buf).mfix$getDurationNanos();
         }

         public boolean isExpired(long currentTs) {
            long duration = this.getDuration();
            return duration > 0L && currentTs - this.lastAccessTime >= duration + DynamicSoundHelpers.SOUND_EVICTION_DELAY;
         }

         public void discard() {
            this.buffer.thenAccept(SoundBuffer::discardAlBuffer);
         }

         @Override
         public String toString() {
            return super.toString();
         }
      }

      private class EntrySet extends AbstractSet<java.util.Map.Entry<ResourceLocation, CompletableFuture<SoundBuffer>>> {
         @Override
         public Iterator<java.util.Map.Entry<ResourceLocation, CompletableFuture<SoundBuffer>>> iterator() {
            final ObjectBidirectionalIterator<java.util.Map.Entry<ResourceLocation, DynamicSoundHelpers.Cache.Entry>> storeIter = Cache.this.store
               .entrySet()
               .iterator();
            return new Iterator<java.util.Map.Entry<ResourceLocation, CompletableFuture<SoundBuffer>>>() {
               @Override
               public boolean hasNext() {
                  return storeIter.hasNext();
               }

               public java.util.Map.Entry<ResourceLocation, CompletableFuture<SoundBuffer>> next() {
                  java.util.Map.Entry<ResourceLocation, DynamicSoundHelpers.Cache.Entry> entry = (java.util.Map.Entry<ResourceLocation, DynamicSoundHelpers.Cache.Entry>)storeIter.next();
                  return new SimpleImmutableEntry<>(entry.getKey(), entry.getValue().buffer);
               }

               // $VF: synthetic method
               // $VF: bridge method
               @Override
               public Object next() {
                  return this.next();
               }
            };
         }

         @Override
         public int size() {
            return Cache.this.store.size();
         }

         @Override
         public void clear() {
            Cache.this.store.clear();
         }
      }
   }

   public interface SoundBufAccess {
      long mfix$getDurationNanos();
   }
}
