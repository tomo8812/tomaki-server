package com.kishku7.chunksmith.lod;

public interface LodSink {
   LodSink NOOP = new LodSink() {
      @Override
      public boolean offer(Object chunk) {
         return true;
      }

      @Override
      public int queueDepth() {
         return 0;
      }

      @Override
      public String toString() {
         return "LodSink.NOOP";
      }
   };

   boolean offer(Object var1);

   int queueDepth();

   default boolean feedsRendererDirectly() {
      return false;
   }
}
