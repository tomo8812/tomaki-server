package org.millenaire.world.sitescore;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public record SiteChunkData(int[] heights, int water, int lava, List<BoundingBox> pieces, byte state, int artificial, int entities) {
   public static final byte NONE = 0;
   public static final byte PENDING = 1;
   public static final byte RESOLVED = 2;
   public static final int NOT_SCANNED = -1;
   public static final SiteChunkData EMPTY = new SiteChunkData(new int[0], 0, 0, List.of(), (byte)0, -1, 0);
   public static final MapCodec<SiteChunkData> MAP_CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Codec.INT_STREAM.xmap(IntStream::toArray, Arrays::stream).fieldOf("h").forGetter(SiteChunkData::heights),
            Codec.INT.optionalFieldOf("w", 0).forGetter(SiteChunkData::water),
            Codec.INT.optionalFieldOf("l", 0).forGetter(SiteChunkData::lava),
            BoundingBox.CODEC.listOf().optionalFieldOf("p", List.of()).forGetter(SiteChunkData::pieces),
            Codec.BYTE.optionalFieldOf("s", (byte)0).forGetter(SiteChunkData::state),
            Codec.INT.optionalFieldOf("a3", -1).forGetter(SiteChunkData::artificial),
            Codec.INT.optionalFieldOf("e3", 0).forGetter(SiteChunkData::entities)
         )
         .apply(i, SiteChunkData::new)
   );
   public static final Codec<SiteChunkData> CODEC = MAP_CODEC.codec();

   public static SiteChunkData of(ChunkTerrainSummary s, List<BoundingBox> pieces, byte state, ArtificialScan scan) {
      return new SiteChunkData(s.heights(), s.waterMask(), s.lavaMask(), List.copyOf(pieces), state, scan.mask(), scan.blockEntities());
   }

   public boolean hasArtificialScan() {
      return this.artificial != -1;
   }

   public SiteChunkData withArtificial(ArtificialScan scan) {
      return new SiteChunkData(this.heights, this.water, this.lava, this.pieces, this.state, scan.mask(), scan.blockEntities());
   }

   public boolean hasSamples() {
      return this.heights.length == 16;
   }

   public boolean worthSaving() {
      return this.hasSamples() || this.state != 0;
   }

   public ChunkTerrainSummary summary(int chunkX, int chunkZ) {
      return new ChunkTerrainSummary(chunkX, chunkZ, this.heights, this.water, this.lava);
   }

   public SiteChunkData withState(byte newState) {
      return new SiteChunkData(this.heights, this.water, this.lava, this.pieces, newState, this.artificial, this.entities);
   }
}
