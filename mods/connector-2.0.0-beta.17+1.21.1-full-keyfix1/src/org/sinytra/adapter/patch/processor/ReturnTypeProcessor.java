package org.sinytra.adapter.patch.processor;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.SourceInterpreter;
import org.objectweb.asm.tree.analysis.SourceValue;
import org.objectweb.asm.tree.analysis.Value;
import org.sinytra.adapter.analysis.method.MethodAnalyzer;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.patch.TxResult;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.types.TypeAdapter;
import org.sinytra.adapter.util.AdapterUtil;

public class ReturnTypeProcessor implements Processor {
   @Override
   public TxResult process(MixinContext context, Configuration dirty, Recipe recipe) {
      Type cleanType = recipe.clean().getReturnType();
      Type dirtyType = dirty.getReturnType();
      if (cleanType.equals(dirtyType)) {
         return TxResult.PASS;
      }

      TypeAdapter adapter = context.getTypeAdapter(cleanType, dirtyType);
      if (adapter == null) {
         return TxResult.PASS;
      }

      ReturnTypeProcessor.ReturnInterpreter inter = MethodAnalyzer.analyzeInterpretMethod(context.methodNode(), new ReturnTypeProcessor.ReturnInterpreter());

      for (AbstractInsnNode insn : inter.insns) {
         if (insn.getOpcode() != 1) {
            adapter.apply(context.methodNode().instructions, insn);
         }
      }

      MethodNode methodNode = context.methodNode();
      methodNode.desc = Type.getMethodDescriptor(dirtyType, Type.getArgumentTypes(methodNode.desc));
      return TxResult.SUCCESS;
   }

   private static class ReturnInterpreter extends SourceInterpreter {
      public final List<AbstractInsnNode> insns = new ArrayList<>();

      public ReturnInterpreter() {
         super(589824);
      }

      public void returnOperation(AbstractInsnNode insn, SourceValue value, SourceValue expected) {
         AbstractInsnNode srcInsn = AdapterUtil.getSingleInsn(value);
         if (srcInsn == null) {
            throw new IllegalStateException();
         }

         this.insns.add(srcInsn);
      }

      // $VF: synthetic method
      // $VF: bridge method
      public void returnOperation(AbstractInsnNode param1, Value nullx, Value nullxx) throws AnalyzerException {
         this.returnOperation(var1, (SourceValue)nullx, (SourceValue)nullxx);
      }
   }
}
