package com.kishku7.chunksmith.lod;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public final class CsLodCodec {
   public static final int MAGIC = 1129532484;
   public static final int VERSION = 1;
   private static final int FLAG_UNIFORM_BLOCK = 1;
   private static final int FLAG_UNIFORM_BIOME = 2;
   private static final int FLAG_UNIFORM_SKY = 4;
   private static final int FLAG_UNIFORM_BLOCK_LIGHT = 8;

   private CsLodCodec() {
   }

   public static byte[] encode(CsLodChunk chunk) throws IOException {
      ByteArrayOutputStream raw = new ByteArrayOutputStream(8192);

      try (DataOutputStream out = new DataOutputStream(new DeflaterOutputStream(raw))) {
         out.writeInt(1129532484);
         out.writeShort(1);
         out.writeUTF(chunk.getDimension());
         out.writeInt(chunk.getChunkX());
         out.writeInt(chunk.getChunkZ());
         out.writeInt(chunk.getMinSectionY());
         out.writeByte(chunk.getSections().size());
         writePalette(out, chunk.getBlockPalette());
         writePalette(out, chunk.getBiomePalette());
         int blockWidth = indexWidth(chunk.getBlockPalette().size());
         int biomeWidth = indexWidth(chunk.getBiomePalette().size());

         for (CsLodChunk.Section section : chunk.getSections()) {
            int flags = 0;
            if (section.getUniformBlock() >= 0) {
               flags |= 1;
            }

            if (section.getUniformBiome() >= 0) {
               flags |= 2;
            }

            if (section.getUniformSky() >= 0) {
               flags |= 4;
            }

            if (section.getUniformBlockLight() >= 0) {
               flags |= 8;
            }

            out.writeByte(flags);
            if (section.getUniformBlock() >= 0) {
               writeVarInt(out, section.getUniformBlock());
            } else {
               writeIndices(out, section.getBlocks(), blockWidth);
            }

            if (section.getUniformBiome() >= 0) {
               writeVarInt(out, section.getUniformBiome());
            } else {
               writeIndices(out, section.getBiomes(), biomeWidth);
            }

            if (section.getUniformSky() >= 0) {
               out.writeByte(section.getUniformSky());
            } else {
               out.write(section.getSkyLight());
            }

            if (section.getUniformBlockLight() >= 0) {
               out.writeByte(section.getUniformBlockLight());
            } else {
               out.write(section.getBlockLight());
            }
         }
      }

      return raw.toByteArray();
   }

   public static CsLodChunk decode(byte[] compressed) throws IOException {
      try (DataInputStream in = new DataInputStream(new InflaterInputStream(new ByteArrayInputStream(compressed)))) {
         int magic = in.readInt();
         if (magic != 1129532484) {
            throw new IOException("Not a CSLOD record (magic " + Integer.toHexString(magic) + ")");
         }

         int version = in.readUnsignedShort();
         if (version != 1) {
            throw new IOException("Unsupported CSLOD version " + version + " (this build reads 1)");
         }

         String dimension = in.readUTF();
         int chunkX = in.readInt();
         int chunkZ = in.readInt();
         int minSectionY = in.readInt();
         int sectionCount = in.readUnsignedByte();
         if (sectionCount > 256) {
            throw new IOException("CSLOD record: section count " + sectionCount + " exceeds 256");
         }

         List<String> blockPalette = readPalette(in);
         List<String> biomePalette = readPalette(in);
         int blockWidth = indexWidth(blockPalette.size());
         int biomeWidth = indexWidth(biomePalette.size());
         List<CsLodChunk.Section> sections = new ArrayList<>(sectionCount);

         for (int i = 0; i < sectionCount; i++) {
            int flags = in.readUnsignedByte();
            int uniformBlock = -1;
            int[] blocks = null;
            if ((flags & 1) != 0) {
               uniformBlock = readVarInt(in);
            } else {
               blocks = readIndices(in, 4096, blockWidth);
            }

            int uniformBiome = -1;
            int[] biomes = null;
            if ((flags & 2) != 0) {
               uniformBiome = readVarInt(in);
            } else {
               biomes = readIndices(in, 64, biomeWidth);
            }

            int uniformSky = -1;
            byte[] skyLight = null;
            if ((flags & 4) != 0) {
               uniformSky = in.readUnsignedByte();
            } else {
               skyLight = new byte[2048];
               in.readFully(skyLight);
            }

            int uniformBlockLight = -1;
            byte[] blockLight = null;
            if ((flags & 8) != 0) {
               uniformBlockLight = in.readUnsignedByte();
            } else {
               blockLight = new byte[2048];
               in.readFully(blockLight);
            }

            sections.add(new CsLodChunk.Section(blocks, uniformBlock, biomes, uniformBiome, skyLight, uniformSky, blockLight, uniformBlockLight));
         }

         return new CsLodChunk(dimension, chunkX, chunkZ, minSectionY, blockPalette, biomePalette, sections);
      }
   }

   private static int indexWidth(int paletteSize) {
      return paletteSize <= 256 ? 1 : 2;
   }

   private static void writeIndices(DataOutputStream out, int[] indices, int width) throws IOException {
      if (width == 1) {
         for (int index : indices) {
            out.writeByte(index);
         }
      } else {
         for (int index : indices) {
            out.writeShort(index);
         }
      }
   }

   private static int[] readIndices(DataInputStream in, int count, int width) throws IOException {
      int[] indices = new int[count];

      for (int i = 0; i < count; i++) {
         indices[i] = width == 1 ? in.readUnsignedByte() : in.readUnsignedShort();
      }

      return indices;
   }

   private static void writePalette(DataOutputStream out, List<String> palette) throws IOException {
      writeVarInt(out, palette.size());

      for (String entry : palette) {
         out.writeUTF(entry);
      }
   }

   private static List<String> readPalette(DataInputStream in) throws IOException {
      int size = readVarInt(in);
      if (size >= 0 && size <= 65536) {
         List<String> palette = new ArrayList<>();

         for (int i = 0; i < size; i++) {
            palette.add(in.readUTF());
         }

         return palette;
      } else {
         throw new IOException("CSLOD record: palette size " + size + " out of range [0, 65536]");
      }
   }

   private static void writeVarInt(DataOutputStream out, int value) throws IOException {
      int remaining;
      for (remaining = value; (remaining & -128) != 0; remaining >>>= 7) {
         out.writeByte(remaining & 127 | 128);
      }

      out.writeByte(remaining & 127);
   }

   private static int readVarInt(DataInputStream in) throws IOException {
      int result = 0;
      int shift = 0;

      do {
         int b = in.readUnsignedByte();
         result |= (b & 127) << shift;
         if ((b & 128) == 0) {
            return result;
         }

         shift += 7;
      } while (shift <= 35);

      throw new IOException("VarInt too long");
   }
}
