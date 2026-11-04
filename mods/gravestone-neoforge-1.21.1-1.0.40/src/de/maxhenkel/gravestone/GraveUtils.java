package de.maxhenkel.gravestone;

import de.maxhenkel.gravestone.tileentity.GraveStoneTileEntity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class GraveUtils {
   public static final UUID EMPTY_UUID = new UUID(0L, 0L);

   @Nullable
   public static BlockPos getGraveStoneLocation(Level world, BlockPos pos) {
      MutableBlockPos location = new MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());
      location.set(world.getWorldBorder().clampToBounds(location));
      if (world.isOutsideBuildHeight(location) && location.getY() <= world.getMinBuildHeight()) {
         location.set(location.getX(), world.getMinBuildHeight() + 1, location.getZ());
      }

      while (!world.isOutsideBuildHeight(location)) {
         if (isReplaceable(world, location)) {
            return location;
         }

         location.move(0, 1, 0);
      }

      if ((Boolean)Main.SERVER_CONFIG.strictPlacement.get()) {
         location.set(location.getX(), world.getMaxBuildHeight(), location.getZ());
         return location;
      } else {
         return null;
      }
   }

   public static boolean isReplaceable(Level world, BlockPos pos) {
      Block b = world.getBlockState(pos).getBlock();
      return world.isEmptyBlock(pos) ? true : Main.SERVER_CONFIG.replaceableBlocks.stream().anyMatch(blockTag -> blockTag.contains(b));
   }

   @Nullable
   public static MutableComponent getDate(long timestamp) {
      if (timestamp <= 0L) {
         return null;
      }

      SimpleDateFormat dateFormat = new SimpleDateFormat(Component.translatable("gui.gravestone.date_format").getString());
      return Component.literal(dateFormat.format(new Date(timestamp)));
   }

   public static boolean canBreakGrave(Level world, Player player, BlockPos pos) {
      if (player.isDeadOrDying()) {
         return false;
      }

      if (!(Boolean)Main.SERVER_CONFIG.onlyOwnersCanBreak.get()) {
         return true;
      }

      if (world.getBlockEntity(pos) instanceof GraveStoneTileEntity grave) {
         if (player instanceof ServerPlayer p && p.hasPermissions(p.server.getOperatorUserPermissionLevel())) {
            return true;
         } else {
            UUID uuid = grave.getDeath().getPlayerUUID();
            return uuid.equals(EMPTY_UUID) ? true : player.getUUID().equals(uuid);
         }
      } else {
         return true;
      }
   }
}
