package org.sinytra.connector;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TargetType;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import cpw.mods.modlauncher.api.ITransformer.Target;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.neoforged.coremod.api.ASMAPI;
import net.neoforged.coremod.api.ASMAPI.MethodType;
import net.neoforged.neoforgespi.coremod.ICoreMod;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.sinytra.adapter.analysis.locals.LocalVariableLookup;
import org.slf4j.Logger;

public class ConnectorCoremods implements ICoreMod {
   private static final Logger LOGGER = LogUtils.getLogger();

   public Iterable<? extends ITransformer<?>> getTransformers() {
      ITransformer<ClassNode> keyMappingFieldTypeTransform = new ConnectorCoremods.BaseTransformer<>(
         TargetType.CLASS, Target.targetClass("net.minecraft.client.KeyMapping"), input -> {
            FieldNode field = input.fields.stream().filter(f -> f.name.equals("CATEGORY_SORT_ORDER")).findFirst().orElse(null);
            if (field != null) {
               int index = input.fields.indexOf(field);
               input.fields.add(index, new FieldNode(26, "MAP", "Ljava/util/Map;", null, null));
               LOGGER.debug("Added field for KeyMapping#MAP at index {}", index + 1);
            }
         }
      );
      ITransformer<ClassNode> creativeModeTabConstructorTransform = new ConnectorCoremods.BaseTransformer<>(
         TargetType.CLASS,
         Target.targetClass("net.minecraft.world.item.CreativeModeTab"),
         input -> {
            MethodVisitor method = input.visitMethod(
               1,
               "<init>",
               "(Lnet/minecraft/world/item/CreativeModeTab$Row;ILnet/minecraft/world/item/CreativeModeTab$Type;Lnet/minecraft/network/chat/Component;Ljava/util/function/Supplier;Lnet/minecraft/world/item/CreativeModeTab$DisplayItemsGenerator;)V",
               null,
               null
            );
            method.visitCode();
            method.visitVarInsn(25, 0);
            method.visitVarInsn(25, 1);
            method.visitVarInsn(21, 2);
            method.visitVarInsn(25, 3);
            method.visitVarInsn(25, 4);
            method.visitVarInsn(25, 5);
            method.visitVarInsn(25, 6);
            method.visitInsn(1);
            method.visitInsn(3);
            method.visitLdcInsn(89);
            method.visitFieldInsn(
               178, "net/minecraft/world/item/CreativeModeTab$Builder", "CREATIVE_INVENTORY_TABS_IMAGE", "Lnet/minecraft/resources/ResourceLocation;"
            );
            method.visitLdcInsn(4210752);
            method.visitLdcInsn(-2130706433);
            method.visitTypeInsn(187, "java/util/ArrayList");
            method.visitInsn(89);
            method.visitMethodInsn(183, "java/util/ArrayList", "<init>", "()V", false);
            method.visitTypeInsn(187, "java/util/ArrayList");
            method.visitInsn(89);
            method.visitMethodInsn(183, "java/util/ArrayList", "<init>", "()V", false);
            method.visitMethodInsn(
               183,
               "net/minecraft/world/item/CreativeModeTab",
               "<init>",
               "(Lnet/minecraft/world/item/CreativeModeTab$Row;ILnet/minecraft/world/item/CreativeModeTab$Type;Lnet/minecraft/network/chat/Component;Ljava/util/function/Supplier;Lnet/minecraft/world/item/CreativeModeTab$DisplayItemsGenerator;Lnet/minecraft/resources/ResourceLocation;ZILnet/minecraft/resources/ResourceLocation;IILjava/util/List;Ljava/util/List;)V",
               false
            );
            method.visitInsn(177);
            method.visitEnd();
            LOGGER.debug("Injected vanilla CreativeModeTab constructor");
         }
      );
      List<ITransformer<?>> addedFields = List.of(
         addOriginalSyntheticField("net.minecraft.client.particle.ParticleEngine", "providers", "Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;"),
         addOriginalSyntheticField("net.minecraft.client.color.block.BlockColors", "blockColors", "Lnet/minecraft/core/IdMapper;"),
         addOriginalSyntheticField("net.minecraft.client.color.item.ItemColors", "itemColors", "Lnet/minecraft/core/IdMapper;")
      );
      ITransformer<?> missingOrderingCall = new ConnectorCoremods.BaseTransformer<>(
         TargetType.METHOD,
         Target.targetMethod(
            "net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen", "renderEffects", "(Lnet/minecraft/client/gui/GuiGraphics;II)V"
         ),
         input -> {
            MethodInsnNode insn = ASMAPI.findFirstMethodCall(
               input, MethodType.INTERFACE, "java/util/stream/Stream", "collect", "(Ljava/util/stream/Collector;)Ljava/lang/Object;"
            );
            if (insn != null && insn.getNext() instanceof TypeInsnNode typeInsn) {
               input.instructions
                  .insert(
                     typeInsn,
                     ASMAPI.listOf(
                        new AbstractInsnNode[]{
                           new MethodInsnNode(184, "com/google/common/collect/Ordering", "natural", "()Lcom/google/common/collect/Ordering;"),
                           new InsnNode(95),
                           new MethodInsnNode(182, "com/google/common/collect/Ordering", "sortedCopy", "(Ljava/lang/Iterable;)Ljava/util/List;")
                        }
                     )
                  );
            }
         }
      );
      ITransformer<?> expandLocalVarScope = new ConnectorCoremods.BaseTransformer<>(
         TargetType.METHOD,
         Target.targetMethod(
            "net.minecraft.world.entity.LivingEntity", "forceAddEffect", "(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)V"
         ),
         input -> {
            LocalVariableLookup lvt = new LocalVariableLookup(input);
            LocalVariableNode mobEffect = lvt.getByTypedOrdinal(Type.getObjectType("net/minecraft/world/effect/MobEffectInstance"), 1).orElse(null);
            if (mobEffect != null) {
               mobEffect.start = lvt.getByIndex(0).start;
               input.instructions.insert(mobEffect.start, ASMAPI.listOf(new AbstractInsnNode[]{new InsnNode(1), new VarInsnNode(58, mobEffect.index)}));
               LOGGER.debug("Expanded local variable scope for LivingEntity#forceAddEffect index {}", mobEffect.index);
            }
         }
      );
      ITransformer<ClassNode> accessTransform = new ConnectorCoremods.BaseTransformer<>(
         TargetType.CLASS, Target.targetClass("net.neoforged.neoforge.network.bundle.PacketAndPayloadAcceptor"), input -> {
            FieldNode field = input.fields.stream().filter(f -> f.name.equals("consumer")).findFirst().orElse(null);
            if (field != null) {
               field.access = 17;
               LOGGER.debug("Made public PacketAndPayloadAcceptor#consumer");
            }
         }
      );
      return ImmutableList.builder()
         .add(new ITransformer[]{keyMappingFieldTypeTransform, creativeModeTabConstructorTransform})
         .addAll(addedFields)
         .addAll(getFabricASMTransformers())
         .add(missingOrderingCall)
         .add(expandLocalVarScope)
         .add(accessTransform)
         .build();
   }

   private static ITransformer<?> addOriginalSyntheticField(String cls, String name, String desc) {
      return new ConnectorCoremods.BaseTransformer<>(TargetType.CLASS, Target.targetClass(cls), input -> {
         Integer originalAccess = input.fields.stream().filter(f -> f.name.equals(name)).findFirst().map(f -> f.access).orElse(1);
         input.fields.add(new FieldNode(originalAccess & -17 | 4096, name, desc, null, null));
         LOGGER.debug("Added field {} to class {}", name, cls);
      });
   }

   private static List<ITransformer<?>> getFabricASMTransformers() {
      ITransformer<MethodNode> injectFabricASM = new ConnectorCoremods.BaseTransformer<>(
         TargetType.METHOD,
         Set.of(
            Target.targetMethod("com.chocohead.mm.Plugin", "fishAddURL", "()Ljava/util/function/Consumer;"),
            Target.targetMethod("me.shedaniel.mm.Plugin", "fishAddURL", "()Ljava/util/function/Consumer;")
         ),
         input -> {
            InsnList insns = new InsnList();
            insns.add(new MethodInsnNode(184, "org/sinytra/connector/service/hacks/FabricASMFixer", "fishAddURL", "()Ljava/util/function/Consumer;"));
            insns.add(new InsnNode(176));
            input.instructions.insert(insns);
            LOGGER.debug("Injected fishAddURL hook into FabricASM plugin");
         }
      );
      ITransformer<MethodNode> renameGeneratedMixinClassName = new ConnectorCoremods.BaseTransformer<>(
         TargetType.METHOD,
         Set.of(
            Target.targetMethod("com.chocohead.mm.Plugin$1", "generate", "()Ljava/util/function/Consumer;"),
            Target.targetMethod("com.chocohead.mm.Plugin$1", "generate", "(Ljava/lang/String;Ljava/util/Collection;)V"),
            Target.targetMethod("me.shedaniel.mm.Plugin$1", "generate", "(Ljava/lang/String;Ljava/util/Collection;)V")
         ),
         input -> {
            InsnList insns = new InsnList();
            insns.add(new VarInsnNode(25, 1));
            insns.add(
               new MethodInsnNode(184, "org/sinytra/connector/service/hacks/FabricASMFixer", "flattenMixinClass", "(Ljava/lang/String;)Ljava/lang/String;")
            );
            insns.add(new VarInsnNode(58, 1));
            input.instructions.insert(insns);
            LOGGER.debug("Injected flattenMixinClass modifier into FabricASM Plugin$1");
         }
      );
      ITransformer<MethodNode> permitEnumSubclass = new ConnectorCoremods.BaseTransformer<>(
         TargetType.METHOD,
         Set.of(
            Target.targetMethod(
               "com.chocohead.mm.EnumSubclasser",
               "defineAnonymousSubclass",
               "(Lorg/objectweb/asm/tree/ClassNode;Lcom/chocohead/mm/api/EnumAdder$EnumAddition;Ljava/lang/String;Ljava/lang/String;)[B"
            ),
            Target.targetMethod(
               "me.shedaniel.mm.EnumSubclasser",
               "defineAnonymousSubclass",
               "(Lorg/objectweb/asm/tree/ClassNode;Lcom/chocohead/mm/api/EnumAdder$EnumAddition;Ljava/lang/String;Ljava/lang/String;)[B"
            )
         ),
         input -> {
            InsnList insns = new InsnList();
            insns.add(new VarInsnNode(25, 0));
            insns.add(new VarInsnNode(25, 2));
            insns.add(
               new MethodInsnNode(
                  184, "org/sinytra/connector/service/hacks/FabricASMFixer", "permitEnumSubclass", "(Lorg/objectweb/asm/tree/ClassNode;Ljava/lang/String;)V"
               )
            );
            input.instructions.insert(insns);
            LOGGER.debug("Injected permitEnumSubclass modifier into FabricASM EnumSubclasser");
         }
      );
      return List.of(injectFabricASM, renameGeneratedMixinClassName, permitEnumSubclass);
   }

   private record BaseTransformer<T>(TargetType<T> type, Set<Target<T>> targets, Consumer<T> transform) implements ITransformer<T> {
      public BaseTransformer(TargetType<T> type, Target<T> target, Consumer<T> transform) {
         this(type, Set.of(target), transform);
      }

      public T transform(T input, ITransformerVotingContext context) {
         this.transform.accept(input);
         return input;
      }

      public TransformerVoteResult castVote(ITransformerVotingContext context) {
         return TransformerVoteResult.YES;
      }

      public TargetType<T> getTargetType() {
         return this.type;
      }
   }
}
