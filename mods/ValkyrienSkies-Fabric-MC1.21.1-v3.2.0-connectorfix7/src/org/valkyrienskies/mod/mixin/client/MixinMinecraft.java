package org.valkyrienskies.mod.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.netty.channel.Channel;
import java.net.SocketAddress;
import net.minecraft.class_1132;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_239;
import net.minecraft.class_2535;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_437;
import net.minecraft.class_634;
import net.minecraft.class_636;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.world.ShipWorld;
import org.valkyrienskies.core.internal.world.VsiClientShipWorld;
import org.valkyrienskies.core.internal.world.VsiPipeline;
import org.valkyrienskies.mod.common.IShipObjectWorldClientCreator;
import org.valkyrienskies.mod.common.IShipObjectWorldClientProvider;
import org.valkyrienskies.mod.common.IShipObjectWorldServerProvider;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;
import org.valkyrienskies.mod.common.util.EntityDragger;
import org.valkyrienskies.mod.mixin.accessors.network.ConnectionAccessor;
import org.valkyrienskies.mod.mixinducks.client.MinecraftDuck;

@Mixin(class_310.class)
public abstract class MixinMinecraft implements MinecraftDuck, IShipObjectWorldClientProvider, IShipObjectWorldClientCreator {
   @Unique
   private static final Logger log = LogManager.getLogger("VS2 MixinMinecraft");
   @Unique
   private static long lastLog = System.currentTimeMillis();
   @Unique
   private static long vs$lastNetworkingAddressLog = 0L;
   @Shadow
   private boolean field_1734;
   @Shadow
   public class_638 field_1687;
   @Unique
   private class_239 originalCrosshairTarget;
   @Unique
   private VsiClientShipWorld shipObjectWorld = null;

   @Shadow
   @Nullable
   public abstract class_1132 method_1576();

   @Override
   public void vs$setOriginalCrosshairTarget(class_239 originalCrosshairTarget) {
      this.originalCrosshairTarget = originalCrosshairTarget;
   }

   @Override
   public class_239 vs$getOriginalCrosshairTarget() {
      return this.originalCrosshairTarget;
   }

   @WrapOperation(
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;useItemOn(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"
      ),
      method = "startUseItem"
   )
   private class_1269 useOriginalCrosshairForBlockPlacement(
      class_636 instance, class_746 localPlayer, class_1268 interactionHand, class_3965 blockHitResult, Operation<class_1269> useItemOn
   ) {
      class_239 originalCrosshairTargetCopy = this.originalCrosshairTarget;
      return originalCrosshairTargetCopy instanceof class_3965
         ? (class_1269)useItemOn.call(new Object[]{instance, localPlayer, interactionHand, originalCrosshairTargetCopy})
         : (class_1269)useItemOn.call(new Object[]{instance, localPlayer, interactionHand, blockHitResult});
   }

   @NotNull
   @Override
   public VsiClientShipWorld getShipObjectWorld() {
      VsiClientShipWorld shipObjectWorldCopy = this.shipObjectWorld;
      if (shipObjectWorldCopy == null) {
         if (lastLog + 5000L < System.currentTimeMillis()) {
            lastLog = System.currentTimeMillis();
            log.warn("Requested getShipObjectWorld() but failed returning dummy world", new IllegalStateException("shipObjectWorld is null"));
         }

         return ValkyrienSkiesMod.getVsCore().getDummyShipWorldClient();
      } else {
         return shipObjectWorldCopy;
      }
   }

   @Shadow
   public abstract class_634 method_1562();

   @Inject(method = "tick", at = @At("RETURN"))
   public void postTick(CallbackInfo ci) {
      if (!this.field_1734 && this.shipObjectWorld != null && this.field_1687 != null && this.method_1562() != null) {
         class_2535 connection = this.method_1562().method_48296();
         SocketAddress remoteAddress = connection.method_10755();
         Channel channel = ((ConnectionAccessor)connection).valkyrienskies$getChannel();
         SocketAddress localAddress = channel != null ? channel.localAddress() : null;
         SocketAddress networkingAddress = remoteAddress != null && remoteAddress.toString().startsWith("local:") ? localAddress : remoteAddress;
         this.shipObjectWorld.tickNetworking(networkingAddress);
         this.shipObjectWorld.postTick();
         if (this.shipObjectWorld.getAllShips().size() > 0) {
            EntityDragger.INSTANCE.dragEntitiesWithShips(this.field_1687.method_18112(), false);
         }
      }
   }

   @Inject(method = "runTick", at = @At("TAIL"))
   public void setGamePause(boolean pauseOnly, CallbackInfo ci) {
      IShipObjectWorldServerProvider provider = (IShipObjectWorldServerProvider)this.method_1576();
      if (provider != null) {
         VsiPipeline pipeline = provider.getVsPipeline();
         if (pipeline != null) {
            pipeline.setArePhysicsRunning(!this.field_1734);
         }
      }
   }

   @Override
   public void createShipObjectWorldClient() {
      if (this.shipObjectWorld != null) {
         throw new IllegalStateException("shipObjectWorld was not null when it should be null?");
      }

      this.shipObjectWorld = ValkyrienSkiesMod.getVsCoreClient().newShipWorldClient();
   }

   @Override
   public void deleteShipObjectWorldClient() {
      VsiClientShipWorld shipObjectWorldCopy = this.shipObjectWorld;
      if (shipObjectWorldCopy == null) {
         throw new IllegalStateException("shipObjectWorld was null when it should be not null?");
      }

      shipObjectWorldCopy.destroyWorld();
      this.shipObjectWorld = null;
   }

   @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;Z)V", at = @At("RETURN"))
   private void postDisconnect(class_437 screen, boolean bl, CallbackInfo ci) {
      if (this.shipObjectWorld != null) {
         this.deleteShipObjectWorldClient();
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @NotNull
   @Override
   public ShipWorld getShipObjectWorld() {
      return this.getShipObjectWorld();
   }
}
