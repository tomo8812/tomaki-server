package dev.diorama.data;

import dev.diorama.Facing;
import dev.diorama.mixin.BiomeManagerAccessor;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.BitStorage;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.jspecify.annotations.Nullable;

public final class ChunkSnapshot {
   public final int chunkX;
   public final int chunkZ;
   public final int minSectionY;
   private final @Nullable PalettedContainer<BlockState>[] states;
   private final @Nullable BlockState[] uniform;
   private final boolean hasSkyLight;
   private final @Nullable DataLayer[] blockLight;
   private final @Nullable DataLayer[] skyLight;
   public static final int BIOME_REACH = 12;
   private final @Nullable PalettedContainerRO<Holder<Biome>>[] @Nullable [] biomes;
   private final int[] surface = new int[256];
   public final long biomeZoomSeed;
   public final int blendRadius;
   public final Holder<Biome> fallbackBiome;
   private volatile ChunkSnapshot.@Nullable Tints tints;
   public final int maxY;
   public final int floorY;
   private final ChunkSnapshot.@Nullable SectionCopycats @Nullable [] copycats;
   private final Facing facing;
   private final @Nullable ChunkSnapshot base;
   private final @Nullable ChunkSnapshot @Nullable [] views;
   private static final int[][] COLUMNS = new int[4][];

   public ChunkSnapshot turned(Facing facing) {
      if (facing == Facing.NORTH) {
         return this;
      }

      ChunkSnapshot view = this.views[facing.turns];
      if (view == null) {
         view = new ChunkSnapshot(this, facing);
         this.views[facing.turns] = view;
      }

      return view;
   }

   private ChunkSnapshot(ChunkSnapshot base, Facing facing) {
      this.base = base;
      this.facing = facing;
      this.views = null;
      this.chunkX = facing.blockX(base.chunkX, base.chunkZ);
      this.chunkZ = facing.blockZ(base.chunkX, base.chunkZ);
      this.minSectionY = base.minSectionY;
      this.states = base.states;
      this.uniform = base.uniform;
      this.hasSkyLight = base.hasSkyLight;
      this.blockLight = base.blockLight;
      this.skyLight = base.skyLight;
      this.biomes = base.biomes;
      this.biomeZoomSeed = base.biomeZoomSeed;
      this.blendRadius = base.blendRadius;
      this.fallbackBiome = base.fallbackBiome;
      this.maxY = base.maxY;
      this.floorY = base.floorY;
      this.copycats = turnedCopycats(base.copycats, facing);

      for (int x = 0; x < 16; x++) {
         for (int z = 0; z < 16; z++) {
            this.surface[z << 4 | x] = base.surface(this.localX(x, z), this.localZ(x, z));
         }
      }
   }

   private int localX(int x, int z) {
      return this.facing.worldBlockX(x, z) & 15;
   }

   private int localZ(int x, int z) {
      return this.facing.worldBlockZ(x, z) & 15;
   }

   private static ChunkSnapshot.@Nullable SectionCopycats @Nullable [] turnedCopycats(
      ChunkSnapshot.@Nullable SectionCopycats @Nullable [] copycats, Facing facing
   ) {
      if (copycats == null) {
         return null;
      }

      ChunkSnapshot.SectionCopycats[] out = new ChunkSnapshot.SectionCopycats[copycats.length];

      for (int i = 0; i < copycats.length; i++) {
         if (copycats[i] != null) {
            int[] at = (int[])copycats[i].at().clone();

            for (int k = 0; k < at.length; k++) {
               int x = at[k] & 15;
               int z = at[k] >> 4 & 15;
               at[k] = at[k] & -256 | (facing.blockZ(x, z) & 15) << 4 | facing.blockX(x, z) & 15;
            }

            BlockState[] materials = (BlockState[])copycats[i].material().clone();

            for (int k = 0; k < materials.length; k++) {
               materials[k] = facing.turned(materials[k]);
            }

            out[i] = new ChunkSnapshot.SectionCopycats(at, materials);
         }
      }

      return out;
   }

   public BlockState state(PalettedContainer<BlockState> states, int x, int y, int z) {
      return this.facing.turned((BlockState)states.get(this.localX(x, z), y, this.localZ(x, z)));
   }

   public ChunkSnapshot chunk() {
      return this.base != null ? this.base : this;
   }

   public BlockState turned(BlockState state) {
      return this.facing.turned(state);
   }

   private ChunkSnapshot(LevelChunk chunk, ClientLevel level) {
      this.facing = Facing.NORTH;
      this.base = null;
      this.views = new ChunkSnapshot[4];
      this.chunkX = chunk.getPos().x;
      this.chunkZ = chunk.getPos().z;
      this.minSectionY = chunk.getMinSection();
      LevelChunkSection[] sections = chunk.getSections();
      this.states = new PalettedContainer[sections.length];
      this.uniform = new BlockState[sections.length];
      this.biomes = new PalettedContainerRO[9][];

      for (int dx = -1; dx <= 1; dx++) {
         for (int dz = -1; dz <= 1; dz++) {
            ChunkAccess around = dx == 0 && dz == 0 ? chunk : level.getChunkSource().getChunk(this.chunkX + dx, this.chunkZ + dz, ChunkStatus.BIOMES, false);
            if (around != null) {
               LevelChunkSection[] aroundSections = around.getSections();
               PalettedContainerRO<Holder<Biome>>[] biomes = new PalettedContainerRO[aroundSections.length];

               for (int i = 0; i < aroundSections.length; i++) {
                  biomes[i] = aroundSections[i] == null ? null : aroundSections[i].getBiomes();
               }

               this.biomes[(dx + 1) * 3 + dz + 1] = biomes;
            }
         }
      }

      this.biomeZoomSeed = ((BiomeManagerAccessor)level.getBiomeManager()).diorama$biomeZoomSeed();
      this.blendRadius = (Integer)Minecraft.getInstance().options.biomeBlendRadius().get();
      this.fallbackBiome = level.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS);
      this.hasSkyLight = level.dimensionType().hasSkyLight();
      this.blockLight = new DataLayer[sections.length];
      this.skyLight = new DataLayer[sections.length];
      LevelLightEngine light = level.getLightEngine();

      for (int i = 0; i < sections.length; i++) {
         LevelChunkSection section = sections[i];
         if (section != null && !section.hasOnlyAir()) {
            PalettedContainer<BlockState> states = section.getStates();
            BlockState single = Sections.singleValue(states);
            if (single != null) {
               this.uniform[i] = single;
            } else {
               this.states[i] = states.copy();
            }
         }

         SectionPos pos = SectionPos.of(this.chunkX, this.minSectionY + i, this.chunkZ);
         DataLayer block = light.getLayerListener(LightLayer.BLOCK).getDataLayerData(pos);
         DataLayer sky = light.getLayerListener(LightLayer.SKY).getDataLayerData(pos);
         this.blockLight[i] = block == null ? null : block.copy();
         this.skyLight[i] = sky == null ? null : sky.copy();
      }

      int top = Integer.MIN_VALUE;
      int floor = Integer.MAX_VALUE;

      for (int z = 0; z < 16; z++) {
         for (int x = 0; x < 16; x++) {
            int surface = chunk.getHeight(Types.WORLD_SURFACE, x, z);
            int solid = chunk.getHeight(Types.MOTION_BLOCKING, x, z);
            top = Math.max(top, surface);
            floor = Math.min(floor, solid);
            this.surface[z << 4 | x] = surface;
         }
      }

      this.maxY = top <= chunk.getMinBuildHeight() ? Integer.MIN_VALUE : top;
      this.floorY = floorY(floor, chunk.getMinBuildHeight());
      this.copycats = Copycats.INSTALLED ? copycats(chunk, sections.length) : null;
   }

   private ChunkSnapshot(ChunkAccess chunk, ClientLevel level, boolean full) {
      this.facing = Facing.NORTH;
      this.base = null;
      this.views = new ChunkSnapshot[4];
      this.chunkX = chunk.getPos().x;
      this.chunkZ = chunk.getPos().z;
      this.minSectionY = chunk.getMinSection();
      LevelChunkSection[] sections = chunk.getSections();
      this.states = new PalettedContainer[sections.length];
      this.uniform = new BlockState[sections.length];

      for (int i = 0; full && i < sections.length; i++) {
         LevelChunkSection section = sections[i];
         if (section != null && !section.hasOnlyAir()) {
            PalettedContainer<BlockState> states = section.getStates();
            BlockState single = Sections.singleValue(states);
            if (single != null) {
               this.uniform[i] = single;
            } else {
               this.states[i] = states.copy();
            }
         }
      }

      this.biomes = new PalettedContainerRO[9][];
      this.biomeZoomSeed = 0L;
      this.blendRadius = 0;
      this.fallbackBiome = level.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS);
      this.hasSkyLight = false;
      this.blockLight = new DataLayer[sections.length];
      this.skyLight = new DataLayer[sections.length];
      int top = Integer.MIN_VALUE;

      for (int z = 0; full && z < 16; z++) {
         for (int x = 0; x < 16; x++) {
            int surface = chunk.getHeight(Types.WORLD_SURFACE, x, z);
            top = Math.max(top, surface);
            this.surface[z << 4 | x] = surface;
         }
      }

      this.maxY = top <= chunk.getMinBuildHeight() ? Integer.MIN_VALUE : top;
      this.floorY = chunk.getMinBuildHeight();
      this.copycats = null;
   }

   public static ChunkSnapshot fromServer(ChunkAccess chunk, ClientLevel level) {
      return new ChunkSnapshot(chunk, level, chunk.getPersistedStatus().isOrAfter(ChunkStatus.FULL));
   }

   private ChunkSnapshot(SavedChunk chunk, @Nullable SavedChunk[] around, Level level, int blendRadius) {
      this.facing = Facing.NORTH;
      this.base = null;
      this.views = new ChunkSnapshot[4];
      this.chunkX = chunk.chunkX();
      this.chunkZ = chunk.chunkZ();
      this.minSectionY = level.getMinSection();
      int count = chunk.states().length;
      this.states = new PalettedContainer[count];
      this.uniform = new BlockState[count];

      for (int i = 0; i < count; i++) {
         PalettedContainer<BlockState> states = chunk.states()[i];
         if (states != null) {
            BlockState single = Sections.singleValue(states);
            if (single == null) {
               this.states[i] = states;
            } else if (!single.isAir()) {
               this.uniform[i] = single;
            }
         }
      }

      this.biomes = new PalettedContainerRO[9][];

      for (int i = 0; i < 9; i++) {
         if (around[i] != null) {
            this.biomes[i] = around[i].biomes();
         }
      }

      this.biomeZoomSeed = ((BiomeManagerAccessor)level.getBiomeManager()).diorama$biomeZoomSeed();
      this.blendRadius = blendRadius;
      this.fallbackBiome = level.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS);
      this.hasSkyLight = level.dimensionType().hasSkyLight();
      this.blockLight = chunk.blockLight();
      this.skyLight = chunk.skyLight();
      int top = Integer.MIN_VALUE;
      int floor = Integer.MAX_VALUE;

      for (int i = 0; i < 256; i++) {
         top = Math.max(top, chunk.surface()[i]);
         floor = Math.min(floor, chunk.solid()[i]);
         this.surface[i] = chunk.surface()[i];
      }

      this.maxY = top <= level.getMinBuildHeight() ? Integer.MIN_VALUE : top;
      this.floorY = floorY(floor, level.getMinBuildHeight());
      this.copycats = null;
   }

   public static ChunkSnapshot fromSave(SavedChunk chunk, @Nullable SavedChunk[] around, Level level) {
      return new ChunkSnapshot(chunk, around, level, (Integer)Minecraft.getInstance().options.biomeBlendRadius().get());
   }

   public static ChunkSnapshot fromSave(SavedChunk chunk, @Nullable SavedChunk[] around, Level level, int blendRadius) {
      return new ChunkSnapshot(chunk, around, level, blendRadius);
   }

   private static int floorY(int solid, int minY) {
      return solid != Integer.MAX_VALUE && solid <= 320 ? solid - 24 : minY;
   }

   private static ChunkSnapshot.@Nullable SectionCopycats @Nullable [] copycats(LevelChunk chunk, int sectionCount) {
      List<Integer>[] at = null;
      List<BlockState>[] materials = null;

      for (BlockEntity entity : chunk.getBlockEntities().values()) {
         BlockState material = Copycats.material(entity);
         if (material != null) {
            BlockPos pos = entity.getBlockPos();
            int section = (pos.getY() >> 4) - chunk.getMinSection();
            if (section >= 0 && section < sectionCount) {
               if (at == null) {
                  at = new List[sectionCount];
                  materials = new List[sectionCount];
               }

               if (at[section] == null) {
                  at[section] = new ArrayList<>();
                  materials[section] = new ArrayList<>();
               }

               at[section].add((pos.getY() & 15) << 8 | (pos.getZ() & 15) << 4 | pos.getX() & 15);
               materials[section].add(material);
            }
         }
      }

      if (at == null) {
         return null;
      }

      ChunkSnapshot.SectionCopycats[] out = new ChunkSnapshot.SectionCopycats[sectionCount];

      for (int i = 0; i < sectionCount; i++) {
         if (at[i] != null) {
            out[i] = new ChunkSnapshot.SectionCopycats(at[i].stream().mapToInt(Integer::intValue).toArray(), materials[i].toArray(new BlockState[0]));
         }
      }

      return out;
   }

   public ChunkSnapshot.@Nullable SectionCopycats copycats(int index) {
      return this.copycats == null ? null : this.copycats[index];
   }

   public @Nullable BlockState copycatMaterial(int x, int y, int z) {
      int section = (y >> 4) - this.minSectionY;
      ChunkSnapshot.SectionCopycats in = this.copycats != null && section >= 0 && section < this.copycats.length ? this.copycats[section] : null;
      if (in != null) {
         int at = (y & 15) << 8 | (z & 15) << 4 | x & 15;

         for (int i = 0; i < in.at.length; i++) {
            if (in.at[i] == at) {
               return in.material[i];
            }
         }
      }

      return null;
   }

   public static ChunkSnapshot take(LevelChunk chunk, ClientLevel level) {
      return new ChunkSnapshot(chunk, level);
   }

   public int sectionCount() {
      return this.states.length;
   }

   public @Nullable PalettedContainer<BlockState> section(int index) {
      return this.states[index];
   }

   public @Nullable BlockState uniformSection(int index) {
      return this.uniform[index];
   }

   public ChunkSnapshot.@Nullable Packed packed(int index) {
      PalettedContainer<BlockState> states = this.states[index];
      return states == null ? null : Sections.packed(states);
   }

   public int @Nullable [] columns() {
      return this.facing == Facing.NORTH ? null : COLUMNS[this.facing.turns];
   }

   public int blockLight(int sectionIndex, int x, int y, int z) {
      DataLayer layer = this.blockLight[sectionIndex];
      return layer == null ? 0 : layer.get(this.localX(x, z), y, this.localZ(x, z));
   }

   public int skyLight(int sectionIndex, int x, int y, int z) {
      if (!this.hasSkyLight) {
         return 0;
      }

      int turnedX = x;
      x = this.localX(turnedX, z);
      z = this.localZ(turnedX, z);
      DataLayer layer = this.skyLight[sectionIndex];
      if (layer != null) {
         return layer.get(x, y, z);
      }

      for (int i = sectionIndex + 1; i < this.skyLight.length; i++) {
         if (this.skyLight[i] != null) {
            return this.skyLight[i].get(x, 0, z);
         }
      }

      return 15;
   }

   public Holder<Biome> noiseBiome(int quartX, int quartY, int quartZ) {
      Holder<Biome> biome = this.knownNoiseBiome(quartX, quartY, quartZ);
      return biome == null ? this.fallbackBiome : biome;
   }

   public @Nullable Holder<Biome> knownNoiseBiome(int quartX, int quartY, int quartZ) {
      if (this.base != null) {
         return this.base.knownNoiseBiome(this.facing.worldBlockX(quartX, quartZ), quartY, this.facing.worldBlockZ(quartX, quartZ));
      }

      int dx = (quartX >> 2) - this.chunkX;
      int dz = (quartZ >> 2) - this.chunkZ;
      PalettedContainerRO<Holder<Biome>>[] sections = dx >= -1 && dx <= 1 && dz >= -1 && dz <= 1 ? this.biomes[(dx + 1) * 3 + dz + 1] : null;
      if (sections == null) {
         return null;
      }

      int minQuart = this.minSectionY << 2;
      int y = Mth.clamp(quartY, minQuart, minQuart + (sections.length << 2) - 1);
      PalettedContainerRO<Holder<Biome>> section = sections[(y >> 2) - this.minSectionY];
      return section == null ? null : (Holder)section.get(quartX & 3, y & 3, quartZ & 3);
   }

   public boolean hasBiomesAround(int dx, int dz) {
      return this.base != null
         ? this.base.hasBiomesAround((int)this.facing.worldX(dx, dz), (int)this.facing.worldZ(dx, dz))
         : this.biomes[(dx + 1) * 3 + dz + 1] != null;
   }

   public int topBelow(int ceiling) {
      if (this.maxY < ceiling) {
         return this.maxY == Integer.MIN_VALUE ? Integer.MIN_VALUE : this.maxY + 1;
      }

      int top = Integer.MIN_VALUE;

      for (int y : this.surface) {
         if (y < ceiling) {
            top = Math.max(top, y + 1);
         }
      }

      return top;
   }

   public int firstSectionFrom(int y) {
      for (int i = Math.max(0, (y >> 4) - this.minSectionY); i < this.states.length; i++) {
         if (this.states[i] != null || this.uniform[i] != null) {
            return this.minSectionY + i << 4;
         }
      }

      return y;
   }

   public int lastSectionTopBelow(int y) {
      for (int i = Math.min(this.states.length, (y >> 4) - this.minSectionY) - 1; i >= 0; i--) {
         if (this.states[i] != null || this.uniform[i] != null) {
            return this.minSectionY + i + 1 << 4;
         }
      }

      return Integer.MIN_VALUE;
   }

   public int surface(int x, int z) {
      return this.surface[(z & 15) << 4 | x & 15];
   }

   public ChunkSnapshot.@Nullable Tints tints() {
      if (this.tints == null && this.base != null && this.base.tints != null) {
         ChunkSnapshot.Tints from = this.base.tints;
         this.tints = new ChunkSnapshot.Tints(
            this.turnedColumns(from.grass()), this.turnedColumns(from.foliage()), this.turnedColumns(from.dryFoliage()), this.turnedColumns(from.water())
         );
      }

      return this.tints;
   }

   private int[] turnedColumns(int[] columns) {
      int[] out = new int[256];

      for (int x = 0; x < 16; x++) {
         for (int z = 0; z < 16; z++) {
            out[z << 4 | x] = columns[this.localZ(x, z) << 4 | this.localX(x, z)];
         }
      }

      return out;
   }

   public void setTints(ChunkSnapshot.Tints tints) {
      this.tints = tints;
   }

   public long key() {
      return key(this.chunkX, this.chunkZ);
   }

   public static long key(int chunkX, int chunkZ) {
      return (long)chunkX << 32 | chunkZ & 4294967295L;
   }

   static {
      for (Facing facing : Facing.values()) {
         int[] columns = new int[256];

         for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
               columns[z << 4 | x] = (facing.worldBlockZ(x, z) & 15) << 4 | facing.worldBlockX(x, z) & 15;
            }
         }

         COLUMNS[facing.turns] = columns;
      }
   }

   public record Packed(BitStorage storage, Palette<BlockState> palette) {
   }

   public record SectionCopycats(int[] at, BlockState[] material) {
   }

   public record Tints(int[] grass, int[] foliage, int[] dryFoliage, int[] water) {
   }
}
