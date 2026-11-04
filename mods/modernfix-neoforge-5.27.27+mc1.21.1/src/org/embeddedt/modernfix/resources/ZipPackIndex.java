package org.embeddedt.modernfix.resources;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PackResources.ResourceOutput;
import net.minecraft.server.packs.resources.IoSupplier;

public class ZipPackIndex {
   private static final int EOCD_SIGNATURE = 101010256;
   private static final int EOCD_SIZE = 22;
   private static final int EOCD_OFF_CD_SIZE = 12;
   private static final int EOCD_OFF_CD_OFFSET = 16;
   private static final int EOCD_MAX_COMMENT_LENGTH = 65535;
   private static final int CD_ENTRY_SIGNATURE = 33639248;
   private static final int CD_ENTRY_HEADER_SIZE = 46;
   private static final int CD_OFF_FILENAME_LENGTH = 28;
   private static final int CD_OFF_EXTRA_LENGTH = 30;
   private static final int CD_OFF_COMMENT_LENGTH = 32;
   private static final IntList EMPTY_OFFSETS = IntList.of();
   private final ByteBuffer cdBuffer;
   private final Set<String> trackedTopLevelDirs;
   private final ZipPackIndex.DirNode root;

   public ZipPackIndex(Path zipPath) throws IOException {
      this.cdBuffer = readCentralDirectory(zipPath);
      Set<String> packTypeDirs = new HashSet<>();

      for (PackType type : PackType.values()) {
         packTypeDirs.add(type.getDirectory());
      }

      this.trackedTopLevelDirs = Set.copyOf(packTypeDirs);
      this.root = this.buildTree();
   }

   private static SeekableByteChannel obtainChannel(Path filePath) throws IOException {
      try {
         return FileChannel.open(filePath, StandardOpenOption.READ);
      } catch (Exception e) {
         return Files.newByteChannel(filePath);
      }
   }

   private static ByteBuffer readCentralDirectory(Path filePath) throws IOException {
      try (SeekableByteChannel channel = obtainChannel(filePath)) {
         long fileSize = channel.size();
         if (fileSize < 22L) {
            return null;
         }

         int tailSize = (int)Math.min(fileSize, 65557L);
         ByteBuffer tail = ByteBuffer.allocate(tailSize);
         tail.order(ByteOrder.LITTLE_ENDIAN);
         long tailStart = fileSize - tailSize;

         while (tail.hasRemaining()) {
            channel.position(tailStart + tail.position());
            int n = channel.read(tail);
            if (n < 0) {
               break;
            }
         }

         if (tail.hasRemaining()) {
            throw new IOException("Failed to read ZIP tail");
         }

         tail.flip();
         int eocdPos = -1;

         for (int i = tailSize - 22; i >= 0; i--) {
            if (tail.getInt(i) == 101010256) {
               int commentLen = Short.toUnsignedInt(tail.getShort(i + 20));
               if (i + 22 + commentLen == tailSize) {
                  eocdPos = i;
                  break;
               }
            }
         }

         if (eocdPos < 0) {
            return null;
         }

         long cdSize = Integer.toUnsignedLong(tail.getInt(eocdPos + 12));
         long cdOffset = Integer.toUnsignedLong(tail.getInt(eocdPos + 16));
         if (cdSize == 0L) {
            return null;
         }

         if (cdSize == 4294967295L || cdOffset == 4294967295L) {
            throw new IOException("ZIP64 not supported by ZipPackIndex");
         }

         if (cdOffset > fileSize - cdSize) {
            throw new IOException("Invalid central directory range");
         }

         if (channel instanceof FileChannel fc) {
            try {
               ByteBuffer buf = fc.map(MapMode.READ_ONLY, cdOffset, cdSize);
               buf.order(ByteOrder.LITTLE_ENDIAN);
               return buf;
            } catch (Exception var17) {
            }
         }

         ByteBuffer buf = ByteBuffer.allocate((int)cdSize);
         buf.order(ByteOrder.LITTLE_ENDIAN);

         while (buf.hasRemaining()) {
            channel.position(cdOffset + buf.position());
            int n = channel.read(buf);
            if (n < 0) {
               throw new IOException("Truncated central directory during heap read");
            }
         }

         buf.flip();
         return buf;
      }
   }

   private ZipPackIndex.DirNode buildTree() throws IOException {
      ByteBuffer cdBuffer = this.cdBuffer;
      ZipPackIndex.DirNode treeRoot = new ZipPackIndex.DirNode();
      if (cdBuffer == null) {
         treeRoot.freeze();
         return treeRoot;
      }

      int pos = 0;
      int limit = cdBuffer.limit();

      while (pos + 46 <= limit && cdBuffer.getInt(pos) == 33639248) {
         pos += this.indexCdEntry(pos, limit, treeRoot, cdBuffer);
      }

      treeRoot.freeze();
      return treeRoot;
   }

   private int indexCdEntry(int pos, int limit, ZipPackIndex.DirNode treeRoot, ByteBuffer cdBuffer) throws IOException {
      int fileNameLen = Short.toUnsignedInt(cdBuffer.getShort(pos + 28));
      int extraLen = Short.toUnsignedInt(cdBuffer.getShort(pos + 30));
      int commentLen = Short.toUnsignedInt(cdBuffer.getShort(pos + 32));
      int recordLen = 46 + fileNameLen + extraLen + commentLen;
      if (pos + recordLen > limit) {
         throw new IOException("Truncated central directory");
      }

      byte[] nameBytes = new byte[fileNameLen];
      cdBuffer.get(pos + 46, nameBytes);
      ZipPackIndex.DirNode current = treeRoot;
      boolean tracked = false;
      boolean skipped = false;
      int segStart = 0;

      for (int i = 0; i < fileNameLen; i++) {
         if (nameBytes[i] == 47) {
            int segLen = i - segStart;
            if (segLen > 0) {
               String segment = new String(nameBytes, segStart, segLen, StandardCharsets.UTF_8);
               if (!tracked) {
                  if (!this.trackedTopLevelDirs.contains(segment)) {
                     skipped = true;
                     break;
                  }

                  tracked = true;
               }

               ZipPackIndex.DirNode next = current.childDirs.get(segment);
               if (next == null) {
                  current.childDirs.put(segment, next = new ZipPackIndex.DirNode());
               }

               current = next;
            }

            segStart = i + 1;
         }
      }

      if (!skipped && tracked && segStart < fileNameLen) {
         if (current.fileChildOffsets == EMPTY_OFFSETS) {
            current.fileChildOffsets = new IntArrayList();
         }

         current.fileChildOffsets.add(pos);
      }

      return recordLen;
   }

   String readBasename(int cdOffset) {
      int nameLen = Short.toUnsignedInt(this.cdBuffer.getShort(cdOffset + 28));
      byte[] nameBytes = new byte[nameLen];
      this.cdBuffer.get(cdOffset + 46, nameBytes);
      int lastSlash = -1;

      for (int i = nameBytes.length - 1; i >= 0; i--) {
         if (nameBytes[i] == 47) {
            lastSlash = i;
            break;
         }
      }

      return new String(nameBytes, lastSlash + 1, nameLen - lastSlash - 1, StandardCharsets.UTF_8);
   }

   public Set<String> getTrackedTopLevelDirs() {
      return this.trackedTopLevelDirs;
   }

   public Set<String> getNamespaces(PackType type) {
      ZipPackIndex.DirNode typeNode = this.root.childDirs.get(type.getDirectory());
      if (typeNode == null) {
         return Set.of();
      }

      Set<String> result = new HashSet<>();

      for (String ns : typeNode.childDirs.keySet()) {
         if (ns.equals(ns.toLowerCase(Locale.ROOT))) {
            result.add(ns);
         }
      }

      return result;
   }

   public boolean hasResource(String... paths) {
      ZipPackIndex.DirNode node = this.root;

      for (int i = 0; i < paths.length - 1; i++) {
         String path = paths[i];
         if (!path.isEmpty()) {
            node = node.childDirs.get(path);
            if (node == null) {
               return false;
            }
         }
      }

      String basename = paths[paths.length - 1];
      IntList offsets = node.fileChildOffsets;

      for (int i = 0; i < offsets.size(); i++) {
         if (basename.equals(this.readBasename(offsets.getInt(i)))) {
            return true;
         }
      }

      return false;
   }

   public void listResources(PackType type, String namespace, String path, ZipFile zipFile, ResourceOutput output) {
      ZipPackIndex.DirNode node = this.root.childDirs.get(type.getDirectory());
      if (node != null) {
         node = node.childDirs.get(namespace);
         if (node != null) {
            String rlSubPath;
            if (!path.isEmpty()) {
               for (String segment : path.split("/")) {
                  if (!segment.isEmpty()) {
                     node = node.childDirs.get(segment);
                     if (node == null) {
                        return;
                     }
                  }
               }

               rlSubPath = path + "/";
            } else {
               rlSubPath = "";
            }

            String entryPrefix = type.getDirectory() + "/" + namespace + "/";
            this.collectResources(node, entryPrefix, rlSubPath, zipFile, namespace, output);
         }
      }
   }

   private void collectResources(ZipPackIndex.DirNode node, String entryPrefix, String rlSubPath, ZipFile zipFile, String namespace, ResourceOutput output) {
      IntList offsets = node.fileChildOffsets;

      for (int i = 0; i < offsets.size(); i++) {
         String basename = this.readBasename(offsets.getInt(i));
         String rlPathFull = rlSubPath + basename;
         ResourceLocation rl = ResourceLocation.tryBuild(namespace, rlPathFull);
         if (rl != null) {
            ZipEntry entry = zipFile.getEntry(entryPrefix + rlPathFull);
            if (entry != null) {
               output.accept(rl, IoSupplier.create(zipFile, entry));
            }
         }
      }

      for (Entry<String, ZipPackIndex.DirNode> child : node.childDirs.entrySet()) {
         this.collectResources(child.getValue(), entryPrefix, rlSubPath + child.getKey() + "/", zipFile, namespace, output);
      }
   }

   static final class DirNode {
      Map<String, ZipPackIndex.DirNode> childDirs = new Object2ObjectOpenHashMap();
      IntList fileChildOffsets = ZipPackIndex.EMPTY_OFFSETS;

      void freeze() {
         if (this.fileChildOffsets instanceof IntArrayList arrayList) {
            arrayList.trim();
         }

         this.childDirs = this.childDirs.isEmpty() ? Map.of() : Map.copyOf(this.childDirs);

         for (ZipPackIndex.DirNode child : this.childDirs.values()) {
            child.freeze();
         }
      }
   }
}
