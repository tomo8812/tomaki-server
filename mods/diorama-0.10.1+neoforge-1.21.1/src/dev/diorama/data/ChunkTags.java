package dev.diorama.data;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.function.Predicate;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Mth;
import net.minecraft.util.SimpleBitStorage;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.minecraft.world.level.chunk.PalettedContainer.Strategy;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import org.jspecify.annotations.Nullable;

public final class ChunkTags {
   private final Level level;
   private final boolean withLight;
   private final Codec<PalettedContainer<BlockState>> blockStates;
   private final Codec<PalettedContainerRO<Holder<Biome>>> biomes;
   private final int heightBits;

   public ChunkTags(Level level, boolean withLight) {
      this.level = level;
      this.withLight = withLight;
      this.blockStates = PalettedContainer.codecRW(Block.BLOCK_STATE_REGISTRY, BlockState.CODEC, Strategy.SECTION_STATES, Blocks.AIR.defaultBlockState());
      Registry<Biome> registry = level.registryAccess().registryOrThrow(Registries.BIOME);
      Holder<Biome> plains = registry.getHolderOrThrow(Biomes.PLAINS);
      this.biomes = PalettedContainer.codecRO(registry.asHolderIdMap(), registry.holderByNameCodec(), Strategy.SECTION_BIOMES, plains);
      this.heightBits = Mth.ceillog2(level.getHeight() + 1);
   }

   public Codec<PalettedContainer<BlockState>> blockStates() {
      return this.blockStates;
   }

   public SavedChunk read(int chunkX, int chunkZ, CompoundTag tag) {
      boolean finished = finished(tag);
      int count = this.level.getSectionsCount();
      int minSection = this.level.getMinSection();
      PalettedContainer<BlockState>[] states = new PalettedContainer[count];
      PalettedContainerRO<Holder<Biome>>[] biomes = new PalettedContainerRO[count];
      DataLayer[] blockLight = new DataLayer[count];
      DataLayer[] skyLight = new DataLayer[count];
      ListTag sections = Nbt.list(tag, "sections");

      for (int s = 0; s < sections.size(); s++) {
         CompoundTag section = Nbt.compound(sections, s);
         int i = Nbt.sectionY(section) - minSection;
         if (i >= 0 && i < count) {
            if (finished) {
               states[i] = this.blockStates(section);
            }

            biomes[i] = (PalettedContainerRO<Holder<Biome>>)this.biomes
               .parse(NbtOps.INSTANCE, Nbt.compound(section, "biomes"))
               .resultOrPartial(e -> {})
               .orElse(null);
            if (finished) {
               blockLight[i] = light(section, "BlockLight");
               if (this.withLight) {
                  skyLight[i] = light(section, "SkyLight");
               }
            }
         }
      }

      int[] surface = finished ? this.surface(tag) : null;
      int[] solid = finished ? this.solid(tag) : null;
      if (finished && surface == null && solid == null && !Nbt.compound(tag, "Heightmaps").contains(Types.WORLD_SURFACE.getSerializationKey())) {
         surface = this.heights(states, minSection, Types.WORLD_SURFACE);
         solid = this.heights(states, minSection, Types.MOTION_BLOCKING);
      }

      finished &= surface != null && solid != null;
      return new SavedChunk(chunkX, chunkZ, finished, states, biomes, blockLight, skyLight, finished ? surface : new int[0], finished ? solid : new int[0]);
   }

   public @Nullable PalettedContainer<BlockState> blockStates(CompoundTag section) {
      return (PalettedContainer<BlockState>)this.blockStates
         .parse(NbtOps.INSTANCE, Nbt.compound(section, "block_states"))
         .resultOrPartial(e -> {})
         .orElse(null);
   }

   public int @Nullable [] surface(CompoundTag tag) {
      return this.heights(Nbt.longs(Nbt.compound(tag, "Heightmaps"), Types.WORLD_SURFACE.getSerializationKey()));
   }

   public int @Nullable [] solid(CompoundTag tag) {
      return this.heights(Nbt.longs(Nbt.compound(tag, "Heightmaps"), Types.MOTION_BLOCKING.getSerializationKey()));
   }

   private int @Nullable [] heights(long @Nullable [] packed) {
      if (packed == null) {
         return null;
      }

      try {
         SimpleBitStorage storage = new SimpleBitStorage(this.heightBits, 256, packed);
         int[] heights = new int[256];
         int minY = this.level.getMinBuildHeight();

         for (int i = 0; i < 256; i++) {
            heights[i] = storage.get(i) + minY - 1;
         }

         return heights;
      } catch (RuntimeException e) {
         return null;
      }
   }

   private int[] heights(PalettedContainer<BlockState>[] states, int minSection, Types type) {
      Predicate<BlockState> counts = type.isOpaque();
      int lowest = Integer.MAX_VALUE;

      for (int i = 0; i < states.length; i++) {
         if (states[i] != null) {
            lowest = Math.min(lowest, (i + minSection) * 16 - 1);
         }
      }

      int[] heights = new int[256];
      Arrays.fill(heights, lowest == Integer.MAX_VALUE ? this.level.getMinBuildHeight() - 1 : lowest);

      label48:
      for (int column = 0; column < 256; column++) {
         int x = column & 15;
         int z = column >> 4;

         for (int i = states.length - 1; i >= 0; i--) {
            if (states[i] != null) {
               for (int y = 15; y >= 0; y--) {
                  if (counts.test((BlockState)states[i].get(x, y, z))) {
                     heights[column] = (i + minSection) * 16 + y;
                     continue label48;
                  }
               }
            }
         }
      }

      return heights;
   }

   public static ChunkTags.Summary summaryOf(int[] surface, int[] solid) {
      int top = Integer.MIN_VALUE;
      int floor = Integer.MAX_VALUE;

      for (int i = 0; i < 256; i++) {
         top = Math.max(top, surface[i]);
         floor = Math.min(floor, solid[i]);
      }

      return new ChunkTags.Summary(true, top, floor - 24);
   }

   public static boolean finished(CompoundTag tag) {
      String status = Nbt.string(tag, "Status");
      if (!"empty".equals(status) && !"minecraft:empty".equals(status)) {
         return status.equals("full") || status.equals("minecraft:full");
      }

      status = Nbt.string(Nbt.compound(tag, "below_zero_retrogen"), "target_status");
      return status.endsWith("heightmaps")
         || status.endsWith("initialize_light")
         || status.endsWith("light")
         || status.endsWith("spawn")
         || status.endsWith("full");
   }

   private static @Nullable DataLayer light(CompoundTag section, String name) {
      byte[] bytes = Nbt.bytes(section, name);
      return bytes != null && bytes.length == 2048 ? new DataLayer(bytes) : null;
   }

   public static int dataVersion() {
      return SharedConstants.getCurrentVersion().getDataVersion().getVersion();
   }

   public record Summary(boolean finished, int maxY, int floorY) {
      public static final ChunkTags.Summary UNFINISHED = new ChunkTags.Summary(false, Integer.MIN_VALUE, Integer.MIN_VALUE);
   }
}
