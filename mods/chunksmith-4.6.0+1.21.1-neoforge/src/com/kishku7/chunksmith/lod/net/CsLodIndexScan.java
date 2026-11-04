package com.kishku7.chunksmith.lod.net;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public final class CsLodIndexScan {
   public static final int REGION_BLOCKS = 512;
   public static final int MAX_REGIONS = 4096;
   public static final long NO_BUDGET = 0L;

   private CsLodIndexScan() {
   }

   public static CsLodIndexScan.Result scan(Path dimensionDir, CsLodIndexScan.Request request, long nowMillis) throws IOException {
      List<CsLodMessages.RegionEntry> found = new ArrayList<>();
      if (dimensionDir != null && Files.isDirectory(dimensionDir)) {
         try (Stream<Path> files = Files.list(dimensionDir)) {
            Iterator var6 = files.toList().iterator();

            label71:
            while (true) {
               Path file;
               int regionX;
               int regionZ;
               while (true) {
                  if (!var6.hasNext()) {
                     break label71;
                  }

                  file = (Path)var6.next();
                  String name = file.getFileName().toString();
                  if (name.endsWith(".cslod")) {
                     String[] parts = name.split("\\.");
                     if (parts.length == 4) {
                        try {
                           regionX = Integer.parseInt(parts[1]);
                           regionZ = Integer.parseInt(parts[2]);
                           break;
                        } catch (NumberFormatException ignored) {
                        }
                     }
                  }
               }

               if (inRange(request, regionX, regionZ)) {
                  BasicFileAttributes attrs;
                  try {
                     attrs = Files.readAttributes(file, BasicFileAttributes.class);
                  } catch (IOException e) {
                     continue;
                  }

                  if (attrs.isRegularFile() && nowMillis - attrs.lastModifiedTime().toMillis() >= 10000L) {
                     found.add(
                        new CsLodMessages.RegionEntry(regionX, regionZ, CsLodRegionHash.of(attrs.lastModifiedTime().toMillis(), attrs.size()), attrs.size())
                     );
                  }
               }
            }
         }

         found.sort(
            Comparator.<CsLodMessages.RegionEntry>comparingLong(e -> distanceSquared(request, e.regionX(), e.regionZ()))
               .thenComparingInt(CsLodMessages.RegionEntry::regionX)
               .thenComparingInt(CsLodMessages.RegionEntry::regionZ)
         );
         return cap(found, request.budgetBytes());
      } else {
         return new CsLodIndexScan.Result(List.of(), 0, 0L);
      }
   }

   public static long aggregate(List<CsLodMessages.RegionEntry> regions) {
      long aggregate = 0L;

      for (CsLodMessages.RegionEntry entry : regions) {
         aggregate = CsLodSummary.fold(aggregate, entry.regionX(), entry.regionZ(), entry.hash());
      }

      return aggregate;
   }

   private static CsLodIndexScan.Result cap(List<CsLodMessages.RegionEntry> found, long budgetBytes) {
      long bytes = 0L;

      for (int i = 0; i < found.size(); i++) {
         long next = bytes + found.get(i).sizeBytes();
         if (i >= 4096 || budgetBytes > 0L && next > budgetBytes) {
            return new CsLodIndexScan.Result(List.copyOf(found.subList(0, i)), found.size(), bytes);
         }

         bytes = next;
      }

      return new CsLodIndexScan.Result(List.copyOf(found), found.size(), bytes);
   }

   public static boolean inRange(CsLodIndexScan.Request request, int regionX, int regionZ) {
      return distanceSquared(request, regionX, regionZ) <= (long)request.radiusBlocks() * request.radiusBlocks();
   }

   public static long distanceSquared(CsLodIndexScan.Request request, int regionX, int regionZ) {
      int minX = regionX * 512;
      int minZ = regionZ * 512;
      int maxX = minX + 512 - 1;
      int maxZ = minZ + 512 - 1;
      int dx = Math.max(0, Math.max(minX - request.px(), request.px() - maxX));
      int dz = Math.max(0, Math.max(minZ - request.pz(), request.pz() - maxZ));
      return (long)dx * dx + (long)dz * dz;
   }

   public record Request(String dimension, int px, int pz, int radiusBlocks, long budgetBytes) {
      public Request(String dimension, int px, int pz, int radiusBlocks) {
         this(dimension, px, pz, radiusBlocks, 0L);
      }
   }

   public record Result(List<CsLodMessages.RegionEntry> regions, int found, long bytes) {
      public boolean capped() {
         return this.regions.size() < this.found;
      }

      public boolean cappedByRegionCount() {
         return this.capped() && this.regions.size() >= 4096;
      }

      public boolean cappedByBudget() {
         return this.capped() && this.regions.size() < 4096;
      }
   }
}
