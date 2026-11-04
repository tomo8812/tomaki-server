package org.sinytra.adapter.types;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.analysis.method.MethodAnalyzer;
import org.sinytra.adapter.env.ann.ClassTarget;
import org.sinytra.adapter.env.ctx.PatchContext;
import org.sinytra.adapter.env.ctx.PatchResult;
import org.sinytra.adapter.transform.ClassTransformer;
import org.sinytra.adapter.util.AdapterUtil;
import org.slf4j.Logger;

public class FieldTypeUsageTransformer implements ClassTransformer {
   private static final Logger LOGGER = LogUtils.getLogger();

   @Override
   public PatchResult apply(ClassNode classNode, ClassTarget classTarget, PatchContext context) {
      BytecodeFixerUpper bfu = context.environment().bytecodeFixerUpper();
      boolean applied = false;
      if (bfu != null) {
         Map<String, Pair<Type, Type>> classUpdatedTypes = new HashMap<>();
         if (context.targetTypes().size() == 1) {
            Type targetType = context.targetTypes().getFirst();

            for (FieldNode field : classNode.fields) {
               if (AdapterUtil.isShadowField(field)) {
                  Pair<Type, Type> updatedTypes = bfu.getFieldTypeChange(targetType.getInternalName(), field.name);
                  if (updatedTypes != null) {
                     field.desc = ((Type)updatedTypes.getSecond()).getDescriptor();
                     classUpdatedTypes.put(field.name, updatedTypes);
                     applied = true;
                  }
               }
            }
         }

         for (MethodNode method : classNode.methods) {
            for (AbstractInsnNode insn : method.instructions) {
               if (insn instanceof FieldInsnNode finsn) {
                  if (finsn.owner.equals(classNode.name)) {
                     Pair<Type, Type> classUpdatedType = classUpdatedTypes.get(finsn.name);
                     if (classUpdatedType != null) {
                        applied |= runFieldFix(bfu, classUpdatedType, method, finsn);
                        continue;
                     }
                  }

                  Pair<Type, Type> updatedTypes = bfu.getFieldTypeChange(finsn.owner, finsn.name);
                  if (updatedTypes != null) {
                     applied |= runFieldFix(bfu, updatedTypes, method, finsn);
                  }
               }
            }

            for (Pair<FieldInsnNode, MethodInsnNode> insn : MethodAnalyzer.analyzeMethod(
               method,
               (m, v) -> m.getOpcode() == 182,
               (insnx, values) -> !values.isEmpty() && AdapterUtil.getSingleInsn(values, 0) instanceof FieldInsnNode finsnx ? Pair.of(finsnx, insnx) : null
            )) {
               FieldInsnNode finsn = (FieldInsnNode)insn.getFirst();
               if (finsn.owner.equals(classNode.name)) {
                  Pair<Type, Type> classUpdatedType = classUpdatedTypes.get(finsn.name);
                  if (classUpdatedType != null) {
                     ((MethodInsnNode)insn.getSecond()).owner = ((Type)classUpdatedType.getSecond()).getInternalName();
                     applied = true;
                  }
               }
            }
         }
      }

      return applied ? PatchResult.APPLY : PatchResult.PASS;
   }

   private static boolean runFieldFix(BytecodeFixerUpper bfu, Pair<Type, Type> updatedTypes, MethodNode method, FieldInsnNode finsn) {
      TypeAdapter typeAdapter = bfu.getTypeAdapter((Type)updatedTypes.getSecond(), (Type)updatedTypes.getFirst());
      if (typeAdapter != null) {
         LOGGER.debug(
            AdapterUtil.MIXINPATCH,
            "Running fixup for field {}.{}{} in method {}{}",
            new Object[]{finsn.owner, finsn.name, finsn.desc, method.name, method.desc}
         );
         finsn.desc = ((Type)updatedTypes.getSecond()).getDescriptor();
         typeAdapter.apply(method.instructions, finsn);
         return true;
      } else {
         return false;
      }
   }
}
