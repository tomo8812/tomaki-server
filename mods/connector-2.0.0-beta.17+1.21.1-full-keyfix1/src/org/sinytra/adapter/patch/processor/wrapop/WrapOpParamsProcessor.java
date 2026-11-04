package org.sinytra.adapter.patch.processor.wrapop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.sinytra.adapter.analysis.locals.LocalVariableLookup;
import org.sinytra.adapter.analysis.method.MethodCallAnalyzer;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.param.MethodParameters;
import org.sinytra.adapter.env.param.Parameter;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.patch.TxResult;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.patch.processor.Processor;
import org.sinytra.adapter.util.AdapterUtil;
import org.sinytra.adapter.util.MethodQualifier;

public class WrapOpParamsProcessor implements Processor {
   private static final MethodQualifier WO_ORIGINAL_CALL = new MethodQualifier(
      "Lcom/llamalad7/mixinextras/injector/wrapoperation/Operation;", "call", "([Ljava/lang/Object;)Ljava/lang/Object;"
   );

   @Override
   public TxResult process(MixinContext context, Configuration dirty, Recipe recipe) {
      if (dirty.getParameters() == null) {
         return TxResult.FAIL;
      }

      List<List<AbstractInsnNode>> cleanCalls = MethodCallAnalyzer.getAllMethodCallSrcInsnsInclusive(context.unmodifiedMethodNode(), WO_ORIGINAL_CALL);
      List<List<AbstractInsnNode>> dirtyCalls = MethodCallAnalyzer.getAllMethodCallSrcInsnsInclusive(context.methodNode(), WO_ORIGINAL_CALL);
      if (cleanCalls.size() != dirtyCalls.size()) {
         return TxResult.FAIL;
      }

      for (int i = 0; i < dirtyCalls.size(); i++) {
         List<AbstractInsnNode> cleanCall = cleanCalls.get(i);
         List<AbstractInsnNode> dirtyCall = dirtyCalls.get(i);
         if (!upgradeOriginalCall(context, dirty, cleanCall, dirtyCall)) {
            return TxResult.FAIL;
         }
      }

      if (dirty.getAtData() != null) {
         AbstractInsnNode cleanInsn = context.methods().findInjectionTargetInsn(recipe.getCleanTarget());
         AbstractInsnNode dirtyInsn = context.methods().findInjectionTargetInsn(recipe.getDirtyTarget(), dirty.getAtData());
         if (cleanInsn instanceof MethodInsnNode cleanMinsn && dirtyInsn instanceof MethodInsnNode dirtyMinsn) {
            List<Type> methodParams = dirty.getParameters().getTypes(MethodParameters.ParamGroup.METHOD_PARAMS);
            WrapOpSurgeon.tryUpgrade(context, recipe, methodParams, cleanMinsn, dirtyMinsn);
         }
      }

      return TxResult.SUCCESS;
   }

   @Nullable
   private static WrapOpParamsProcessor.WrapOpOriginalCall parseOriginalCall(MethodNode methodNode, List<AbstractInsnNode> callInsns) {
      if (callInsns.size() < 2) {
         return null;
      } else {
         AbstractInsnNode singleArg = callInsns.get(1);
         if (singleArg instanceof TypeInsnNode tinsn && tinsn.getOpcode() == 189) {
            List<List<AbstractInsnNode>> arrayInitArgs = WrapOpAnalyzer.groupArrayInitializers(methodNode, singleArg);
            return WrapOpParamsProcessor.WrapOpOriginalCall.parse(arrayInitArgs);
         } else {
            return null;
         }
      }
   }

   private static boolean upgradeOriginalCall(
      MixinContext context, Configuration dirty, List<AbstractInsnNode> cleanCallInsns, List<AbstractInsnNode> dirtyCallInsns
   ) {
      WrapOpParamsProcessor.WrapOpOriginalCall cleanCall = parseOriginalCall(context.unmodifiedMethodNode(), cleanCallInsns);
      if (cleanCall == null) {
         return false;
      }

      WrapOpParamsProcessor.WrapOpOriginalCall dirtyCall = parseOriginalCall(context.methodNode(), dirtyCallInsns);
      if (dirtyCall == null) {
         return false;
      }

      AbstractInsnNode arrayInit = dirtyCallInsns.get(1);
      List<Parameter> params = dirty.getParameters().get(MethodParameters.ParamGroup.METHOD_PARAMS);
      LocalVariableLookup lookup = new LocalVariableLookup(context.methodNode());
      List<WrapOpParamsProcessor.WrapOpOriginalCall.CallArg> args = IntStream.range(0, params.size()).mapToObj(i -> {
         Parameter param = params.get(i);
         int index = lookup.getByParameterOrdinal(i).index;
         Type type = param.type();
         List<AbstractInsnNode> loadInsns = new ArrayList<>();
         loadInsns.add(AdapterUtil.loadType(type, index));
         if (type.getSort() != 10) {
            loadInsns.add(AdapterUtil.box(type));
         }

         return WrapOpParamsProcessor.WrapOpOriginalCall.CallArg.create(i, loadInsns);
      }).toList();
      WrapOpParamsProcessor.WrapOpOriginalCall reconstruct = new WrapOpParamsProcessor.WrapOpOriginalCall(args);
      AbstractInsnNode start = arrayInit.getNext();
      AbstractInsnNode dirtyInvoke = dirtyCallInsns.getLast();
      if (!(dirtyInvoke instanceof MethodInsnNode)) {
         return false;
      }

      AbstractInsnNode end = dirtyInvoke.getPrevious();
      InsnList methodInsns = context.methodNode().instructions;
      AdapterUtil.replaceRangeInclusive(methodInsns, start, end, reconstruct.merge());
      if (AdapterUtil.getIntConstValue(arrayInit.getPrevious()).isPresent()) {
         methodInsns.set(arrayInit.getPrevious(), AdapterUtil.getIntConstInsn(args.size()));
      } else {
         InsnList loadNewSize = AdapterUtil.insnsWithAdapter(c -> {
            c.pop();
            c.iconst(args.size());
         });
         methodInsns.insertBefore(arrayInit, loadNewSize);
      }

      return true;
   }

   public record WrapOpOriginalCall(List<WrapOpParamsProcessor.WrapOpOriginalCall.CallArg> args) {
      public List<AbstractInsnNode> merge() {
         return this.args.stream().flatMap(a -> Stream.of(a.pre, a.load, a.post).flatMap(Collection::stream)).toList();
      }

      @Nullable
      public static WrapOpParamsProcessor.WrapOpOriginalCall parse(List<List<AbstractInsnNode>> argsInsns) {
         if (argsInsns.isEmpty()) {
            return null;
         }

         List<WrapOpParamsProcessor.WrapOpOriginalCall.CallArg> args = new ArrayList<>();

         for (List<AbstractInsnNode> insns : argsInsns) {
            WrapOpParamsProcessor.WrapOpOriginalCall.CallArg arg = WrapOpParamsProcessor.WrapOpOriginalCall.CallArg.parse(insns);
            if (arg == null) {
               return null;
            }

            args.add(arg);
         }

         return new WrapOpParamsProcessor.WrapOpOriginalCall(args);
      }

      public record CallArg(List<AbstractInsnNode> pre, List<AbstractInsnNode> load, List<AbstractInsnNode> post) {
         public static WrapOpParamsProcessor.WrapOpOriginalCall.CallArg create(int index, List<AbstractInsnNode> load) {
            List<AbstractInsnNode> pre = List.of(new InsnNode(89), AdapterUtil.getIntConstInsn(index));
            List<AbstractInsnNode> post = List.of(new InsnNode(83));
            return new WrapOpParamsProcessor.WrapOpOriginalCall.CallArg(pre, load, post);
         }

         @Nullable
         public static WrapOpParamsProcessor.WrapOpOriginalCall.CallArg parse(List<AbstractInsnNode> insns) {
            if (insns.size() >= 4 && insns.getFirst().getOpcode() == 89) {
               AbstractInsnNode idxInsn = insns.get(1);
               if (AdapterUtil.getIntConstValue(idxInsn).isEmpty()) {
                  return null;
               }

               AbstractInsnNode aastore = insns.getLast();
               return aastore.getOpcode() != 83
                  ? null
                  : new WrapOpParamsProcessor.WrapOpOriginalCall.CallArg(insns.subList(0, 2), insns.subList(2, insns.size() - 1), List.of(insns.getLast()));
            } else {
               return null;
            }
         }
      }
   }
}
