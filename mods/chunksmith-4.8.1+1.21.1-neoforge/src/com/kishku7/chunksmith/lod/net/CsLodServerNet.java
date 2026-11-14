package com.kishku7.chunksmith.lod.net;

import com.kishku7.chunksmith.ChunksmithProvider;
import com.kishku7.chunksmith.lod.HorizonGuard;
import com.kishku7.chunksmith.lod.LodSupport;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CsLodServerNet {
   private static final Logger LOGGER = LoggerFactory.getLogger("Chunksmith");
   private static final CsLodCapNotice CAP_NOTICE = new CsLodCapNotice();
   private static final CsLodReachNotice REACH_NOTICE = new CsLodReachNotice();
   private static final CsLodTokens TOKENS = new CsLodTokens();
   private static final int MAX_RADIUS_BLOCKS = 16384;
   private static final Pattern DIM_DIR = Pattern.compile("[a-z0-9_.-]{1,64}");
   private static final Map<UUID, Integer> RADIUS = new ConcurrentHashMap<>();
   private static final CsLodBackfill BACKFILL = new CsLodBackfill();
   private static final Set<UUID> WAITING = ConcurrentHashMap.newKeySet();
   private static final Map<UUID, Set<String>> ANNOUNCED = new ConcurrentHashMap<>();
   private static final Map<UUID, Integer> GREETED = new ConcurrentHashMap<>();
   private static final Set<UUID> SCANNING = ConcurrentHashMap.newKeySet();
   private static final Set<UUID> ARMED = ConcurrentHashMap.newKeySet();
   private static final int STORE_WATCH_TICKS = 100;
   private static int sinceStoreWatch;
   private static CsLodHttpServer http;
   private static MinecraftServer server;
   private static volatile ExecutorService scanPool;

   private CsLodServerNet() {
   }

   public static void register() {
      CsLodChannel.register();
   }

   public static void onDisconnect(UUID player) {
      TOKENS.revoke(player);
      CsLodInBandSender.forget(player);
      REACH_NOTICE.forget(player);
      CAP_NOTICE.forget(player);
      RADIUS.remove(player);
      BACKFILL.forget(player);
      WAITING.remove(player);
      ANNOUNCED.remove(player);
      GREETED.remove(player);
      SCANNING.remove(player);
      ARMED.remove(player);
   }

   public static void onServerStarted(MinecraftServer current) {
      server = current;
      if (!LodSupport.lodEnabled(current)) {
         LOGGER.info("Chunksmith: LOD is disabled; not serving LODs");
      } else {
         Path root = LodSupport.storeRootBase(current);

         try {
            Files.createDirectories(root);
         } catch (IOException e) {
            LOGGER.warn("Chunksmith: cannot create the LOD store root " + root + ": " + e);
            return;
         }

         scanPool = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "chunksmith-lod-scan");
            thread.setDaemon(true);
            return thread;
         });
         http = new CsLodHttpServer(root, TOKENS, CsLodServerNet::isOnline);
         http.start(current.getLocalIp(), current.getPort(), configuredPort(), configuredBind());
         CsLodControl.register(CsLodServerNet::rebind, current::getPort, () -> http == null ? "backchannel: not running (in-band fallback)" : http.describe());
      }
   }

   private static int configuredPort() {
      return ChunksmithProvider.isLoaded() ? ChunksmithProvider.get().getConfig().getLodBackchannelPort() : 0;
   }

   private static String configuredBind() {
      return ChunksmithProvider.isLoaded() ? ChunksmithProvider.get().getConfig().getLodBackchannelBindAddress() : "";
   }

   private static String advertisedHost() {
      return ChunksmithProvider.isLoaded() ? ChunksmithProvider.get().getConfig().getLodBackchannelHost() : "";
   }

   private static String worldIdOf(ServerPlayer player) {
      MinecraftServer server = player.level().getServer();
      return server == null ? "" : LodSupport.worldId(server);
   }

   public static int rebind() {
      MinecraftServer current = server;
      if (current == null) {
         return 0;
      }

      if (http != null) {
         http.stop();
         http = null;
      }

      if (!LodSupport.lodEnabled(current)) {
         return 0;
      }

      Path root = LodSupport.storeRootBase(current);

      try {
         Files.createDirectories(root);
      } catch (IOException e) {
         LOGGER.warn("Chunksmith: cannot create the LOD store root " + root + ": " + e);
         return 0;
      }

      http = new CsLodHttpServer(root, TOKENS, CsLodServerNet::isOnline);
      int bound = http.start(current.getLocalIp(), current.getPort(), configuredPort(), configuredBind());
      readvertise(current, bound);
      return bound;
   }

   private static void readvertise(MinecraftServer current, int port) {
      List<String> dims = dimensions();
      boolean available = !dims.isEmpty();
      int told = 0;

      for (ServerPlayer player : current.getPlayerList().getPlayers()) {
         if (GREETED.containsKey(player.getUUID())) {
            String token = available && port != 0 ? TOKENS.issue(player.getUUID()) : "";

            try {
               send(player, CsLodMessages.encode(new CsLodMessages.ServerHello(4, available, port, token, dims, advertisedHost(), worldIdOf(player))));
               told++;
            } catch (IOException e) {
               LOGGER.warn("Chunksmith: could not tell {} about the new backchannel port: {}", nameOf(player), e.toString());
            }
         }
      }

      LOGGER.info(
         "Chunksmith: LOD backchannel moved to port {}; {} connected client(s) re-issued a token. No relog needed.",
         port == 0 ? "none (in-band)" : String.valueOf(port),
         told
      );
   }

   public static void onServerStopped() {
      CsLodControl.clear();
      if (http != null) {
         http.stop();
         http = null;
      }

      ExecutorService current = scanPool;
      if (current != null) {
         current.shutdown();

         try {
            if (!current.awaitTermination(2L, TimeUnit.SECONDS)) {
               current.shutdownNow();
            }
         } catch (InterruptedException e) {
            current.shutdownNow();
            Thread.currentThread().interrupt();
         }

         scanPool = null;
      }

      TOKENS.clear();
      BACKFILL.clear();
      WAITING.clear();
      ANNOUNCED.clear();
      GREETED.clear();
      SCANNING.clear();
      ARMED.clear();
      CsLodGrowth.reset();
      sinceStoreWatch = 0;
      server = null;
   }

   public static String describe() {
      String inBand = CsLodInBandSender.pending() > 0 ? " | in-band backlog: " + CsLodInBandSender.pending() + " regions" : "";
      return (http == null ? "LOD serving: in-band only (no backchannel)" : "LOD serving: " + http.describe()) + inBand;
   }

   public static String issueFor(ServerPlayer player) {
      return http != null && http.getPort() != 0 ? TOKENS.issue(player.getUUID()) : null;
   }

   private static boolean isOnline(UUID player) {
      MinecraftServer current = server;
      return current != null && current.getPlayerList().getPlayer(player) != null;
   }

   public static void receive(ServerPlayer player, byte[] data) {
      if (data.length != 0) {
         try (DataInputStream in = CsLodMessages.reader(data)) {
            byte id = in.readByte();
            switch (id) {
               case 1:
                  hello(player, CsLodMessages.decodeClientHello(in));
                  break;
               case 2:
                  dispatch(player, in.readUTF(), false, false);
                  break;
               case 3:
                  inBand(player, in);
                  break;
               case 4:
                  CsLodInBandSender.cancel(player);
                  LOGGER.debug("Chunksmith: LOD transfer cancelled by client");
                  break;
               case 5:
                  dispatch(player, in.readUTF(), true, true);
                  break;
               default:
                  LOGGER.warn("Chunksmith: unknown LOD message id " + id);
            }
         } catch (IOException e) {
            LOGGER.warn("Chunksmith: malformed LOD message from " + nameOf(player) + ": " + e);
         }
      }
   }

   private static void hello(ServerPlayer player, CsLodMessages.ClientHello hello) throws IOException {
      if (hello.protocolVersion() != 4) {
         LOGGER.info(
            "Chunksmith: "
               + nameOf(player)
               + " speaks LOD protocol v"
               + hello.protocolVersion()
               + ", we speak v4, so we are not serving. Their Chunksmith needs to match this server's."
         );
         send(player, CsLodMessages.encode(new CsLodMessages.ServerHello(4, false, 0, "", List.of())));
         GREETED.put(player.getUUID(), hello.protocolVersion());
         MinecraftServer owner = player.level().getServer();
         if (owner != null) {
            owner.getCommands().sendCommands(player);
         }
      } else if (!hello.hasVoxy() && !hello.hasDh()) {
         send(player, CsLodMessages.encode(new CsLodMessages.ServerHello(4, false, 0, "", List.of())));
         if (GREETED.put(player.getUUID(), hello.protocolVersion()) == null) {
            LOGGER.info(
               "Chunksmith: LOD hello from " + nameOf(player) + " (voxy=false dh=false, no LOD renderer) -> serving no data; /cslod set can reach them"
            );
         }
      } else {
         List<String> dims = dimensions();
         boolean available = !dims.isEmpty();
         int port = http == null ? 0 : http.getPort();
         String token = available && port != 0 ? TOKENS.issue(player.getUUID()) : "";
         send(player, CsLodMessages.encode(new CsLodMessages.ServerHello(4, available, port, token, dims, advertisedHost(), worldIdOf(player))));
         RADIUS.put(player.getUUID(), Math.min(16384, Math.max(16, hello.radiusBlocks())));
         if (available) {
            WAITING.remove(player.getUUID());
            ANNOUNCED.computeIfAbsent(player.getUUID(), ignored -> ConcurrentHashMap.newKeySet()).addAll(dims);
         } else {
            WAITING.add(player.getUUID());
         }

         String line = "Chunksmith: LOD hello from "
            + nameOf(player)
            + " (voxy="
            + hello.hasVoxy()
            + " dh="
            + hello.hasDh()
            + " radius="
            + hello.radiusBlocks()
            + ") -> store="
            + available
            + " backchannel="
            + (port == 0 ? "none (in-band)" : port);
         if (GREETED.put(player.getUUID(), hello.protocolVersion()) == null) {
            LOGGER.info(line);
         } else {
            LOGGER.debug(line);
         }
      }
   }

   private static void inBand(ServerPlayer player, DataInputStream in) throws IOException {
      String requested = in.readUTF();
      int count = in.readInt();
      if (count >= 0 && count <= 4096) {
         List<CsLodMessages.RegionEntry> wanted = new ArrayList<>(count);

         for (int i = 0; i < count; i++) {
            wanted.add(new CsLodMessages.RegionEntry(in.readInt(), in.readInt(), 0L, 0L));
         }

         Path root = storeBase();
         if (root != null) {
            if (safeDimensionDir(root, requested) == null) {
               LOGGER.warn("Chunksmith: ignoring an in-band LOD request from {} for a malformed dimension id", nameOf(player));
            } else {
               String dimension = dimensionOf(player);
               if (!dimension.isEmpty()) {
                  if (!dimension.equals(requested)) {
                     LOGGER.info(
                        "Chunksmith: {} asked in-band for {} while standing in {}; serving {} instead.",
                        new Object[]{nameOf(player), requested, dimension, dimension}
                     );
                  }

                  CsLodInBandSender.queue(player, root, dimension, wanted);
                  LOGGER.info(
                     "Chunksmith: in-band LOD fetch for {}. {} regions of {} (no backchannel; this is the slow path)",
                     new Object[]{nameOf(player), wanted.size(), dimension}
                  );
                  CsLodHttpServer server = http;
                  if (server != null
                     && server.getPort() > 0
                     && CsLodReachNotice.unreached(true, server.servedCount(), server.rejectedCount())
                     && REACH_NOTICE.shouldWarn(player.getUUID(), System.currentTimeMillis())) {
                     LOGGER.warn(CsLodReachNotice.explain(server.getPort(), true));
                  }
               }
            }
         }
      } else {
         LOGGER.warn("Chunksmith: ignoring an in-band LOD request from {} for {} regions (max {})", new Object[]{nameOf(player), count, 4096});
      }
   }

   public static void tick(MinecraftServer current) {
      HorizonGuard.tick(current);

      for (ServerPlayer player : current.getPlayerList().getPlayers()) {
         CsLodInBandSender.tick(player);
      }

      storeWatchTick(current);
      growthTick(current);
   }

   private static void growthTick(MinecraftServer current) {
      if (!ARMED.isEmpty() && CsLodGrowth.pushDue(System.currentTimeMillis())) {
         int pushed = 0;

         for (UUID uuid : List.copyOf(ARMED)) {
            ServerPlayer player = current.getPlayerList().getPlayer(uuid);
            if (player == null) {
               ARMED.remove(uuid);
            } else {
               String dimension = dimensionOf(player);
               if (!dimension.isEmpty()) {
                  try {
                     dispatch(player, dimension, true, false);
                     pushed++;
                  } catch (IOException e) {
                     LOGGER.debug("Chunksmith: growth push to {} failed: {}", nameOf(player), e.toString());
                  }
               }
            }
         }

         LOGGER.debug("Chunksmith: the LOD store grew; sent {} player(s) a fresh summary", pushed);
      }
   }

   private static void storeWatchTick(MinecraftServer current) {
      if (WAITING.isEmpty()) {
         sinceStoreWatch = 0;
      } else if (++sinceStoreWatch >= 100) {
         sinceStoreWatch = 0;
         List<String> dims = dimensions();
         if (!dims.isEmpty()) {
            int port = http == null ? 0 : http.getPort();

            for (UUID uuid : List.copyOf(WAITING)) {
               ServerPlayer player = current.getPlayerList().getPlayer(uuid);
               if (player == null) {
                  WAITING.remove(uuid);
               } else {
                  Set<String> told = ANNOUNCED.computeIfAbsent(uuid, ignored -> ConcurrentHashMap.newKeySet());
                  if (!told.addAll(dims)) {
                     WAITING.remove(uuid);
                  } else {
                     WAITING.remove(uuid);
                     String token = port != 0 ? TOKENS.issue(uuid) : "";

                     try {
                        send(player, CsLodMessages.encode(new CsLodMessages.ServerHello(4, true, port, token, dims, advertisedHost(), worldIdOf(player))));
                     } catch (IOException e) {
                        LOGGER.warn("Chunksmith: could not tell {} that the LOD store is ready: {}", nameOf(player), e.toString());
                        continue;
                     }

                     LOGGER.info(
                        "Chunksmith: the LOD store now has data for {}. Telling {}, who joined before it existed. No relog needed.", dims, nameOf(player)
                     );
                  }
               }
            }
         }
      }
   }

   public static void sendTo(ServerPlayer player, byte[] data) {
      send(player, data);
   }

   public static boolean hasLodClient(ServerPlayer player) {
      return GREETED.containsKey(player.getUUID());
   }

   public static boolean isLegacyClient(ServerPlayer player) {
      Integer spoken = GREETED.get(player.getUUID());
      return spoken != null && spoken < 4;
   }

   public static boolean sendClientSetting(ServerPlayer player, byte action, String name, String value) {
      try {
         send(player, CsLodMessages.encode(new CsLodMessages.ClientSetting(action, name, value)));
         return true;
      } catch (IOException e) {
         LOGGER.warn("Chunksmith: could not encode a client-setting message: {}", e.toString());
         return false;
      }
   }

   private static void dispatch(ServerPlayer player, String requested, boolean summaryOnly, boolean clientPoll) throws IOException {
      Path root = storeBase();
      if (root != null) {
         if (safeDimensionDir(root, requested) == null) {
            LOGGER.warn("Chunksmith: ignoring a LOD request from {} for a malformed dimension id", nameOf(player));
         } else {
            String dimension = dimensionOf(player);
            if (!dimension.isEmpty()) {
               if (!dimension.equals(requested)) {
                  LOGGER.info(
                     "Chunksmith: {} asked for the LOD index of {} while standing in {}, serving {} instead.",
                     new Object[]{nameOf(player), requested, dimension, dimension}
                  );
               }

               UUID uuid = player.getUUID();
               if (!summaryOnly) {
                  ARMED.add(uuid);
               }

               if (!SCANNING.add(uuid)) {
                  LOGGER.debug("Chunksmith: {} already has a LOD scan in flight; dropping the duplicate request", nameOf(player));
               } else {
                  int radius = RADIUS.getOrDefault(uuid, 256);
                  int served = radius;
                  if (ChunksmithProvider.get().getConfig().isLodBackgroundSendEnabled()) {
                     served = clientPoll && ARMED.contains(uuid) ? BACKFILL.advance(uuid, radius, 16384) : BACKFILL.radius(uuid, radius);
                  }

                  CsLodServerNet.Request request = new CsLodServerNet.Request(
                     uuid, nameOf(player), dimension, (int)player.getX(), (int)player.getZ(), served, summaryOnly, served > radius
                  );
                  ExecutorService pool = scanPool;
                  if (pool == null) {
                     SCANNING.remove(uuid);
                  } else {
                     try {
                        pool.execute(() -> run(root, request));
                     } catch (RejectedExecutionException e) {
                        SCANNING.remove(uuid);
                     }
                  }
               }
            }
         }
      }
   }

   private static void run(Path root, CsLodServerNet.Request request) {
      try {
         Path dir = safeDimensionDir(root, request.dimension());
         if (dir == null) {
            return;
         }

         long budgetMb = ChunksmithProvider.get().getConfig().getLodIndexBudgetMb();
         CsLodIndexScan.Result scanned = CsLodIndexScan.scan(
            dir,
            new CsLodIndexScan.Request(request.dimension(), request.px(), request.pz(), request.radius(), budgetMb * 1024L * 1024L),
            System.currentTimeMillis()
         );
         if (scanned.capped() && !request.grown() && CAP_NOTICE.shouldWarn(request.uuid(), System.currentTimeMillis())) {
            LOGGER.warn(
               "Chunksmith: LOD index for {} capped at {} of {} regions ({} MB, radius {}): {}",
               new Object[]{
                  request.name(),
                  scanned.regions().size(),
                  scanned.found(),
                  scanned.bytes() / 1048576L,
                  request.radius(),
                  CsLodCapNotice.explain(scanned, budgetMb)
               }
            );
         }

         List<CsLodMessages.RegionEntry> regions = scanned.regions();
         byte[] message;
         if (request.summaryOnly()) {
            message = CsLodMessages.encode(new CsLodMessages.RegionSummary(request.dimension(), regions.size(), CsLodIndexScan.aggregate(regions)));
            LOGGER.debug("Chunksmith: LOD sync summary for {}, {} regions of {}", new Object[]{request.name(), regions.size(), request.dimension()});
         } else {
            message = CsLodMessages.encode(new CsLodMessages.RegionIndex(request.dimension(), regions));
         }

         MinecraftServer current = server;
         if (current != null) {
            current.execute(() -> {
               ServerPlayer player = current.getPlayerList().getPlayer(request.uuid());
               if (player != null) {
                  send(player, message);
               }
            });
            return;
         }
      } catch (IOException e) {
         LOGGER.warn("Chunksmith: could not scan the LOD store for {}: {}", request.name(), e.toString());
         return;
      } finally {
         SCANNING.remove(request.uuid());
      }
   }

   private static Path safeDimensionDir(Path root, String dimension) {
      if (dimension != null && !dimension.isEmpty() && DIM_DIR.matcher(dimension).matches()) {
         Path dir = root.resolve(dimension).normalize();
         return dir.startsWith(root) ? dir : null;
      } else {
         return null;
      }
   }

   private static List<String> dimensions() {
      MinecraftServer current = server;
      if (current == null) {
         return List.of();
      }

      List<Path> dirs = new ArrayList<>();

      for (ServerLevel level : current.getAllLevels()) {
         dirs.add(LodSupport.storeRoot(level));
      }

      return CsLodStoreScan.servable(dirs, System.currentTimeMillis());
   }

   private static String dimensionOf(ServerPlayer player) {
      MinecraftServer current = server;
      if (current == null) {
         return "";
      }

      for (ServerLevel level : current.getAllLevels()) {
         if (level == player.level()) {
            return LodSupport.dimensionKey(level);
         }
      }

      return "";
   }

   private static Path storeBase() {
      MinecraftServer current = server;
      return current == null ? null : LodSupport.storeRootBase(current);
   }

   private static String nameOf(ServerPlayer player) {
      return player.getGameProfile().getName();
   }

   private static void send(ServerPlayer player, byte[] data) {
      CsLodChannel.send(player, data);
   }

   private record Request(UUID uuid, String name, String dimension, int px, int pz, int radius, boolean summaryOnly, boolean grown) {
   }
}
