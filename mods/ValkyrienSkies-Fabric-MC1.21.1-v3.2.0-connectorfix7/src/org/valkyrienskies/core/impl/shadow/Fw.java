package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import java.io.InputStream;
import java.util.Arrays;
import kotlin.Deprecated;
import kotlin.Unit;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.KClass;
import org.valkyrienskies.core.api.event.RegisteredListener;
import org.valkyrienskies.core.impl.networking.simple.SimplePacket;
import org.valkyrienskies.core.impl.networking.simple.SimplePacketNetworking;
import org.valkyrienskies.core.impl.util.serialization.VSJacksonUtil;
import org.valkyrienskies.core.internal.world.VsiPlayer;

public final class Fw {
   private static final SimplePacketNetworking a = Fe.a.a().b;

   public static final ByteBuf a(SimplePacket var0) {
      Intrinsics.checkNotNullParameter(var0, "");
      ByteBuf var10000 = Unpooled.wrappedBuffer(VSJacksonUtil.INSTANCE.getPacketMapper().writeValueAsBytes(var0));
      Intrinsics.checkNotNullExpressionValue(var10000, "");
      return var10000;
   }

   public static final <T extends SimplePacket> T a(KClass<T> var0, ByteBuf var1) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      ObjectMapper var10000 = VSJacksonUtil.INSTANCE.getPacketMapper();
      ByteBuf var10001 = var1.duplicate();
      Intrinsics.checkNotNullExpressionValue(var10001, "");
      Class var2 = JvmClassMappingKt.getJavaClass(var0);
      var1 = var10001;
      ObjectMapper var3 = var10000;
      Intrinsics.checkNotNullParameter(var10000, "");
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      return var3.readValue((InputStream)(new ByteBufInputStream(var1)), var2);
   }

   @Deprecated(message = "This is global state; please inject a SimplePacketNetworking and use that instead")
   private static <T extends SimplePacket> RegisteredListener a(KClass<T> var0, Function2<? super T, ? super VsiPlayer, Unit> var1) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      SimplePacketNetworking var2 = a;
      return a.registerServerHandler1(var0, var1);
   }

   @Deprecated(message = "This is global state; please inject a SimplePacketNetworking and use that instead")
   private static <T extends SimplePacket> RegisteredListener a(KClass<T> var0, Function1<? super T, Unit> var1) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      SimplePacketNetworking var2 = a;
      return a.registerClientHandler1(var0, var1);
   }

   @Deprecated(message = "This is global state; please inject a SimplePacketNetworking and use that instead")
   private static void b(SimplePacket var0) {
      Intrinsics.checkNotNullParameter(var0, "");
      SimplePacketNetworking var1 = a;
      a.sendToServer1(var0);
   }

   @Deprecated(message = "This is global state; please inject a SimplePacketNetworking and use that instead")
   private static void a(SimplePacket var0, VsiPlayer var1) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      SimplePacketNetworking var2 = a;
      a.sendToClient1(var0, var1);
   }

   @Deprecated(message = "This is global state; please inject a SimplePacketNetworking and use that instead")
   private static void a(SimplePacket var0, VsiPlayer... var1) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      SimplePacketNetworking var2;
      (var2 = a).sendToClients1(var0, Arrays.copyOf(var1, var1.length));
   }

   @Deprecated(message = "This is global state; please inject a SimplePacketNetworking and use that instead")
   private static void c(SimplePacket var0) {
      Intrinsics.checkNotNullParameter(var0, "");
      SimplePacketNetworking var1 = a;
      a.sendToAllClients1(var0);
   }

   @Deprecated(message = "This is global state; please inject a SimplePacketNetworking and use that instead")
   private static Void a(KClass<? extends SimplePacket> var0, String var1) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      throw new UnsupportedOperationException("This method has been removed.");
   }

   // $VF: synthetic method
   private static Void a(KClass var0, Fb var1, String var2, int var3) {
      if ((var3 & 2) != 0) {
         var2 = "SimplePacket - " + JvmClassMappingKt.getJavaClass(var0);
      }

      String var4 = var2;
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var4, "");
      throw new UnsupportedOperationException("This method has been removed.");
   }
}
