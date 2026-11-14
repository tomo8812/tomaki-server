package com.kishku7.chunksmith.lod.net;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerPlayer;

public final class CsLodInBandSender {
   private static final int SLICE_BYTES = 24576;
   private static final int SLICES_PER_TICK = 4;
   private static final Map<UUID, CsLodInBandSender.Transfer> TRANSFERS = new ConcurrentHashMap<>();

   private CsLodInBandSender() {
   }

   public static void queue(ServerPlayer player, Path storeRoot, String dimension, List<CsLodMessages.RegionEntry> wanted) throws IOException {
      cancel(player);
      Deque<CsLodInBandSender.Region> regions = new ArrayDeque<>();

      for (CsLodMessages.RegionEntry entry : wanted) {
         Path file = storeRoot.resolve(dimension).resolve("r." + entry.regionX() + "." + entry.regionZ() + ".cslod");
         if (Files.isRegularFile(file)) {
            regions.add(new CsLodInBandSender.Region(entry.regionX(), entry.regionZ(), file, Files.size(file)));
         }
      }

      TRANSFERS.put(player.getUUID(), new CsLodInBandSender.Transfer(dimension, regions));
   }

   public static void tick(ServerPlayer player) {
      CsLodInBandSender.Transfer transfer = TRANSFERS.get(player.getUUID());
      if (transfer != null) {
         try {
            for (int i = 0; i < 4; i++) {
               if (!transfer.sendNext(player)) {
                  finish(player.getUUID(), transfer);
                  CsLodServerNet.sendTo(player, CsLodMessages.done());
                  return;
               }
            }
         } catch (IOException e) {
            finish(player.getUUID(), transfer);
         }
      }
   }

   public static void cancel(ServerPlayer player) {
      forget(player.getUUID());
   }

   public static void forget(UUID player) {
      CsLodInBandSender.Transfer transfer = TRANSFERS.get(player);
      if (transfer != null) {
         finish(player, transfer);
      }
   }

   private static void finish(UUID player, CsLodInBandSender.Transfer transfer) {
      TRANSFERS.remove(player, transfer);
      transfer.close();
   }

   public static int pending() {
      return TRANSFERS.values().stream().mapToInt(CsLodInBandSender.Transfer::remaining).sum();
   }

   private record Region(int regionX, int regionZ, Path file, long size) {
   }

   private static final class Transfer {
      private final String dimension;
      private final Deque<CsLodInBandSender.Region> pending;
      private CsLodInBandSender.Region current;
      private InputStream in;
      private long sent;

      private Transfer(String dimension, Deque<CsLodInBandSender.Region> pending) {
         this.dimension = dimension;
         this.pending = pending;
      }

      private boolean sendNext(ServerPlayer player) throws IOException {
         if (this.in == null) {
            this.current = this.pending.poll();
            if (this.current == null) {
               return false;
            }

            this.in = Files.newInputStream(this.current.file());
            this.sent = 0L;
         }

         byte[] buffer = new byte[24576];
         int read = this.in.readNBytes(buffer, 0, 24576);
         if (read <= 0) {
            this.closeCurrent();
            return true;
         }

         this.sent += read;
         boolean last = read < 24576 || this.sent >= this.current.size();
         byte[] data = read == 24576 ? buffer : Arrays.copyOf(buffer, read);
         CsLodServerNet.sendTo(
            player, CsLodMessages.encode(new CsLodMessages.RegionSlice(this.dimension, this.current.regionX(), this.current.regionZ(), last, data))
         );
         if (last) {
            this.closeCurrent();
         }

         return true;
      }

      private int remaining() {
         return this.pending.size() + (this.current == null ? 0 : 1);
      }

      private void closeCurrent() {
         if (this.in != null) {
            try {
               this.in.close();
            } catch (IOException var2) {
            }

            this.in = null;
         }

         this.current = null;
      }

      private void close() {
         this.pending.clear();
         this.closeCurrent();
      }
   }
}
