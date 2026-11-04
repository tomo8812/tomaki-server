package org.valkyrienskies.mod.fabric.common

import com.electronwill.nightconfig.core.CommentedConfig
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents
import java.util.Arrays
import java.util.Locale
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.class_1311
import net.minecraft.class_1747
import net.minecraft.class_1792
import net.minecraft.class_2248
import net.minecraft.class_2314
import net.minecraft.class_2319
import net.minecraft.class_2378
import net.minecraft.class_2561
import net.minecraft.class_2960
import net.minecraft.class_304
import net.minecraft.class_310
import net.minecraft.class_3264
import net.minecraft.class_3300
import net.minecraft.class_3695
import net.minecraft.class_5455
import net.minecraft.class_5498
import net.minecraft.class_7157
import net.minecraft.class_746
import net.minecraft.class_7923
import net.minecraft.class_897
import net.minecraft.class_1299.class_1300
import net.minecraft.class_1792.class_1793
import net.minecraft.class_2170.class_5364
import net.minecraft.class_3302.class_4045
import net.minecraft.class_5617.class_5618
import net.neoforged.fml.config.IConfigSpec
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.config.IConfigSpec.ILoadedConfig
import net.neoforged.fml.config.ModConfig.Type
import net.neoforged.neoforge.common.ModConfigSpec
import org.valkyrienskies.mod.client.EmptyRenderer
import org.valkyrienskies.mod.client.ShipCameraZoom
import org.valkyrienskies.mod.client.ShipDebugRender
import org.valkyrienskies.mod.client.ShipMountPerspective
import org.valkyrienskies.mod.common.ValkyrienSkiesMod
import org.valkyrienskies.mod.common.block.TestChairBlock
import org.valkyrienskies.mod.common.block.TestFlapBlock
import org.valkyrienskies.mod.common.block.TestHingeBlock
import org.valkyrienskies.mod.common.block.TestWingBlock
import org.valkyrienskies.mod.common.command.VSCommands
import org.valkyrienskies.mod.common.command.arguments.RelativeVector3Argument
import org.valkyrienskies.mod.common.command.arguments.ShipArgument
import org.valkyrienskies.mod.common.command.arguments.ShipArgumentInfo
import org.valkyrienskies.mod.common.config.MassDatapackResolver
import org.valkyrienskies.mod.common.config.VSClientConfig
import org.valkyrienskies.mod.common.config.VSClientConfigLoader
import org.valkyrienskies.mod.common.config.VSConfigUpdater
import org.valkyrienskies.mod.common.config.VSEntityHandlerDataLoader
import org.valkyrienskies.mod.common.config.VSGameConfig
import org.valkyrienskies.mod.common.config.VSKeyBindings
import org.valkyrienskies.mod.common.entity.ShipMountingEntity
import org.valkyrienskies.mod.common.hooks.VSGameEvents
import org.valkyrienskies.mod.common.item.ShipAssemblerItem
import org.valkyrienskies.mod.common.item.ShipCreatorItem
import org.valkyrienskies.mod.common.networking.PacketRequestPassengerSeat

@SourceDebugExtension(["SMAP\nValkyrienSkiesModFabric.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ValkyrienSkiesModFabric.kt\norg/valkyrienskies/mod/fabric/common/ValkyrienSkiesModFabric\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,433:1\n1#2:434\n1863#3,2:435\n1863#3,2:437\n*S KotlinDebug\n*F\n+ 1 ValkyrienSkiesModFabric.kt\norg/valkyrienskies/mod/fabric/common/ValkyrienSkiesModFabric\n*L\n281#1:435,2\n299#1:437,2\n*E\n"])
public class ValkyrienSkiesModFabric : ModInitializer {
   private final val INFLUENCE_FACE_NAMES: List<String> = CollectionsKt.listOf(arrayOf("Top", "Bottom", "Left", "Right", "Front", "Back"))

   public open fun onInitialize() {
      if (!hasInitialized.getAndSet(true)) {
         ValkyrienSkiesMod.INSTANCE.TEST_CHAIR = TestChairBlock() as class_2248
         ValkyrienSkiesMod.INSTANCE.TEST_HINGE = TestHingeBlock.INSTANCE as class_2248
         ValkyrienSkiesMod.INSTANCE.TEST_FLAP = TestFlapBlock() as class_2248
         ValkyrienSkiesMod.INSTANCE.TEST_WING = TestWingBlock() as class_2248
         ValkyrienSkiesMod.INSTANCE.CONNECTION_CHECKER_ITEM = class_1792(class_1793())
         ValkyrienSkiesMod.INSTANCE.SHIP_CREATOR_ITEM = ShipCreatorItem(class_1793(), { 
            1.0
         }, { 
            VSGameConfig.SERVER.minScaling
         }) as class_1792
         ValkyrienSkiesMod.INSTANCE.SHIP_ASSEMBLER_ITEM = ShipAssemblerItem(class_1793()) as class_1792
         ValkyrienSkiesMod.INSTANCE.AREA_ASSEMBLER_ITEM = class_1792(class_1793())
         ValkyrienSkiesMod.INSTANCE.SHIP_CREATOR_ITEM_SMALLER = ShipCreatorItem(class_1793(), { 
            VSGameConfig.SERVER.miniShipSize
         }, { 
            VSGameConfig.SERVER.minScaling
         }) as class_1792
         ValkyrienSkiesMod.INSTANCE.PHYSICS_ENTITY_CREATOR_ITEM = class_1792(class_1793())
         ValkyrienSkiesMod.INSTANCE.SHIP_MOUNTING_ENTITY_TYPE = class_1300.method_5903(
               org/valkyrienskies/mod/fabric/common/ValkyrienSkiesModFabric##Lambda_4_276(), class_1311.field_17715
            )
            .method_17687(0.3F, 0.3F)
            .method_5905(class_2960.method_60655("valkyrienskies", "ship_mounting_entity").toString())
            ValkyrienSkiesMod.INSTANCE.TEST_HINGE_BLOCK_ENTITY_TYPE = FabricBlockEntityTypeBuilder.create(
               org/valkyrienskies/mod/fabric/common/ValkyrienSkiesModFabric##Lambda_5_326(), arrayOf(ValkyrienSkiesMod.INSTANCE.TEST_HINGE)
            )
            .build()
            if (FabricLoader.getInstance().getEnvironmentType() === EnvType.CLIENT) {
            VSClientConfigLoader.loadOrCreate()
            this.onInitializeClient()
         }

         ValkyrienSkiesMod.INSTANCE.init()
         ArgumentTypeRegistry.registerArgumentType(class_2960.method_60655("valkyrienskies", "ship_argument"), ShipArgument::class.java, ShipArgumentInfo())
         ArgumentTypeRegistry.registerArgumentType(
            class_2960.method_60655("valkyrienskies", "relative_vector3_argument"),
            RelativeVector3Argument::class.java,
            class_2319.method_41999(org/valkyrienskies/mod/fabric/common/ValkyrienSkiesModFabric##Lambda_6_398()) as class_2314
         )
         NeoForgeConfigRegistry.INSTANCE
            .register("valkyrienskies", Type.STARTUP, VSConfigUpdater.INSTANCE.CORE_SERVER_SPEC as IConfigSpec, "valkyrienskies-core-server.toml")
            NeoForgeConfigRegistry.INSTANCE
            .register("valkyrienskies", Type.SERVER, VSConfigUpdater.INSTANCE.SERVER_SPEC as IConfigSpec, "valkyrienskies-server.toml")
            NeoForgeConfigRegistry.INSTANCE
            .register("valkyrienskies", Type.COMMON, VSConfigUpdater.INSTANCE.COMMON_SPEC as IConfigSpec, "valkyrienskies-common.toml")
            NeoForgeConfigRegistry.INSTANCE
            .register("valkyrienskies", Type.CLIENT, VSConfigUpdater.INSTANCE.CLIENT_SPEC as IConfigSpec, "valkyrienskies-client.toml")
            val applyConfig: Function1 = lambda_5@{ config: ModConfig ->
            val var3: IConfigSpec = config.getSpec()
            val var10000: ModConfigSpec = var3 as? ModConfigSpec
            if ((var3 as? ModConfigSpec) == null) {
               return@lambda_5 Unit.INSTANCE
            } else {
               val var4: ILoadedConfig = config.getLoadedConfig()
               if (var4 != null) {
                  val var5: CommentedConfig = var4.config()
                  if (var5 != null) {
                     VSConfigUpdater.INSTANCE.applyFromConfigLoad(var10000, { key: java.lang.String ->
                        `$loaded`.get(key)
                     })
                     return@lambda_5 Unit.INSTANCE
                  }
               }

               return@lambda_5 Unit.INSTANCE
            }
         }
         NeoForgeModConfigEvents.loading("valkyrienskies").register({ it: ModConfig ->
            `$applyConfig`(it)
         })
         NeoForgeModConfigEvents.reloading("valkyrienskies").register({ it: ModConfig ->
            `$applyConfig`(it)
         })
         this.registerBlockAndItem("test_chair", ValkyrienSkiesMod.INSTANCE.TEST_CHAIR)
         this.registerBlockAndItem("test_hinge", ValkyrienSkiesMod.INSTANCE.TEST_HINGE)
         this.registerBlockAndItem("test_flap", ValkyrienSkiesMod.INSTANCE.TEST_FLAP)
         this.registerBlockAndItem("test_wing", ValkyrienSkiesMod.INSTANCE.TEST_WING)
         class_2378.method_10230(
            class_7923.field_41178 as class_2378,
            class_2960.method_60655("valkyrienskies", "connection_checker"),
            ValkyrienSkiesMod.INSTANCE.CONNECTION_CHECKER_ITEM
         )
         class_2378.method_10230(
            class_7923.field_41178 as class_2378, class_2960.method_60655("valkyrienskies", "area_assembler"), ValkyrienSkiesMod.INSTANCE.AREA_ASSEMBLER_ITEM
         )
         class_2378.method_10230(
            class_7923.field_41178 as class_2378, class_2960.method_60655("valkyrienskies", "ship_assembler"), ValkyrienSkiesMod.INSTANCE.SHIP_ASSEMBLER_ITEM
         )
         class_2378.method_10230(
            class_7923.field_41178 as class_2378, class_2960.method_60655("valkyrienskies", "ship_creator"), ValkyrienSkiesMod.INSTANCE.SHIP_CREATOR_ITEM
         )
         class_2378.method_10230(
            class_7923.field_41178 as class_2378,
            class_2960.method_60655("valkyrienskies", "ship_creator_smaller"),
            ValkyrienSkiesMod.INSTANCE.SHIP_CREATOR_ITEM_SMALLER
         )
         class_2378.method_10230(
            class_7923.field_41178 as class_2378,
            class_2960.method_60655("valkyrienskies", "physics_entity_creator"),
            ValkyrienSkiesMod.INSTANCE.PHYSICS_ENTITY_CREATOR_ITEM
         )
         class_2378.method_10230(
            class_7923.field_41177 as class_2378,
            class_2960.method_60655("valkyrienskies", "ship_mounting_entity"),
            ValkyrienSkiesMod.INSTANCE.SHIP_MOUNTING_ENTITY_TYPE
         )
         class_2378.method_10230(
            class_7923.field_41181,
            class_2960.method_60655("valkyrienskies", "test_hinge_block_entity"),
            ValkyrienSkiesMod.INSTANCE.TEST_HINGE_BLOCK_ENTITY_TYPE
         )
         class_2378.method_39197(class_7923.field_44687, ValkyrienSkiesMod.INSTANCE.VS_CREATIVE_TAB, ValkyrienSkiesMod.INSTANCE.createCreativeTab())
         CommandRegistrationCallback.EVENT.register({ dispatcher: CommandDispatcher, d: class_7157, var2: class_5364 ->
            val var10000: VSCommands = VSCommands.INSTANCE
            var10000.registerServerCommands(dispatcher)
         })
         val loader1: MassDatapackResolver.VSMassDataLoader = MassDatapackResolver.INSTANCE.loader
         val loader2: VSEntityHandlerDataLoader = VSEntityHandlerDataLoader.INSTANCE
         ResourceManagerHelper.get(class_3264.field_14190)
            .registerReloadListener(
               object : IdentifiableResourceReloadListener {
                  public open fun getFabricId(): class_2960 {
                     val var10000: class_2960 = class_2960.method_60655("valkyrienskies", "vs_mass")
                     return var10000
                  }

                  fun method_25931(
                     stage: class_4045,
                     resourceManager: class_3300,
                     preparationsProfiler: class_3695,
                     reloadProfiler: class_3695,
                     backgroundExecutor: Executor,
                     gameExecutor: Executor
                  ): CompletableFuture<Void> {
                     val var7: CompletableFuture = loader1.method_25931(
                           stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor
                        )
                        .thenAcceptBoth(
                           loader2.method_25931(stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor),
                           { p0: Any, p1: Any ->
                              `$tmp0`(p0, p1)
                           }
                        )
                        var7
                  }
               }
            )
            CommonLifecycleEvents.TAGS_LOADED.register({ var0: class_5455, var1: Boolean ->
            VSGameEvents.INSTANCE.tagsAreLoaded.emit(Unit.INSTANCE)
         })
         VSDataComponents.INSTANCE.registerDataComponents()
      }
   }

   private fun onInitializeClient() {
      ClientCommandRegistrationCallback.EVENT
         .register(
            { dispatcher: CommandDispatcher, var2: class_7157 ->
               dispatcher.register(
                  ((ClientCommandManager.literal("vs")
                           .then(
                              ClientCommandManager.literal("influence-border")
                                 .then(
                                    ClientCommandManager.argument("enabled", BoolArgumentType.bool() as ArgumentType)
                                       .executes(
                                          { ctx: CommandContext ->
                                             val enabled: Boolean = BoolArgumentType.getBool(ctx, "enabled")
                                             ShipDebugRender.influenceBorder = enabled
                                             (ctx.getSource() as FabricClientCommandSource)
                                                .sendFeedback(
                                                   class_2561.method_43470("VS influence border ${if (enabled) "ENABLED" else "DISABLED"}") as class_2561
                                                )
                                                1
                                          }
                                       )
                                 )
                           ) as LiteralArgumentBuilder)
                        .then(
                           ClientCommandManager.literal("expand-influence")
                              .then(
                                 ClientCommandManager.argument("amount", DoubleArgumentType.doubleArg(0.0) as ArgumentType)
                                    .then(
                                       ClientCommandManager.argument("direction", StringArgumentType.word() as ArgumentType)
                                          .suggests({ var1: CommandContext, builder: SuggestionsBuilder ->
                                             for (`element$iv` in `this$0`.INFLUENCE_FACE_NAMES) {
                                                builder.suggest(`element$iv` as java.lang.String)
                                             }

                                             builder.buildFuture()
                                          })
                                          .executes({ ctx: CommandContext ->
                                             var var10001: FabricClientCommandSource = (FabricClientCommandSource)ctx.getSource()
                                             var10001 = var10001
                                             val var10002: java.lang.String = StringArgumentType.getString(ctx, "direction")
                                             `this$0`.adjustInfluenceExtend(var10001, var10002, DoubleArgumentType.getDouble(ctx, "amount"))
                                          })
                                    )
                              )
                        ) as LiteralArgumentBuilder)
                     .then(
                        ClientCommandManager.literal("contract-influence")
                           .then(
                              ClientCommandManager.argument("amount", DoubleArgumentType.doubleArg(0.0) as ArgumentType)
                                 .then(
                                    ClientCommandManager.argument("direction", StringArgumentType.word() as ArgumentType)
                                       .suggests({ var1: CommandContext, builder: SuggestionsBuilder ->
                                          for (`element$iv` in `this$0`.INFLUENCE_FACE_NAMES) {
                                             builder.suggest(`element$iv` as java.lang.String)
                                          }

                                          builder.buildFuture()
                                       })
                                       .executes({ ctx: CommandContext ->
                                          var var10001: FabricClientCommandSource = (FabricClientCommandSource)ctx.getSource()
                                          var10001 = var10001
                                          val var10002: java.lang.String = StringArgumentType.getString(ctx, "direction")
                                          `this$0`.adjustInfluenceExtend(var10001, var10002, -DoubleArgumentType.getDouble(ctx, "amount"))
                                       })
                                 )
                           )
                     ) as LiteralArgumentBuilder
               )
            }
         )
         EntityRendererRegistry.register(ValkyrienSkiesMod.INSTANCE.SHIP_MOUNTING_ENTITY_TYPE, { context: class_5618 ->
         EmptyRenderer(context) as class_897
      })
      VSKeyBindings.INSTANCE.clientSetup({ it: class_304 ->
         KeyBindingHelper.registerKeyBinding(it)
      })
      ClientTickEvents.START_CLIENT_TICK.register({ client: class_310 ->
         if ((if (client.field_1724 != null) client.field_1724.method_5854() else null) is ShipMountingEntity) {
            ShipMountPerspective.tickMounted()
            if (client.field_1755 == null && client.method_18506() == null) {
               while (client.field_1690.field_1824.method_1436()) {
                  ShipMountPerspective.cycleMounted(client)
               }
            }
         }
      })
      ClientTickEvents.END_CLIENT_TICK.register({ client: class_310 ->
         val player: class_746 = client.field_1724

         while (VSKeyBindings.INSTANCE.shipSeat.get().method_1436()) {
            if (player != null && !player.method_5765()) {
               vsCore.simplePacketNetworking.sendToServer1(PacketRequestPassengerSeat(false, 1, null))
            }
         }

         val riding: Boolean = (if (player != null) player.method_5854() else null) is ShipMountingEntity
         if (`$wasRidingShipMount`.element && !riding) {
            ShipCameraZoom.reset()
            ShipMountPerspective.reset()
            if (player != null && !client.field_1690.method_31044().method_31034()) {
               client.field_1690.method_31043(class_5498.field_26664)
            }
         }

         `$wasRidingShipMount`.element = riding
      })
      ShipInfluenceBorderRenderer.INSTANCE.register()
   }

   private fun adjustInfluenceExtend(source: FabricClientCommandSource, direction: String, delta: Double): Int {
      run label102@{
         val c: VSClientConfig.Client = VSClientConfig.CLIENT
var var10000: java.lang.String = direction.toLowerCase(Locale.ROOT)
var var22: Double
         when (var10000.hashCode()) {
            -1383228885 -> {
               if (!var10000.equals("bottom")) {
                  return@label102
               }

               var22 = c.influenceExtendBottom
               break
            }
            115029 -> {
               if (!var10000.equals("top")) {
                  return@label102
               }

               var22 = c.influenceExtendTop
               break
            }
            3015911 -> {
               if (!var10000.equals("back")) {
                  return@label102
               }

               var22 = c.influenceExtendBack
               break
            }
            3317767 -> {
               if (!var10000.equals("left")) {
                  return@label102
               }

               var22 = c.influenceExtendLeft
               break
            }
            97705513 -> {
               if (!var10000.equals("front")) {
                  return@label102
               }

               var22 = c.influenceExtendFront
               break
            }
            108511772 -> {
               if (var10000.equals("right")) {
                  var22 = c.influenceExtendRight
                  break
               }
            }
            else -> return@label102
         }

         val var15: Double = RangesKt.coerceAtLeast(var22 + delta, 0.0)
var10000 = direction.toLowerCase(Locale.ROOT)
         when (var10000.hashCode()) {
            -1383228885 -> {
               if (var10000.equals("bottom")) {
                  c.influenceExtendBottom = var15
               }
            }
            115029 -> {
               if (var10000.equals("top")) {
                  c.influenceExtendTop = var15
               }
            }
            3015911 -> {
               if (var10000.equals("back")) {
                  c.influenceExtendBack = var15
               }
            }
            3317767 -> {
               if (var10000.equals("left")) {
                  c.influenceExtendLeft = var15
               }
            }
            97705513 -> {
               if (var10000.equals("front")) {
                  c.influenceExtendFront = var15
               }
            }
            108511772 -> {
               if (var10000.equals("right")) {
                  c.influenceExtendRight = var15
               }
            }
            else -> {}
         }

         VSClientConfigLoader.save()
         var10000 = direction.toLowerCase(Locale.ROOT)
         if (var10000.length() > 0) {
            val var25: StringBuilder = StringBuilder()
            val var26: java.lang.String = java.lang.String.valueOf(var10000.charAt(0))
            val var27: java.lang.String = var26.toUpperCase(Locale.ROOT)
            val var28: StringBuilder = var25.append((Object)var27)
            val var10001: java.lang.String = var10000.substring(1)
            var10000 = var28.append(var10001).toString()
         } else {
            var10000 = var10000
         }

         val var17: java.lang.String = "Influence border: $var10000 %.1f -> %.1f blocks"
         val var20: Array<Any> = arrayOf(var22, var15)
         val var30: java.lang.String = java.lang.String.format(var17, Arrays.copyOf(var20, var20.length))
         source.sendFeedback(class_2561.method_43470(var30) as class_2561)
         return 1
      }

      source.sendError(class_2561.method_43470("Unknown direction '$direction' -- use Top, Bottom, Left, Right, Front, or Back.") as class_2561)
      return 0
   }

   private fun registerBlockAndItem(registryName: String, block: class_2248): class_1792 {
      class_2378.method_10230(class_7923.field_41175 as class_2378, class_2960.method_60655("valkyrienskies", registryName), block)
      val item: class_1747 = class_1747(block, class_1793())
      class_2378.method_10230(class_7923.field_41178 as class_2378, class_2960.method_60655("valkyrienskies", registryName), item)
      return item as class_1792
   }

   public companion object {
      private final val hasInitialized: AtomicBoolean
   }
}
