package com.kishku7.chunksmith.iterator;

import com.kishku7.chunksmith.Selection;
import com.kishku7.chunksmith.util.ChunkCoordinate;
import com.kishku7.chunksmith.util.Input;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public final class CsvChunkIterator implements ChunkIterator {
   private final Queue<ChunkCoordinate> chunks;
   private final long total;
   private final String name;

   public CsvChunkIterator(Selection selection, long count) {
      this(selection);

      for (int i = 0; i < count && this.hasNext(); i++) {
         this.chunks.poll();
      }
   }

   public CsvChunkIterator(Selection selection) {
      Path filePath = selection.pattern()
         .getValue()
         .map(value -> selection.chunky().getConfig().getDirectory().resolve(String.format("%s.csv", value)))
         .orElse(null);
      this.chunks = new LinkedList<>();
      AtomicLong valid = new AtomicLong();
      if (filePath != null) {
         try (Stream<String> lines = Files.lines(filePath)) {
            lines.forEach(line -> {
               String[] split = line.split(",");
               if (split.length > 1) {
                  Optional<Integer> x = Input.tryInteger(split[0]);
                  Optional<Integer> z = Input.tryInteger(split[1]);
                  if (x.isPresent() && z.isPresent()) {
                     this.chunks.add(new ChunkCoordinate(x.get(), z.get()));
                     valid.incrementAndGet();
                  }
               }
            });
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

      this.total = valid.get();
      this.name = selection.pattern().toString();
   }

   @Override
   public boolean hasNext() {
      return !this.chunks.isEmpty();
   }

   public ChunkCoordinate next() {
      if (this.chunks.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.chunks.poll();
      }
   }

   @Override
   public long total() {
      return this.total;
   }

   @Override
   public String name() {
      return this.name;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object next() {
      return this.next();
   }
}
