package dev.diorama.share;

import dev.diorama.data.ChunkTags;
import dev.diorama.data.Nbt;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.jspecify.annotations.Nullable;

final class ChunkPacker {
   private static final int ORDINARY_TOP = 320;
   private static final int CACHED_SECTIONS = 8192;
   private static final long[] OPEN = new long[64];
   private final Level level;
   private final ChunkTags tags;
   private final BlockState hidden;
   private final BiFunction<Integer, Integer, @Nullable CompoundTag> neighbours;
   private final Map<BlockState, Boolean> opaque = new IdentityHashMap<>();
   private final Map<Long, long[]> sections = new LinkedHashMap<Long, long[]>(8192, 0.75F, true) {
      @Override
      protected boolean removeEldestEntry(Entry<Long, long[]> eldest) {
         return this.size() > 8192;
      }
   };
   private final Map<Long, Map<Integer, CompoundTag>> neighbourTags = new LinkedHashMap<Long, Map<Integer, CompoundTag>>(64, 0.75F, true) {
      @Override
      protected boolean removeEldestEntry(Entry<Long, Map<Integer, CompoundTag>> eldest) {
         return this.size() > 32;
      }
   };
   private final Map<Long, Boolean> missing = new LinkedHashMap<Long, Boolean>(1024, 0.75F, true) {
      @Override
      protected boolean removeEldestEntry(Entry<Long, Boolean> eldest) {
         return this.size() > 4096;
      }
   };
   long hiddenBlocks;
   private long keptSince = System.currentTimeMillis();
   private static final long KEEP_MS = 30000L;

   ChunkPacker(Level level, ChunkTags tags, BiFunction<Integer, Integer, @Nullable CompoundTag> neighbours) {
      this.level = level;
      this.tags = tags;
      this.neighbours = neighbours;
      this.hidden = level.dimension() == Level.END ? Blocks.END_STONE.defaultBlockState() : Blocks.STONE.defaultBlockState();
   }

   void forgetIfOld() {
      long now = System.currentTimeMillis();
      if (now - this.keptSince > 30000L) {
         this.sections.clear();
         this.neighbourTags.clear();
         this.missing.clear();
         this.keptSince = now;
      }
   }

   byte @Nullable [] pack(int chunkX, int chunkZ, CompoundTag tag) {
      if (!ChunkTags.finished(tag)) {
         return null;
      }

      int[] surface = this.tags.surface(tag);
      int[] solid = this.tags.solid(tag);
      if (surface != null && solid != null) {
         ChunkTags.Summary summary = ChunkTags.summaryOf(surface, solid);
         if (summary.maxY() <= this.level.getMinBuildHeight()) {
            return null;
         }

         int minSolid = summary.floorY() + 24;
         int floorY = minSolid > 320 ? this.level.getMinBuildHeight() : summary.floorY();
         int minSection = this.level.getMinSection();
         int maxSection = minSection + this.level.getSectionsCount() - 1;
         int lo = Math.max(minSection, Math.floorDiv(floorY, 16));
         int hi = Math.min(maxSection, Math.floorDiv(summary.maxY(), 16));
         Map<Integer, CompoundTag> byY = sectionsByY(tag);
         Map<Integer, PalettedContainer<BlockState>> states = new HashMap<>();
         Map<Integer, long[]> opacity = new HashMap<>();

         for (int y = lo - 1; y <= hi + 1; y++) {
            CompoundTag section = byY.get(y);
            PalettedContainer<BlockState> blocks = section == null ? null : this.tags.blockStates(section);
            if (blocks != null) {
               states.put(y, blocks);
            }

            long[] bits = blocks == null ? OPEN : this.opacity(blocks);
            opacity.put(y, bits);
            if (y >= lo && y <= hi) {
               this.sections.put(sectionKey(chunkX, chunkZ, y), bits);
            }
         }

         for (int y = lo; y <= hi; y++) {
            PalettedContainer<BlockState> blocks = states.get(y);
            if (blocks != null) {
               this.hide(chunkX, chunkZ, y, blocks, opacity);
            }
         }

         CompoundTag out = new CompoundTag();
         out.putInt("DataVersion", ChunkTags.dataVersion());
         out.putString("Status", "minecraft:full");
         out.putInt("xPos", chunkX);
         out.putInt("zPos", chunkZ);
         ListTag sections = new ListTag();

         for (Entry<Integer, CompoundTag> found : new TreeMap<>(byY).entrySet()) {
            int y = found.getKey();
            CompoundTag section = found.getValue();
            CompoundTag kept = new CompoundTag();
            kept.putByte("Y", (byte)y);
            CompoundTag biomes = Nbt.compound(section, "biomes");
            if (!biomes.isEmpty()) {
               kept.put("biomes", biomes);
            }

            if (y >= lo && y <= hi) {
               PalettedContainer<BlockState> blocks = states.get(y);
               if (blocks != null) {
                  Tag encoded = (Tag)this.tags.blockStates().encodeStart(NbtOps.INSTANCE, blocks).result().orElse(null);
                  if (encoded != null) {
                     kept.put("block_states", encoded);
                  }
               }

               byte[] light = Nbt.bytes(section, "BlockLight");
               if (light != null) {
                  kept.putByteArray("BlockLight", light);
               }
            }

            sections.add(kept);
         }

         out.put("sections", sections);
         ByteArrayOutputStream bytes = new ByteArrayOutputStream(2048);
         Deflater deflater = new Deflater(6);

         try (DataOutputStream data = new DataOutputStream(new DeflaterOutputStream(bytes, deflater))) {
            NbtIo.write(out, data);
         } catch (IOException e) {
            throw new UncheckedIOException(e);
         } finally {
            deflater.end();
         }

         return bytes.toByteArray();
      } else {
         return null;
      }
   }

   private void hide(int chunkX, int chunkZ, int sectionY, PalettedContainer<BlockState> blocks, Map<Integer, long[]> opacity) {
      long[] self = opacity.get(sectionY);
      long[] below = opacity.get(sectionY - 1);
      long[] above = opacity.get(sectionY + 1);
      long[] west = null;
      long[] east = null;
      long[] north = null;
      long[] south = null;

      for (int i = 0; i < 4096; i++) {
         int x = i & 15;
         int z = i >> 4 & 15;
         int y = i >> 8;
         if ((y == 0 ? bit(below, i | 3840) : bit(self, i - 256)) && (y == 15 ? bit(above, i & 0xFF) : bit(self, i + 256))) {
            if (x == 0) {
               west = west != null ? west : this.neighbour(chunkX - 1, chunkZ, sectionY);
               if (!bit(west, i | 15)) {
                  continue;
               }
            } else if (!bit(self, i - 1)) {
               continue;
            }

            if (x == 15) {
               east = east != null ? east : this.neighbour(chunkX + 1, chunkZ, sectionY);
               if (!bit(east, i & -16)) {
                  continue;
               }
            } else if (!bit(self, i + 1)) {
               continue;
            }

            if (z == 0) {
               north = north != null ? north : this.neighbour(chunkX, chunkZ - 1, sectionY);
               if (!bit(north, i | 240)) {
                  continue;
               }
            } else if (!bit(self, i - 16)) {
               continue;
            }

            if (z == 15) {
               south = south != null ? south : this.neighbour(chunkX, chunkZ + 1, sectionY);
               if (!bit(south, i & -241)) {
                  continue;
               }
            } else if (!bit(self, i + 16)) {
               continue;
            }

            if (blocks.get(x, y, z) != this.hidden) {
               blocks.getAndSetUnchecked(x, y, z, this.hidden);
               this.hiddenBlocks++;
            }
         }
      }
   }

   private static boolean bit(long[] bits, int i) {
      return (bits[i >> 6] & 1L << (i & 63)) != 0L;
   }

   private long[] neighbour(int chunkX, int chunkZ, int sectionY) {
      long key = sectionKey(chunkX, chunkZ, sectionY);
      long[] cached = this.sections.get(key);
      if (cached != null) {
         return cached;
      }

      long chunk = sectionKey(chunkX, chunkZ, 0);
      Map<Integer, CompoundTag> byY = this.neighbourTags.get(chunk);
      if (byY == null) {
         if (this.missing.containsKey(chunk)) {
            return OPEN;
         }

         CompoundTag tag = this.neighbours.apply(chunkX, chunkZ);
         if (tag == null || !ChunkTags.finished(tag)) {
            this.missing.put(chunk, Boolean.TRUE);
            return OPEN;
         }

         byY = sectionsByY(tag);
         this.neighbourTags.put(chunk, byY);
      }

      CompoundTag section = byY.get(sectionY);
      PalettedContainer<BlockState> blocks = section == null ? null : this.tags.blockStates(section);
      long[] bits = blocks == null ? OPEN : this.opacity(blocks);
      this.sections.put(key, bits);
      return bits;
   }

   private long[] opacity(PalettedContainer<BlockState> blocks) {
      long[] bits = new long[64];

      for (int i = 0; i < 4096; i++) {
         if (this.opaque((BlockState)blocks.get(i & 15, i >> 8, i >> 4 & 15))) {
            bits[i >> 6] = bits[i >> 6] | 1L << (i & 63);
         }
      }

      return bits;
   }

   private boolean opaque(BlockState state) {
      Boolean known = this.opaque.get(state);
      if (known == null) {
         known = state.isSolidRender(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
         this.opaque.put(state, known);
      }

      return known;
   }

   private static Map<Integer, CompoundTag> sectionsByY(CompoundTag tag) {
      Map<Integer, CompoundTag> byY = new HashMap<>();
      ListTag sections = Nbt.list(tag, "sections");

      for (int s = 0; s < sections.size(); s++) {
         CompoundTag section = Nbt.compound(sections, s);
         byY.put(Nbt.sectionY(section), section);
      }

      return byY;
   }

   private static long sectionKey(int chunkX, int chunkZ, int sectionY) {
      return (long)(chunkX & 4194303) << 30 | (long)(chunkZ & 4194303) << 8 | sectionY & 0xFF;
   }
}
