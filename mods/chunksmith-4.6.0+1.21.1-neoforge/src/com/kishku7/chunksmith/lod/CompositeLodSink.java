package com.kishku7.chunksmith.lod;

import java.util.List;

public final class CompositeLodSink implements LodSink {
   private final List<LodSink> sinks;

   public CompositeLodSink(List<LodSink> sinks) {
      this.sinks = List.copyOf(sinks);
   }

   public List<LodSink> getSinks() {
      return this.sinks;
   }

   @Override
   public boolean offer(Object chunk) {
      boolean accepted = true;

      for (LodSink sink : this.sinks) {
         if (!sink.offer(chunk)) {
            accepted = false;
         }
      }

      return accepted;
   }

   @Override
   public int queueDepth() {
      int depth = 0;

      for (LodSink sink : this.sinks) {
         depth = Math.max(depth, sink.queueDepth());
      }

      return depth;
   }

   @Override
   public boolean feedsRendererDirectly() {
      for (LodSink sink : this.sinks) {
         if (sink.feedsRendererDirectly()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public String toString() {
      return "CompositeLodSink" + this.sinks;
   }
}
