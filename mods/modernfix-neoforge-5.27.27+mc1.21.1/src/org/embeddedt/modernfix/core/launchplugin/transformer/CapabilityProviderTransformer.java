package org.embeddedt.modernfix.core.launchplugin.transformer;

import org.embeddedt.modernfix.core.launchplugin.CoreLaunchPluginService;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class CapabilityProviderTransformer implements CoreLaunchPluginService.Transformer {
   private static final String OWNER = "net/minecraftforge/common/capabilities/CapabilityProvider";
   private static final String COMPOUND_TAG = "net/minecraft/nbt/CompoundTag";
   private static final String HELPER_NAME = "mfix$skipLazyInit";
   private static final String HELPER_DESC = "(Lnet/minecraftforge/common/capabilities/CapabilityProvider;Lnet/minecraftforge/common/capabilities/CapabilityProvider;)Z";

   @Override
   public int transform(ClassNode node) {
      String targetDesc = "(Lnet/minecraftforge/common/capabilities/CapabilityProvider;)Z";

      for (MethodNode method : node.methods) {
         if (method.name.equals("areCapsCompatible") && method.desc.equals(targetDesc)) {
            this.injectCall(method);
            node.methods.add(this.buildHelper());
            break;
         }
      }

      return 2;
   }

   private MethodNode buildHelper() {
      MethodNode mn = new MethodNode(
         10,
         "mfix$skipLazyInit",
         "(Lnet/minecraftforge/common/capabilities/CapabilityProvider;Lnet/minecraftforge/common/capabilities/CapabilityProvider;)Z",
         null,
         null
      );
      InsnList il = mn.instructions;
      LabelNode returnFalse = new LabelNode();
      il.add(new VarInsnNode(25, 0));
      il.add(new FieldInsnNode(180, "net/minecraftforge/common/capabilities/CapabilityProvider", "isLazy", "Z"));
      il.add(new JumpInsnNode(153, returnFalse));
      il.add(new VarInsnNode(25, 0));
      il.add(new FieldInsnNode(180, "net/minecraftforge/common/capabilities/CapabilityProvider", "initialized", "Z"));
      il.add(new JumpInsnNode(154, returnFalse));
      il.add(new VarInsnNode(25, 1));
      il.add(new FieldInsnNode(180, "net/minecraftforge/common/capabilities/CapabilityProvider", "isLazy", "Z"));
      il.add(new JumpInsnNode(153, returnFalse));
      il.add(new VarInsnNode(25, 1));
      il.add(new FieldInsnNode(180, "net/minecraftforge/common/capabilities/CapabilityProvider", "initialized", "Z"));
      il.add(new JumpInsnNode(154, returnFalse));
      il.add(new VarInsnNode(25, 1));
      il.add(new MethodInsnNode(182, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false));
      il.add(new VarInsnNode(25, 0));
      il.add(new MethodInsnNode(182, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false));
      il.add(new JumpInsnNode(166, returnFalse));
      il.add(new VarInsnNode(25, 0));
      il.add(new FieldInsnNode(180, "net/minecraftforge/common/capabilities/CapabilityProvider", "lazyData", "Lnet/minecraft/nbt/CompoundTag;"));
      il.add(new VarInsnNode(25, 1));
      il.add(new FieldInsnNode(180, "net/minecraftforge/common/capabilities/CapabilityProvider", "lazyData", "Lnet/minecraft/nbt/CompoundTag;"));
      il.add(new MethodInsnNode(184, "java/util/Objects", "equals", "(Ljava/lang/Object;Ljava/lang/Object;)Z", false));
      il.add(new JumpInsnNode(153, returnFalse));
      il.add(new InsnNode(4));
      il.add(new InsnNode(172));
      il.add(returnFalse);
      il.add(new InsnNode(3));
      il.add(new InsnNode(172));
      mn.maxLocals = 2;
      mn.maxStack = 2;
      return mn;
   }

   private void injectCall(MethodNode method) {
      InsnList il = new InsnList();
      LabelNode skip = new LabelNode();
      il.add(new VarInsnNode(25, 0));
      il.add(new VarInsnNode(25, 1));
      il.add(
         new MethodInsnNode(
            184,
            "net/minecraftforge/common/capabilities/CapabilityProvider",
            "mfix$skipLazyInit",
            "(Lnet/minecraftforge/common/capabilities/CapabilityProvider;Lnet/minecraftforge/common/capabilities/CapabilityProvider;)Z",
            false
         )
      );
      il.add(new JumpInsnNode(153, skip));
      il.add(new InsnNode(4));
      il.add(new InsnNode(172));
      il.add(skip);
      method.instructions.insert(il);
   }
}
