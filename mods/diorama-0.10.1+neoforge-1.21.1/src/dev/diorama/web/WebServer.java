package dev.diorama.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dev.diorama.Diorama;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import org.jspecify.annotations.Nullable;

final class WebServer {
   private final HttpServer server;
   private final ExecutorService workers;

   private WebServer(HttpServer server, ExecutorService workers) {
      this.server = server;
      this.workers = workers;
   }

   static @Nullable WebServer start(Path folder, String address, int port, Predicate<String> closed) {
      try {
         Path root = folder.toAbsolutePath().normalize();
         return start(address, port, exchange -> serve(root, closed, exchange));
      } catch (IOException | RuntimeException e) {
         Diorama.LOGGER
            .warn("Diorama's web map can't serve on port {} ({}); set web-map-port to a free one, or 0 to serve the folder yourself", port, e.toString());
         return null;
      } catch (NoClassDefFoundError e) {
         Diorama.LOGGER.warn("This Java has no built-in web server; serve {} with one of your own", folder);
         return null;
      }
   }

   static WebServer start(String address, int port, HttpHandler handler) throws IOException {
      InetSocketAddress bind = address.isEmpty() ? new InetSocketAddress(port) : new InetSocketAddress(address, port);
      HttpServer server = HttpServer.create(bind, 64);
      server.createContext("/", handler);
      ExecutorService workers = Executors.newFixedThreadPool(2, task -> {
         Thread thread = new Thread(task, "Diorama web map server");
         thread.setDaemon(true);
         return thread;
      });
      server.setExecutor(workers);
      server.start();
      return new WebServer(server, workers);
   }

   int port() {
      return this.server.getAddress().getPort();
   }

   void stop() {
      this.server.stop(0);
      this.workers.shutdownNow();
   }

   private static void serve(Path root, Predicate<String> closed, HttpExchange exchange) throws IOException {
      try {
         try {
            String method = exchange.getRequestMethod();
            if (!method.equals("GET") && !method.equals("HEAD")) {
               send(exchange, 405, "text/plain", null);
               return;
            }

            String path = exchange.getRequestURI().getPath();
            if (path.equals("/") || path.isEmpty()) {
               path = "/index.html";
            }

            Path file = root.resolve(path.substring(1)).normalize();
            String name = file.getFileName() == null ? "" : file.getFileName().toString();
            String type = name.endsWith(".jpg")
               ? "image/jpeg"
               : (name.endsWith(".json") ? "application/json" : (name.endsWith(".html") ? "text/html; charset=utf-8" : null));
            if (!file.startsWith(root)
               || type == null
               || file.getNameCount() > root.getNameCount() + 1 && closed.test(file.getName(root.getNameCount()).toString().toLowerCase(Locale.ROOT))
               || !Files.isRegularFile(file)
               || !file.toRealPath().startsWith(root.toRealPath())) {
               send(exchange, 404, "text/plain", null);
               return;
            }

            Instant modified = Files.getLastModifiedTime(file).toInstant().truncatedTo(ChronoUnit.SECONDS);
            exchange.getResponseHeaders().set("Last-Modified", DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.ofInstant(modified, ZoneOffset.UTC)));
            String query = exchange.getRequestURI().getRawQuery();
            exchange.getResponseHeaders()
               .set("Cache-Control", type.equals("image/jpeg") && query != null && query.startsWith("v=") ? "public, max-age=31536000, immutable" : "no-cache");
            String since = exchange.getRequestHeaders().getFirst("If-Modified-Since");
            if (since != null) {
               try {
                  if (!modified.isAfter(ZonedDateTime.parse(since, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant())) {
                     exchange.sendResponseHeaders(304, -1L);
                     return;
                  }
               } catch (DateTimeParseException var19) {
               }
            }

            byte[] body;
            try {
               body = Files.readAllBytes(file);
            } catch (IOException e) {
               send(exchange, 404, "text/plain", null);
               return;
            }

            send(exchange, 200, type, method.equals("HEAD") ? null : body);
         } catch (IOException | RuntimeException var20) {
         }
      } finally {
         exchange.close();
      }
   }

   static void send(HttpExchange exchange, int code, String type, byte @Nullable [] body) throws IOException {
      exchange.getResponseHeaders().set("Content-Type", type);
      exchange.sendResponseHeaders(code, body == null ? -1L : body.length);
      if (body != null) {
         try (OutputStream out = exchange.getResponseBody()) {
            out.write(body);
         }
      }
   }
}
