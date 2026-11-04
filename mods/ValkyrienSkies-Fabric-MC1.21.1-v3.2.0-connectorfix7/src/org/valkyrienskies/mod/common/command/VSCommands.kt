package org.valkyrienskies.mod.common.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import kotlin.jvm.internal.PropertyReference1
import kotlin.jvm.internal.PropertyReference1Impl
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KProperty
import net.minecraft.class_2168
import net.minecraft.class_2170
import org.apache.logging.log4j.Logger
import org.valkyrienskies.mod.common.command.commands.BackendCommand
import org.valkyrienskies.mod.common.command.commands.DeleteCommand
import org.valkyrienskies.mod.common.command.commands.DesnowCommand
import org.valkyrienskies.mod.common.command.commands.DryCommand
import org.valkyrienskies.mod.common.command.commands.GetAirCommand
import org.valkyrienskies.mod.common.command.commands.GetGravityCommand
import org.valkyrienskies.mod.common.command.commands.GetShipCommand
import org.valkyrienskies.mod.common.command.commands.KeepActiveCommand
import org.valkyrienskies.mod.common.command.commands.RemassCommand
import org.valkyrienskies.mod.common.command.commands.RenameCommand
import org.valkyrienskies.mod.common.command.commands.ScaleCommand
import org.valkyrienskies.mod.common.command.commands.SpawnShipCubeCommand
import org.valkyrienskies.mod.common.command.commands.SplittingCommand
import org.valkyrienskies.mod.common.command.commands.StaticCommand
import org.valkyrienskies.mod.common.command.commands.TeleportCommand
import org.valkyrienskies.mod.util.ClassLogger
import org.valkyrienskies.mod.util.LoggingKt

public object VSCommands {
   @JvmStatic
   @JvmSynthetic
   KProperty<Object>[] $$delegatedProperties = arrayOf(
      Reflection.property1(PropertyReference1Impl(VSCommands.class, "LOGGER", "getLOGGER()Lorg/apache/logging/log4j/Logger;", 0) as PropertyReference1)
   );

   private final val LOGGER: Logger by LoggingKt.logger().provideDelegate_j8jxBIw/* $VF was: provideDelegate-j8jxBIw */(INSTANCE, $$delegatedProperties[0])
      private final get() {
         return ClassLogger.getValue_impl/* $VF was: getValue-impl */(LOGGER$delegate, this, $$delegatedProperties[0])
      }


   public fun registerServerCommands(dispatcher: CommandDispatcher<class_2168>) {
      val vs: LiteralArgumentBuilder = class_2170.method_9247("vs")
      val var10000: BackendCommand = BackendCommand.INSTANCE
      var10000.register(vs)
      val var18: DeleteCommand = DeleteCommand.INSTANCE
      var18.register(vs)
      val var19: DesnowCommand = DesnowCommand.INSTANCE
      var19.register(vs)
      val var20: DryCommand = DryCommand.INSTANCE
      var20.register(vs)
      val var21: GetAirCommand = GetAirCommand.INSTANCE
      var21.register(vs)
      val var22: GetGravityCommand = GetGravityCommand.INSTANCE
      var22.register(vs)
      val var23: GetShipCommand = GetShipCommand.INSTANCE
      var23.register(vs)
      val var24: KeepActiveCommand = KeepActiveCommand.INSTANCE
      var24.register(vs)
      val var25: RemassCommand = RemassCommand.INSTANCE
      var25.register(vs)
      val var26: RenameCommand = RenameCommand.INSTANCE
      var26.register(vs)
      val var27: ScaleCommand = ScaleCommand.INSTANCE
      var27.register(vs)
      val var28: SplittingCommand = SplittingCommand.INSTANCE
      var28.register(vs)
      val var29: SpawnShipCubeCommand = SpawnShipCubeCommand.INSTANCE
      var29.register(vs)
      val var30: StaticCommand = StaticCommand.INSTANCE
      var30.register(vs)
      val var31: TeleportCommand = TeleportCommand.INSTANCE
      var31.register(vs)
      dispatcher.register(vs)
   }

   public fun registerClientCommands(dispatcher: CommandDispatcher<class_2168>) {
   }
}
