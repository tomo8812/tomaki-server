package com.kishku7.chunksmith.lod.net;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CsLodHttpServer {
   private static final Logger LOGGER = LoggerFactory.getLogger("Chunksmith");
   private static final Pattern REGION_FILE = Pattern.compile("r\\.-?\\d{1,7}\\.-?\\d{1,7}\\.cslod");
   private static final Pattern DIM_DIR = Pattern.compile("[a-z0-9_.-]{1,64}");
   private static final int MAX_CONCURRENT_PER_IP = 6;
   private static final int STOP_GRACE_SECONDS = 2;
   static final long STALL_MILLIS = 30000L;
   private static final long WATCH_PERIOD_MILLIS = 5000L;
   private final CsLodHttpServer.RootResolver roots;
   private final CsLodTokens tokens;
   private final CsLodTokens.OnlineCheck onlineCheck;
   private final Map<String, Integer> inFlightByIp = new ConcurrentHashMap<>();
   private final AtomicLong served = new AtomicLong();
   private final AtomicLong bytes = new AtomicLong();
   private final AtomicLong rejected = new AtomicLong();
   private final AtomicLong stalled = new AtomicLong();
   private final Map<HttpExchange, AtomicLong> sending = new ConcurrentHashMap<>();
   private HttpServer server;
   private ExecutorService pool;
   private ScheduledExecutorService watchdog;
   private long stallMillis = 30000L;
   private long watchPeriodMillis = 5000L;
   private int port;
   private boolean derived = true;

   public CsLodHttpServer(Path storeRoot, CsLodTokens tokens, CsLodTokens.OnlineCheck onlineCheck) {
      this(dimension -> storeRoot.toAbsolutePath().normalize().resolve(dimension), tokens, onlineCheck);
   }

   public CsLodHttpServer(CsLodHttpServer.RootResolver roots, CsLodTokens tokens, CsLodTokens.OnlineCheck onlineCheck) {
      this.roots = roots;
      this.tokens = tokens;
      this.onlineCheck = onlineCheck;
   }

   public int start(String bindAddress, int gamePort, int configuredPort, String configuredBind) {
      String wantedBind = configuredBind != null && !configuredBind.isBlank() ? configuredBind : bindAddress;
      return this.bind(wantedBind, gamePort, configuredPort);
   }

   private int bind(String bindAddress, int gamePort, int configuredPort) {
      this.derived = configuredPort == 0;
      int wanted = CsLodProtocol.httpPort(gamePort, configuredPort);
      if (wanted == 0 && this.derived && gamePort <= 0) {
         LOGGER.info("Chunksmith: this world is not open to the network, so there is no LOD backchannel to run; LOD is sent in-band.");
         return 0;
      }

      if (wanted == 0) {
         if (this.derived) {
            LOGGER.warn(
               "Chunksmith: no room for a LOD backchannel port above "
                  + gamePort
                  + "; falling back to the in-band channel (slower). Set lodBackchannelPort to name a port explicitly."
            );
         } else {
            LOGGER.warn(
               "Chunksmith: lodBackchannelPort "
                  + configuredPort
                  + " cannot be used"
                  + (configuredPort == gamePort ? " because it is the game's own port" : " because it is outside 1024-65535")
                  + "; falling back to the in-band channel (slower)"
            );
         }

         return 0;
      } else {
         try {
            InetSocketAddress address = bindAddress != null && !bindAddress.isBlank()
               ? new InetSocketAddress(InetAddress.getByName(bindAddress), wanted)
               : new InetSocketAddress(wanted);
            this.server = HttpServer.create(address, 32);
            this.pool = Executors.newFixedThreadPool(4, runnable -> {
               Thread thread = new Thread(runnable, "chunksmith-lod-http");
               thread.setDaemon(true);
               return thread;
            });
            this.server.setExecutor(this.pool);
            this.server.createContext("/lod/", this::handle);
            this.server.start();
            this.watchdog = Executors.newSingleThreadScheduledExecutor(runnable -> {
               Thread thread = new Thread(runnable, "chunksmith-lod-http-watchdog");
               thread.setDaemon(true);
               return thread;
            });
            this.watchdog.scheduleWithFixedDelay(this::closeStalled, this.watchPeriodMillis, this.watchPeriodMillis, TimeUnit.MILLISECONDS);
            this.port = wanted;
            LOGGER.info(
               "Chunksmith: LOD backchannel listening on "
                  + address
                  + " (port "
                  + this.port
                  + (this.derived ? ", derived from the game port" : ", set by lodBackchannelPort")
                  + "). Open this port to clients for fast LOD downloads."
            );
            return this.port;
         } catch (IOException e) {
            LOGGER.warn(
               "Chunksmith: the LOD backchannel could not bind port "
                  + wanted
                  + " ("
                  + e.getMessage()
                  + "). Falling back to the in-band channel, which works but is much slower. Either open port "
                  + wanted
                  + ", or set lodBackchannelPort to a port your host does give you (/cs set lodBackchannelPort <port>)."
            );
            this.server = null;
            this.port = 0;
            return 0;
         }
      }
   }

   public void stop() {
      if (this.server != null) {
         this.server.stop(2);
         this.server = null;
      }

      if (this.pool != null) {
         this.pool.shutdownNow();
         this.pool = null;
      }

      if (this.watchdog != null) {
         this.watchdog.shutdownNow();
         this.watchdog = null;
      }

      this.sending.clear();
      this.tokens.clear();
   }

   public long servedCount() {
      return this.served.get();
   }

   public long rejectedCount() {
      return this.rejected.get();
   }

   public int getPort() {
      return this.port;
   }

   public boolean isDerived() {
      return this.derived;
   }

   public String describe() {
      return this.server == null
         ? "backchannel: not running (in-band fallback)"
         : "backchannel: port "
            + this.port
            + (this.derived ? " (derived)" : " (configured)")
            + ", "
            + this.served.get()
            + " files, "
            + this.bytes.get()
            + " bytes, "
            + this.rejected.get()
            + " rejected, "
            + this.stalled.get()
            + " stalled, "
            + this.tokens.size()
            + " live tokens";
   }

   private void handle(HttpExchange exchange) throws IOException {
      String ip = exchange.getRemoteAddress().getAddress().getHostAddress();

      try {
         String method = exchange.getRequestMethod();
         if ("GET".equals(method) || "HEAD".equals(method)) {
            UUID player = this.tokens.validate(exchange.getRequestHeaders().getFirst("X-Chunksmith-Token"), ip, this.onlineCheck);
            if (player == null) {
               this.fail(exchange);
               return;
            }

            Path file = this.resolve(exchange.getRequestURI().getPath());
            if (file != null && Files.isRegularFile(file)) {
               if (this.acquire(ip)) {
                  try {
                     this.sendFile(exchange, file, "HEAD".equals(method));
                     return;
                  } finally {
                     this.release(ip);
                  }
               }

               exchange.sendResponseHeaders(429, -1L);
               return;
            }

            this.fail(exchange);
            return;
         }

         this.fail(exchange);
      } catch (RuntimeException e) {
         LOGGER.warn("Chunksmith: LOD backchannel error: " + e);
         this.fail(exchange);
         return;
      } finally {
         exchange.close();
      }
   }

   private Path resolve(String requestPath) {
      if (requestPath != null && requestPath.startsWith("/lod/")) {
         String relative = requestPath.substring("/lod/".length());
         String[] parts = relative.split("/");
         if (parts.length != 2) {
            return null;
         }

         if (DIM_DIR.matcher(parts[0]).matches() && REGION_FILE.matcher(parts[1]).matches()) {
            Path dimensionRoot = this.roots.rootFor(parts[0]);
            if (dimensionRoot == null) {
               return null;
            }

            Path base = dimensionRoot.toAbsolutePath().normalize();
            Path candidate = base.resolve(parts[1]).toAbsolutePath().normalize();
            return candidate.startsWith(base) ? candidate : null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private void sendFile(HttpExchange exchange, Path file, boolean headOnly) throws IOException {
      long size = Files.size(file);
      long[] range = parseRange(exchange.getRequestHeaders().getFirst("Range"), size);
      exchange.getResponseHeaders().set("Content-Type", "application/octet-stream");
      exchange.getResponseHeaders().set("Accept-Ranges", "bytes");
      long offset = range[0];
      long length = range[1];
      int status;
      if (offset <= 0L && length == size) {
         status = 200;
      } else {
         exchange.getResponseHeaders().set("Content-Range", "bytes " + offset + "-" + (offset + length - 1L) + "/" + size);
         status = 206;
      }

      AtomicLong lastProgress = new AtomicLong(System.currentTimeMillis());
      this.sending.put(exchange, lastProgress);

      label126: {
         try {
            if (!headOnly) {
               exchange.sendResponseHeaders(status, length);

               try (
                  InputStream in = Files.newInputStream(file);
                  OutputStream out = exchange.getResponseBody();
               ) {
                  in.skipNBytes(offset);
                  byte[] buffer = new byte[65536];
                  long remaining = length;

                  while (remaining > 0L) {
                     int read = in.read(buffer, 0, (int)Math.min(buffer.length, remaining));
                     if (read < 0) {
                        break label126;
                     }

                     out.write(buffer, 0, read);
                     lastProgress.set(System.currentTimeMillis());
                     remaining -= read;
                  }
                  break label126;
               }
            }

            exchange.getResponseHeaders().set("Content-Length", Long.toString(length));
            exchange.sendResponseHeaders(status, -1L);
         } finally {
            this.sending.remove(exchange);
         }

         return;
      }

      this.served.incrementAndGet();
      this.bytes.addAndGet(length);
   }

   private void closeStalled() {
      long now = System.currentTimeMillis();

      for (Entry<HttpExchange, AtomicLong> entry : this.sending.entrySet()) {
         if (isStalled(entry.getValue().get(), now, this.stallMillis) && this.sending.remove(entry.getKey(), entry.getValue())) {
            this.stalled.incrementAndGet();
            HttpExchange exchange = entry.getKey();

            String peer;
            try {
               peer = exchange.getRemoteAddress().getAddress().getHostAddress();
            } catch (RuntimeException e) {
               peer = "?";
            }

            LOGGER.warn(
               "Chunksmith: LOD backchannel response to "
                  + peer
                  + " made no progress in "
                  + this.stallMillis / 1000L
                  + "s; closing it so it cannot hold a download thread"
            );

            try {
               exchange.close();
            } catch (RuntimeException var8) {
            }
         }
      }
   }

   static boolean isStalled(long lastProgressMillis, long nowMillis, long stallMillis) {
      return nowMillis - lastProgressMillis >= stallMillis;
   }

   void stallTimingForTest(long stallMillis, long watchPeriodMillis) {
      this.stallMillis = stallMillis;
      this.watchPeriodMillis = watchPeriodMillis;
   }

   private static long[] parseRange(String header, long size) {
      if (header != null && header.startsWith("bytes=") && header.indexOf(44) < 0) {
         String spec = header.substring("bytes=".length()).trim();
         int dash = spec.indexOf(45);
         if (dash < 0) {
            return new long[]{0L, size};
         }

         try {
            String from = spec.substring(0, dash).trim();
            String to = spec.substring(dash + 1).trim();
            if (from.isEmpty()) {
               long suffix = Math.min(Long.parseLong(to), size);
               return new long[]{size - suffix, suffix};
            } else {
               long start = Long.parseLong(from);
               if (start >= 0L && start < size) {
                  long end = to.isEmpty() ? size - 1L : Math.min(Long.parseLong(to), size - 1L);
                  return end < start ? new long[]{0L, size} : new long[]{start, end - start + 1L};
               } else {
                  return new long[]{0L, size};
               }
            }
         } catch (NumberFormatException e) {
            return new long[]{0L, size};
         }
      } else {
         return new long[]{0L, size};
      }
   }

   private boolean acquire(String ip) {
      boolean[] admitted = new boolean[]{false};
      this.inFlightByIp.compute(ip, (key, current) -> {
         int inFlight = current == null ? 0 : current;
         if (inFlight >= 6) {
            return inFlight;
         }

         admitted[0] = true;
         return inFlight + 1;
      });
      return admitted[0];
   }

   private void release(String ip) {
      this.inFlightByIp.computeIfPresent(ip, (key, current) -> current <= 1 ? null : current - 1);
   }

   private void fail(HttpExchange exchange) throws IOException {
      this.rejected.incrementAndGet();
      exchange.sendResponseHeaders(404, -1L);
   }

   @FunctionalInterface
   public interface RootResolver {
      Path rootFor(String var1);
   }
}
