package org.valkyrienskies.mod.mixin.mod_compat.cc_tweaked;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.shared.turtle.core.MoveDirection;
import dan200.computercraft.shared.turtle.core.TurtleMoveCommand;
import dan200.computercraft.shared.turtle.core.TurtlePlayer;
import dan200.computercraft.shared.util.WorldUtil;
import net.minecraft.class_1923;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2382;
import net.minecraft.class_3218;
import net.minecraft.class_4970.class_4971;
import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.joml.primitives.AABBic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.config.VSGameConfig;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Pseudo
@Mixin(value = TurtleMoveCommand.class, priority = 2000)
public abstract class MixinTurtleMoveCommand {
   @Shadow(remap = false)
   @Final
   private MoveDirection direction;
   @Unique
   private class_2350 movingDirection = null;

   @Redirect(
      method = "execute",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;relative(Lnet/minecraft/core/Direction;)Lnet/minecraft/core/BlockPos;", remap = true),
      remap = false
   )
   public class_2338 execute$oldPosition$relative(class_2338 oldPosition, class_2350 direction, ITurtleAccess turtle) {
      class_2338 newPosition = oldPosition.method_10093(direction);
      this.movingDirection = null;
      Ship ship = VSGameUtilsKt.getShipManagingPos(turtle.getLevel(), turtle.getPosition());
      if (ship == null) {
         return newPosition;
      }

      AABBic box = ship.getShipAABB();
      if (box.minX() <= newPosition.method_10263()
         && newPosition.method_10263() < box.maxX()
         && box.minY() <= newPosition.method_10264()
         && newPosition.method_10264() < box.maxY()
         && box.minZ() <= newPosition.method_10260()
         && newPosition.method_10260() < box.maxZ()) {
         return newPosition;
      }

      if (!ship.getTransform().getShipToWorldScaling().equals(1.0, 1.0, 1.0) && !VSGameConfig.SERVER.getComputerCraft().getCanTurtlesLeaveScaledShips()) {
         return newPosition;
      }

      Matrix4dc transform = ship.getShipToWorld();
      class_2382 dirVec = direction.method_10163();
      Vector3d movingVec = transform.transformDirection(new Vector3d(dirVec.method_10263(), dirVec.method_10264(), dirVec.method_10260()));
      this.movingDirection = class_2350.method_10142(movingVec.x, movingVec.y, movingVec.z);
      Vector3d worldPos = transform.transformPosition(
         new Vector3d(newPosition.method_10263() + 0.5, newPosition.method_10264() + 0.5, newPosition.method_10260() + 0.5)
      );
      return class_2338.method_49637(worldPos.x, worldPos.y, worldPos.z);
   }

   @Redirect(
      method = "execute",
      slice = @Slice(from = @At(value = "INVOKE", target = "Ldan200/computercraft/api/turtle/ITurtleAccess;consumeFuel")),
      at = @At(
         value = "FIELD",
         target = "Ldan200/computercraft/shared/turtle/core/TurtleMoveCommand;direction:Ldan200/computercraft/shared/turtle/core/MoveDirection;",
         opcode = 180
      ),
      remap = false
   )
   public MoveDirection execute$load$direction(TurtleMoveCommand command, ITurtleAccess turtle) {
      if (this.movingDirection == null) {
         return this.direction;
      } else {
         class_2350 turtleDir = turtle.getDirection();
         if (this.movingDirection == turtleDir) {
            return MoveDirection.FORWARD;
         } else if (this.movingDirection == turtleDir.method_10153()) {
            return MoveDirection.BACK;
         } else if (this.movingDirection == class_2350.field_11036) {
            return MoveDirection.UP;
         } else {
            return this.movingDirection == class_2350.field_11033 ? MoveDirection.DOWN : this.direction;
         }
      }
   }

   @Inject(method = "canEnter", at = @At("HEAD"), remap = false, cancellable = true)
   private static void canEnter(TurtlePlayer turtlePlayer, class_3218 world, class_2338 position, CallbackInfoReturnable<TurtleCommandResult> cir) {
      Ship ship = VSGameUtilsKt.getShipManagingPos(world, position);
      Vector3d testPos = new Vector3d(position.method_10263() + 0.5, position.method_10264() + 0.5, position.method_10260() + 0.5);
      if (ship != null) {
         class_1923 chunk = world.method_8500(position).method_12004();
         if (!ship.getChunkClaim().contains(chunk.field_9181, chunk.field_9180)) {
            cir.setReturnValue(TurtleCommandResult.failure("Out of ship chunk"));
            return;
         }

         VSGameUtilsKt.toWorldCoordinates(world, testPos);
      }

      boolean isAir = VSGameUtilsKt.transformToNearbyShipsAndWorld(world, testPos.x, testPos.y, testPos.z, 0.1)
         .stream()
         .map(VectorConversionsMCKt::toMinecraft)
         .<class_2338>map(class_2338::method_49638)
         .filter(pos -> VSGameUtilsKt.getShipManagingPos(world, pos) != ship)
         .map(world::method_8320)
         .allMatch((WorldUtil::isEmptyBlock).or(class_4971::method_45474));
      if (!isAir) {
         cir.setReturnValue(TurtleCommandResult.failure("Movement obstructed by ship"));
      }
   }
}
