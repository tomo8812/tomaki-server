package org.embeddedt.modernfix.common.mixin.perf.suspend_integrated_server_during_load;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.DataFixer;
import java.net.Proxy;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import org.embeddedt.modernfix.annotation.ClientOnlyMixin;
import org.embeddedt.modernfix.duck.suspend_integrated_server_during_load.IDeferrableIntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IntegratedServer.class)
@ClientOnlyMixin
public abstract class IntegratedServerMixin extends MinecraftServer implements IDeferrableIntegratedServer {
   @Shadow
   private boolean paused;
   private int mfix$numTickServerCalls = 0;
   private final AtomicBoolean mfix$hasPrimaryClientJoined = new AtomicBoolean(false);

   public IntegratedServerMixin(
      Thread serverThread,
      LevelStorageAccess storageSource,
      PackRepository packRepository,
      WorldStem worldStem,
      Proxy proxy,
      DataFixer fixerUpper,
      Services services,
      ChunkProgressListenerFactory progressListenerFactory
   ) {
      super(serverThread, storageSource, packRepository, worldStem, proxy, fixerUpper, services, progressListenerFactory);
   }

   @WrapOperation(method = "tickServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isPaused()Z", ordinal = 0))
   private boolean preventTicks(Minecraft instance, Operation<Boolean> original) {
      return !this.mfix$hasPrimaryClientJoined.get() || (Boolean)original.call(new Object[]{instance});
   }

   @Inject(method = "tickServer", at = @At("HEAD"))
   private void mfix$countTicks(CallbackInfo ci) {
      this.mfix$numTickServerCalls++;
   }

   @WrapWithCondition(
      method = "tickServer",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;tickServer(Ljava/util/function/BooleanSupplier;)V", ordinal = 0)
   )
   private boolean preventRunningFullServerTick(MinecraftServer server, BooleanSupplier hasTimeLeft) {
      if (this.mfix$numTickServerCalls >= 2 && this.paused && !this.mfix$hasPrimaryClientJoined.get()) {
         ServerConnectionListener conn = this.getConnection();
         if (conn != null) {
            conn.tick();
         }

         return false;
      } else {
         return true;
      }
   }

   @Override
   public void mfix$markClientLoadFinished() {
      this.mfix$hasPrimaryClientJoined.set(true);
   }
}
