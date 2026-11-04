package org.valkyrienskies.mod.compat

import kotlin.enums.EnumEntries
import kotlin.jvm.internal.PropertyReference0
import kotlin.jvm.internal.PropertyReference0Impl
import kotlin.jvm.internal.Reflection
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

public object LoadedMods {
   @JvmStatic
   @JvmSynthetic
   KProperty<Object>[] $$delegatedProperties = arrayOf(
      Reflection.property0(PropertyReference0Impl(INSTANCE, LoadedMods.class, "iris", "getIris()Z", 0) as PropertyReference0),
      Reflection.property0(PropertyReference0Impl(INSTANCE, LoadedMods.class, "weather2", "getWeather2()Z", 0) as PropertyReference0),
      Reflection.property0(PropertyReference0Impl(INSTANCE, LoadedMods.class, "alexCaves", "getAlexCaves()Z", 0) as PropertyReference0),
      Reflection.property0(PropertyReference0Impl(INSTANCE, LoadedMods.class, "immersivePortals", "getImmersivePortals()Z", 0) as PropertyReference0),
      Reflection.property0(PropertyReference0Impl(INSTANCE, LoadedMods.class, "create", "getCreate()Z", 0) as PropertyReference0),
      Reflection.property0(PropertyReference0Impl(INSTANCE, LoadedMods.class, "oldCreate", "getOldCreate()Z", 0) as PropertyReference0),
      Reflection.property0(PropertyReference0Impl(INSTANCE, LoadedMods.class, "sableCompanion", "getSableCompanion()Z", 0) as PropertyReference0),
      Reflection.property0(PropertyReference0Impl(INSTANCE, LoadedMods.class, "sable", "getSable()Z", 0) as PropertyReference0)
   );

   @JvmStatic
   public final val iris: Boolean by LoadedMods.CompatInfo("net.coderbot.iris.Iris")
      public final get() {
         return iris$delegate.getValue(INSTANCE, $$delegatedProperties[0])
      }


   @JvmStatic
   public final val weather2: Boolean by LoadedMods.CompatInfo("weather2.Weather")
      public final get() {
         return weather2$delegate.getValue(INSTANCE, $$delegatedProperties[1])
      }


   @JvmStatic
   public final val alexCaves: Boolean by LoadedMods.CompatInfo("com.github.alexmodguy.alexscaves.AlexsCaves")
      public final get() {
         return alexCaves$delegate.getValue(INSTANCE, $$delegatedProperties[2])
      }


   @JvmStatic
   public final val immersivePortals: Boolean by LoadedMods.CompatInfo("qouteall.imm_ptl.core.IPModMain")
      public final get() {
         return immersivePortals$delegate.getValue(INSTANCE, $$delegatedProperties[3])
      }


   @JvmStatic
   public final val create: Boolean by LoadedMods.CompatInfo("com.simibubi.create.AllMountedDispenseItemBehaviors")
      public final get() {
         return create$delegate.getValue(INSTANCE, $$delegatedProperties[4])
      }


   @JvmStatic
   public final val oldCreate: Boolean by LoadedMods.CompatInfo("com.simibubi.create.foundation.render.AllInstanceFormats")
      public final get() {
         return oldCreate$delegate.getValue(INSTANCE, $$delegatedProperties[5])
      }


   @JvmStatic
   public final val sableCompanion: Boolean by LoadedMods.CompatInfo("dev.ryanhcode.sable.companion.SableCompanion")
      public final get() {
         return sableCompanion$delegate.getValue(INSTANCE, $$delegatedProperties[6])
      }


   @JvmStatic
   public final val sable: Boolean by LoadedMods.CompatInfo("dev.ryanhcode.sable.companion.SableCompanion")
      public final get() {
         return sable$delegate.getValue(INSTANCE, $$delegatedProperties[7])
      }


   @JvmStatic
   public final val flywheel: org.valkyrienskies.mod.compat.LoadedMods.FlywheelVersion by LazyKt.lazy({ 
      var var0: LoadedMods.FlywheelVersion
      try {
         Class.forName("dev.engine_room.flywheel.backend.FlwBackend")
         var0 = LoadedMods.FlywheelVersion.V1
      } catch (var5: ClassNotFoundException) {
         var var2: LoadedMods.FlywheelVersion
         try {
            Class.forName("com.jozufozu.flywheel.Flywheel")
            var2 = LoadedMods.FlywheelVersion.V06
         } catch (var4: ClassNotFoundException) {
            var2 = LoadedMods.FlywheelVersion.NONE
         }

         var0 = var2
      }

      var0
   })
      public final get() {
         return flywheel$delegate.getValue() as LoadedMods.FlywheelVersion
      }


   @JvmStatic
   public final val bluemap: String by LazyKt.lazy({ 
      var claz: java.lang.String
      try {
         val var10000: Any = Class.forName("de.bluecolored.bluemap.core.BlueMap").getField("VERSION").get(null)
         claz = var10000 as java.lang.String
      } catch (var2: ClassNotFoundException) {
         claz = "NONE"
      }

      claz
   })
      public final get() {
         return bluemap$delegate.getValue() as java.lang.String
      }


   public enum class BluemapVersion {
      V53,
      V512,
      NONE;

      @JvmStatic
      fun getEntries(): EnumEntries<LoadedMods.BluemapVersion> {
         $ENTRIES
      }

      @JvmStatic
      @JvmSynthetic
      fun `$values`(): Array<LoadedMods.BluemapVersion> {
         arrayOf(V53, V512, NONE)
      }
   }

   public class CompatInfo(className: String) : ReadOnlyProperty<Object, java.lang.Boolean> {
      private final val className: String
      private final var isLoaded: Boolean?

      init {
         this.className = className
      }

      public open operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
         if (this.isLoaded == null) {
            val var5: LoadedMods.CompatInfo = this

            var var3: Boolean
            var var10000: LoadedMods.CompatInfo
            try {
               var10000 = var5
               Class.forName(this.className, false, LoadedMods.class.getClassLoader())
               var3 = true
            } catch (var6: ClassNotFoundException) {
               var10000 = this
               var3 = false
            }

            var10000.isLoaded = var3
         }

         val var7: java.lang.Boolean = this.isLoaded
         return var7
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      fun getValue(thisRef: Any, property: KProperty) {
         this.getValue(thisRef, property)
      }
   }

   public enum class FlywheelVersion {
      V1,
      V06,
      NONE;

      @JvmStatic
      fun getEntries(): EnumEntries<LoadedMods.FlywheelVersion> {
         $ENTRIES
      }

      @JvmStatic
      @JvmSynthetic
      fun `$values`(): Array<LoadedMods.FlywheelVersion> {
         arrayOf(V1, V06, NONE)
      }
   }
}
