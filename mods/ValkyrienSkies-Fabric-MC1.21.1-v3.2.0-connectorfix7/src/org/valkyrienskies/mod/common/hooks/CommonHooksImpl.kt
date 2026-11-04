package org.valkyrienskies.mod.common.hooks

import net.minecraft.class_2535
import net.minecraft.class_310
import net.minecraft.class_634
import net.minecraft.server.MinecraftServer
import org.valkyrienskies.core.api.world.ShipWorld
import org.valkyrienskies.core.internal.hooks.VsiCoreHooksOut
import org.valkyrienskies.core.internal.hooks.VsiPlayState
import org.valkyrienskies.core.internal.world.VsiClientShipWorld

public abstract class CommonHooksImpl : VsiCoreHooksOut {
   public open var enableBlockEdgeConnectivity: Boolean
      public open get() {
         return vsCore.hooks.getEnableBlockEdgeConnectivity()
      }

      public open set(value) {
      }


   public open var enableBlockCornerConnectivity: Boolean
      public open get() {
         return vsCore.hooks.getEnableBlockCornerConnectivity()
      }

      public open set(value) {
      }


   public open var enableConnectivity: Boolean
      public open get() {
         return vsCore.hooks.getEnableConnectivity()
      }

      public open set(value) {
      }


   public open var enableWorldConnectivity: Boolean
      public open get() {
         return vsCore.hooks.getEnableWorldConnectivity()
      }

      public open set(value) {
      }


   public open var enableSplitting: Boolean
      public open get() {
         return vsCore.hooks.getEnableSplitting()
      }

      public open set(value) {
      }


   public open var enableDrag: Boolean
      public open get() {
         return vsCore.hooks.getEnableDrag()
      }

      public open set(value) {
      }


   public open var enableLift: Boolean
      public open get() {
         return vsCore.hooks.getEnableLift()
      }

      public open set(value) {
      }


   public open var dragCoefficient: Double
      public open get() {
         return vsCore.hooks.getDragCoefficient()
      }

      public open set(value) {
      }


   public open var dragMultiplier: Double
      public open get() {
         return vsCore.hooks.getDragMultiplier()
      }

      public open set(value) {
      }


   public open var liftMultiplier: Double
      public open get() {
         return vsCore.hooks.getLiftMultiplier()
      }

      public open set(value) {
      }


   public open val playState: VsiPlayState
      public open get() {
         if (!this.isPhysicalClient()) {
            return VsiPlayState.SERVERSIDE
         } else {
            var var2: Boolean
            run label36@{
               val var10000: class_634 = class_310.method_1551().method_1562()
               if (var10000 != null) {
                  val var1: class_2535 = var10000.method_48296()
                  if (var1 != null) {
                     var2 = var1.method_10758()
                     return@label36
                  }
               }

               var2 = false
            }

            if (!var2) {
               return VsiPlayState.CLIENT_TITLESCREEN
            } else {
               return if (class_310.method_1551().method_1576() != null) VsiPlayState.CLIENT_SINGLEPLAYER else VsiPlayState.CLIENT_MULTIPLAYER
            }
         }
      }


   public open val currentShipServerWorld: ShipWorld?
      public open get() {
         val var10000: MinecraftServer = currentServer
         return if (var10000 != null) shipObjectWorld else null
      }


   public open val currentShipClientWorld: ShipWorld
      public open get() {
         val var10000: class_310 = class_310.method_1551()
         val var1: VsiClientShipWorld = shipObjectWorld
         return var1
      }

}
