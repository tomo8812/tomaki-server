package com.kishku7.chunksmith.nbt.util;

import com.kishku7.chunksmith.nbt.Tag;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.zip.InflaterInputStream;

public final class RegionFile {
   private static final int ENTRIES = 1024;
   private static final int SECTOR_SIZE = 4096;
   private final Set<Chunk> chunks = new HashSet<>();
   private final Map<ChunkPos, Chunk> chunkMap = new HashMap<>();

   public RegionFile(File file) {
      this(file, null);
   }

   public RegionFile(File file, ChunkFilter filter) {
      try (RandomAccessFile region = new RandomAccessFile(file, "r")) {
         if (region.length() >= 4096L) {
            String regionFileName = file.getName();
            if (regionFileName.startsWith("r.")) {
               int extension = regionFileName.indexOf(".mca");
               if (extension >= 2) {
                  String regionCoordinates = regionFileName.substring(2, extension);
                  int separator = regionCoordinates.indexOf(46);

                  int regionX;
                  int regionZ;
                  try {
                     regionX = Integer.parseInt(regionCoordinates.substring(0, separator));
                     regionZ = Integer.parseInt(regionCoordinates.substring(separator + 1));
                  } catch (NumberFormatException e) {
                     return;
                  }

                  int[] offsetTable = new int[1024];
                  int[] sizeTable = new int[1024];

                  for (int i = 0; i < 1024; i++) {
                     int location = region.readInt();
                     offsetTable[i] = location >> 8 & 16777215;
                     sizeTable[i] = location & 0xFF;
                  }

                  int[] timestampTable = new int[1024];

                  for (int i = 0; i < 1024; i++) {
                     timestampTable[i] = region.readInt();
                  }

                  for (int i = 0; i < 1024; i++) {
                     int offset = offsetTable[i] * 4096;
                     int size = sizeTable[i] * 4096;
                     if (offset != 0 || size != 0) {
                        region.seek(offset);
                        int length = region.readInt();
                        byte compressionType = region.readByte();
                        if (compressionType != 2) {
                           throw new UnsupportedOperationException("Not in zlib format");
                        }

                        byte[] compressed = new byte[length - 1];
                        region.readFully(compressed);

                        try (
                           ByteArrayInputStream bytes = new ByteArrayInputStream(compressed);
                           InflaterInputStream inflater = new InflaterInputStream(bytes);
                           BufferedInputStream buffer = new BufferedInputStream(inflater);
                           DataInputStream input = new DataInputStream(buffer);
                        ) {
                           int x = regionX * 32 + i % 32;
                           int z = regionZ * 32 + i / 32;
                           Tag data;
                           if (filter == null) {
                              data = Tag.load(input);
                           } else {
                              data = Tag.find(input, filter.getType(), filter.getName());
                           }

                           Chunk chunk = new Chunk(x, z, data, timestampTable[i]);
                           this.chunks.add(chunk);
                           this.chunkMap.put(ChunkPos.of(x, z), chunk);
                        }
                     }
                  }
               }
            }
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public Collection<Chunk> getChunks() {
      return this.chunks;
   }

   public Optional<Chunk> getChunk(int x, int z) {
      return Optional.ofNullable(this.chunkMap.get(ChunkPos.of(x, z)));
   }
}
