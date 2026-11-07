package maki.chunksmithcache;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

final class RecoveryClaims {
   private static final Set<Path> READY = ConcurrentHashMap.newKeySet();
   private static final ConcurrentHashMap<Path, CompletableFuture<Void>> PREPARING = new ConcurrentHashMap<>();

   private RecoveryClaims() {
   }

   static Path root(Path dimension) {
      return dimension.resolve(".promotions");
   }

   static boolean claimExists(Path claim) throws IOException {
      Path key = claim.toAbsolutePath().normalize();

      try {
         BasicFileAttributes attributes = Files.readAttributes(key, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
         if (!attributes.isRegularFile()) {
            throw new IOException("Promotion claim path is not a regular file: " + key);
         } else {
            return true;
         }
      } catch (NoSuchFileException missing) {
         return false;
      }
   }

   static CompletableFuture<Void> prepare(Path dimension, Executor executor) {
      Path key = dimension.toAbsolutePath().normalize();
      CompletableFuture<Void> result = new CompletableFuture<>();
      CompletableFuture<Void> previous = PREPARING.putIfAbsent(key, result);
      if (previous != null) {
         return previous;
      }

      result.whenComplete((ignored, error) -> {
         if (error != null) {
            PREPARING.remove(key, result);
         }
      });

      try {
         executor.execute(() -> {
            try {
               initialize(key);
               result.complete(null);
            } catch (IOException error) {
               result.completeExceptionally(new UncheckedIOException("Cannot recover chunk-cache promotion ownership at " + key, error));
            } catch (RuntimeException error) {
               result.completeExceptionally(error);
            }
         });
      } catch (RuntimeException error) {
         result.completeExceptionally(error);
      }

      return result;
   }

   static void initialize(Path dimension) throws IOException {
      final Path key = dimension.toAbsolutePath().normalize();

      try (CachePathLocks.Guard ignored = CachePathLocks.lock(root(key))) {
         if (READY.contains(key)) {
            return;
         }

         if (Files.isDirectory(key)) {
            Files.walkFileTree(
               key,
               new SimpleFileVisitor<Path>() {
                  public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attrs) {
                     return directory.equals(RecoveryClaims.root(key)) ? FileVisitResult.SKIP_SUBTREE : FileVisitResult.CONTINUE;
                  }

                  public FileVisitResult visitFile(Path source, BasicFileAttributes attrs) throws IOException {
                     String name = source.getFileName().toString();
                     if (attrs.isRegularFile()
                        && name.matches("c\\.-?\\d+\\.-?\\d+\\.nbt\\.gz\\.in-use")
                        && source.getParent().getFileName().toString().matches("r\\.-?\\d+\\.-?\\d+")) {
                        Path target = RecoveryClaims.root(key).resolve(source.getParent().getFileName()).resolve(name);

                        try (CachePathLocks.Guard claimLock = CachePathLocks.lock(target)) {
                           Files.createDirectories(target.getParent());
                           if (Files.exists(target)) {
                              target = RecoveryClaims.root(key).resolve(".conflicts").resolve(UUID.randomUUID().toString()).resolve(name);
                              Files.createDirectories(target.getParent());
                           }

                           Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
                        }

                        return FileVisitResult.CONTINUE;
                     } else {
                        return FileVisitResult.CONTINUE;
                     }
                  }

                  // $VF: synthetic method
                  // $VF: bridge method
                  @Override
                  public FileVisitResult visitFile(Object param1, BasicFileAttributes nullx) throws IOException {
                     return this.visitFile((Path)var1, nullx);
                  }

                  // $VF: synthetic method
                  // $VF: bridge method
                  @Override
                  public FileVisitResult preVisitDirectory(Object param1, BasicFileAttributes nullx) throws IOException {
                     return this.preVisitDirectory((Path)var1, nullx);
                  }
               }
            );
         }

         READY.add(key);
      }
   }

   static void clear(Path dimension) {
      Path key = dimension.toAbsolutePath().normalize();
      PREPARING.remove(key);
      READY.remove(key);
   }
}
