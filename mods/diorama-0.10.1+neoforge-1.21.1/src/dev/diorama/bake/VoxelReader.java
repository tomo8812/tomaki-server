package dev.diorama.bake;

import dev.diorama.Diorama;
import dev.diorama.data.ChunkSnapshot;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.util.BitStorage;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.jspecify.annotations.Nullable;

public final class VoxelReader {
   public static final int UNKNOWN = -1;
   static final int AIR_ID = Block.BLOCK_STATE_REGISTRY.getId(Blocks.AIR.defaultBlockState());
   private static final int[] AIR_IDS = new int[4096];
   private static final VoxelReader.Section AIR_SECTION = new VoxelReader.Section(AIR_ID, null, null, null, null);
   private static final VoxelReader.Section BY_BLOCK = new VoxelReader.Section(-1, null, null, null, null);
   private static final long[] NOTHING_SOLID = new long[64];
   private static final long[] NONE_OF_ONE = new long[64];
   private static final long[] ALL_SOLID = new long[64];
   private static final int TABLE_BITS = 5;
   private static final int TABLE_MASK = 31;
   private final ChunkSource source;
   private final @Nullable Looks looks;
   private final byte @Nullable [] kinds;
   private final int @Nullable [] shapes;
   private final int minY;
   private final int maxY;
   private final Long2ObjectOpenHashMap<VoxelReader.@Nullable Decoded> cache = new Long2ObjectOpenHashMap();
   private final int[] tableX = new int[1024];
   private final int[] tableZ = new int[1024];
   private final VoxelReader.@Nullable Decoded[] tableChunk = new VoxelReader.Decoded[1024];
   private int sectionX = Integer.MIN_VALUE;
   private int sectionY = Integer.MIN_VALUE;
   private int sectionZ = Integer.MIN_VALUE;
   private int @Nullable [] sectionIds;
   private int solidX = Integer.MIN_VALUE;
   private int solidY = Integer.MIN_VALUE;
   private int solidZ = Integer.MIN_VALUE;
   private long @Nullable [] solidMask;
   private long @Nullable [] partialMask;
   private final int[] unpacked = new int[4096];
   static final boolean DEBUG = Diorama.DEBUG;
   final Set<Long> missing = DEBUG ? new HashSet<>() : null;
   static final int CEILING_UNDERSIDE = 2;
   private static final long WEST_EDGE = 281479271743489L;

   VoxelReader(ChunkSource source) {
      this(source, null);
   }

   public VoxelReader(ChunkSource source, @Nullable Looks looks) {
      this(source, looks, false);
   }

   public VoxelReader(ChunkSource source, @Nullable Looks looks, boolean shapes) {
      this.source = source;
      this.looks = looks;
      this.kinds = looks == null ? null : looks.kind;
      this.shapes = looks != null && shapes ? looks.shape : null;
      this.minY = source.minY();
      this.maxY = source.maxY();
      Arrays.fill(this.tableX, Integer.MIN_VALUE);
   }

   public int state(int x, int y, int z) {
      if (y >= this.minY && y < this.maxY) {
         int sx = x >> 4;
         int sy = y >> 4;
         int sz = z >> 4;
         if (sx != this.sectionX || sy != this.sectionY || sz != this.sectionZ) {
            this.enterSection(sx, sy, sz);
         }

         int[] ids = this.sectionIds;
         return ids == null ? -1 : ids[(y & 15) << 8 | (z & 15) << 4 | x & 15];
      } else {
         return AIR_ID;
      }
   }

   private void enterSection(int sx, int sy, int sz) {
      VoxelReader.Decoded chunk = this.chunk(sx, sz);
      if (chunk == null) {
         if (DEBUG) {
            this.missing.add(ChunkSnapshot.key(sx, sz));
         }

         this.sectionIds = null;
      } else {
         this.sectionIds = chunk.ids(sy - chunk.snapshot.minSectionY, this.unpacked);
      }

      this.sectionX = sx;
      this.sectionY = sy;
      this.sectionZ = sz;
   }

   public int solid(int x, int y, int z) {
      if (y >= this.minY && y < this.maxY) {
         int sx = x >> 4;
         int sy = y >> 4;
         int sz = z >> 4;
         if (sx != this.solidX || sy != this.solidY || sz != this.solidZ) {
            this.enterSolidSection(sx, sy, sz);
         }

         long[] mask = this.solidMask;
         if (mask == null) {
            return -1;
         }

         int i = (y & 15) << 8 | (z & 15) << 4 | x & 15;
         return (mask[i >> 6] >>> i & 1L) == 0L ? 0 : 1 + ((int)(this.partialMask[i >> 6] >>> i) & 1);
      } else {
         return 0;
      }
   }

   boolean pocket(int x, int y, int z) {
      return this.solid(x, y + 1, z) > 0 && this.solid(x, y - 1, z) > 0;
   }

   int lid(int x, int z, int startY, int limitY) {
      int y = startY - 1;

      while (y >= limitY && (this.solid(x, y, z) > 0 || y + 1 < startY && this.pocket(x, y, z))) {
         y--;
      }

      return y + 1;
   }

   boolean ceilingUnderside(int x, int y, int z, int lid, int limitY) {
      return y >= lid && y < lid + 2 && lid > limitY && this.solid(x, lid - 1, z) == 0;
   }

   private void enterSolidSection(int sx, int sy, int sz) {
      VoxelReader.Decoded chunk = this.chunk(sx, sz);
      if (chunk == null) {
         if (DEBUG) {
            this.missing.add(ChunkSnapshot.key(sx, sz));
         }

         this.solidMask = null;
      } else {
         int index = sy - chunk.snapshot.minSectionY;
         this.solidMask = chunk.solid(index, this.kinds, this.shapes, this.unpacked);
         this.partialMask = chunk.partial(index);
      }

      this.solidX = sx;
      this.solidY = sy;
      this.solidZ = sz;
   }

   long @Nullable [] solidSection(int sx, int sy, int sz) {
      VoxelReader.Decoded chunk = this.chunk(sx, sz);
      return chunk == null ? null : chunk.solid(sy - chunk.snapshot.minSectionY, this.kinds, this.shapes, this.unpacked);
   }

   long @Nullable [] partialSection(int sx, int sy, int sz) {
      VoxelReader.Decoded chunk = this.chunk(sx, sz);
      if (chunk == null) {
         return null;
      }

      int index = sy - chunk.snapshot.minSectionY;
      chunk.solid(index, this.kinds, this.shapes, this.unpacked);
      return chunk.partial(index);
   }

   public long @Nullable [] casterSection(int sx, int sy, int sz) {
      VoxelReader.Decoded chunk = this.chunk(sx, sz);
      if (chunk == null) {
         return null;
      }

      int index = sy - chunk.snapshot.minSectionY;
      if (index >= 0 && index < chunk.solid.length) {
         long[] out = chunk.casters[index];
         if (out == null) {
            long[] solid = chunk.solid(index, this.kinds, this.shapes, this.unpacked);
            long[] partial = chunk.partial(index);
            int upIndex = index + 1 < chunk.solid.length ? index + 1 : -1;
            long[] upSolid = upIndex < 0 ? null : chunk.solid(upIndex, this.kinds, this.shapes, this.unpacked);
            long[] upPartial = upIndex < 0 ? null : chunk.partial(upIndex);
            VoxelReader.Decoded west = this.peek(sx - 1, sz);
            int westIndex = west == null ? -1 : sy - west.snapshot.minSectionY;
            boolean westRead = westIndex >= 0 && westIndex < west.solid.length;
            long[] westSolid = westRead ? west.solid(westIndex, this.kinds, this.shapes, this.unpacked) : null;
            long[] westPartial = westRead ? west.partial(westIndex) : null;
            VoxelReader.Decoded south = this.peek(sx, sz + 1);
            int southIndex = south == null ? -1 : sy - south.snapshot.minSectionY;
            boolean southRead = southIndex >= 0 && southIndex < south.solid.length;
            long[] southSolid = southRead ? south.solid(southIndex, this.kinds, this.shapes, this.unpacked) : null;
            long[] southPartial = southRead ? south.partial(southIndex) : null;
            out = new long[64];

            for (int w = 0; w < 64; w++) {
               long cubes = solid[w] & ~partial[w];
               long above = w < 60 ? solid[w + 4] & ~partial[w + 4] : (upSolid == null ? 0L : upSolid[w - 60] & ~upPartial[w - 60]);
               long westCubes = westSolid == null ? 0L : westSolid[w] & ~westPartial[w];
               long westOf = cubes << 1 & -281479271743490L | westCubes >>> 15 & 281479271743489L;
               long nextRow = (w & 3) < 3 ? solid[w + 1] & ~partial[w + 1] : (southSolid == null ? 0L : southSolid[w - 3] & ~southPartial[w - 3]);
               long southOf = cubes >>> 16 | nextRow << 48;
               out[w] = solid[w] & ~(cubes & above & westOf & southOf);
            }

            chunk.casters[index] = out;
         }

         return out;
      } else {
         return null;
      }
   }

   private VoxelReader.@Nullable Decoded peek(int chunkX, int chunkZ) {
      int slot = (chunkX & 31) << 5 | chunkZ & 31;
      return this.tableX[slot] == chunkX && this.tableZ[slot] == chunkZ
         ? this.tableChunk[slot]
         : (VoxelReader.Decoded)this.cache.get(ChunkSnapshot.key(chunkX, chunkZ));
   }

   static boolean nothingSolid(long[] mask) {
      return mask == NOTHING_SOLID;
   }

   int solidTop(int chunkX0, int chunkZ0, int chunkX1, int chunkZ1) {
      int top = Integer.MIN_VALUE;

      for (int cx = chunkX0; cx <= chunkX1; cx++) {
         for (int cz = chunkZ0; cz <= chunkZ1; cz++) {
            int slot = (cx & 31) << 5 | cz & 31;
            VoxelReader.Decoded chunk;
            if (this.tableX[slot] == cx && this.tableZ[slot] == cz) {
               chunk = this.tableChunk[slot];
            } else {
               long key = ChunkSnapshot.key(cx, cz);
               if (!this.cache.containsKey(key)) {
                  return Integer.MAX_VALUE;
               }

               chunk = (VoxelReader.Decoded)this.cache.get(key);
            }

            if (chunk != null) {
               top = Math.max(top, chunk.solidTop(this.kinds, this.shapes, this.unpacked));
            }
         }
      }

      return top;
   }

   int blockLight(int x, int y, int z) {
      if (y >= this.minY && y < this.maxY) {
         VoxelReader.Decoded chunk = this.chunk(x >> 4, z >> 4);
         return chunk == null ? 0 : chunk.snapshot.blockLight((y >> 4) - chunk.snapshot.minSectionY, x & 15, y & 15, z & 15);
      } else {
         return 0;
      }
   }

   int skyLight(int x, int y, int z) {
      if (y < this.minY) {
         return 0;
      }

      if (y >= this.maxY) {
         return 15;
      }

      VoxelReader.Decoded chunk = this.chunk(x >> 4, z >> 4);
      return chunk == null ? 15 : chunk.snapshot.skyLight((y >> 4) - chunk.snapshot.minSectionY, x & 15, y & 15, z & 15);
   }

   @Nullable ChunkSnapshot snapshot(int x, int z) {
      VoxelReader.Decoded chunk = this.chunk(x >> 4, z >> 4);
      return chunk == null ? null : chunk.snapshot;
   }

   private VoxelReader.@Nullable Decoded chunk(int chunkX, int chunkZ) {
      int slot = (chunkX & 31) << 5 | chunkZ & 31;
      if (this.tableX[slot] == chunkX && this.tableZ[slot] == chunkZ) {
         return this.tableChunk[slot];
      }

      long key = ChunkSnapshot.key(chunkX, chunkZ);
      VoxelReader.Decoded decoded = (VoxelReader.Decoded)this.cache.get(key);
      if (decoded == null && !this.cache.containsKey(key)) {
         ChunkSnapshot snapshot = this.source.chunk(chunkX, chunkZ);
         decoded = snapshot == null ? null : new VoxelReader.Decoded(snapshot, this.looks);
         this.cache.put(key, decoded);
      }

      this.tableX[slot] = chunkX;
      this.tableZ[slot] = chunkZ;
      this.tableChunk[slot] = decoded;
      return decoded;
   }

   static {
      Arrays.fill(ALL_SOLID, -1L);
      Arrays.fill(AIR_IDS, AIR_ID);
   }

   private static final class Decoded {
      final ChunkSnapshot snapshot;
      final @Nullable Looks looks;
      final VoxelReader.@Nullable Section[] sections;
      final int[][] ids;
      final long[][] solid;
      final long[][] partial;
      final long[][] casters;
      int solidTop = Integer.MAX_VALUE;

      Decoded(ChunkSnapshot snapshot, @Nullable Looks looks) {
         this.snapshot = snapshot;
         this.looks = looks;
         this.sections = new VoxelReader.Section[snapshot.sectionCount()];
         this.ids = new int[snapshot.sectionCount()][];
         this.solid = new long[snapshot.sectionCount()][];
         this.partial = new long[snapshot.sectionCount()][];
         this.casters = new long[snapshot.sectionCount()][];
      }

      long[] solid(int index, byte[] kinds, int @Nullable [] shapes, int[] unpacked) {
         if (index >= 0 && index < this.solid.length) {
            long[] mask = this.solid[index];
            if (mask == null) {
               long[] partial = VoxelReader.NOTHING_SOLID;
               VoxelReader.Section section = this.section(index);
               if (section == VoxelReader.AIR_SECTION) {
                  mask = VoxelReader.NOTHING_SOLID;
               } else if (section.single >= 0) {
                  int id = section.single;
                  mask = kinds[id] == 1 ? VoxelReader.ALL_SOLID : VoxelReader.NONE_OF_ONE;
                  if (mask == VoxelReader.ALL_SOLID && shapes != null && shapes[id] != 0) {
                     partial = VoxelReader.ALL_SOLID;
                  }
               } else if (section == VoxelReader.BY_BLOCK && this.ids[index] == null) {
                  PalettedContainer<BlockState> states = this.snapshot.section(index);
                  mask = new long[64];
                  BlockState last = null;
                  int f = 0;

                  for (int i = 0; i < 4096; i++) {
                     BlockState state = this.snapshot.state(states, i & 15, i >> 8, i >> 4 & 15);
                     if (state != last) {
                        last = state;
                        f = solidFlags(this.id(state), kinds, shapes);
                     }

                     mask[i >> 6] = mask[i >> 6] | (long)(f & 1) << i;
                     if (f > 1) {
                        if (partial == VoxelReader.NOTHING_SOLID) {
                           partial = new long[64];
                        }

                        partial[i >> 6] = partial[i >> 6] | 1L << i;
                     }
                  }
               } else {
                  int[] ids = section == VoxelReader.BY_BLOCK ? this.ids[index] : section.ids;
                  int[] columns = section.columns;
                  byte[] flags;
                  if (ids == null) {
                     section.storage.unpack(unpacked);
                     int[] palette = section.palette;
                     flags = new byte[palette.length];

                     for (int p = 0; p < palette.length; p++) {
                        flags[p] = solidFlags(palette[p], kinds, shapes);
                     }
                  } else {
                     columns = null;
                     flags = null;
                  }

                  mask = new long[64];

                  for (int w = 0; w < 64; w++) {
                     long solid = 0L;
                     long smaller = 0L;

                     for (int b = 0; b < 64; b++) {
                        int i = w << 6 | b;
                        int f = flags == null ? solidFlags(ids[i], kinds, shapes) : flags[unpacked[columns == null ? i : i & 3840 | columns[i & 0xFF]]];
                        solid |= (long)(f & 1) << b;
                        smaller |= (long)(f >> 1) << b;
                     }

                     mask[w] = solid;
                     if (smaller != 0L) {
                        if (partial == VoxelReader.NOTHING_SOLID) {
                           partial = new long[64];
                        }

                        partial[w] = smaller;
                     }
                  }
               }

               this.partial[index] = partial;
               this.solid[index] = mask;
            }

            return mask;
         } else {
            return VoxelReader.NOTHING_SOLID;
         }
      }

      private static byte solidFlags(int id, byte[] kinds, int @Nullable [] shapes) {
         if (kinds[id] != 1) {
            return 0;
         } else {
            return (byte)(shapes != null && shapes[id] != 0 ? 3 : 1);
         }
      }

      long[] partial(int index) {
         return index >= 0 && index < this.partial.length && this.partial[index] != null ? this.partial[index] : VoxelReader.NOTHING_SOLID;
      }

      int solidTop(byte[] kinds, int @Nullable [] shapes, int[] unpacked) {
         if (this.solidTop == Integer.MAX_VALUE) {
            int top = Integer.MIN_VALUE;

            for (int index = this.sections.length - 1; index >= 0 && top == Integer.MIN_VALUE; index--) {
               long[] mask = this.solid(index, kinds, shapes, unpacked);

               for (int word = 63; word >= 0; word--) {
                  if (mask[word] != 0L) {
                     int highest = (word << 6) + 63 - Long.numberOfLeadingZeros(mask[word]);
                     top = (this.snapshot.minSectionY + index << 4) + (highest >> 8) + 1;
                     break;
                  }
               }
            }

            this.solidTop = top;
         }

         return this.solidTop;
      }

      int[] ids(int index, int[] unpacked) {
         if (index >= 0 && index < this.ids.length) {
            int[] ids = this.ids[index];
            if (ids == null) {
               VoxelReader.Section section = this.section(index);
               if (section == VoxelReader.AIR_SECTION) {
                  ids = VoxelReader.AIR_IDS;
               } else if (section.ids != null) {
                  ids = section.ids;
               } else if (section == VoxelReader.BY_BLOCK) {
                  ids = this.byBlock(index);
               } else if (section.single >= 0) {
                  ids = new int[4096];
                  Arrays.fill(ids, section.single);
               } else {
                  ids = new int[4096];
                  int[] palette = section.palette;
                  int[] columns = section.columns;
                  if (columns == null) {
                     section.storage.unpack(ids);

                     for (int i = 0; i < 4096; i++) {
                        ids[i] = palette[ids[i]];
                     }
                  } else {
                     section.storage.unpack(unpacked);

                     for (int i = 0; i < 4096; i++) {
                        ids[i] = palette[unpacked[i & 3840 | columns[i & 0xFF]]];
                     }
                  }
               }

               this.ids[index] = ids;
            }

            return ids;
         } else {
            return VoxelReader.AIR_IDS;
         }
      }

      private int id(BlockState state) {
         return this.looks == null ? BlockLooks.bakeId(state) : this.looks.bakeLookId(state);
      }

      VoxelReader.Section section(int index) {
         if (index >= 0 && index < this.sections.length) {
            VoxelReader.Section section = this.sections[index];
            if (section == null) {
               section = this.read(index);
               this.sections[index] = section;
            }

            return section;
         } else {
            return VoxelReader.AIR_SECTION;
         }
      }

      private VoxelReader.Section read(int index) {
         PalettedContainer<BlockState> states = this.snapshot.section(index);
         BlockState uniform = this.snapshot.uniformSection(index);
         if (states == null && uniform == null) {
            return VoxelReader.AIR_SECTION;
         }

         ChunkSnapshot.SectionCopycats copycats = this.looks == null ? null : this.snapshot.copycats(index);
         if (copycats == null) {
            if (uniform != null) {
               return new VoxelReader.Section(this.id(this.snapshot.turned(uniform)), null, null, null, null);
            }

            ChunkSnapshot.Packed packed = this.snapshot.packed(index);
            int[] palette = packed == null ? null : this.paletteIds(packed);
            return palette != null ? new VoxelReader.Section(-1, null, packed.storage(), palette, this.snapshot.columns()) : VoxelReader.BY_BLOCK;
         } else {
            int[] ids;
            if (uniform != null) {
               ids = new int[4096];
               Arrays.fill(ids, this.id(this.snapshot.turned(uniform)));
            } else {
               ids = this.byBlock(index);
            }

            for (int i = 0; i < copycats.at().length; i++) {
               int at = copycats.at()[i];
               ids[at] = this.looks.copycat(ids[at], Block.BLOCK_STATE_REGISTRY.getId(copycats.material()[i]));
            }

            return new VoxelReader.Section(-1, ids, null, null, null);
         }
      }

      private int[] byBlock(int index) {
         PalettedContainer<BlockState> states = this.snapshot.section(index);
         int[] ids = new int[4096];
         BlockState lastState = null;
         int lastId = 0;

         for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
               for (int x = 0; x < 16; x++) {
                  BlockState state = this.snapshot.state(states, x, y, z);
                  if (state != lastState) {
                     lastState = state;
                     lastId = this.id(state);
                  }

                  ids[y << 8 | z << 4 | x] = lastId;
               }
            }
         }

         return ids;
      }

      private int @Nullable [] paletteIds(ChunkSnapshot.Packed packed) {
         int bits = packed.storage().getBits();
         int size = packed.palette().getSize();
         if (bits <= 8 && size <= 1 << bits) {
            int[] ids = new int[1 << bits];
            Arrays.fill(ids, VoxelReader.AIR_ID);

            for (int p = 0; p < size; p++) {
               BlockState state = (BlockState)packed.palette().valueFor(p);
               if (state != null) {
                  ids[p] = this.id(this.snapshot.turned(state));
               }
            }

            return ids;
         } else {
            return null;
         }
      }
   }

   private static final class Section {
      final int single;
      final int @Nullable [] ids;
      final @Nullable BitStorage storage;
      final int @Nullable [] palette;
      final int @Nullable [] columns;

      Section(int single, int @Nullable [] ids, @Nullable BitStorage storage, int @Nullable [] palette, int @Nullable [] columns) {
         this.single = single;
         this.ids = ids;
         this.storage = storage;
         this.palette = palette;
         this.columns = columns;
      }
   }
}
