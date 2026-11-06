package maki.airship.mixin;

import maki.airship.MountedCameraRotation;
import maki.airship.SleepCompat;
import maki.airship.SleepingCameraPose;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.mod.client.ShipMountPerspective;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.entity.ShipMountedToData;
import org.valkyrienskies.mod.common.entity.ShipMountingEntity;
import org.valkyrienskies.mod.common.world.RaycastUtilsKt;

@Mixin(value = Camera.class, remap = false)
public abstract class MountedCameraMixin {
    @Shadow @Final private Quaternionf rotation;
    @Shadow @Final private Vector3f forwards;
    @Shadow @Final private Vector3f up;
    @Shadow @Final private Vector3f left;
    @Shadow private float xRot;
    @Shadow private float yRot;
    @Shadow private float eyeHeight;
    @Shadow private float eyeHeightOld;
    @Shadow private Vec3 position;

    @Shadow protected abstract void setPosition(double x, double y, double z);
    @Shadow protected abstract void move(float distance, float vertical, float horizontal);

    @Inject(method = "setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V", at = @At("TAIL"), remap = false)
    private void maki$normalMountedView(
            BlockGetter blockGetter, Entity entity, boolean detached, boolean reverse, float partialTicks, CallbackInfo ci) {
        if (!(blockGetter instanceof Level level)) return;

        ClientShip ship;
        Vector3d localMount;
        float yaw;
        float pitch;
        boolean sleeping = false;

        if (entity instanceof LivingEntity living && SleepCompat.anchored(living) && !living.isPassenger()) {
            BlockPos bed = living.getSleepingPos().orElseThrow();
            if (!(VSGameUtilsKt.getLoadedShipManagingPos(level, bed) instanceof ClientShip clientShip)) return;
            ship = clientShip;
            Vec3 point = SleepCompat.bedPoint(bed);
            localMount = new Vector3d(point.x, point.y, point.z);
            yaw = SleepingCameraPose.localYaw(living.getBedOrientation());
            pitch = 0.0F;
            sleeping = true;
        } else {
            if (!(entity.getVehicle() instanceof ShipMountingEntity) || ShipMountPerspective.isShipViewEngaged()) return;
            ShipMountedToData data = VSGameUtilsKt.getShipMountedToData(entity, partialTicks);
            if (data == null || !(data.getShipMountedTo() instanceof ClientShip clientShip)) return;
            ship = clientShip;
            localMount = new Vector3d(data.getMountPosInShip());
            yaw = entity.getViewYRot(partialTicks);
            pitch = entity.getViewXRot(partialTicks);
        }

        ShipTransform transform = ship.getRenderTransform();
        rotation.set(MountedCameraRotation.worldRotation(transform.getShipToWorldRotation(), yaw, pitch, detached && reverse));
        forwards.set(0.0F, 0.0F, -1.0F);
        rotation.transform(forwards);
        up.set(0.0F, 1.0F, 0.0F);
        rotation.transform(up);
        left.set(-1.0F, 0.0F, 0.0F);
        rotation.transform(left);
        yRot = (float) Math.toDegrees(Math.atan2(-forwards.x, forwards.z));
        double clamped = Math.max(-1.0, Math.min(1.0, -forwards.y));
        xRot = (float) Math.toDegrees(Math.asin(clamped));

        Vector3d mountWorld = transform.getShipToWorld().transformPosition(localMount, new Vector3d());
        double eye = eyeHeightOld + (eyeHeight - eyeHeightOld) * partialTicks;
        if (sleeping && !detached) eye += 0.3;
        Vector3d cameraWorld = transform.getShipToWorldRotation()
            .transform(new Vector3d(0.0, eye, 0.0)).add(mountWorld);
        setPosition(cameraWorld.x, cameraWorld.y, cameraWorld.z);

        if (!detached) return;
        double desired = 4.0 * (entity instanceof LivingEntity living ? living.getScale() : 1.0);
        double allowed = desired;
        for (int corner = 0; corner < 8; corner++) {
            double dx = ((corner & 1) * 2 - 1) * 0.1;
            double dy = (((corner >> 1) & 1) * 2 - 1) * 0.1;
            double dz = (((corner >> 2) & 1) * 2 - 1) * 0.1;
            Vec3 start = position.add(dx, dy, dz);
            Vec3 end = start.add(-forwards.x * desired, -forwards.y * desired, -forwards.z * desired);
            BlockHitResult hit = RaycastUtilsKt.clipIncludeShips(
                level,
                new ClipContext(start, end, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, entity),
                true, null, false);
            if (hit.getType() != Type.MISS) allowed = Math.min(allowed, hit.getLocation().distanceTo(position));
        }
        move((float) -allowed, 0.0F, 0.0F);
    }
}
