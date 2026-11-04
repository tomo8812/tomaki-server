package org.valkyrienskies.core.api.ships

import org.joml.Matrix4dc

public interface WingManager {
   public abstract fun createWingGroup(isContraption: Boolean = false): Int {
   }

   public abstract fun deleteWingGroup(wingGroupId: Int): Boolean {
   }

   public abstract fun setWing(wingGroupId: Int, posX: Int, posY: Int, posZ: Int, wing: Wing?): Boolean {
   }

   public abstract fun getWing(wingGroupId: Int, posX: Int, posY: Int, posZ: Int): Wing? {
   }

   public abstract fun setWingGroupTransform(wingGroupId: Int, transform: Matrix4dc) {
   }

   public abstract fun getWingChanges(): WingManagerChanges? {
   }

   public abstract fun clearWingChanges() {
   }

   public abstract fun getFirstWingGroupId(): Int {
   }
}
