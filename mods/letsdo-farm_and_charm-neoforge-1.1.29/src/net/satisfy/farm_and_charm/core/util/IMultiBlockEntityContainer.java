package net.satisfy.farm_and_charm.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public interface IMultiBlockEntityContainer {
   BlockPos getController();

   <T extends BlockEntity & IMultiBlockEntityContainer> T getControllerBE();

   boolean isController();

   void setController(BlockPos var1);

   void removeController(boolean var1);

   void preventConnectivityUpdate();

   void notifyMultiUpdated();

   @Nullable
   default Object getExtraData() {
      return null;
   }

   default void setExtraData(@Nullable Object data) {
   }

   default Object modifyExtraData(Object data) {
      return data;
   }

   Axis getMainConnectionAxis();

   int getMaxLength(Axis var1, int var2);

   int getMaxWidth();

   int getHeight();

   void setHeight(int var1);

   int getWidth();

   void setWidth(int var1);

   interface Inventory extends IMultiBlockEntityContainer {
      default boolean hasInventory() {
         return false;
      }
   }
}
