package org.valkyrienskies.physics_api_krunch;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.valkyrienskies.core.impl.shadow.Hm;

public final class KrunchByteBuffer implements Hm {
   private final ByteBuffer a;
   private final long b;
   private final int c;

   public KrunchByteBuffer(int size) {
      this.a = ByteBuffer.allocateDirect(size);
      this.b = getDirectBufferAddress(this.a);
      this.c = size;
      this.a.order(ByteOrder.LITTLE_ENDIAN);
   }

   @Override
   public final ByteBuffer a() {
      return this.a;
   }

   @Override
   public final long b() {
      return this.b;
   }

   @Override
   public final int c() {
      return this.c;
   }

   private static native long getDirectBufferAddress(ByteBuffer var0);
}
