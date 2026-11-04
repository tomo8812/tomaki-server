package org.sinytra.connector.transformer.transform;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.env.ctx.PatchResult;
import org.sinytra.adapter.transform.patch.MethodPatch;

public class MixinPatches {
   public static List<MethodPatch> getPriorityPatches() {
      return List.of(
         MethodPatch.builder()
            .targetClass("net/minecraft/world/item/ItemStack")
            .targetMethod("useOn")
            .targetInjectionPoint("INVOKE", "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;")
            .modifyTarget("connector_useOn")
            .replaceInjectionPoint("RETURN", null)
            .build(),
         MethodPatch.builder()
            .targetClass("net/minecraft/client/Minecraft")
            .targetMethod("<init>")
            .targetInjectionPoint("Lnet/fabricmc/loader/impl/game/minecraft/Hooks;startClient(Ljava/io/File;Ljava/lang/Object;)V")
            .modifyInjectionPoint("Ljava/lang/Thread;currentThread()Ljava/lang/Thread;")
            .build(),
         MethodPatch.builder()
            .targetClass("net/minecraft/server/Main")
            .targetMethod("main([Ljava/lang/String;)V")
            .targetInjectionPoint("Lnet/fabricmc/loader/impl/game/minecraft/Hooks;startServer(Ljava/io/File;Ljava/lang/Object;)V")
            .modifyInjectionPoint("Lnet/neoforged/neoforge/server/loading/ServerModLoader;load()V")
            .build(),
         MethodPatch.builder()
            .targetClass("net/minecraft/world/level/block/entity/BeaconBlockEntity")
            .targetMethod("tick")
            .targetInjectionPoint("INVOKE", "Lnet/minecraft/world/item/DyeColor;getTextureDiffuseColor()I")
            .modifyStatic(false)
            .extractMixin("net/neoforged/neoforge/common/extensions/IBlockExtension")
            .modifyTarget("connector_getTextureDiffuseColor")
            .modifyInjectionPoint("INVOKE", "Lnet/minecraft/world/item/DyeColor;getTextureDiffuseColor()I")
            .build()
      );
   }

   public static List<MethodPatch> getPatches() {
      List<Object> patches = List.of(
         MethodPatch.builder()
            .targetClass("net/minecraft/client/KeyMapping")
            .targetMethod("set")
            .targetInjectionPoint("TAIL", null)
            .modifyInjectionPoint("INVOKE", "Lnet/minecraft/client/KeyMapping;setDown(Z)V")
            .build(),
         MethodPatch.builder()
            .targetClass("net/minecraft/client/KeyMapping")
            .targetMethod("click")
            .targetInjectionPoint("TAIL", null)
            .modifyInjectionPoint("FIELD", "Lnet/minecraft/client/KeyMapping;clickCount:I")
            .build(),
         MethodPatch.builder()
            .targetClass("net/minecraft/world/level/block/piston/PistonStructureResolver")
            .targetMethod("isSticky")
            .modifyTarget("canStickTo(Lnet/minecraft/world/level/block/state/BlockState;)Z")
            .modifyStatic(false)
            .extractMixin("net/neoforged/neoforge/common/extensions/IBlockStateExtension")
            .build(),
         MethodPatch.builder()
            .targetClass("net/minecraft/world/entity/LivingEntity")
            .targetMethod("baseTick")
            .targetInjectionPoint("Lnet/minecraft/world/entity/LivingEntity;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z")
            .modifyTarget("onLivingBreathe")
            .modifyInjectionPoint("Lnet/neoforged/neoforge/fluids/FluidType;isAir()Z")
            .extractMixin("net/neoforged/neoforge/common/CommonHooks")
            .build(),
         MethodPatch.builder()
            .targetClass("net/minecraft/world/entity/LivingEntity")
            .targetMethod("goDownInWater()V")
            .targetConstant(-0.04F)
            .extractMixin("net/neoforged/neoforge/common/extensions/ILivingEntityExtension")
            .modifyTarget("sinkInFluid(Lnet/neoforged/neoforge/fluids/FluidType;)V")
            .build(),
         MethodPatch.builder()
            .targetClass("net/minecraft/world/entity/LivingEntity")
            .targetMethod("updateFallFlying")
            .targetInjectionPoint(
               "INVOKE", "Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V"
            )
            .extractMixin("net/minecraft/world/item/ElytraItem")
            .modifyTarget("elytraFlightTick(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;I)Z")
            .build(),
         MethodPatch.builder()
            .targetClass("net/minecraft/client/KeyMapping")
            .targetMethod("resetMapping()V")
            .targetInjectionPoint("Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;")
            .modifyInjectionPoint(
               "Lnet/minecraftforge/client/settings/KeyMappingLookup;put(Lcom/mojang/blaze3d/platform/InputConstants$Key;Lnet/minecraft/client/KeyMapping;)V"
            )
            .targetMixinType("Lorg/spongepowered/asm/mixin/injection/Redirect;")
            .transform(
               (context, configuration) -> {
                  MethodNode methodNode = context.methodNode();

                  for (AbstractInsnNode insn : methodNode.instructions) {
                     if (insn.getOpcode() == 176) {
                        methodNode.instructions.insertBefore(insn, new InsnNode(87));
                        methodNode.instructions.set(insn, new InsnNode(177));
                     } else if (insn instanceof MethodInsnNode minsn
                        && minsn.name.equals("put")
                        && minsn.desc.equals("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;")) {
                        minsn.desc = "(Lcom/mojang/blaze3d/platform/InputConstants$Key;Lnet/minecraft/client/KeyMapping;)V";
                        minsn.itf = false;
                        minsn.setOpcode(182);
                        methodNode.instructions.insert(minsn, new InsnNode(1));
                     }
                  }

                  methodNode.desc = Type.getMethodDescriptor(Type.VOID_TYPE, Type.getArgumentTypes(methodNode.desc));
                  return PatchResult.APPLY;
               }
            )
            .build(),
         MethodPatch.builder()
            .targetClass(
               "net/minecraft/world/entity/animal/SnowGolem", "net/minecraft/world/entity/animal/Sheep", "net/minecraft/world/entity/animal/MushroomCow"
            )
            .targetMethod("mobInteract")
            .targetInjectionPoint("Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z")
            .disable()
            .build(),
         MethodPatch.builder()
            .targetClass("net/minecraft/client/renderer/ShaderInstance", "net/minecraft/client/renderer/EffectInstance")
            .targetMethod("<init>", "getOrCreate")
            .targetInjectionPoint(
               "Lnet/minecraft/resources/ResourceLocation;withDefaultNamespace(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"
            )
            .targetInjectionPoint("Lnet/minecraft/resources/ResourceLocation;withDefaultNamespace(Ljava/lang/String;)Lnet/minecraft/resources/Identifier;")
            .disable()
            .build(),
         MethodPatch.builder()
            .targetClass("net/minecraft/client/renderer/PostChain")
            .targetMethod("parsePassNode")
            .targetInjectionPoint("NEW", "net/minecraft/resources/ResourceLocation")
            .targetInjectionPoint("NEW", "(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;")
            .targetMixinType("Lorg/spongepowered/asm/mixin/injection/Redirect;")
            .disable()
            .build(),
         MethodPatch.builder()
            .targetClass("net/minecraft/client/renderer/EffectInstance")
            .targetMethod("<init>", "getOrCreate")
            .targetInjectionPoint(
               "Lnet/minecraft/resources/ResourceLocation;withDefaultNamespace(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"
            )
            .targetMixinType("Lorg/spongepowered/asm/mixin/injection/Redirect;")
            .disable()
            .build(),
         MethodPatch.builder()
            .targetClass("net/minecraft/client/renderer/entity/layers/ElytraLayer")
            .targetMethod(
               "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V"
            )
            .targetInjectionPoint("Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z")
            .targetMixinType("Lorg/spongepowered/asm/mixin/injection/Redirect;")
            .divertRedirector(
               adapter -> {
                  adapter.visitVarInsn(25, 1);
                  adapter.visitVarInsn(25, 2);
                  adapter.visitVarInsn(25, 3);
                  adapter.invokevirtual(
                     "net/minecraft/client/renderer/entity/layers/ElytraLayer",
                     "shouldRender",
                     "(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Z",
                     false
                  );
               }
            )
            .modifyInjectionPoint(
               "Lnet/minecraft/client/renderer/entity/layers/ElytraLayer;shouldRender(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Z"
            )
            .build(),
         MethodPatch.builder()
            .targetClass("net/minecraft/world/entity/vehicle/Boat")
            .targetMethod("m_38394_", "m_38393_", "m_38371_", "m_7840_")
            .targetInjectionPoint("Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z")
            .targetMixinType("Lorg/spongepowered/asm/mixin/injection/Redirect;")
            .modifyInjectionPoint("Lnet/minecraft/world/entity/vehicle/Boat;canBoatInFluid(Lnet/minecraft/world/level/material/FluidState;)Z")
            .build(),
         MethodPatch.builder()
            .targetClass("net/minecraft/world/entity/player/Player")
            .targetMethod("hurtCurrentlyUsedShield(F)V")
            .targetInjectionPoint("Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z")
            .targetMixinType("Lcom/llamalad7/mixinextras/injector/wrapoperation/WrapOperation;")
            .modifyInjectionPoint("Lnet/minecraft/world/item/ItemStack;canPerformAction(Lnet/minecraftforge/common/ToolAction;)Z")
            .build(),
         MethodPatch.builder()
            .targetClass("net/minecraft/client/renderer/entity/FishingHookRenderer")
            .targetMethod(
               "render(Lnet/minecraft/world/entity/projectile/FishingHook;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
            )
            .targetInjectionPoint("Lnet/minecraft/world/item/ItemStack;m_150930_(Lnet/minecraft/world/item/Item;)Z")
            .targetMixinType("Lcom/llamalad7/mixinextras/injector/wrapoperation/WrapOperation;")
            .modifyInjectionPoint("Lnet/minecraft/world/item/ItemStack;canPerformAction(Lnet/minecraftforge/common/ToolAction;)Z")
            .build(),
         MethodPatch.builder()
            .targetClass("net/minecraft/world/level/block/PowderSnowBlock")
            .targetMethod("canEntityWalkOnPowderSnow(Lnet/minecraft/world/entity/Entity;)Z")
            .targetInjectionPoint("Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z")
            .targetMixinType("Lcom/llamalad7/mixinextras/injector/wrapoperation/WrapOperation;")
            .modifyInjectionPoint("Lnet/minecraft/world/item/ItemStack;canWalkOnPowderedSnow(Lnet/minecraft/world/entity/LivingEntity;)Z")
            .build(),
         MethodPatch.builder()
            .targetClass("net/minecraft/client/renderer/ItemInHandRenderer")
            .targetMethod("renderArmWithItem")
            .targetInjectionPoint("Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z")
            .targetMixinType("Lorg/spongepowered/asm/mixin/injection/ModifyArg;")
            .modifyMixinType("Lorg/spongepowered/asm/mixin/injection/Redirect;")
            .modifyTarget("renderArmWithItem")
            .modifyInjectionPoint("INVOKE", "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;")
            .build()
      );
      return patches.stream().flatMap(p -> p instanceof List<?> lst ? lst.stream() : Stream.of(p)).map(o -> (MethodPatch)o).collect(Collectors.toList());
   }
}
