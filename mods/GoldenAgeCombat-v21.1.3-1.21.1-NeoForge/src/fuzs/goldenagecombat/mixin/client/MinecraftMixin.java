package fuzs.goldenagecombat.mixin.client;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.goldenagecombat.config.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
abstract class MinecraftMixin {
   @Shadow
   @Final
   private static Logger LOGGER;
   @Shadow
   @Final
   public Options options;
   @Shadow
   public MultiPlayerGameMode gameMode;
   @Shadow
   public ClientLevel level;
   @Shadow
   public LocalPlayer player;
   @Shadow
   protected int missTime;
   @Shadow
   public HitResult hitResult;

   @Inject(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z", ordinal = 0))
   public void handleKeybinds(CallbackInfo callback) {
      if (((ServerConfig)GoldenAgeCombat.CONFIG.get(ServerConfig.class)).interactWhileUsing && this.player.isUsingItem()) {
         while (this.options.keyAttack.consumeClick()) {
            this.goldenagecombat$startBlockAttack();
         }
      }
   }

   @Unique
   private void goldenagecombat$startBlockAttack() {
      if (this.missTime <= 0) {
         if (this.hitResult == null) {
            LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
            if (this.gameMode.hasMissTime()) {
               this.missTime = 10;
            }
         } else if (this.player.getItemInHand(InteractionHand.MAIN_HAND).isItemEnabled(this.level.enabledFeatures())
            && !this.player.isHandsBusy()
            && this.hitResult.getType() == Type.BLOCK) {
            BlockHitResult blockhitresult = (BlockHitResult)this.hitResult;
            BlockPos blockpos = blockhitresult.getBlockPos();
            if (!this.level.isEmptyBlock(blockpos)) {
               this.gameMode.startDestroyBlock(blockpos, blockhitresult.getDirection());
               return;
            }

            this.player.swing(InteractionHand.MAIN_HAND);
         }
      }
   }

   @Redirect(method = "continueAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"))
   public boolean continueAttack(LocalPlayer player) {
      return !((ServerConfig)GoldenAgeCombat.CONFIG.get(ServerConfig.class)).interactWhileUsing ? player.isUsingItem() : false;
   }

   @Redirect(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;isDestroying()Z"))
   public boolean startUseItem(MultiPlayerGameMode gameMode) {
      return !((ServerConfig)GoldenAgeCombat.CONFIG.get(ServerConfig.class)).interactWhileUsing ? gameMode.isDestroying() : false;
   }
}
