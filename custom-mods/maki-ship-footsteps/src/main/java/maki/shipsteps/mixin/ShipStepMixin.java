package maki.shipsteps.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.EntityDraggingInformation;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

@Mixin(value = Entity.class, remap = false)
public abstract class ShipStepMixin {
    @Unique private long maki$ship;
    @Unique private boolean maki$sampled;
    @Unique private double maki$x;
    @Unique private double maki$z;
    @Unique private int maki$lastMotionTick;
    @Unique private boolean maki$moved;

    @Inject(method = "move", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/world/entity/Entity;getMovementEmission()Lnet/minecraft/world/entity/Entity$MovementEmission;"))
    private void maki$sampleDeckMotion(MoverType mover, Vec3 movement, CallbackInfo ci) {
        if (!((Object) this instanceof Player player) || player.isLocalPlayer()) return;
        EntityDraggingInformation dragging = ((IEntityDraggingInformationProvider) player).getDraggingInformation();
        Long ship = dragging.getLastShipStoodOn();
        Vector3dc relative = player.level().isClientSide
            ? dragging.getRelativePositionOnShip()
            : dragging.getServerRelativePlayerPosition();
        if (ship == null || relative == null || !player.onGround() || player.isPassenger()) {
            maki$sampled = false;
            maki$moved = false;
            return;
        }

        if (maki$sampled && maki$ship == ship) {
            double dx = relative.x() - maki$x;
            double dz = relative.z() - maki$z;
            double distanceSquared = dx * dx + dz * dz;
            if (distanceSquared > 1.0E-4 && distanceSquared < 16.0) {
                maki$moved = true;
                maki$lastMotionTick = player.tickCount;
            } else if (distanceSquared >= 16.0 || player.tickCount - maki$lastMotionTick > 1) {
                maki$moved = false;
            }
        } else {
            maki$moved = false;
        }

        maki$ship = ship;
        maki$x = relative.x();
        maki$z = relative.z();
        maki$sampled = true;
    }

    @ModifyArgs(method = "move", at = @At(value = "INVOKE", ordinal = 0,
        target = "Lnet/minecraft/world/entity/Entity;vibrationAndSoundEffectsFromBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;ZZLnet/minecraft/world/phys/Vec3;)Z"),
        require = 1, allow = 1)
    private void maki$useDeckForSound(Args args) {
        if (!((Object) this instanceof Player player) || !(Boolean) args.get(2) || !player.onGround()) return;
        BlockPos deck = player.getOnPos();
        Ship ship = VSGameUtilsKt.getShipManagingPos(player.level(), deck);
        if (ship == null) return;

        boolean moving = player.isLocalPlayer()
            ? player.xxa != 0.0F || player.zza != 0.0F
            : maki$sampled && maki$ship == ship.getId() && maki$moved;
        if (!moving) {
            args.set(2, false);
            return;
        }

        BlockPos vanilla = args.get(0);
        if (!deck.equals(vanilla) && VSGameUtilsKt.getShipManagingPos(player.level(), vanilla) == null) {
            BlockState state = player.level().getBlockState(deck);
            if (!state.isAir()) {
                args.set(0, deck);
                args.set(1, state);
            }
        }
    }
}
