package com.kishku7.chunksmith.lod.client.net;

import com.kishku7.chunksmith.lod.CsLodWorldId;
import com.kishku7.chunksmith.lod.LiveFeedLedger;
import com.kishku7.chunksmith.lod.LodSinks;
import com.kishku7.chunksmith.lod.LodSupport;
import com.kishku7.chunksmith.lod.client.ClientPlatform;
import com.kishku7.chunksmith.lod.client.CsLodCache;
import com.kishku7.chunksmith.lod.client.CsLodClientConfig;
import com.kishku7.chunksmith.lod.client.CsLodClientSettings;
import com.kishku7.chunksmith.lod.client.CsLodDimension;
import com.kishku7.chunksmith.lod.client.CsLodDiskBudget;
import com.kishku7.chunksmith.lod.client.CsLodDownloader;
import com.kishku7.chunksmith.lod.client.CsLodManifest;
import com.kishku7.chunksmith.lod.client.CsLodStore;
import com.kishku7.chunksmith.lod.client.Renderers;
import com.kishku7.chunksmith.lod.client.render.DhTarget;
import com.kishku7.chunksmith.lod.client.render.LodInjector;
import com.kishku7.chunksmith.lod.legacy.CsLodLegacySettings;
import com.kishku7.chunksmith.lod.net.CsLodMessages;
import com.kishku7.chunksmith.lod.net.CsLodRetry;
import com.kishku7.chunksmith.lod.net.CsLodSummary;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CsLodClientNet {
   private static final Logger LOGGER = LoggerFactory.getLogger("Chunksmith");
   private static final double REFRESH_MOVE_BLOCKS = 256.0;
   private static final long MIN_REFRESH_MILLIS = 5000L;
   private static final long TOKEN_REFRESH_MILLIS = 450000L;
   private static volatile CsLodDownloader downloader;
   private static volatile String token = "";
   private static volatile long tokenIssuedMillis;
   private static volatile int backchannelPort;
   private static volatile int advertisedBackchannelPort;
   private static volatile String host = "";
   private static volatile List<String> spPreexisting;
   private static final Set<String> SP_CAUGHT_UP = ConcurrentHashMap.newKeySet();
   private static volatile boolean spStoodDownTold;
   private static volatile String advertisedHost = "";
   private static volatile String worldId = "";
   private static final Map<String, List<CsLodMessages.RegionEntry>> DECLINED = new HashMap<>();
   private static boolean mismatchTold = false;
   private static volatile String activeDimension = "";
   private static volatile String playerDimension = "";
   private static volatile List<String> serverDimensions = List.of();
   private static volatile boolean awaitingStore;
   private static final CsLodRetry RETRY = new CsLodRetry();
   private static volatile boolean capsVoxy;
   private static volatile boolean capsDh;
   private static volatile int capsRadius;
   private static final AtomicBoolean busy = new AtomicBoolean();
   private static final long HELLO_TIMEOUT_MILLIS = 10000L;
   private static volatile List<CsLodMessages.RegionEntry> lastIndex = List.of();
   private static volatile long lastSyncMillis;
   private static volatile long helloSentMillis;
   private static volatile boolean helloAnswered;
   private static volatile boolean silenceReported;
   private static volatile double lastIndexX;
   private static volatile double lastIndexZ;
   private static volatile long lastIndexMillis;
   private static volatile Path inBandRoot;
   private static volatile String inBandDimension = "";
   private static volatile List<CsLodMessages.RegionEntry> inBandRegions = List.of();
   private static volatile CsLodManifest inBandManifest;
   private static final Map<String, ByteArrayOutputStream> PARTIAL = new HashMap<>();
   static final long IN_BAND_STALL_MILLIS = 60000L;
   private static volatile boolean inBandPending;
   private static volatile long inBandProgressMillis;
   private static boolean inBandStallTold;

   private CsLodClientNet() {
   }

   public static void register() {
      ClientPlatform.registerClientNetworking(CsLodClientNet::handle);
      ClientPlatform.onJoin(CsLodClientNet::hello);
      ClientPlatform.onDisconnect(CsLodClientNet::reset);
      ClientPlatform.onClientTick(CsLodClientNet::travelTick);
   }

   private static void travelTick() {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         if (!dimensionTick()) {
            inBandStallTick();
            if (!busy.get()) {
               if (activeDimension.isEmpty()) {
                  retryTick();
                  silenceTick();
               } else if (singleplayerLiveFeed()) {
                  tellStoodDown();
               } else {
                  syncTick();
                  long now = System.currentTimeMillis();
                  if (now - lastIndexMillis >= 5000L) {
                     double dx = player.getX() - lastIndexX;
                     double dz = player.getZ() - lastIndexZ;
                     if (!(dx * dx + dz * dz < 65536.0)) {
                        if (backchannelPort != 0 && !token.isEmpty() && now - tokenIssuedMillis >= 450000L) {
                           lastIndexMillis = now;
                           lastIndexX = player.getX();
                           lastIndexZ = player.getZ();
                           LOGGER.debug("Chunksmith: renewing the backchannel token before this travel refresh");
                           sendHello(false);
                        } else {
                           requestIndex(activeDimension);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean dimensionTick() {
      String now = CsLodDimension.current();
      if (now.isEmpty()) {
         return false;
      }

      if (now.equals(playerDimension)) {
         return false;
      }

      String from = playerDimension;
      playerDimension = now;
      activeDimension = "";
      inBandPending = false;
      inBandRoot = null;
      inBandDimension = "";
      inBandRegions = List.of();
      inBandManifest = null;
      lastIndex = List.of();
      lastSyncMillis = 0L;
      PARTIAL.clear();
      CsLodDownloader current = downloader;
      if (current != null) {
         current.cancel();
      }

      if (busy.get()) {
         send(CsLodMessages.cancel());
      }

      downloader = null;
      busy.set(false);
      lastIndexMillis = 0L;
      if (!capsVoxy && !capsDh) {
         return true;
      }

      if (from.isEmpty()) {
         return true;
      }

      LOGGER.info(
         "Chunksmith: the player moved from {} to {}. The LOD data for {} is a DIFFERENT world, so asking the server what it has for {}",
         new Object[]{from, now, from, now}
      );
      awaitingStore = false;
      RETRY.reset();
      sendHello(true);
      return true;
   }

   private static void syncTick() {
      if (CsLodClientConfig.isLoaded()) {
         long now = System.currentTimeMillis();
         if (now - lastSyncMillis >= CsLodClientConfig.syncIntervalMillis()) {
            lastSyncMillis = now;

            try {
               send(CsLodMessages.requestSummary(activeDimension));
            } catch (IOException e) {
               LOGGER.warn("Chunksmith: failed to ask the server for a LOD summary: {}", e.toString());
            }
         }
      }
   }

   private static void summary(CsLodMessages.RegionSummary summary) {
      String dimension = summary.dimension();
      if (dimension.equals(activeDimension)) {
         if (!singleplayerLiveFeed()) {
            Path root = storeRoot();
            Path dir = CsLodStore.dimensionDir(root, dimension);
            if (dir == null) {
               LOGGER.warn("Chunksmith: server sent a malformed dimension id in a LOD summary; ignoring it");
            } else {
               List<CsLodMessages.RegionEntry> mine = lastIndex;
               Thread worker = new Thread(
                  () -> {
                     CsLodManifest manifest = CsLodManifest.open(root, dimension);
                     if (manifest != null) {
                        CsLodSummary.Snapshot ours = manifest.fold(dir, mine);
                        int theirCount = summary.count();
                        long theirAggregate = summary.aggregate();

                        for (CsLodMessages.RegionEntry skipped : declinedFor(dimension)) {
                           theirCount--;
                           theirAggregate = CsLodDiskBudget.foldOut(theirAggregate, skipped);
                        }

                        if (ours.count() == theirCount && ours.aggregate() == theirAggregate) {
                           LOGGER.debug("Chunksmith: LOD sync, nothing has changed ({} regions of {})", ours.count(), dimension);
                        } else {
                           LOGGER.debug(
                              "Chunksmith: LOD sync -- {} no longer matches the server (it has {} regions in my radius, I can vouch for {}). Pulling the index and fetching only the difference. No relog, and I did not have to move.",
                              new Object[]{dimension, summary.count(), ours.count()}
                           );
                           Minecraft.getInstance().execute(() -> {
                              if (dimension.equals(activeDimension)) {
                                 requestIndex(dimension);
                              }
                           });
                        }
                     }
                  },
                  "chunksmith-lod-sync"
               );
               worker.setDaemon(true);
               worker.start();
            }
         }
      }
   }

   private static void silenceTick() {
      if (!helloAnswered && !silenceReported && helloSentMillis != 0L && !host.isEmpty()) {
         if (capsVoxy || capsDh) {
            if (System.currentTimeMillis() - helloSentMillis >= 10000L) {
               silenceReported = true;
               LOGGER.info(
                  "Chunksmith: no LOD data is being offered by this server (it did not answer our hello within {}s). Either it does not run Chunksmith at all -- which is normal, and nothing is wrong -- or it runs one old enough not to recognise our hello (3.1.0-beta-3 or earlier). A server on any later Chunksmith 3 DOES answer, and says so itself. If you expected LOD terrain here, the server and every client must both be on Chunksmith 4 or newer, which is what speaks LOD protocol v{}.",
                  10L,
                  4
               );
            }
         }
      }
   }

   private static void retryTick() {
      if (awaitingStore) {
         if (!host.isEmpty()) {
            long now = System.currentTimeMillis();
            if (RETRY.due(now)) {
               RETRY.attempted(now);
               String line = "Chunksmith: the server still has no LOD data (asked "
                  + RETRY.attempts()
                  + " more time(s)); asking again in "
                  + RETRY.delayMillis() / 1000L
                  + "s";
               if (RETRY.attempts() <= 3) {
                  LOGGER.info(line);
               } else {
                  LOGGER.debug(line);
               }

               sendHello(false);
            }
         }
      }
   }

   private static void hello() {
      boolean voxy = Renderers.hasVoxy();
      boolean dh = Renderers.hasDh();
      Minecraft client = Minecraft.getInstance();
      if (client.getCurrentServer() != null) {
         host = client.getCurrentServer().ip;
         int colon = host.lastIndexOf(58);
         if (colon > 0) {
            host = host.substring(0, colon);
         }
      }

      capsVoxy = voxy;
      capsDh = dh;
      capsRadius = Renderers.configuredRadiusBlocks();
      LOGGER.info("Chunksmith: {}", CsLodClientConfig.load(ClientPlatform.gameDir().resolve("config")));
      if (!voxy && !dh) {
         LOGGER.info(
            "Chunksmith: hello, no LOD renderer installed (voxy / Distant Horizons), so no LOD data will be requested or drawn. Saying hello anyway so that /cslod set can reach this client's settings."
         );
      } else {
         LOGGER.info("Chunksmith: saying hello with voxy={} dh={} radius={} blocks", new Object[]{voxy, dh, capsRadius});
      }

      if (dh) {
         LOGGER.info("Chunksmith: feeding {}", DhTarget.version());
      }

      sendHello(true);
   }

   private static void sendHello(boolean first) {
      try {
         send(CsLodMessages.encode(new CsLodMessages.ClientHello(4, capsVoxy, capsDh, capsRadius)));
         if (first) {
            RETRY.started(System.currentTimeMillis());
            helloSentMillis = System.currentTimeMillis();
         }
      } catch (IOException e) {
         LOGGER.warn("Chunksmith: failed to say hello: " + e);
      }
   }

   private static void handle(byte[] data) {
      if (data.length != 0) {
         try (DataInputStream in = CsLodMessages.reader(data)) {
            byte id = in.readByte();
            switch (id) {
               case 101:
                  serverHello(CsLodMessages.decodeServerHello(in));
                  break;
               case 102:
                  index(CsLodMessages.decodeRegionIndex(in));
                  break;
               case 103:
                  slice(CsLodMessages.decodeRegionSlice(in));
                  break;
               case 104:
                  inBandPending = false;
                  LOGGER.info("Chunksmith: in-band transfer complete");
                  CsLodManifest manifest = inBandManifest;
                  if (manifest != null) {
                     try {
                        manifest.save();
                     } catch (IOException e) {
                        LOGGER.warn("Chunksmith: could not write the region manifest ({}); these regions will be re-fetched next session", e.toString());
                     }
                  }

                  if (inBandRoot != null) {
                     injectAsync(inBandRoot, inBandDimension, inBandRegions);
                  } else {
                     busy.set(false);
                  }
                  break;
               case 105:
                  summary(CsLodMessages.decodeRegionSummary(in));
                  break;
               case 106:
                  CsLodLegacySettings.handle(CsLodMessages.decodeClientSetting(in));
                  break;
               default:
                  LOGGER.debug("Chunksmith: unhandled LOD message " + id);
            }
         } catch (IOException e) {
            LOGGER.warn("Chunksmith: malformed LOD message: " + e);
         }
      }
   }

   private static void serverHello(CsLodMessages.ServerHello hello) {
      helloAnswered = true;
      if (hello.protocolVersion() != 4) {
         String line = "[chunksmith] LOD is off: this server speaks LOD protocol v"
            + hello.protocolVersion()
            + " and this client speaks v4. Chunksmith 4 changed the protocol, so the server and the client must BOTH be on Chunksmith 4 or newer for distant terrain to load. Everything else in Chunksmith still works.";
         LOGGER.warn(line);
         if (!mismatchTold) {
            mismatchTold = true;
            chat(line);
         }
      } else if (!capsVoxy && !capsDh) {
         LOGGER.debug("Chunksmith: the server answered our hello; with no renderer installed nothing will be fetched, but /cslod set can now reach this client");
      } else {
         if (host.isEmpty() && spPreexisting == null) {
            spPreexisting = hello.storeAvailable() ? List.copyOf(hello.dimensions()) : List.of();
         }

         if (hello.storeAvailable() && !hello.dimensions().isEmpty()) {
            token = hello.token();
            tokenIssuedMillis = System.currentTimeMillis();
            backchannelPort = hello.backchannelPort();
            advertisedBackchannelPort = backchannelPort;
            advertisedHost = hello.advertisedHost() == null ? "" : hello.advertisedHost();
            String offered = hello.worldId() == null ? "" : hello.worldId();
            worldId = CsLodWorldId.isValid(offered) ? offered : "";
            serverDimensions = hello.dimensions();
            String mine = CsLodDimension.current();
            if (!mine.isEmpty()) {
               playerDimension = mine;
               if (!serverDimensions.contains(mine)) {
                  if (!awaitingStore) {
                     awaitingStore = true;
                     LOGGER.info(
                        "Chunksmith: the server has LOD data for {}, but nothing for {}, the dimension you are in. Nothing will be drawn here (data from another dimension is NOT a substitute). Checking again as you play.",
                        serverDimensions,
                        mine
                     );
                  }

                  activeDimension = "";
               } else {
                  if (awaitingStore) {
                     LOGGER.info("Chunksmith: the server NOW has LOD data for {}. Fetching it (after {} check(s), with no relog)", mine, RETRY.attempts());
                     awaitingStore = false;
                     RETRY.reset();
                  }

                  boolean arming = activeDimension.isEmpty();
                  if (backchannelPort == 0) {
                     if (arming) {
                        LOGGER.info("Chunksmith: server has LOD data for {} but no backchannel; using the in-band fallback (slower)", mine);
                     }
                  } else if (arming) {
                     LOGGER.info("Chunksmith: server has LOD data for {}; backchannel on port {}", mine, backchannelPort);
                  }

                  if (host.isEmpty()) {
                     boolean catchUp = spPreexisting != null && spPreexisting.contains(mine) && SP_CAUGHT_UP.add(mine);
                     if (!catchUp && singleplayerLiveFeed()) {
                        activeDimension = mine;
                        tellStoodDown();
                        return;
                     }
                  }

                  activeDimension = mine;
                  requestIndex(mine);
               }
            }
         } else {
            if (!awaitingStore) {
               awaitingStore = true;
               LOGGER.info(
                  "Chunksmith: the server has no pregenerated LOD data yet. Staying connected and checking again (every {}s at first, then every {}s); it will arrive on its own if the operator pregenerates, and you do NOT need to relog.",
                  15L,
                  120L
               );
            }
         }
      }
   }

   private static boolean singleplayerLiveFeed() {
      return host.isEmpty() && capsVoxy && !capsDh && LodSinks.get().feedsRendererDirectly();
   }

   private static CsLodMessages.RegionIndex skipLiveFed(CsLodMessages.RegionIndex index) {
      if (host.isEmpty() && !index.regions().isEmpty()) {
         MinecraftServer own = Minecraft.getInstance().getSingleplayerServer();
         if (own == null) {
            return index;
         }

         Map<Long, Long> ledger = LiveFeedLedger.read(LodSupport.storeRootBase(own).resolve(index.dimension()));
         if (ledger.isEmpty()) {
            return index;
         }

         List<CsLodMessages.RegionEntry> keep = new ArrayList<>();

         for (CsLodMessages.RegionEntry region : index.regions()) {
            if (!LiveFeedLedger.vouches(ledger, region.regionX(), region.regionZ(), region.hash())) {
               keep.add(region);
            }
         }

         int skipped = index.regions().size() - keep.size();
         if (skipped > 0) {
            LOGGER.info(
               "Chunksmith: single player -- {} of {} region(s) of {} were fed to voxy directly as they were generated; catching up on the other {}",
               new Object[]{skipped, index.regions().size(), index.dimension(), keep.size()}
            );
         }

         return new CsLodMessages.RegionIndex(index.dimension(), keep);
      } else {
         return index;
      }
   }

   private static void tellStoodDown() {
      if (!spStoodDownTold) {
         spStoodDownTold = true;
         LOGGER.info(
            "Chunksmith: single player -- this world is feeding voxy directly as it generates, so the LOD download stands down (it would only send voxy what it already has)"
         );
      }
   }

   private static String fetchHost() {
      String named = advertisedHost;
      return named.isEmpty() ? host : named;
   }

   private static void requestIndex(String dimension) {
      if (busy.compareAndSet(false, true)) {
         LocalPlayer player = Minecraft.getInstance().player;
         if (player != null) {
            lastIndexX = player.getX();
            lastIndexZ = player.getZ();
         }

         lastIndexMillis = System.currentTimeMillis();

         try {
            send(CsLodMessages.requestIndex(dimension));
         } catch (IOException e) {
            busy.set(false);
            LOGGER.warn("Chunksmith: failed to request the region index: {}", e.toString());
         }
      }
   }

   private static void index(CsLodMessages.RegionIndex advertised) {
      Path root = storeRoot();
      if (CsLodStore.dimensionDir(root, advertised.dimension()) == null) {
         LOGGER.warn("Chunksmith: server sent a malformed dimension id; ignoring the region index");
         busy.set(false);
      } else {
         lastIndex = advertised.regions();
         lastSyncMillis = System.currentTimeMillis();
         CsLodMessages.RegionIndex index = skipLiveFed(applyDiskBudget(advertised, root));
         if (index.regions().isEmpty()) {
            busy.set(false);
         } else {
            String fetchHost = fetchHost();
            if (backchannelPort != 0 && !token.isEmpty() && !fetchHost.isEmpty()) {
               downloader = new CsLodDownloader(root);
               int port = backchannelPort;
               boolean[] handedOff = new boolean[]{false};
               Thread worker = new Thread(
                  () -> {
                     try {
                        if (!reachable(fetchHost, port)) {
                           LOGGER.warn(
                              "Chunksmith: the backchannel on port {} is advertised but unreachable; falling back to the in-band channel (slower)", port
                           );
                           backchannelPort = 0;
                           handedOff[0] = true;
                           Minecraft.getInstance().execute(() -> inBand(index, root));
                           return;
                        }

                        CsLodDownloader current = downloader;
                        current.download(fetchHost, port, token, index, line -> LOGGER.info("Chunksmith: {}", line));
                        if (current.wasCancelled()) {
                           return;
                        }

                        if (current.fetched() != 0L || current.failed() <= 0L) {
                           LodInjector.injectRegions(root, index.dimension(), index.regions(), line -> LOGGER.info("Chunksmith: {}", line));
                           return;
                        }

                        LOGGER.warn(
                           "Chunksmith: the backchannel on port {} accepted a connection but served nothing ({} regions failed); falling back to the in-band channel (slower) and trying the backchannel again at the next sync",
                           port,
                           current.failed()
                        );
                        handedOff[0] = true;
                        Minecraft.getInstance().execute(() -> inBand(index, root));
                     } finally {
                        if (!handedOff[0]) {
                           busy.set(false);
                        }
                     }
                  },
                  "chunksmith-lod-client"
               );
               worker.setDaemon(true);
               worker.start();
            } else {
               inBand(index, root);
            }
         }
      }
   }

   private static void inBandStallTick() {
      if (inBandPending && inBandStalled(inBandProgressMillis, System.currentTimeMillis())) {
         inBandPending = false;
         if (!inBandStallTold) {
            inBandStallTold = true;
            LOGGER.warn(
               "Chunksmith: the server sent no in-band LOD data in {}s (a Bukkit/Paper server cannot send LOD that way, only over its backchannel port); stopping, and the backchannel will be tried again at the next sync",
               60L
            );
         }

         if (backchannelPort == 0) {
            backchannelPort = advertisedBackchannelPort;
         }

         inBandRoot = null;
         inBandDimension = "";
         inBandRegions = List.of();
         inBandManifest = null;
         PARTIAL.clear();
         busy.set(false);
      }
   }

   static boolean inBandStalled(long lastProgressMillis, long nowMillis) {
      return nowMillis - lastProgressMillis >= 60000L;
   }

   private static boolean reachable(String address, int port) {
      try (Socket socket = new Socket()) {
         socket.connect(new InetSocketAddress(address, port), 2000);
         return true;
      } catch (IOException e) {
         return false;
      }
   }

   private static void inBand(CsLodMessages.RegionIndex index, Path root) {
      inBandRoot = root;
      inBandDimension = index.dimension();
      inBandRegions = index.regions();
      CsLodManifest manifest = CsLodManifest.open(root, index.dimension());
      inBandManifest = manifest;
      List<CsLodMessages.RegionEntry> wanted = new ArrayList<>();

      for (CsLodMessages.RegionEntry entry : index.regions()) {
         if (!CsLodCache.have(root, index.dimension(), manifest, entry)) {
            wanted.add(entry);
         }
      }

      LOGGER.info(
         "Chunksmith: in-band fetch. {} regions within my radius, {} already cached, {} to fetch (this is the slow path)",
         new Object[]{index.regions().size(), index.regions().size() - wanted.size(), wanted.size()}
      );
      if (wanted.isEmpty()) {
         injectAsync(root, index.dimension(), index.regions());
      } else {
         try {
            inBandProgressMillis = System.currentTimeMillis();
            inBandPending = true;
            send(CsLodMessages.requestRegions(index.dimension(), wanted));
         } catch (IOException e) {
            inBandPending = false;
            busy.set(false);
            LOGGER.warn("Chunksmith: failed to request in-band regions: {}", e.toString());
         }
      }
   }

   private static void slice(CsLodMessages.RegionSlice slice) {
      Path root = inBandRoot;
      if (root != null) {
         inBandProgressMillis = System.currentTimeMillis();
         String key = slice.regionX() + "." + slice.regionZ();
         ByteArrayOutputStream buffer = PARTIAL.computeIfAbsent(key, ignored -> new ByteArrayOutputStream());
         buffer.writeBytes(slice.data());
         if (slice.last()) {
            PARTIAL.remove(key);

            try {
               Path dimDir = CsLodStore.dimensionDir(root, slice.dimension());
               if (dimDir == null) {
                  LOGGER.warn("Chunksmith: dropping an in-band slice with a malformed dimension id");
                  return;
               }

               Path target = dimDir.resolve("r." + slice.regionX() + "." + slice.regionZ() + ".cslod");
               Files.createDirectories(target.getParent());
               Path temp = target.resolveSibling(target.getFileName() + ".part");
               Files.write(temp, buffer.toByteArray());
               Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
               CsLodManifest manifest = inBandManifest;
               CsLodMessages.RegionEntry advertised = advertised(slice.regionX(), slice.regionZ());
               if (manifest != null && advertised != null) {
                  manifest.put(slice.regionX(), slice.regionZ(), advertised.hash(), Files.size(target));
               }
            } catch (IOException e) {
               LOGGER.warn("Chunksmith: failed to store in-band region {}: {}", key, e.toString());
            }
         }
      }
   }

   private static CsLodMessages.RegionEntry advertised(int regionX, int regionZ) {
      for (CsLodMessages.RegionEntry entry : inBandRegions) {
         if (entry.regionX() == regionX && entry.regionZ() == regionZ) {
            return entry;
         }
      }

      return null;
   }

   private static void injectAsync(Path root, String dimension, List<CsLodMessages.RegionEntry> regions) {
      Thread worker = new Thread(() -> {
         try {
            LodInjector.injectRegions(root, dimension, regions, line -> LOGGER.info("Chunksmith: {}", line));
         } finally {
            busy.set(false);
         }
      }, "chunksmith-lod-inject");
      worker.setDaemon(true);
      worker.start();
   }

   public static void cancel() {
      CsLodDownloader current = downloader;
      if (current != null) {
         current.cancel();
         send(CsLodMessages.cancel());
      }
   }

   public static String describe() {
      CsLodDownloader current = downloader;
      return current == null ? "idle" : current.describe();
   }

   public static boolean reinjectNow() {
      if (activeDimension.isEmpty()) {
         return false;
      }

      LodInjector.reset();
      requestIndex(activeDimension);
      return true;
   }

   private static CsLodMessages.RegionIndex applyDiskBudget(CsLodMessages.RegionIndex index, Path root) {
      long budget = CsLodClientConfig.maxDiskBytes();
      String dimension = index.dimension();
      if (budget <= 0L) {
         synchronized (DECLINED) {
            DECLINED.remove(dimension);
            return index;
         }
      } else {
         Path dir = CsLodStore.dimensionDir(root, dimension);
         CsLodDiskBudget.Split split = CsLodDiskBudget.apply(index.regions(), sizeOnDisk(dir), budget);
         synchronized (DECLINED) {
            if (split.declined().isEmpty()) {
               DECLINED.remove(dimension);
            } else {
               DECLINED.put(dimension, List.copyOf(split.declined()));
            }
         }

         if (split.cappedAnything()) {
            LOGGER.info(
               "Chunksmith: disk ceiling reached for {} -- keeping {} region(s), skipping {} ({} MB). Nearest terrain is kept first; raise or clear it with /csclient set {} <mb>.",
               new Object[]{dimension, split.keep().size(), split.declined().size(), split.declinedBytes() / 1048576L, "max-disk-mb"}
            );
         }

         return new CsLodMessages.RegionIndex(dimension, split.keep());
      }
   }

   private static List<CsLodMessages.RegionEntry> declinedFor(String dimension) {
      synchronized (DECLINED) {
         return DECLINED.getOrDefault(dimension, List.of());
      }
   }

   private static long sizeOnDisk(Path dir) {
      if (dir != null && Files.isDirectory(dir)) {
         try (Stream<Path> walk = Files.walk(dir)) {
            return walk.filter(x$0 -> Files.isRegularFile(x$0)).mapToLong(path -> {
               try {
                  return Files.size(path);
               } catch (IOException e) {
                  return 0L;
               }
            }).sum();
         } catch (IOException e) {
            return Long.MAX_VALUE;
         }
      } else {
         return 0L;
      }
   }

   public static Path storePath() {
      return storeRoot();
   }

   public static List<String> status() {
      List<String> out = new ArrayList<>();
      Path store = storeRoot();
      String fetch = fetchHost();
      boolean multiplayer = !host.isEmpty();
      out.add(
         "mode:         "
            + (
               multiplayer
                  ? "multiplayer (receiving LOD from a server)"
                  : "single player -- the renderer builds LOD locally as Chunksmith generates; nothing is downloaded, so the transport fields below are omitted"
            )
      );
      out.add("server:       " + (multiplayer ? host : "(not connected)"));
      if (!advertisedHost.isEmpty() && !advertisedHost.equals(host)) {
         out.add("fetching from: " + fetch + " (server advertised it)");
      }

      String idLine;
      if (!worldId.isEmpty()) {
         idLine = worldId;
      } else if (host.isEmpty()) {
         idLine = "(not connected -- nothing to key a store on)";
      } else {
         idLine = "(none -- this server predates 4.0.0, so the store is keyed by address)";
      }

      out.add("world id:     " + idLine);
      out.add("store:        " + store);
      out.add("dimension:    " + (activeDimension.isEmpty() ? "(none)" : activeDimension));
      if (multiplayer) {
         long[] counted = countStore(store);
         out.add("regions:      " + counted[0] + " file(s) on disk");
         out.add("size:         " + counted[1] / 1024L + " KB");
      }

      if (multiplayer) {
         out.add("transport:    " + (backchannelPort == 0 ? "in-band (no backchannel)" : "backchannel " + backchannelPort));
         out.add("token:        " + (token.isEmpty() ? "none" : "held"));
      }

      out.add("renderers:    voxy=" + (capsVoxy ? "yes" : "no") + " dh=" + (capsDh ? "yes" : "no"));
      out.add("queue:        " + LodSinks.get().queueDepth() + " chunk(s) awaiting the renderer");
      out.add("radius:       " + (capsRadius <= 0 ? "(unknown)" : capsRadius + " blocks"));
      if (multiplayer) {
         out.add("injected:     " + LodInjector.describe());
         out.add("download:     " + describe());
      }

      for (CsLodClientSettings.Setting setting : CsLodClientSettings.all()) {
         out.add(setting.name() + ": " + setting.read());
      }

      List<String> orphans = legacyStores(store);
      if (!orphans.isEmpty()) {
         out.add("pre-4.0 stores: " + String.join(", ", orphans) + " (unused; /csclient reset clears them)");
      }

      return out;
   }

   private static long[] countStore(Path store) {
      if (!Files.isDirectory(store)) {
         return new long[]{0L, 0L};
      }

      long files = 0L;
      long bytes = 0L;

      try (Stream<Path> walk = Files.walk(store)) {
         for (Path path : walk.filter(x$0 -> Files.isRegularFile(x$0))::iterator) {
            files++;

            try {
               bytes += Files.size(path);
            } catch (IOException var10) {
            }
         }
      } catch (IOException e) {
         return new long[]{-1L, -1L};
      }

      return new long[]{files, bytes};
   }

   private static List<String> legacyStores(Path current) {
      List<String> out = new ArrayList<>();
      Path lod = ClientPlatform.gameDir().resolve("chunksmith").resolve("lod");
      if (!Files.isDirectory(lod)) {
         return out;
      }

      try (Stream<Path> entries = Files.list(lod)) {
         for (Path path : entries.filter(x$0 -> Files.isDirectory(x$0))::iterator) {
            String name = path.getFileName().toString();
            if (!path.equals(current) && !CsLodWorldId.isValid(name)) {
               out.add(name);
            }
         }
      } catch (IOException var9) {
      }

      return out;
   }

   public static long resetAll() {
      Path store = storeRoot();
      LodInjector.stop();
      LodInjector.reset();
      long removed = deleteTree(store);
      reset();
      hello();
      return removed;
   }

   private static long deleteTree(Path dir) {
      if (!Files.isDirectory(dir)) {
         return 0L;
      }

      long removed = 0L;

      try (Stream<Path> walk = Files.walk(dir)) {
         List<Path> paths = new ArrayList<>();
         walk.forEach(paths::add);
         paths.sort(Comparator.reverseOrder());

         for (Path path : paths) {
            try {
               if (Files.deleteIfExists(path)) {
                  removed++;
               }
            } catch (IOException e) {
               LOGGER.warn("Chunksmith: could not remove {} during reset: {}", path, e.toString());
            }
         }

         return removed;
      } catch (IOException e) {
         LOGGER.warn("Chunksmith: could not walk {} during reset: {}", dir, e.toString());
         return -1L;
      }
   }

   private static void reset() {
      cancel();
      downloader = null;
      token = "";
      tokenIssuedMillis = 0L;
      backchannelPort = 0;
      advertisedBackchannelPort = 0;
      inBandPending = false;
      inBandStallTold = false;
      host = "";
      advertisedHost = "";
      worldId = "";
      activeDimension = "";
      playerDimension = "";
      serverDimensions = List.of();
      awaitingStore = false;
      RETRY.reset();
      capsVoxy = false;
      capsDh = false;
      capsRadius = 0;
      busy.set(false);
      lastIndexMillis = 0L;
      lastSyncMillis = 0L;
      lastIndex = List.of();
      helloSentMillis = 0L;
      helloAnswered = false;
      mismatchTold = false;
      silenceReported = false;
      inBandRoot = null;
      inBandDimension = "";
      inBandRegions = List.of();
      inBandManifest = null;
      PARTIAL.clear();
      spPreexisting = null;
      SP_CAUGHT_UP.clear();
      spStoodDownTold = false;
      LodInjector.stop();
      LodInjector.reset();
   }

   private static Path storeRoot() {
      Path lod = ClientPlatform.gameDir().resolve("chunksmith").resolve("lod");
      if (!worldId.isEmpty()) {
         return lod.resolve(worldId);
      }

      String key = host.isEmpty() ? "unknown" : host.replaceAll("[^a-zA-Z0-9._-]", "_");
      return lod.resolve(key);
   }

   public static void chat(String line) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         say(player, Component.literal(line));
      }
   }

   private static void say(LocalPlayer player, Component line) {
      player.displayClientMessage(line, false);
   }

   private static void send(byte[] data) {
      ClientPlatform.sendToServer(data);
   }
}
