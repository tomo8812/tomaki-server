package org.sinytra.adapter.transform.param;

import com.mojang.logging.LogUtils;
import java.util.List;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.sinytra.adapter.analysis.locals.LocalVariableLookup;
import org.sinytra.adapter.analysis.method.MethodCallAnalyzer;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.PatchResult;
import org.sinytra.adapter.env.param.Parameters;
import org.sinytra.adapter.types.BytecodeFixerUpper;
import org.sinytra.adapter.types.TypeAdapter;
import org.sinytra.adapter.util.AdapterUtil;
import org.slf4j.Logger;

public record ReplaceParametersTransformer(int index, Type type, boolean upgradeUsage) implements ParameterTransformer {
   private static final Logger LOGGER = LogUtils.getLogger();

   public ReplaceParametersTransformer(int index, Type type) {
      this(index, type, true);
   }

   @Override
   public PatchResult apply(ClassNode classNode, MethodNode methodNode, MixinContext context, List<Type> parameters, int offset) {
      int paramIndex = this.index + offset;
      if (methodNode.parameters.size() <= paramIndex) {
         return PatchResult.PASS;
      }

      LOGGER.info(AdapterUtil.MIXINPATCH, "Replacing parameter {} with type {} in {}.{}", new Object[]{paramIndex, this.type, classNode.name, methodNode.name});
      parameters.set(paramIndex, this.type);
      LocalVariableLookup lvtLookup = new LocalVariableLookup(methodNode);
      LocalVariableNode localVar = lvtLookup.getByParameterOrdinal(paramIndex);
      Type originalType = Type.getType(localVar.desc);
      localVar.desc = this.type.getDescriptor();
      localVar.signature = null;
      List<AbstractInsnNode> ignoreInsns = ParamTransformationUtil.findWrapOperationOriginalCall(methodNode);
      BytecodeFixerUpper bfu = context.environment().bytecodeFixerUpper();
      if (this.upgradeUsage && this.type.getSort() == 10 && originalType.getSort() == 10) {
         for (AbstractInsnNode insn : methodNode.instructions) {
            if (!ignoreInsns.contains(insn)) {
               if (insn instanceof VarInsnNode varInsn && varInsn.var == localVar.index) {
                  int nextOp = insn.getNext().getOpcode();
                  if (bfu != null && nextOp != 198 && nextOp != 199) {
                     TypeAdapter typeFix = bfu.getTypeAdapter(this.type, originalType);
                     if (typeFix == null && context.methodAnnotation().matchesDesc("Lcom/llamalad7/mixinextras/injector/wrapoperation/WrapOperation;")) {
                        List<Type> params = Parameters.getParameterTypes(methodNode.desc);
                        if (!params.isEmpty()) {
                           typeFix = bfu.getTypeAdapter(params.getFirst(), originalType);
                           if (typeFix != null) {
                              varInsn.var = lvtLookup.getByParameterOrdinal(0).index;
                           }
                        }
                     }

                     if (typeFix != null) {
                        typeFix.apply(methodNode.instructions, varInsn);
                     }
                  }
               }

               if (insn instanceof MethodInsnNode minsn && minsn.owner.equals(originalType.getInternalName())) {
                  for (AbstractInsnNode callInsn : MethodCallAnalyzer.getMethodCallInsns(methodNode, minsn)) {
                     if (callInsn instanceof VarInsnNode varInsn && varInsn.var == localVar.index) {
                        minsn.owner = this.type.getInternalName();
                     }
                  }
               }
            }
         }
      }

      return PatchResult.COMPUTE_FRAMES;
   }
}
