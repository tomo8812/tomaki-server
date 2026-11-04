package com.kishku7.chunksmith.util;

import java.util.Objects;
import java.util.Optional;

public record ChunkCoordinate(int x, int z) implements Comparable<ChunkCoordinate> {
   public static Optional<ChunkCoordinate> fromRegionFile(String regionFileName) {
      if (!regionFileName.startsWith("r.")) {
         return Optional.empty();
      }

      int extension = regionFileName.indexOf(".mca");
      if (extension < 2) {
         return Optional.empty();
      }

      String regionCoordinates = regionFileName.substring(2, extension);
      int separator = regionCoordinates.indexOf(46);
      Optional<Integer> regionX = Input.tryInteger(regionCoordinates.substring(0, separator));
      Optional<Integer> regionZ = Input.tryInteger(regionCoordinates.substring(separator + 1));
      return regionX.isPresent() && regionZ.isPresent() ? Optional.of(new ChunkCoordinate(regionX.get(), regionZ.get())) : Optional.empty();
   }

   public int compareTo(ChunkCoordinate o) {
      return this.x == o.x ? Integer.compare(this.z, o.z) : Integer.compare(this.x, o.x);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ChunkCoordinate that = (ChunkCoordinate)o;
         return this.x == that.x && this.z == that.z;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.x, this.z);
   }

   @Override
   public String toString() {
      return String.format("%d, %d", this.x, this.z);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public int compareTo(Object param1) {
      return this.compareTo((ChunkCoordinate)var1);
   }
}
