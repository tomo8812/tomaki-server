package org.sinytra.adapter.patch.processor;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.sinytra.adapter.env.ctx.MethodHelper;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.patch.TxResult;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.patch.config.key.SpecialKeys;

public class StaticAccessProcessor implements Processor {
   @Override
   public TxResult process(MixinContext context, Configuration dirty, Recipe recipe) {
      if (recipe.clean().hasProperty(SpecialKeys.STATIC) && dirty.hasProperty(SpecialKeys.STATIC)) {
         boolean cleanStatic = recipe.clean().getProperty(SpecialKeys.STATIC).orElseThrow();
         boolean dirtyStatic = recipe.dirty().getProperty(SpecialKeys.STATIC).orElseThrow();
         if (cleanStatic == dirtyStatic) {
            return TxResult.PASS;
         }

         MethodNode method = context.methodNode();
         boolean actuallyStatic = MethodHelper.isStatic(context.methodNode());
         if (!actuallyStatic && !cleanStatic && dirtyStatic) {
            context.recordCtxAudit("Made method static");
            method.access |= 8;
            return TxResult.SUCCESS;
         }

         if (actuallyStatic && cleanStatic && !dirtyStatic) {
            context.recordCtxAudit("Made method non-static");
            method.access &= -9;
            LocalVariableNode firstParam = method.localVariables.stream().filter(lvn -> lvn.index == 0).findFirst().orElseThrow();

            for (LocalVariableNode lvn : method.localVariables) {
               lvn.index++;
            }

            for (AbstractInsnNode insn : method.instructions) {
               if (insn instanceof VarInsnNode varInsn) {
                  varInsn.var++;
               }
            }

            method.localVariables
               .add(new LocalVariableNode("this", Type.getObjectType(context.classNode().name).getDescriptor(), null, firstParam.start, firstParam.end, 0));
            return TxResult.SUCCESS;
         } else {
            return TxResult.PASS;
         }
      } else {
         return TxResult.PASS;
      }
   }
}
