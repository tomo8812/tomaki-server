package org.sinytra.adapter.analysis.method;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Handle;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Interpreter;
import org.objectweb.asm.tree.analysis.SourceInterpreter;
import org.objectweb.asm.tree.analysis.SourceValue;
import org.objectweb.asm.tree.analysis.Value;
import org.sinytra.adapter.analysis.selector.FrameUtil;
import org.sinytra.adapter.env.ctx.TargetPair;
import org.sinytra.adapter.env.util.TypeConstants;
import org.sinytra.adapter.util.AdapterUtil;
import org.sinytra.adapter.util.MethodQualifier;

public class MethodAnalyzer {
   public static final String LAMBDA_PREFIX = "lambda$";

   public static boolean isLambda(MethodNode methodNode) {
      return methodNode.name.startsWith("lambda$");
   }

   public static boolean isLambda(MethodQualifier qualifier) {
      return qualifier.name() != null && qualifier.name().startsWith("lambda$");
   }

   public static boolean isDirtyDeprecatedMethod(MethodNode clean, MethodNode dirty) {
      return !AdapterUtil.hasAnnotation(clean.visibleAnnotations, TypeConstants.DEPRECATED)
         && AdapterUtil.hasAnnotation(dirty.visibleAnnotations, TypeConstants.DEPRECATED);
   }

   public static List<MethodNode> getOwnMethodCalls(TargetPair targetPair) {
      MethodNode methodNode = targetPair.methodNode();
      Frame<SourceValue>[] frames = FrameUtil.getFrames(methodNode);
      Set<MethodInsnNode> consumedCalls = new HashSet<>();

      for (AbstractInsnNode insn : methodNode.instructions) {
         if (insn instanceof MethodInsnNode call) {
            int index = methodNode.instructions.indexOf(insn);
            Frame<SourceValue> frame = frames[index];
            if (frame != null) {
               int argCount = FrameUtil.getPopCount(call);
               int stackTop = frame.getStackSize();

               for (int i = 0; i < argCount; i++) {
                  SourceValue arg = (SourceValue)frame.getStack(stackTop - 1 - i);

                  for (AbstractInsnNode producer : arg.insns) {
                     if (producer instanceof MethodInsnNode producerCall) {
                        consumedCalls.add(producerCall);
                     }
                  }
               }
            }
         }
      }

      List<MethodNode> topTierCalls = new ArrayList<>();
      ClassNode classNode = targetPair.classNode();

      for (AbstractInsnNode insn : methodNode.instructions) {
         if (insn instanceof MethodInsnNode call && call.owner.equals(classNode.name) && !consumedCalls.contains(call)) {
            classNode.methods.stream().filter(m -> m.name.equals(call.name) && m.desc.equals(call.desc)).findFirst().ifPresent(topTierCalls::add);
         }
      }

      return topTierCalls;
   }

   public static List<String> findLambdasInMethod(ClassNode cls, MethodNode method, @Nullable Multimap<String, MethodNode> methods) {
      List<String> list = new ArrayList<>();

      for (AbstractInsnNode insn : method.instructions) {
         if (insn instanceof InvokeDynamicInsnNode indy && indy.bsmArgs.length >= 3) {
            Object[] var7 = indy.bsmArgs;
            int var8 = var7.length;

            for (int var9 = 0; var9 < var8; var9++) {
               if (var7[var9] instanceof Handle handle && handle.getOwner().equals(cls.name) && handle.getName().startsWith("lambda$")) {
                  String name = handle.getName();
                  list.add(name);
                  if (methods != null) {
                     MethodNode lambda = findUniqueMethod(methods, name);
                     if (lambda != null) {
                        list.addAll(findLambdasInMethod(cls, lambda, methods));
                     }
                  }
                  break;
               }
            }
         }
      }

      return list;
   }

   public static Multimap<String, MethodInsnNode> getMethodCalls(MethodNode node) {
      Builder<String, MethodInsnNode> calls = ImmutableMultimap.builder();

      for (AbstractInsnNode insn : node.instructions) {
         if (insn instanceof MethodInsnNode minsn) {
            String qualifier = MethodQualifier.create(minsn).asDescriptor();
            calls.put(qualifier, minsn);
         }
      }

      return calls.build();
   }

   public static boolean containsMethodCall(MethodNode methodNode, MethodQualifier qualifier) {
      for (AbstractInsnNode insn : methodNode.instructions) {
         if (insn instanceof MethodInsnNode minsn && qualifier.matches(minsn)) {
            return true;
         }
      }

      return false;
   }

   public static <T> List<T> analyzeMethod(
      MethodNode methodNode, BiPredicate<MethodInsnNode, List<? extends SourceValue>> filter, MethodAnalyzer.NaryOperationHandler<T> handler
   ) {
      MethodAnalyzer.AnalysingSourceInterpreter<T> i = analyzeInterpretMethod(methodNode, new MethodAnalyzer.AnalysingSourceInterpreter<>(filter, handler));
      return i.getResults();
   }

   public static <T extends Interpreter<V>, V extends Value> T analyzeInterpretMethod(MethodNode methodNode, T interpreter) {
      Analyzer<V> analyzer = new Analyzer(interpreter);

      try {
         analyzer.analyze(methodNode.name, methodNode);
         return interpreter;
      } catch (AnalyzerException e) {
         throw new RuntimeException(e);
      }
   }

   @Nullable
   private static MethodNode findUniqueMethod(Multimap<String, MethodNode> methods, String name) {
      Collection<MethodNode> values = methods.get(name);
      if (values != null && !values.isEmpty()) {
         return values.size() > 1 ? null : values.iterator().next();
      } else {
         return null;
      }
   }

   private static class AnalysingSourceInterpreter<T> extends SourceInterpreter {
      private final BiPredicate<MethodInsnNode, List<? extends SourceValue>> filter;
      private final MethodAnalyzer.NaryOperationHandler<T> handler;
      private final List<T> results = new ArrayList<>();
      private final Collection<MethodInsnNode> seen = new HashSet<>();

      public AnalysingSourceInterpreter(BiPredicate<MethodInsnNode, List<? extends SourceValue>> filter, MethodAnalyzer.NaryOperationHandler<T> handler) {
         super(589824);
         this.filter = filter;
         this.handler = handler;
      }

      public List<T> getResults() {
         return this.results;
      }

      public SourceValue naryOperation(AbstractInsnNode insn, List<? extends SourceValue> values) {
         if (insn instanceof MethodInsnNode minsn && this.filter.test(minsn, values) && !this.seen.contains(minsn)) {
            T result = this.handler.accept(minsn, values);
            if (result != null) {
               this.results.add(result);
               this.seen.add(minsn);
            }
         }

         return super.naryOperation(insn, values);
      }

      // $VF: synthetic method
      // $VF: bridge method
      public Value naryOperation(AbstractInsnNode param1, List nullx) throws AnalyzerException {
         return this.naryOperation(var1, nullx);
      }
   }

   public interface NaryOperationHandler<T> {
      T accept(MethodInsnNode var1, List<? extends SourceValue> var2);
   }
}
