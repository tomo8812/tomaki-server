package org.valkyrienskies.eureka.block

import com.mojang.serialization.Codec
import java.util.Locale
import kotlin.enums.EnumEntries
import net.minecraft.class_2246
import net.minecraft.class_2248
import net.minecraft.class_3542
import net.minecraft.class_3542.class_7292

public enum class WoodType(logBlock: class_2248, plankBlock: class_2248) : IWoodType {
   ACACIA,
   BIRCH,
   CRIMSON,
   DARK_OAK,
   JUNGLE,
   OAK,
   SPRUCE,
   WARPED;

   public final val logBlock: class_2248
   public final val plankBlock: class_2248

   init {
      this.logBlock = logBlock
      this.plankBlock = plankBlock
   }

   fun method_15434(): java.lang.String {
      val var10000: java.lang.String = this.name().toLowerCase(Locale.ROOT)
      var10000
   }

   public override fun getWood(): class_2248 {
      return this.logBlock
   }

   public override fun getPlanks(): class_2248 {
      return this.plankBlock
   }

   @JvmStatic
   fun getEntries(): EnumEntries<WoodType> {
      $ENTRIES
   }

   @JvmStatic
   @JvmSynthetic
   fun `$values`(): Array<WoodType> {
      arrayOf(ACACIA, BIRCH, CRIMSON, DARK_OAK, JUNGLE, OAK, SPRUCE, WARPED)
   }

   // $VF: Failed to inline enum fields
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @JvmStatic
   fun {
      var var10004: class_2248 = class_2246.field_10533
      var var10005: class_2248 = class_2246.field_10218
      ACACIA = WoodType(var10004, var10005)
      var10004 = class_2246.field_10511
      var10005 = class_2246.field_10148
      BIRCH = WoodType(var10004, var10005)
      var10004 = class_2246.field_22118
      var10005 = class_2246.field_22126
      CRIMSON = WoodType(var10004, var10005)
      var10004 = class_2246.field_10010
      var10005 = class_2246.field_10075
      DARK_OAK = WoodType(var10004, var10005)
      var10004 = class_2246.field_10306
      var10005 = class_2246.field_10334
      JUNGLE = WoodType(var10004, var10005)
      var10004 = class_2246.field_10431
      var10005 = class_2246.field_10161
      OAK = WoodType(var10004, var10005)
      var10004 = class_2246.field_10037
      var10005 = class_2246.field_9975
      SPRUCE = WoodType(var10004, var10005)
      var10004 = class_2246.field_22111
      var10005 = class_2246.field_22127
      WARPED = WoodType(var10004, var10005)
      val var10000: class_7292 = class_3542.method_28140(org/valkyrienskies/eureka/block/WoodType##Lambda_0_264())
      CODEC = var10000 as Codec<WoodType>
   }

   public companion object {
      public final val CODEC: Codec<WoodType>
   }
}
