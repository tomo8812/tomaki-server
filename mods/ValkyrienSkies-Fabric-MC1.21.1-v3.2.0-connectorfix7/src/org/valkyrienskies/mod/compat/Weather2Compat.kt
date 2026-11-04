package org.valkyrienskies.mod.compat

import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import net.minecraft.class_2338
import net.minecraft.class_2374
import net.minecraft.class_243
import net.minecraft.class_3218
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.DragController
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.mod.common.ValkyrienSkiesMod
import org.valkyrienskies.mod.common.config.VSGameConfig
import org.valkyrienskies.mod.common.util.GameToPhysicsAdapter
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt
import weather2.ServerTickHandler
import weather2.weathersystem.WeatherManagerServer
import weather2.weathersystem.storm.StormObject
import weather2.weathersystem.storm.WeatherObject

@SourceDebugExtension(["SMAP\nWeather2Compat.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Weather2Compat.kt\norg/valkyrienskies/mod/compat/Weather2Compat\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,90:1\n1863#2:91\n1863#2,2:92\n1864#2:94\n*S KotlinDebug\n*F\n+ 1 Weather2Compat.kt\norg/valkyrienskies/mod/compat/Weather2Compat\n*L\n27#1:91\n72#1:92,2\n27#1:94\n*E\n"])
public object Weather2Compat {
   public fun tick(level: class_3218) {
      if (VSGameConfig.SERVER.Weather2.enableWeatherCompat) {
         val mgr: WeatherManagerServer = ServerTickHandler.getWeatherManagerFor(level.method_27983())
         val windMult: Float = VSGameConfig.SERVER.Weather2.windMultiplier / 1000
         val windMax: Float = VSGameConfig.SERVER.Weather2.windMaxVel
         val stormDampen: Float = 1.0F - VSGameConfig.SERVER.Weather2.stormDampening
         val stormRange: Double = VSGameConfig.SERVER.Weather2.stormRange

         for (`element$iv` in shipObjectWorld.loadedShips) {
            val ship: LoadedServerShip = `element$iv` as LoadedServerShip
            val vec: Vector3d = Vector3d()
            val forces: GameToPhysicsAdapter = ValkyrienSkiesMod.getOrCreateGTPA(ship.getChunkClaimDimension())
            val com: Vector3dc = ship.getInertiaData().centerOfMassInShip
            val pos: class_243 = VectorConversionsMCKt.toMinecraft(vec)
            ship.getShipToWorld().transformPosition(com, vec)
            var var10000: DragController = ship.dragController
            if (var10000 != null) {
               val var10001: Vector3d = Vector3d(0.0, 0.0, -1.0).rotateY(Math.toRadians((double)mgr.getWindManager().getWindAngle(pos)))
               var10000.setWindDirection(var10001, "WEATHER2")
            }

            var10000 = ship.dragController
            if (var10000 != null) {
               var10000.setWindSpeed((double)mgr.getWindManager().getWindSpeed(class_2338.method_49638(pos as class_2374)), "WEATHER2")
            }

            val motion: class_243 = VectorConversionsMCKt.toMinecraft(ship.getVelocity())
            val mass: Double = ship.getInertiaData().getMass()
            val forcePlusMotion: ObjectRef = ObjectRef()
            forcePlusMotion.element = mgr.getWindManager().applyWindForceImpl(pos, motion, (float)mass, windMult, windMax, true)
            val var29: java.util.List = mgr.getStormsAround(pos, stormRange)

            for (`element$ivx` in var29) {
               val it: WeatherObject = `element$ivx` as WeatherObject
               if (`element$ivx` as WeatherObject is StormObject && ((`element$ivx` as WeatherObject) as StormObject).getTornadoFunnelSimple() != null) {
                  forcePlusMotion.element = (it as StormObject)
                     .spinObject(pos, forcePlusMotion.element as class_243, false, stormDampen, stormDampen, true, 0.0F)
                     tick$lambda$1$applyForcePlusMotion(vec, forcePlusMotion, ship, mass, forces)
               }
            }
         }
      }
   }
}
