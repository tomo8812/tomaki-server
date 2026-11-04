package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.TypeIntrinsics;
import org.valkyrienskies.core.api.attachment.AttachmentRegistration;
import org.valkyrienskies.core.api.attachment.AttachmentSerializer;
import org.valkyrienskies.core.api.attachment.JacksonAttachmentSerializer;
import org.valkyrienskies.core.api.attachment.LegacyAttachmentSerializer;
import org.valkyrienskies.core.api.attachment.TransientAttachmentSerializer;

public final class CF<T> implements AttachmentRegistration.Builder<T> {
   private final Class<T> a;
   private String b;
   private AttachmentSerializer c;
   private CL d;

   public CF(Class<T> var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      super();
      this.a = var1;
      String var10001 = this.getAttachmentClass().getName();
      Intrinsics.checkNotNullExpressionValue(var10001, "");
      this.b = var10001;
      this.c = JacksonAttachmentSerializer.INSTANCE;
   }

   @Override
   public final Class<T> getAttachmentClass() {
      return this.a;
   }

   @Override
   public final String getKey() {
      return this.b;
   }

   @Override
   public final void setKey(String <set-?>) {
      Intrinsics.checkNotNullParameter(var1/* $VF was: <set-?> */, "");
      this.b = var1/* $VF was: <set-?> */;
   }

   @Override
   public final AttachmentSerializer getSerializer() {
      return this.c;
   }

   @Override
   public final void setSerializer(AttachmentSerializer <set-?>) {
      Intrinsics.checkNotNullParameter(var1/* $VF was: <set-?> */, "");
      this.c = var1/* $VF was: <set-?> */;
   }

   private CL a() {
      return this.d;
   }

   private void a(CL var1) {
      this.d = var1;
   }

   private CF<T> b() {
      CF var1 = this;
      this.setSerializer(JacksonAttachmentSerializer.INSTANCE);
      return var1;
   }

   private CF<T> c() {
      CF var1 = this;
      this.setSerializer(LegacyAttachmentSerializer.INSTANCE);
      return var1;
   }

   private CF<T> d() {
      CF var1 = this;
      this.setSerializer(TransientAttachmentSerializer.INSTANCE);
      return var1;
   }

   private <NEW> CF<T> a(Class<NEW> var1, Function1<? super T, ? extends NEW> var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      CF var3 = this;
      this.d = new CL(this.getAttachmentClass(), var1, (Function1<Object, ? extends Object>)TypeIntrinsics.beforeCheckcastToFunctionOfArity(var2, 1));
      return var3;
   }

   @Override
   public final AttachmentRegistration<T> build() {
      return new CG<>(this.getAttachmentClass(), this.getKey(), this.getSerializer(), this.d);
   }

   // $VF: synthetic method
   @Override
   public final AttachmentRegistration.Builder useJacksonSerializer() {
      CF var1 = this;
      CF var2 = this;
      var1.setSerializer(JacksonAttachmentSerializer.INSTANCE);
      return var2;
   }

   // $VF: synthetic method
   @Override
   public final AttachmentRegistration.Builder useLegacySerializer() {
      CF var1 = this;
      CF var2 = this;
      var1.setSerializer(LegacyAttachmentSerializer.INSTANCE);
      return var2;
   }

   // $VF: synthetic method
   @Override
   public final AttachmentRegistration.Builder useTransientSerializer() {
      CF var1 = this;
      CF var2 = this;
      var1.setSerializer(TransientAttachmentSerializer.INSTANCE);
      return var2;
   }

   // $VF: synthetic method
   @Override
   public final AttachmentRegistration.Builder useUpdater(Class newAttachmentClass, Function1 updater) {
      Function1 var3 = updater;
      Class var6 = newAttachmentClass;
      CF var5 = this;
      Intrinsics.checkNotNullParameter(var6, "");
      Intrinsics.checkNotNullParameter(var3, "");
      CF var4 = var5;
      var5.d = new CL(var5.getAttachmentClass(), var6, (Function1<Object, ? extends Object>)TypeIntrinsics.beforeCheckcastToFunctionOfArity(var3, 1));
      return var4;
   }
}
