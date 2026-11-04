package maki.airship;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.EntityDraggingInformation;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

public final class ClientDiagnostics {
   private static final Logger LOG = LoggerFactory.getLogger("MakiAirshipDiag");
   private static Object world;
   private static int count;
   private static long next;
   private static boolean errorLogged;

   private static Object field(Object var0, String var1) throws ReflectiveOperationException {
      for (Class var2 = var0.getClass(); var2 != null; var2 = var2.getSuperclass()) {
         try {
            Field var3 = var2.getDeclaredField(var1);
            var3.setAccessible(true);
            return var3.get(var0);
         } catch (NoSuchFieldException var4) {
         }
      }

      throw new NoSuchFieldException(var1);
   }

   private static Object call(Object var0, String var1) throws ReflectiveOperationException {
      Method var2 = var0.getClass().getMethod(var1);
      var2.setAccessible(true);
      return var2.invoke(var0);
   }

   public static void frame() {
      try {
         capture();
      } catch (RuntimeException | LinkageError var1) {
         if (!errorLogged) {
            LOG.debug("Render diagnostics skipped", var1);
            errorLogged = true;
         }
      }
   }

   private static void capture() {
      Minecraft var0 = Minecraft.getInstance();
      if (var0.level != null && var0.player != null) {
         if (world != var0.level) {
            world = var0.level;
            count = 0;
            next = 0L;
            errorLogged = false;
         }

         if (count < 120 && System.nanoTime() >= next) {
            boolean var1 = VSGameUtilsKt.getShipMountedToData(var0.player, 0.0F) != null
               || ((IEntityDraggingInformationProvider)var0.player).getDraggingInformation().isEntityBeingDraggedByAShip();
            if (var1) {
               next = System.nanoTime() + 1000000000L;
               count++;
               String var2 = "unavailable";
               String var3 = "unavailable";

               try {
                  Class var4 = Class.forName("net.irisshaders.iris.Iris", false, ClientDiagnostics.class.getClassLoader());
                  Object var5 = var4.getMethod("getPipelineManager").invoke(null);
                  Object var6 = call(var5, "getPipelineNullable");
                  Object var7 = var6 == null ? null : call(var6, "getFrameUpdateNotifier");
                  if (var7 != null) {
                     Object var8 = field(var7, "listeners");
                     if (var8 instanceof Iterable) {
                        label81:
                        for (Object var11 : (Iterable)var8) {
                           for (Field var15 : var11.getClass().getDeclaredFields()) {
                              if (!Modifier.isStatic(var15.getModifiers())) {
                                 var15.setAccessible(true);
                                 Object var16 = var15.get(var11);
                                 if (var16 != null && var16.getClass().getName().endsWith("CameraPositionTracker")) {
                                    Object var17 = field(var16, "currentCameraPosition");
                                    var2 = String.valueOf(field(var17, "y"));
                                    break label81;
                                 }
                              }
                           }
                        }
                     }
                  }

                  Object var23 = call(var0.levelRenderer, "voxy$getRenderSystem");
                  if (var23 != null) {
                     Object var25 = field(var23, "viewportSelector");
                     Object var27 = field(var25, "defaultViewport");
                     var3 = field(var27, "cameraX") + "," + field(var27, "cameraY") + "," + field(var27, "cameraZ") + " frame=" + field(var27, "frameId");
                  }
               } catch (ReflectiveOperationException | RuntimeException | LinkageError var18) {
                  if (!errorLogged) {
                     LOG.debug("Optional render-state diagnostics unavailable", var18);
                     errorLogged = true;
                  }
               }

               ArrayList var19 = new ArrayList();

               for (AbstractClientPlayer var21 : var0.level.players()) {
                  String var22 = "";
                  if (var21 instanceof IEntityDraggingInformationProvider var24) {
                     EntityDraggingInformation var26 = var24.getDraggingInformation();
                     var22 = " ship=" + var26.getLastShipStoodOn() + " packetAge=" + var26.getTicksSinceLastServerPacket();
                  }

                  var19.add(
                     "id="
                        + var21.getId()
                        + " pos="
                        + var21.position()
                        + " pose="
                        + var21.getPose()
                        + " sleepingPos="
                        + var21.getSleepingPos()
                        + " vehicle="
                        + (var21.getVehicle() == null ? "none" : var21.getVehicle().getId())
                        + var22
                  );
               }

               LOG.info(
                  "sample={} camera={} shaderCameraY={} lodCamera={} cloudHeight={} players={}",
                  new Object[]{count, var0.gameRenderer.getMainCamera().getPosition(), var2, var3, var0.level.effects().getCloudHeight(), var19}
               );
            }
         }
      }
   }
}
