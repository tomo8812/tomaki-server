package org.sinytra.adapter.patch.processor;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.sinytra.adapter.analysis.locals.LocalVariableLookup;
import org.sinytra.adapter.analysis.method.MethodCallAnalyzer;
import org.sinytra.adapter.analysis.params.EnhancedParamsDiff;
import org.sinytra.adapter.analysis.params.ParamsDiffSnapshot;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.param.MethodParameters;
import org.sinytra.adapter.env.param.Parameters;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.patch.TxResult;
import org.sinytra.adapter.patch.config.Configuration;

public class ParametersPostProcessor implements Processor {
   @Override
   public TxResult process(MixinContext context, Configuration dirty, Recipe recipe) {
      MethodParameters cleanParams = recipe.clean().getParameters();
      MethodParameters dirtyParams = recipe.clean().getParameters();
      if (cleanParams.has(MethodParameters.ParamGroup.METHOD_PARAMS) && dirtyParams.has(MethodParameters.ParamGroup.METHOD_PARAMS)) {
         List<Type> cleanTypes = recipe.clean().getParameters().getTypes(MethodParameters.ParamGroup.METHOD_PARAMS);
         List<Type> dirtyTypes = recipe.dirty().getParameters().getTypes(MethodParameters.ParamGroup.METHOD_PARAMS);
         ParamsDiffSnapshot diff = EnhancedParamsDiff.createLayered(cleanTypes, dirtyTypes);
         MethodNode methodNode = context.methodNode();
         LocalVariableLookup lookup = new LocalVariableLookup(context.methodNode());
         boolean matched = false;

         for (Pair<Integer, Type> replacement : diff.replacements()) {
            int ordinal = (Integer)replacement.getFirst();
            LocalVariableNode node = lookup.getByParameterOrdinal(ordinal);
            Type currentType = Type.getType(node.desc);

            for (AbstractInsnNode insn : methodNode.instructions) {
               if (insn instanceof MethodInsnNode minsn) {
                  List<Type> methodArgs = Parameters.getParameterTypes(minsn.desc);
                  List<AbstractInsnNode> callArgs = MethodCallAnalyzer.getMethodCallSrcInsns(methodNode, minsn);
                  int instanceOffset = minsn.getOpcode() == 184 ? 0 : 1;

                  for (int i = instanceOffset; i < callArgs.size(); i++) {
                     AbstractInsnNode arg = callArgs.get(i);
                     if (arg instanceof VarInsnNode varInsn && varInsn.var == node.index) {
                        Type type = methodArgs.get(i - instanceOffset);
                        if (!currentType.equals(type)) {
                           methodNode.instructions.insert(varInsn, new TypeInsnNode(192, type.getInternalName()));
                           matched = true;
                        }
                     }
                  }
               }
            }
         }

         return matched ? TxResult.SUCCESS : TxResult.PASS;
      } else {
         return TxResult.PASS;
      }
   }
}
