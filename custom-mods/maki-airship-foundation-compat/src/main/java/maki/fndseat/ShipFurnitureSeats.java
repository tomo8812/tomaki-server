package maki.fndseat;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.satisfy.foundation.seat.ChairEntity;
import org.joml.Vector3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.core.api.ships.LoadedShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.entity.ShipMountingEntity;

public final class ShipFurnitureSeats {
    private static final Map<ShipMountingEntity, Anchor> ANCHORS = new WeakHashMap<>();
    private static final Map<Level, Map<BlockPos, WeakReference<ShipMountingEntity>>> OCCUPIED = new WeakHashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger("MakiFurnitureSeat");

    private ShipFurnitureSeats() {}

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static ItemInteractionResult use(Level level, Player player, InteractionHand hand, BlockHitResult hit, double height) {
        LoadedShip ship = VSGameUtilsKt.getLoadedShipManagingPos(level, hit.getBlockPos());
        if (ship == null) return null;
        if (player.isShiftKeyDown() || hit.getDirection() == Direction.DOWN || !player.getItemInHand(hand).isEmpty()) return null;
        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;
        if (!(level instanceof ServerLevel serverLevel)) return null;

        BlockPos block = hit.getBlockPos().immutable();
        Map<BlockPos, WeakReference<ShipMountingEntity>> occupied = OCCUPIED.computeIfAbsent(level, ignored -> new HashMap<>());
        occupied.entrySet().removeIf(entry -> {
            ShipMountingEntity seat = entry.getValue().get();
            return seat == null || seat.isRemoved() || seat.getPassengers().isEmpty();
        });
        if (occupied.get(block) != null) return ItemInteractionResult.FAIL;

        Vec3 previous = player.position();
        Vector3d localPrevious = FurnitureCoordinates.localPrevious(
            previous.x, previous.y, previous.z, VSGameUtilsKt.isBlockInShipyard(level, previous), ship.getWorldToShip());

        EntityType<?> chairType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.fromNamespaceAndPath("foundation", "chair"));
        ChairEntity chair = new ChairEntity((EntityType) chairType, level);
        chair.moveTo(block.getX() + 0.5, block.getY() + 0.25 + height, block.getZ() + 0.5, 0.0F, 0.0F);
        Vec3 riding = chair.getPassengerRidingPosition(player).subtract(player.getVehicleAttachmentPoint(chair));
        Vector3d localSeat = new Vector3d(riding.x, riding.y + 0.5, riding.z);
        Vector3d worldSeat = ship.getShipToWorld().transformPosition(new Vector3d(localSeat));
        if (!worldSeat.isFinite() || !localPrevious.isFinite()) return ItemInteractionResult.FAIL;

        ShipMountingEntity seat = ShipMountingEntity.spawnPassengerSeat(
            serverLevel, worldSeat.x, worldSeat.y, worldSeat.z, player.getYRot(), player.getXRot(),
            ship.getId(), localSeat.x, localSeat.y, localSeat.z, true);
        if (seat == null) return ItemInteractionResult.FAIL;

        ANCHORS.put(seat, new Anchor(block, level.getBlockState(block).getBlock(), ship.getId(), localPrevious));
        occupied.put(block, new WeakReference<>(seat));
        if (!player.startRiding(seat, true)) {
            remove(seat);
            seat.discard();
            LOG.warn("Furniture mount cancelled; released seat at {}", block);
            return ItemInteractionResult.FAIL;
        }
        LOG.info("Mounted ship furniture: ship={} block={} seat={}", ship.getId(), block, seat.getId());
        return ItemInteractionResult.SUCCESS;
    }

    public static void tick(ShipMountingEntity seat) {
        Anchor anchor = ANCHORS.get(seat);
        if (anchor == null || seat.level().isClientSide()) return;
        LoadedShip ship = VSGameUtilsKt.getLoadedShipManagingPos(seat.level(), anchor.block());
        if (ship == null || ship.getId() != anchor.shipId() || seat.level().getBlockState(anchor.block()).getBlock() != anchor.type()) {
            seat.ejectPassengers();
            seat.discard();
            remove(seat);
        }
    }

    public static Vec3 dismount(ShipMountingEntity seat, LivingEntity passenger) {
        Anchor anchor = ANCHORS.get(seat);
        if (anchor == null || seat.level().isClientSide()) return null;
        LoadedShip ship = VSGameUtilsKt.getLoadedShipManagingPos(seat.level(), anchor.block());
        if (ship == null || ship.getId() != anchor.shipId()) return null;
        Vector3d world = ship.getShipToWorld().transformPosition(new Vector3d(anchor.previous()));
        if (!world.isFinite()) return null;
        for (double offset : new double[]{0.0, 0.5, 1.0, 1.5, 2.0}) {
            Vec3 candidate = new Vec3(world.x, world.y + offset, world.z);
            if (seat.level().noCollision(passenger, passenger.getBoundingBox().move(candidate.subtract(passenger.position())))) {
                return candidate;
            }
        }
        return passenger.position();
    }

    public static void remove(ShipMountingEntity seat) {
        Anchor anchor = ANCHORS.remove(seat);
        if (anchor == null) return;
        Map<BlockPos, WeakReference<ShipMountingEntity>> occupied = OCCUPIED.get(seat.level());
        if (occupied == null) return;
        WeakReference<ShipMountingEntity> ref = occupied.get(anchor.block());
        if (ref != null && ref.get() == seat) occupied.remove(anchor.block());
    }

    private record Anchor(BlockPos block, Block type, long shipId, Vector3d previous) {}
}
