package com.kishku7.chunksmith.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class ChunkSettleWindow {
   private static final int COMPLETE = 9;
   private final Map<Long, Integer> arrived = new HashMap<>();
   private final Map<Long, Runnable> held = new LinkedHashMap<>();
   private final Map<Long, Long> due = new HashMap<>();
   private final long delayTicks;
   private final long maxHeld;
   private long releasedCount;
   private long evictedCount;
   private boolean drained;

   public ChunkSettleWindow(long delayTicks) {
      this(delayTicks, 0L);
   }

   public ChunkSettleWindow(long delayTicks, long maxHeld) {
      this.delayTicks = Math.max(0L, delayTicks);
      this.maxHeld = Math.max(0L, maxHeld);
   }

   public void offer(int chunkX, int chunkZ, long now, Runnable release) {
      long key = key(chunkX, chunkZ);
      if (this.held.putIfAbsent(key, release) == null) {
         for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
               this.bumpAndTest(chunkX + dx, chunkZ + dz, now);
            }
         }

         this.releaseDue(now);
         this.evictBeyondCap();
      }
   }

   public void releaseDue(long now) {
      if (!this.due.isEmpty()) {
         List<Long> ready = null;

         for (Entry<Long, Long> entry : this.due.entrySet()) {
            if (entry.getValue() <= now) {
               if (ready == null) {
                  ready = new ArrayList<>();
               }

               ready.add(entry.getKey());
            }
         }

         if (ready != null) {
            for (Long key : ready) {
               this.due.remove(key);
               this.run(key);
            }
         }
      }
   }

   public void releaseAllHeld() {
      for (Long key : new ArrayList<>(this.held.keySet())) {
         this.due.remove(key);
         this.run(key);
      }
   }

   public void drain() {
      this.drained = true;

      for (Long key : new ArrayList<>(this.held.keySet())) {
         this.due.remove(key);
         this.run(key);
      }

      this.arrived.clear();
      this.due.clear();
   }

   private void evictBeyondCap() {
      if (this.maxHeld > 0L && this.held.size() > this.maxHeld) {
         while (this.held.size() > this.maxHeld) {
            Iterator<Entry<Long, Runnable>> it = this.held.entrySet().iterator();
            if (!it.hasNext()) {
               return;
            }

            Long oldest = it.next().getKey();
            this.due.remove(oldest);
            this.evictedCount++;
            this.run(oldest);
         }
      }
   }

   public boolean isDrained() {
      return this.drained;
   }

   public int heldCount() {
      return this.held.size();
   }

   public int trackedCount() {
      return this.arrived.size();
   }

   public long releasedCount() {
      return this.releasedCount;
   }

   public long evictedCount() {
      return this.evictedCount;
   }

   boolean neighbourhoodComplete(int chunkX, int chunkZ) {
      Integer count = this.arrived.get(key(chunkX, chunkZ));
      return count != null && count >= 9;
   }

   private void bumpAndTest(int chunkX, int chunkZ, long now) {
      long key = key(chunkX, chunkZ);
      int count = this.arrived.merge(key, 1, Integer::sum);
      if (count >= 9) {
         if (this.held.containsKey(key)) {
            this.due.putIfAbsent(key, now + this.delayTicks);
         } else {
            this.arrived.remove(key);
         }
      }
   }

   private void run(Long key) {
      Runnable release = this.held.remove(key);
      if (release != null) {
         this.releasedCount++;
         this.arrived.remove(key);
         release.run();
      }

      this.pruneStale();
   }

   private void pruneStale() {
      if (this.arrived.size() > this.held.size() * 9 + 9) {
         Iterator<Entry<Long, Integer>> it = this.arrived.entrySet().iterator();

         while (it.hasNext()) {
            Entry<Long, Integer> entry = it.next();
            if (entry.getValue() >= 9 && !this.held.containsKey(entry.getKey())) {
               it.remove();
            }
         }
      }
   }

   static long key(int chunkX, int chunkZ) {
      return (long)chunkX << 32 | chunkZ & 4294967295L;
   }
}
