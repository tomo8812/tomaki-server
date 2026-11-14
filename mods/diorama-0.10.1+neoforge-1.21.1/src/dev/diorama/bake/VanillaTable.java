package dev.diorama.bake;

import dev.diorama.Diorama;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

public final class VanillaTable {
   public static final String RESOURCE = "/diorama/vanilla_looks.dat";
   private static final int MAGIC = 1146506324;
   private static final int FORMAT = 1;
   static final int MAP_STEP = 4;
   static final int MAP_SIZE = 64;
   public static final int GRASS = 0;
   public static final int FOLIAGE = 1;
   public static final int DRY_FOLIAGE = 2;
   final List<VanillaTable.Look> looks;
   final Map<String, VanillaTable.Entry> blocks;
   final int @Nullable [][] colourMaps;
   private static @Nullable VanillaTable shipped;
   private static boolean loaded;

   VanillaTable(List<VanillaTable.Look> looks, Map<String, VanillaTable.Entry> blocks, int @Nullable [][] colourMaps) {
      this.looks = looks;
      this.blocks = blocks;
      this.colourMaps = colourMaps;
   }

   public static synchronized @Nullable VanillaTable shipped() {
      if (!loaded) {
         loaded = true;

         try (InputStream in = VanillaTable.class.getResourceAsStream("/diorama/vanilla_looks.dat")) {
            shipped = in == null ? null : read(in);
         } catch (IOException | RuntimeException e) {
            Diorama.LOGGER.warn("Couldn't read the vanilla colour table: {}", e.toString());
         }
      }

      return shipped;
   }

   public String describe() {
      return this.looks.size() + " looks for " + this.blocks.size() + " blocks";
   }

   int[] lookByStateId() {
      int[] out = new int[Block.BLOCK_STATE_REGISTRY.size()];
      Arrays.fill(out, -1);

      for (Block block : BuiltInRegistries.BLOCK) {
         VanillaTable.Entry entry = this.blocks.get(BuiltInRegistries.BLOCK.getKey(block).toString());
         if (entry != null) {
            List<BlockState> states = block.getStateDefinition().getPossibleStates();
            if (states.size() == entry.looks().length && signature(block).equals(entry.signature())) {
               for (int i = 0; i < states.size(); i++) {
                  int id = Block.BLOCK_STATE_REGISTRY.getId(states.get(i));
                  if (id >= 0 && id < out.length) {
                     out[id] = entry.looks()[i];
                  }
               }
            }
         }
      }

      return out;
   }

   public int @Nullable [] colourMap(int which) {
      int[][] maps = this.colourMaps;
      int[] small = maps != null && which < maps.length ? maps[which] : null;
      if (small == null) {
         return null;
      }

      int[] out = new int[65536];

      for (int y = 0; y < 256; y++) {
         float fy = Math.max(0.0F, Math.min(63.0F, (y + 0.5F) / 4.0F - 0.5F));
         int y0 = (int)fy;
         int y1 = Math.min(63, y0 + 1);
         float ty = fy - y0;

         for (int x = 0; x < 256; x++) {
            float fx = Math.max(0.0F, Math.min(63.0F, (x + 0.5F) / 4.0F - 0.5F));
            int x0 = (int)fx;
            int x1 = Math.min(63, x0 + 1);
            float tx = fx - x0;
            int rgb = 0;

            for (int shift = 0; shift <= 16; shift += 8) {
               float a = (small[y0 * 64 + x0] >> shift & 0xFF) * (1.0F - tx) + (small[y0 * 64 + x1] >> shift & 0xFF) * tx;
               float b = (small[y1 * 64 + x0] >> shift & 0xFF) * (1.0F - tx) + (small[y1 * 64 + x1] >> shift & 0xFF) * tx;
               rgb |= Math.round(a * (1.0F - ty) + b * ty) << shift;
            }

            out[y * 256 + x] = 0xFF000000 | rgb;
         }
      }

      return out;
   }

   static List<String> signature(Block block) {
      List<String> out = new ArrayList<>();

      for (Property<?> property : block.getStateDefinition().getProperties()) {
         out.add(property.getName() + "=" + String.join(",", valueNames(property)));
      }

      return out;
   }

   private static <T extends Comparable<T>> List<String> valueNames(Property<T> property) {
      List<String> out = new ArrayList<>();

      for (T value : property.getPossibleValues()) {
         out.add(property.getName(value));
      }

      return out;
   }

   public static VanillaTable of(Looks looks) {
      List<VanillaTable.Look> kept = new ArrayList<>();
      Map<VanillaTable.Look, Integer> index = new HashMap<>();
      Map<String, VanillaTable.Entry> blocks = new TreeMap<>();

      for (Block block : BuiltInRegistries.BLOCK) {
         String name = BuiltInRegistries.BLOCK.getKey(block).toString();
         if (name.startsWith("minecraft:")) {
            List<BlockState> states = block.getStateDefinition().getPossibleStates();
            int[] ids = new int[states.size()];

            for (int i = 0; i < ids.length; i++) {
               int id = looks.lookId(states.get(i));
               ids[i] = index.computeIfAbsent(VanillaTable.Look.of(looks, id), look -> {
                  kept.add(look);
                  return kept.size() - 1;
               });
            }

            blocks.put(name, new VanillaTable.Entry(signature(block), ids));
         }
      }

      int[][] maps = new int[][]{shrink(GrassColor::get), shrink(FoliageColor::get), null};
      return new VanillaTable(kept, blocks, maps);
   }

   private static int[] shrink(VanillaTable.ColourMap map) {
      int[] full = new int[65536];
      boolean[] reached = new boolean[65536];

      for (int y = 0; y < 256; y++) {
         for (int x = 0; x <= y; x++) {
            double temp = 1.0 - (x + 0.5) / 255.0;
            double rain = (1.0 - (y + 0.5) / 255.0) / temp;
            if (!(rain > 1.0) && !(temp <= 0.0)) {
               full[y * 256 + x] = map.get(temp, rain) & 16777215;
               reached[y * 256 + x] = true;
            }
         }
      }

      int[] small = new int[4096];
      boolean[] filled = new boolean[4096];

      for (int sy = 0; sy < 64; sy++) {
         for (int sx = 0; sx < 64; sx++) {
            int r = 0;
            int g = 0;
            int b = 0;
            int n = 0;

            for (int y = sy * 4; y < (sy + 1) * 4; y++) {
               for (int x = sx * 4; x < (sx + 1) * 4; x++) {
                  if (reached[y * 256 + x]) {
                     int c = full[y * 256 + x];
                     r += c >> 16 & 0xFF;
                     g += c >> 8 & 0xFF;
                     b += c & 0xFF;
                     n++;
                  }
               }
            }

            if (n > 0) {
               small[sy * 64 + sx] = Math.round((float)r / n) << 16 | Math.round((float)g / n) << 8 | Math.round((float)b / n);
               filled[sy * 64 + sx] = true;
            }
         }
      }

      for (int sy = 0; sy < 64; sy++) {
         for (int sx = 0; sx < 64; sx++) {
            if (!filled[sy * 64 + sx]) {
               int near = Math.min(sy, 63);
               small[sy * 64 + sx] = small[sy * 64 + near];
            }
         }
      }

      return small;
   }

   public void write(OutputStream raw) throws IOException {
      try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(raw)))) {
         out.writeInt(1146506324);
         out.writeInt(1);
         out.writeInt(this.looks.size());

         for (VanillaTable.Look look : this.looks) {
            out.writeByte(look.kind());
            out.writeByte(look.tint());
            out.writeInt(look.top());
            out.writeInt(look.topTinted());
            out.writeFloat(look.topTintWeight());
            out.writeInt(look.side());
            out.writeInt(look.sideTinted());
            out.writeFloat(look.sideTintWeight());
            out.writeInt(look.constantTint());
         }

         out.writeInt(this.blocks.size());

         for (Map.Entry<String, VanillaTable.Entry> block : this.blocks.entrySet()) {
            out.writeUTF(block.getKey());
            List<String> signature = block.getValue().signature();
            out.writeByte(signature.size());

            for (String property : signature) {
               out.writeUTF(property);
            }

            int[] ids = block.getValue().looks();
            out.writeInt(ids.length);

            for (int id : ids) {
               writeVarInt(out, id);
            }
         }

         int[][] maps = this.colourMaps == null ? new int[0][] : this.colourMaps;
         out.writeByte(maps.length);

         for (int[] map : maps) {
            out.writeBoolean(map != null);
            if (map != null) {
               for (int rgb : map) {
                  out.writeByte(rgb >> 16);
                  out.writeByte(rgb >> 8);
                  out.writeByte(rgb);
               }
            }
         }
      }
   }

   public static VanillaTable read(InputStream raw) throws IOException {
      DataInputStream in = new DataInputStream(new BufferedInputStream(new GZIPInputStream(raw)));
      if (in.readInt() == 1146506324 && in.readInt() == 1) {
         int count = in.readInt();
         List<VanillaTable.Look> looks = new ArrayList<>(count);

         for (int i = 0; i < count; i++) {
            looks.add(
               new VanillaTable.Look(
                  in.readByte(), in.readByte(), in.readInt(), in.readInt(), in.readFloat(), in.readInt(), in.readInt(), in.readFloat(), in.readInt()
               )
            );
         }

         int blockCount = in.readInt();
         Map<String, VanillaTable.Entry> blocks = new HashMap<>(blockCount * 2);

         for (int b = 0; b < blockCount; b++) {
            String name = in.readUTF();
            int properties = in.readUnsignedByte();
            List<String> signature = new ArrayList<>(properties);

            for (int p = 0; p < properties; p++) {
               signature.add(in.readUTF());
            }

            int[] ids = new int[in.readInt()];

            for (int i = 0; i < ids.length; i++) {
               ids[i] = readVarInt(in);
               if (ids[i] < 0 || ids[i] >= count) {
                  throw new IOException("look " + ids[i] + " of " + name + " is past the " + count + " looks");
               }
            }

            blocks.put(name, new VanillaTable.Entry(List.copyOf(signature), ids));
         }

         int mapCount = in.readUnsignedByte();
         int[][] maps = new int[mapCount][];

         for (int m = 0; m < mapCount; m++) {
            if (in.readBoolean()) {
               int[] map = new int[4096];

               for (int i = 0; i < map.length; i++) {
                  map[i] = in.readUnsignedByte() << 16 | in.readUnsignedByte() << 8 | in.readUnsignedByte();
               }

               maps[m] = map;
            }
         }

         return new VanillaTable(looks, blocks, maps);
      } else {
         throw new IOException("not a vanilla colour table of format 1");
      }
   }

   public List<String> differences(VanillaTable other, int limit) {
      List<String> out = new ArrayList<>();
      if (limit <= 0) {
         return out;
      }

      Set<String> names = new TreeSet<>(this.blocks.keySet());
      names.addAll(other.blocks.keySet());

      for (String name : names) {
         VanillaTable.Entry a = this.blocks.get(name);
         VanillaTable.Entry b = other.blocks.get(name);
         if (a != null && b != null) {
            if (a.signature().equals(b.signature()) && a.looks().length == b.looks().length) {
               for (int i = 0; i < a.looks().length; i++) {
                  VanillaTable.Look la = this.looks.get(a.looks()[i]);
                  VanillaTable.Look lb = other.looks.get(b.looks()[i]);
                  if (!la.equals(lb)) {
                     out.add(name + " state " + i + ": " + la + " vs " + lb);
                     break;
                  }
               }
            } else {
               out.add(name + " has other properties: " + a.signature() + " vs " + b.signature());
            }
         } else {
            out.add(name + (a == null ? " is new" : " is gone"));
         }

         if (out.size() >= limit) {
            return out;
         }
      }

      for (int m = 0; m < 3; m++) {
         int[] a = this.colourMaps != null && m < this.colourMaps.length ? this.colourMaps[m] : null;
         int[] b = other.colourMaps != null && m < other.colourMaps.length ? other.colourMaps[m] : null;
         if (!Arrays.equals(a, b) && out.size() < limit) {
            out.add("colour map " + m + " differs");
         }
      }

      return out;
   }

   private static void writeVarInt(DataOutputStream out, int value) throws IOException {
      while ((value & -128) != 0) {
         out.writeByte(value & 127 | 128);
         value >>>= 7;
      }

      out.writeByte(value);
   }

   private static int readVarInt(DataInputStream in) throws IOException {
      int value = 0;

      for (int shift = 0; shift < 35; shift += 7) {
         int b = in.readUnsignedByte();
         value |= (b & 127) << shift;
         if ((b & 128) == 0) {
            return value;
         }
      }

      throw new IOException("varint too long");
   }

   private interface ColourMap {
      int get(double var1, double var3);
   }

   record Entry(List<String> signature, int[] looks) {
   }

   public record Look(byte kind, byte tint, int top, int topTinted, float topTintWeight, int side, int sideTinted, float sideTintWeight, int constantTint) {
      static VanillaTable.Look of(Looks looks, int id) {
         return new VanillaTable.Look(
            looks.kind[id],
            looks.tint[id],
            looks.top[id],
            looks.topTinted[id],
            looks.topTintWeight[id],
            looks.side[id],
            looks.sideTinted[id],
            looks.sideTintWeight[id],
            looks.constantTint[id]
         );
      }
   }
}
