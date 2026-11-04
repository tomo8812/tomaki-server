package net.satisfy.bakery.client.gui.overlay;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.satisfy.bakery.core.block.BreadBox;
import net.satisfy.bakery.core.block.CakeDisplayBlock;
import net.satisfy.bakery.core.block.CakeStandBlock;
import net.satisfy.bakery.core.block.StackingStorageBlock;
import net.satisfy.bakery.core.block.TrayBlock;
import net.satisfy.bakery.platform.PlatformHelper;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import net.satisfy.foundation.storage.StorageBlockEntity;
import org.jetbrains.annotations.Nullable;

public class DisplayInfoProvider implements BlockInfoProvider {
   private static final Vec3[] STAND_SPOTS = new Vec3[]{new Vec3(-0.16, 0.52, 0.16), new Vec3(-0.08, 0.52, -0.16), new Vec3(0.16, 0.52, 0.08)};

   public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
      if (hit != null && PlatformHelper.showDisplayInfo() && (hasGlassTop(state) || BakerStationInfoProvider.wearsDungarees())) {
         if (level.getBlockEntity(pos) instanceof StorageBlockEntity storage) {
            if (state.getBlock() instanceof StackingStorageBlock display) {
               return hoveredSpot(display, storage, state, hit);
            } else if (hasGlassTop(state)) {
               return nearestStandSpot(state, pos, storage);
            } else if (state.getBlock() instanceof BreadBox) {
               List<InfoSection> sections = new ArrayList<>(contents(Component.translatable("hud.bakery.bread_box.in"), storage));
               sections.add(
                  InfoSection.title(
                     Component.translatable(state.getValue(BreadBox.OPEN) ? "hud.bakery.bread_box.close" : "hud.bakery.bread_box.open")
                        .withStyle(ChatFormatting.GRAY)
                  )
               );
               return sections;
            } else {
               return state.getBlock() instanceof TrayBlock ? contents(Component.translatable("hud.bakery.tray.on"), storage) : List.of();
            }
         } else {
            return List.of();
         }
      } else {
         return List.of();
      }
   }

   private static boolean hasGlassTop(BlockState state) {
      return state.getBlock() instanceof CakeDisplayBlock || state.getBlock() instanceof CakeStandBlock && !(state.getBlock() instanceof TrayBlock);
   }

   private static List<InfoSection> hoveredSpot(StackingStorageBlock display, StorageBlockEntity storage, BlockState state, BlockHitResult hit) {
      Vec3 local = hit.getLocation().subtract(Vec3.atLowerCornerWithOffset(hit.getBlockPos(), 0.0, 0.0, 0.0));

      float across = switch ((Direction)state.getValue(StackingStorageBlock.FACING)) {
         case SOUTH -> (float)local.x;
         case WEST -> (float)local.z;
         case EAST -> 1.0F - (float)local.z;
         default -> 1.0F - (float)local.x;
      };
      int section = display.getSection(Mth.clamp(across, 0.0F, 0.999F), Mth.clamp((float)local.y, 0.0F, 0.999F));
      if (section >= 0 && section < storage.getInventory().size()) {
         ItemStack stack = (ItemStack)storage.getInventory().get(section);
         return stack.isEmpty()
            ? List.of()
            : List.of(
               InfoSection.icons(Component.translatable("hud.bakery.display_slot", new Object[]{stack.getHoverName(), stack.getCount()}), List.of(stack), 6)
            );
      } else {
         return List.of();
      }
   }

   private static List<InfoSection> nearestStandSpot(BlockState state, BlockPos pos, StorageBlockEntity storage) {
      Player player = Minecraft.getInstance().player;
      if (player == null) {
         return List.of();
      }

      Vec3 eye = player.getEyePosition();
      Vec3 look = player.getViewVector(1.0F);
      float angle = (180.0F - ((Direction)state.getValue(CakeStandBlock.FACING)).toYRot()) * (float) (Math.PI / 180.0);
      float cos = Mth.cos(angle);
      float sin = Mth.sin(angle);
      ItemStack nearest = ItemStack.EMPTY;
      double best = Double.MAX_VALUE;

      for (int i = 0; i < Math.min(STAND_SPOTS.length, storage.getInventory().size()); i++) {
         ItemStack stack = (ItemStack)storage.getInventory().get(i);
         if (!stack.isEmpty()) {
            Vec3 offset = STAND_SPOTS[i];
            Vec3 spot = new Vec3(pos.getX() + 0.5 + offset.x * cos + offset.z * sin, pos.getY() + offset.y, pos.getZ() + 0.5 - offset.x * sin + offset.z * cos);
            double distance = spot.subtract(eye).cross(look).lengthSqr();
            if (distance < best) {
               best = distance;
               nearest = stack;
            }
         }
      }

      return nearest.isEmpty()
         ? List.of()
         : List.of(
            InfoSection.icons(Component.translatable("hud.bakery.display_slot", new Object[]{nearest.getHoverName(), nearest.getCount()}), List.of(nearest), 6)
         );
   }

   private static List<InfoSection> contents(Component title, StorageBlockEntity storage) {
      List<ItemStack> merged = new ArrayList<>();

      for (ItemStack stack : storage.getInventory()) {
         if (!stack.isEmpty()) {
            ItemStack same = merged.stream().filter(existing -> ItemStack.isSameItemSameComponents(existing, stack)).findFirst().orElse(null);
            if (same != null) {
               same.grow(stack.getCount());
            } else {
               merged.add(stack.copy());
            }
         }
      }

      return merged.isEmpty() ? List.of() : List.of(InfoSection.icons(title, merged, 6).withDecorations());
   }
}
