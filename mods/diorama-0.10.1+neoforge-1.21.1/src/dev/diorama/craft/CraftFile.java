package dev.diorama.craft;

import dev.diorama.Diorama;
import dev.diorama.bake.CraftBlocks;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

final class CraftFile {
   private static final int MAGIC = 1145262641;

   private CraftFile() {
   }

   static List<CraftFile.Saved> read(Path file) {
      List<CraftFile.Saved> saved = new ArrayList<>();
      if (!Files.isRegularFile(file)) {
         return saved;
      }

      try (
         InputStream in = Files.newInputStream(file);
         DataInputStream data = new DataInputStream(new InflaterInputStream(in));
      ) {
         if (data.readInt() == 1145262641) {
            int count = data.readInt();

            for (int c = 0; c < count; c++) {
               UUID id = new UUID(data.readLong(), data.readLong());
               double[] pose = new double[12];

               for (int i = 0; i < 12; i++) {
                  pose[i] = data.readDouble();
               }

               int[] size = new int[]{data.readInt(), data.readInt(), data.readInt()};
               if (size[0] <= 0 || size[1] <= 0 || size[2] <= 0 || (long)size[0] * size[1] * size[2] > 4194304L) {
                  throw new IOException("bad craft size");
               }

               int limit = size[0] * size[1] * size[2];
               String[] palette = new String[checked(data.readInt(), limit * 2)];

               for (int i = 0; i < palette.length; i++) {
                  palette[i] = data.readUTF();
               }

               int[] runs = new int[checked(data.readInt(), limit * 2)];

               for (int i = 0; i < runs.length; i++) {
                  runs[i] = data.readInt();
               }

               int[] copycats = new int[checked(data.readInt(), limit * 2)];

               for (int i = 0; i < copycats.length; i++) {
                  copycats[i] = data.readInt();
               }

               saved.add(new CraftFile.Saved(id, pose, size, palette, runs, copycats));
            }

            return saved;
         } else {
            return saved;
         }
      } catch (IOException | RuntimeException e) {
         Diorama.LOGGER.warn("Couldn't read the parked crafts from {}", file, e);
         return saved;
      }
   }

   private static int checked(int length, int most) throws IOException {
      if (length >= 0 && length <= most) {
         return length;
      } else {
         throw new IOException("bad craft array length " + length);
      }
   }

   static void write(Path file, List<CraftFile.Saved> crafts) {
      try {
         if (crafts.isEmpty()) {
            Files.deleteIfExists(file);
            return;
         }

         Files.createDirectories(file.getParent());
         Path temp = file.resolveSibling(file.getFileName() + ".tmp");

         try (
            OutputStream out = Files.newOutputStream(temp);
            DataOutputStream data = new DataOutputStream(new DeflaterOutputStream(out));
         ) {
            data.writeInt(1145262641);
            data.writeInt(crafts.size());

            for (CraftFile.Saved s : crafts) {
               data.writeLong(s.id().getMostSignificantBits());
               data.writeLong(s.id().getLeastSignificantBits());

               for (double d : s.pose()) {
                  data.writeDouble(d);
               }

               for (int i : s.size()) {
                  data.writeInt(i);
               }

               data.writeInt(s.palette().length);

               for (String name : s.palette()) {
                  data.writeUTF(name);
               }

               data.writeInt(s.runs().length);

               for (int i : s.runs()) {
                  data.writeInt(i);
               }

               data.writeInt(s.copycats().length);

               for (int i : s.copycats()) {
                  data.writeInt(i);
               }
            }
         }

         Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      } catch (IOException | RuntimeException e) {
         Diorama.LOGGER.error("Couldn't save the parked crafts to {}", file, e);
      }
   }

   record Saved(UUID id, double[] pose, int[] size, String[] palette, int[] runs, int[] copycats) {
      static CraftFile.Saved of(UUID id, double[] pose, Body.Blocks blocks) {
         CraftBlocks b = blocks.blocks();
         Map<Integer, Integer> index = new HashMap<>();
         List<String> palette = new ArrayList<>();
         List<Integer> runs = new ArrayList<>();
         int last = -1;
         int length = 0;

         for (int i = 0; i < b.volume(); i++) {
            int p = paletteIndex(b.state(i), index, palette);
            if (p == last) {
               length++;
            } else {
               if (length > 0) {
                  runs.add(last);
                  runs.add(length);
               }

               last = p;
               length = 1;
            }
         }

         if (length > 0) {
            runs.add(last);
            runs.add(length);
         }

         List<Integer> copycats = new ArrayList<>();

         for (int i = 0; i < b.volume(); i++) {
            int material = b.copycat(i);
            if (material != 0) {
               copycats.add(i);
               copycats.add(paletteIndex(material, index, palette));
            }
         }

         return new CraftFile.Saved(
            id,
            (double[])pose.clone(),
            new int[]{b.sizeX, b.sizeY, b.sizeZ},
            palette.toArray(String[]::new),
            runs.stream().mapToInt(Integer::intValue).toArray(),
            copycats.stream().mapToInt(Integer::intValue).toArray()
         );
      }

      private static int paletteIndex(int state, Map<Integer, Integer> index, List<String> palette) {
         return index.computeIfAbsent(state, s -> {
            BlockState blockState = (BlockState)Block.BLOCK_STATE_REGISTRY.byId(s);
            palette.add(blockState != null && !blockState.isAir() ? BlockStateParser.serialize(blockState) : "");
            return palette.size() - 1;
         });
      }

      Body.@Nullable Blocks decode(Level level) {
         HolderLookup<Block> blocks = level.registryAccess().lookupOrThrow(Registries.BLOCK);
         int[] ids = new int[this.palette.length];

         for (int i = 0; i < ids.length; i++) {
            if (!this.palette[i].isEmpty()) {
               try {
                  ids[i] = Block.BLOCK_STATE_REGISTRY.getId(BlockStateParser.parseForBlock(blocks, this.palette[i], false).blockState());
               } catch (Exception e) {
                  ids[i] = 0;
               }
            }
         }

         int volume = this.size[0] * this.size[1] * this.size[2];
         int[] states = CraftBlocks.empty(this.size[0], this.size[1], this.size[2]);
         int at = 0;

         for (int r = 0; r + 1 < this.runs.length; r += 2) {
            int p = this.runs[r];
            int length = this.runs[r + 1];
            if (p < 0 || p >= ids.length || length < 0 || length > volume - at) {
               return null;
            }

            Arrays.fill(states, at, at + length, ids[p]);
            at += length;
         }

         if (at != volume) {
            return null;
         }

         Int2IntMap copycats = new Int2IntOpenHashMap();

         for (int c = 0; c + 1 < this.copycats.length; c += 2) {
            int cell = this.copycats[c];
            int p = this.copycats[c + 1];
            if (cell >= 0 && cell < volume && p >= 0 && p < ids.length && ids[p] != 0) {
               copycats.put(cell, ids[p]);
            }
         }

         return new Body.Blocks(new CraftBlocks(this.size[0], this.size[1], this.size[2], states, copycats), new int[3]);
      }
   }
}
