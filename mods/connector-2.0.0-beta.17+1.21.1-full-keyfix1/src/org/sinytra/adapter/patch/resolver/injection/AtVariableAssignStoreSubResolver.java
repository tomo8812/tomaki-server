package org.sinytra.adapter.patch.resolver.injection;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.TargetPair;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.patch.config.MutableConfiguration;
import org.sinytra.adapter.patch.mixin.MixinFlag;
import org.sinytra.adapter.patch.resolver.SubResolver;
import org.sinytra.adapter.util.AdapterUtil;
import org.sinytra.adapter.util.OpcodeUtil;

public class AtVariableAssignStoreSubResolver implements SubResolver {
   @Nullable
   @Override
   public Configuration resolve(MixinContext context, Recipe recipe) {
      if (!recipe.clean().getAtData().getValue().equals("INVOKE")) {
         return null;
      }

      TargetPair cleanPair = recipe.getCleanTarget();
      AbstractInsnNode cleanInsn = context.methods().findInjectionTargetInsn(cleanPair);
      if (cleanInsn == null) {
         return null;
      }

      TargetPair dirtyPair = recipe.getDirtyTarget();
      if (dirtyPair == null) {
         return null;
      }

      if (findNextUsefulInsn(cleanInsn) instanceof VarInsnNode varInsn && OpcodeUtil.isStoreOpcode(varInsn.getOpcode())) {
         LocalVariableNode cleanLocal = recipe.cleanLocalsTable().getByIndexOrNull(varInsn.var);
         if (cleanLocal == null) {
            return null;
         } else {
            List<LocalVariableNode> cleanLocals = recipe.cleanLocalsTable().getForType(cleanLocal);
            List<LocalVariableNode> dirtyLocals = recipe.dirtyLocalsTable().getForType(cleanLocal);
            if (cleanLocals.size() != dirtyLocals.size()) {
               return null;
            } else {
               LocalVariableNode dirtyLocal = dirtyLocals.get(cleanLocals.indexOf(cleanLocal));
               List<AbstractInsnNode> cleanStoreInsns = findStoreInsns(cleanPair.methodNode().instructions, cleanLocal.index);
               int cleanStoreInsnIndex = cleanStoreInsns.indexOf(varInsn);
               if (cleanStoreInsnIndex == -1) {
                  return null;
               } else {
                  List<AbstractInsnNode> dirtyStoreInsns = findStoreInsns(dirtyPair.methodNode().instructions, dirtyLocal.index);
                  if (cleanStoreInsns.size() != dirtyStoreInsns.size()) {
                     return null;
                  } else {
                     AbstractInsnNode dirtyStoreInsn = dirtyStoreInsns.get(cleanStoreInsnIndex);
                     MethodInsnNode previousMethodCall = (MethodInsnNode)AdapterUtil.iterateInsns(
                        dirtyStoreInsn, AbstractInsnNode::getPrevious, i -> i instanceof MethodInsnNode
                     );
                     if (previousMethodCall == null) {
                        return null;
                     } else {
                        return context.hasFlag(MixinFlag.AT_TARGET_SENSITIVE) && !previousMethodCall.owner.equals(dirtyPair.classNode().name)
                           ? null
                           : MutableConfiguration.create().setAtData(recipe.clean().getAtData().withTarget(previousMethodCall));
                     }
                  }
               }
            }
         }
      } else {
         return null;
      }
   }

   private static List<AbstractInsnNode> findStoreInsns(InsnList insns, int index) {
      List<AbstractInsnNode> list = new ArrayList<>();

      for (AbstractInsnNode insn : insns) {
         if (insn instanceof VarInsnNode varInsn && varInsn.var == index && OpcodeUtil.isStoreOpcode(varInsn.getOpcode())) {
            list.add(insn);
         }
      }

      return list;
   }

   private static AbstractInsnNode findNextUsefulInsn(AbstractInsnNode insn) {
      return AdapterUtil.iterateInsns(
         insn,
         AbstractInsnNode::getNext,
         i -> !(i instanceof TypeInsnNode) && !(i instanceof FrameNode) && !(i instanceof LineNumberNode) && !(i instanceof LabelNode)
      );
   }
}
