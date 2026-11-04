package org.sinytra.connector.transformer.patch;

import com.mojang.logging.LogUtils;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.PatchResult;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.transform.MethodTransformer;
import org.sinytra.adapter.util.AdapterUtil;
import org.slf4j.Logger;

public record AccessorToInvokerTransformer(String value) implements MethodTransformer {
   private static final Logger LOGGER = LogUtils.getLogger();

   @Override
   public PatchResult apply(MixinContext context, Configuration config) {
      if (!context.methodAnnotation().matchesDesc("Lorg/spongepowered/asm/mixin/gen/Accessor;")) {
         return PatchResult.PASS;
      }

      ClassNode classNode = context.classNode();
      MethodNode methodNode = context.methodNode();
      AnnotationVisitor visitor = methodNode.visitAnnotation("Lorg/spongepowered/asm/mixin/gen/Invoker;", true);
      visitor.visit("value", this.value);
      visitor.visitEnd();
      methodNode.visibleAnnotations.remove(context.methodAnnotation().unwrap());
      LOGGER.info(AdapterUtil.MIXINPATCH, "Redirecting accessor {}.{} to invoke method {}", new Object[]{classNode.name, methodNode.name, this.value});
      return PatchResult.APPLY;
   }
}
