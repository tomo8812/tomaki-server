package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.api.attachment.AttachmentRegistration;
import org.valkyrienskies.core.api.attachment.AttachmentSerializer;

public final class CG<T> implements AttachmentRegistration<T> {
   private final Class<T> b;
   private final String c;
   private final AttachmentSerializer d;
   final CL a;

   public CG(Class<T> var1, String var2, AttachmentSerializer var3, CL var4) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var3, "");
      super();
      this.b = var1;
      this.c = var2;
      this.d = var3;
      this.a = var4;
   }

   @Override
   public final Class<T> getAttachmentClass() {
      return this.b;
   }

   @Override
   public final String getKey() {
      return this.c;
   }

   @Override
   public final AttachmentSerializer getSerializer() {
      return this.d;
   }

   public final CL a() {
      return this.a;
   }
}
