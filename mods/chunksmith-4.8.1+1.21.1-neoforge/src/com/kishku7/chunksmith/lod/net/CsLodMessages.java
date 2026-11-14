package com.kishku7.chunksmith.lod.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class CsLodMessages {
   private CsLodMessages() {
   }

   public static byte[] encode(CsLodMessages.ClientHello hello) throws IOException {
      ByteArrayOutputStream raw = new ByteArrayOutputStream();

      try (DataOutputStream out = new DataOutputStream(raw)) {
         out.writeByte(1);
         out.writeInt(hello.protocolVersion());
         out.writeBoolean(hello.hasVoxy());
         out.writeBoolean(hello.hasDh());
         out.writeInt(hello.radiusBlocks());
      }

      return raw.toByteArray();
   }

   public static CsLodMessages.ClientHello decodeClientHello(DataInputStream in) throws IOException {
      return new CsLodMessages.ClientHello(in.readInt(), in.readBoolean(), in.readBoolean(), in.readInt());
   }

   public static byte[] encode(CsLodMessages.ServerHello hello) throws IOException {
      ByteArrayOutputStream raw = new ByteArrayOutputStream();

      try (DataOutputStream out = new DataOutputStream(raw)) {
         out.writeByte(101);
         out.writeInt(hello.protocolVersion());
         out.writeBoolean(hello.storeAvailable());
         out.writeInt(hello.backchannelPort());
         out.writeUTF(hello.token() == null ? "" : hello.token());
         out.writeInt(hello.dimensions().size());

         for (String dimension : hello.dimensions()) {
            out.writeUTF(dimension);
         }

         out.writeUTF(hello.advertisedHost() == null ? "" : hello.advertisedHost());
         out.writeUTF(hello.worldId() == null ? "" : hello.worldId());
      }

      return raw.toByteArray();
   }

   public static CsLodMessages.ServerHello decodeServerHello(DataInputStream in) throws IOException {
      int version = in.readInt();
      boolean available = in.readBoolean();
      int port = in.readInt();
      String token = in.readUTF();
      int count = in.readInt();
      if (count >= 0 && count <= 4096) {
         List<String> dimensions = new ArrayList<>();

         for (int i = 0; i < count; i++) {
            dimensions.add(in.readUTF());
         }

         String advertisedHost = in.available() > 0 ? in.readUTF() : "";
         String worldId = in.available() > 0 ? in.readUTF() : "";
         return new CsLodMessages.ServerHello(version, available, port, token, dimensions, advertisedHost, worldId);
      } else {
         throw new IOException("CSLOD hello: dimension count " + count + " out of range [0, 4096]");
      }
   }

   public static byte[] encode(CsLodMessages.RegionIndex index) throws IOException {
      ByteArrayOutputStream raw = new ByteArrayOutputStream();

      try (DataOutputStream out = new DataOutputStream(raw)) {
         out.writeByte(102);
         out.writeUTF(index.dimension());
         out.writeInt(index.regions().size());

         for (CsLodMessages.RegionEntry entry : index.regions()) {
            out.writeInt(entry.regionX());
            out.writeInt(entry.regionZ());
            out.writeLong(entry.hash());
            out.writeLong(entry.sizeBytes());
         }
      }

      return raw.toByteArray();
   }

   public static CsLodMessages.RegionIndex decodeRegionIndex(DataInputStream in) throws IOException {
      String dimension = in.readUTF();
      int count = in.readInt();
      if (count >= 0 && count <= 65536) {
         List<CsLodMessages.RegionEntry> regions = new ArrayList<>();

         for (int i = 0; i < count; i++) {
            regions.add(new CsLodMessages.RegionEntry(in.readInt(), in.readInt(), in.readLong(), in.readLong()));
         }

         return new CsLodMessages.RegionIndex(dimension, regions);
      } else {
         throw new IOException("CSLOD index: region count " + count + " out of range [0, 65536]");
      }
   }

   public static byte[] requestSummary(String dimension) throws IOException {
      ByteArrayOutputStream raw = new ByteArrayOutputStream();

      try (DataOutputStream out = new DataOutputStream(raw)) {
         out.writeByte(5);
         out.writeUTF(dimension);
      }

      return raw.toByteArray();
   }

   public static byte[] encode(CsLodMessages.RegionSummary summary) throws IOException {
      ByteArrayOutputStream raw = new ByteArrayOutputStream();

      try (DataOutputStream out = new DataOutputStream(raw)) {
         out.writeByte(105);
         out.writeUTF(summary.dimension());
         out.writeInt(summary.count());
         out.writeLong(summary.aggregate());
      }

      return raw.toByteArray();
   }

   public static CsLodMessages.RegionSummary decodeRegionSummary(DataInputStream in) throws IOException {
      String dimension = in.readUTF();
      int count = in.readInt();
      if (count >= 0 && count <= 65536) {
         return new CsLodMessages.RegionSummary(dimension, count, in.readLong());
      } else {
         throw new IOException("CSLOD summary: region count " + count + " out of range [0, 65536]");
      }
   }

   public static byte[] requestIndex(String dimension) throws IOException {
      ByteArrayOutputStream raw = new ByteArrayOutputStream();

      try (DataOutputStream out = new DataOutputStream(raw)) {
         out.writeByte(2);
         out.writeUTF(dimension);
      }

      return raw.toByteArray();
   }

   public static byte[] requestRegions(String dimension, List<CsLodMessages.RegionEntry> wanted) throws IOException {
      ByteArrayOutputStream raw = new ByteArrayOutputStream();

      try (DataOutputStream out = new DataOutputStream(raw)) {
         out.writeByte(3);
         out.writeUTF(dimension);
         out.writeInt(wanted.size());

         for (CsLodMessages.RegionEntry entry : wanted) {
            out.writeInt(entry.regionX());
            out.writeInt(entry.regionZ());
         }
      }

      return raw.toByteArray();
   }

   public static byte[] encode(CsLodMessages.RegionSlice slice) throws IOException {
      ByteArrayOutputStream raw = new ByteArrayOutputStream(slice.data().length + 64);

      try (DataOutputStream out = new DataOutputStream(raw)) {
         out.writeByte(103);
         out.writeUTF(slice.dimension());
         out.writeInt(slice.regionX());
         out.writeInt(slice.regionZ());
         out.writeBoolean(slice.last());
         out.writeInt(slice.data().length);
         out.write(slice.data());
      }

      return raw.toByteArray();
   }

   public static CsLodMessages.RegionSlice decodeRegionSlice(DataInputStream in) throws IOException {
      String dimension = in.readUTF();
      int x = in.readInt();
      int z = in.readInt();
      boolean last = in.readBoolean();
      int length = in.readInt();
      if (length >= 0 && length <= 1048576) {
         byte[] data = new byte[length];
         in.readFully(data);
         return new CsLodMessages.RegionSlice(dimension, x, z, last, data);
      } else {
         throw new IOException("CSLOD slice: payload length " + length + " out of range [0, 1048576]");
      }
   }

   public static byte[] done() {
      return new byte[]{104};
   }

   public static byte[] cancel() {
      return new byte[]{4};
   }

   public static byte[] encode(CsLodMessages.ClientSetting setting) throws IOException {
      ByteArrayOutputStream raw = new ByteArrayOutputStream();

      try (DataOutputStream out = new DataOutputStream(raw)) {
         out.writeByte(106);
         out.writeByte(setting.action());
         out.writeUTF(setting.name());
         out.writeUTF(setting.value());
      }

      return raw.toByteArray();
   }

   public static CsLodMessages.ClientSetting decodeClientSetting(DataInputStream in) throws IOException {
      byte action = in.readByte();
      if (action != 0 && action != 1 && action != 2) {
         throw new IOException("CSLOD client setting: unknown action " + action);
      } else {
         return new CsLodMessages.ClientSetting(action, in.readUTF(), in.readUTF());
      }
   }

   public static DataInputStream reader(byte[] payload) {
      return new DataInputStream(new ByteArrayInputStream(payload));
   }

   public record ClientHello(int protocolVersion, boolean hasVoxy, boolean hasDh, int radiusBlocks) {
   }

   public record ClientSetting(byte action, String name, String value) {
   }

   public record RegionEntry(int regionX, int regionZ, long hash, long sizeBytes) {
   }

   public record RegionIndex(String dimension, List<CsLodMessages.RegionEntry> regions) {
   }

   public record RegionSlice(String dimension, int regionX, int regionZ, boolean last, byte[] data) {
   }

   public record RegionSummary(String dimension, int count, long aggregate) {
   }

   public record ServerHello(
      int protocolVersion, boolean storeAvailable, int backchannelPort, String token, List<String> dimensions, String advertisedHost, String worldId
   ) {
      public ServerHello(int protocolVersion, boolean storeAvailable, int backchannelPort, String token, List<String> dimensions) {
         this(protocolVersion, storeAvailable, backchannelPort, token, dimensions, "", "");
      }
   }
}
