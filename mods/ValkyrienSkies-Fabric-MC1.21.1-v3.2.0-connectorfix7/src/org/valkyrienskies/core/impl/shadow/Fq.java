package org.valkyrienskies.core.impl.shadow;

import io.netty.buffer.ByteBuf;

public interface Fq<T> {
   ByteBuf a(T var1, T var2, ByteBuf var3);

   T a(T var1, ByteBuf var2);
}
