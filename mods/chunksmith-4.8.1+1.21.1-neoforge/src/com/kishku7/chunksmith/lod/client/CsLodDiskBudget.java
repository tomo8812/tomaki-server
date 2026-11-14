package com.kishku7.chunksmith.lod.client;

import com.kishku7.chunksmith.lod.net.CsLodMessages;
import com.kishku7.chunksmith.lod.net.CsLodSummary;
import java.util.ArrayList;
import java.util.List;

public final class CsLodDiskBudget {
   private CsLodDiskBudget() {
   }

   public static CsLodDiskBudget.Split apply(List<CsLodMessages.RegionEntry> regions, long onDiskBytes, long budgetBytes) {
      if (regions != null && !regions.isEmpty()) {
         if (budgetBytes <= 0L) {
            return new CsLodDiskBudget.Split(List.copyOf(regions), List.of());
         }

         List<CsLodMessages.RegionEntry> keep = new ArrayList<>();
         List<CsLodMessages.RegionEntry> declined = new ArrayList<>();
         long used = Math.max(0L, onDiskBytes);

         for (CsLodMessages.RegionEntry entry : regions) {
            long cost = Math.max(0L, entry.sizeBytes());
            if (used + cost <= budgetBytes) {
               keep.add(entry);
               used += cost;
            } else {
               declined.add(entry);
            }
         }

         return new CsLodDiskBudget.Split(keep, declined);
      } else {
         return new CsLodDiskBudget.Split(List.of(), List.of());
      }
   }

   public static long foldOut(long aggregate, CsLodMessages.RegionEntry entry) {
      return CsLodSummary.fold(aggregate, entry.regionX(), entry.regionZ(), entry.hash());
   }

   public record Split(List<CsLodMessages.RegionEntry> keep, List<CsLodMessages.RegionEntry> declined) {
      public boolean cappedAnything() {
         return !this.declined.isEmpty();
      }

      public long declinedBytes() {
         long total = 0L;

         for (CsLodMessages.RegionEntry entry : this.declined) {
            total += Math.max(0L, entry.sizeBytes());
         }

         return total;
      }
   }
}
