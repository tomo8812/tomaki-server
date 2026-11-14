package dev.diorama.render.gpu;

public interface MapTexture extends AutoCloseable {
   static MapTexture create(String label, int size, int levels) {
      return new GlIdTexture(size, levels);
   }

   static MapTexture createTarget(String label, int size) {
      return new GlIdTexture(size, 1);
   }

   void write(int var1, int var2, int var3, int var4, int[] var5);

   @Override
   void close();
}
