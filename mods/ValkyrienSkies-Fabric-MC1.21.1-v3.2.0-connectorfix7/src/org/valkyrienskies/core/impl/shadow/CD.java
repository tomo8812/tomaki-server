package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.api.attachment.AttachmentSerializer;

public final class CD {
   final Class<?> a;
   final String b;
   final AttachmentSerializer c;
   final CL d;

   public CD(Class<?> var1, String var2, AttachmentSerializer var3, CL var4) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var3, "");
      super();
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = var4;
   }

   public final Class<?> a() {
      return this.a;
   }

   public final String b() {
      return this.b;
   }

   public final AttachmentSerializer c() {
      return this.c;
   }

   public final CL d() {
      return this.d;
   }
}
