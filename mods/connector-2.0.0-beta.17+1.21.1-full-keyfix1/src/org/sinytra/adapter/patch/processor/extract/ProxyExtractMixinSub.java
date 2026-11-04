package org.sinytra.adapter.patch.processor.extract;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.PatchEnvironment;
import org.sinytra.adapter.env.ctx.TargetPair;
import org.sinytra.adapter.env.param.Parameters;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.patch.TxResult;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.patch.config.key.MixinKeys;
import org.sinytra.adapter.patch.config.key.SpecialKeys;
import org.sinytra.adapter.patch.processor.Processor;
import org.sinytra.adapter.util.AdapterUtil;
import org.sinytra.adapter.util.OpcodeUtil;

public class ProxyExtractMixinSub implements Processor {
   public static final ProxyExtractMixinSub INSTANCE = new ProxyExtractMixinSub();

   @Override
   public TxResult process(MixinContext context, Configuration dirty, Recipe recipe) {
      if (recipe.clean().getTargetClass().equals(dirty.getTargetClass())) {
         return TxResult.PASS;
      }

      if (dirty.hasProperty(SpecialKeys.EXTRACT_ORIGIN_PARAM) && !recipe.clean().hasProperty(MixinKeys.LOCALS)) {
         TargetPair dirtyTarget = recipe.getNewCleanTarget();
         if (dirtyTarget == null) {
            return TxResult.FAIL;
         }

         ClassNode classNode = context.classNode();
         MethodNode methodNode = context.methodNode();
         PatchEnvironment environment = context.environment();
         int originParamIndex = dirty.getProperty(SpecialKeys.EXTRACT_ORIGIN_PARAM).orElseThrow();
         List<Type> destParams = Parameters.getParameterTypes(dirty.getTargetMethod().desc());
         Type instanceType = destParams.get(originParamIndex);
         String destinationClass = dirty.getTargetClass();
         ClassNode generatedTarget = environment.classGenerator().getOrGenerateMixinClass(classNode, destinationClass, null);
         environment.refmapHolder().copyEntries(classNode.name, generatedTarget.name);
         String name = methodNode.name + "$adapter$mirror$" + AdapterUtil.randomString(5);
         List<Type> originalParams = Parameters.getParameterTypes(methodNode.desc);
         List<Type> newParams = new ArrayList<>(originalParams);
         int capturedInstanceParamIndex = newParams.size();
         newParams.add(instanceType);
         String newDesc = Type.getMethodDescriptor(Type.getReturnType(methodNode.desc), newParams.toArray(Type[]::new));
         MethodNode invokerMixinMethod = (MethodNode)generatedTarget.visitMethod(10, name, newDesc, null, null);
         invokerMixinMethod.visibleAnnotations = new ArrayList(methodNode.visibleAnnotations);
         AnnotationVisitor visitor = invokerMixinMethod.visitParameterAnnotation(capturedInstanceParamIndex, "Lcom/llamalad7/mixinextras/sugar/Local;", false);
         visitor.visit("argsOnly", true);
         visitor.visit("index", originParamIndex);
         methodNode.access = OpcodeUtil.setAccessVisibility(methodNode.access, 1);
         methodNode.visibleAnnotations.remove(context.methodAnnotation().unwrap());
         methodNode.visitAnnotation("Lorg/spongepowered/asm/mixin/Unique;", true);
         GeneratorAdapter gen = new GeneratorAdapter(invokerMixinMethod, invokerMixinMethod.access, invokerMixinMethod.name, invokerMixinMethod.desc);
         gen.newLabel();
         gen.loadArg(capturedInstanceParamIndex);

         for (int i = 0; i < originalParams.size(); i++) {
            gen.loadArg(i);
         }

         gen.invokeVirtual(instanceType, new Method(methodNode.name, methodNode.desc));
         gen.newLabel();
         gen.returnValue();
         gen.newLabel();
         gen.endMethod();
         invokerMixinMethod.maxLocals = invokerMixinMethod.maxStack = 999;
         context.recordCtxAudit("Proxy (mirror) mixin to target %s", destinationClass);
         return TxResult.SUCCESS;
      } else {
         return TxResult.FAIL;
      }
   }
}
