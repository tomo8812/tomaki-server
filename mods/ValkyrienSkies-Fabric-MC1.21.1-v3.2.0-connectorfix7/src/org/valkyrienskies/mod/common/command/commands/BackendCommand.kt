package org.valkyrienskies.mod.common.command.commands

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.class_2168
import net.minecraft.class_2170
import net.minecraft.class_2561
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue
import org.valkyrienskies.core.impl.api_impl.config.ConfigPhysicsBackendType
import org.valkyrienskies.core.impl.config.VSCoreConfig
import org.valkyrienskies.mod.common.config.VSGameConfig

public object BackendCommand {
   private const val LOD_CURRENT_MESSAGE: String = "command.valkyrienskies.lod.current"
   private const val LOD_SET_MESSAGE: String = "command.valkyrienskies.lod.set"
   private const val BACKEND_CURRENT_MESSAGE: String = "command.valkyrienskies.backend.current"
   private const val BACKEND_SET_MESSAGE: String = "command.valkyrienskies.backend.set"
   private const val LOD_DISABLED_MESSAGE: String = "command.valkyrienskies.lod.disabled"

   public fun register(vs: LiteralArgumentBuilder<class_2168>) {
      vs.then(
         ((class_2170.method_9247("backend").requires({ it: class_2168 ->
               it.method_9259(VSGameConfig.SERVER.Commands.changeBackendCommandPerms)
            }) as LiteralArgumentBuilder).then(((((class_2170.method_9247("engine").then(class_2170.method_9247("krunch").executes({ it: CommandContext ->
               VSCoreConfig.SERVER.physics.physicsBackend = ConfigPhysicsBackendType.KRUNCH_CLASSIC
               val var10000: Any = forgeConfigValuesMap.get("physicsBackend")
               (var10000 as ConfigValue).set("KRUNCH_CLASSIC")
               (it.getSource() as class_2168).method_9226({ 
                  class_2561.method_43469("command.valkyrienskies.backend.set", arrayOf(VSCoreConfig.SERVER.physics.physicsBackend.name())) as class_2561
               }, true)
               1
            })) as LiteralArgumentBuilder).then(class_2170.method_9247("DEFAULT").executes({ it: CommandContext ->
               VSCoreConfig.SERVER.physics.physicsBackend = ConfigPhysicsBackendType.KRUNCH_CLASSIC
               val var10000: Any = forgeConfigValuesMap.get("physicsBackend")
               (var10000 as ConfigValue).set("KRUNCH_CLASSIC")
               (it.getSource() as class_2168).method_9226({ 
                  class_2561.method_43469("command.valkyrienskies.backend.set", arrayOf(VSCoreConfig.SERVER.physics.physicsBackend.name())) as class_2561
               }, true)
               1
            })) as LiteralArgumentBuilder).then(class_2170.method_9247("physx").executes({ it: CommandContext ->
               VSCoreConfig.SERVER.physics.physicsBackend = ConfigPhysicsBackendType.KRUNCH_PHYSX
               val var10000: Any = forgeConfigValuesMap.get("physicsBackend")
               (var10000 as ConfigValue).set("KRUNCH_PHYSX")
               (it.getSource() as class_2168).method_9226({ 
                  class_2561.method_43469("command.valkyrienskies.backend.set", arrayOf(VSCoreConfig.SERVER.physics.physicsBackend.name())) as class_2561
               }, true)
               1
            })) as LiteralArgumentBuilder).then(class_2170.method_9247("jolt").executes({ it: CommandContext ->
               VSCoreConfig.SERVER.physics.physicsBackend = ConfigPhysicsBackendType.KRUNCH_JOLT
               val var10000: Any = forgeConfigValuesMap.get("physicsBackend")
               (var10000 as ConfigValue).set("KRUNCH_JOLT")
               (it.getSource() as class_2168).method_9226({ 
                  class_2561.method_43469("command.valkyrienskies.backend.set", arrayOf(VSCoreConfig.SERVER.physics.physicsBackend.name())) as class_2561
               }, true)
               1
            })) as LiteralArgumentBuilder).executes({ it: CommandContext ->
               (it.getSource() as class_2168).method_9226({ 
                  class_2561.method_43469("command.valkyrienskies.backend.current", arrayOf(VSCoreConfig.SERVER.physics.physicsBackend.name())) as class_2561
               }, true)
               1
            })) as LiteralArgumentBuilder)
            .then(
               (class_2170.method_9247("lodDetail")
                     .then(class_2170.method_9244("amount", IntegerArgumentType.integer(-1) as ArgumentType).executes({ it: CommandContext ->
                        val amount: Int = IntegerArgumentType.getInteger(it, "amount")
                        VSCoreConfig.SERVER.physics.lodDetail = amount
                        val var10000: Any = forgeConfigValuesMap.get("lodDetail")
                        (var10000 as ConfigValue).set(amount)
                        (it.getSource() as class_2168).method_9226({ 
                           class_2561.method_43469("command.valkyrienskies.lod.set", arrayOf(`$msg`)) as class_2561
                        }, true)
                        1
                     })) as LiteralArgumentBuilder)
                  .executes({ it: CommandContext ->
                     val lod: Int = VSCoreConfig.SERVER.physics.lodDetail
                     (it.getSource() as class_2168).method_9226({ 
                        class_2561.method_43469("command.valkyrienskies.lod.current", arrayOf(`$msg`)) as class_2561
                     }, true)
                     1
                  })
            )
      )
   }
}
