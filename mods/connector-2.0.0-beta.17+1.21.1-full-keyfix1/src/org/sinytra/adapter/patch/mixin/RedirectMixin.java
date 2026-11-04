package org.sinytra.adapter.patch.mixin;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.objectweb.asm.Type;
import org.sinytra.adapter.env.ctx.MethodHelper;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.TargetPair;
import org.sinytra.adapter.env.param.MethodParameters;
import org.sinytra.adapter.env.param.Parameters;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.patch.TxResult;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.patch.config.ConfigurationTemplates;
import org.sinytra.adapter.patch.config.MutableConfiguration;
import org.sinytra.adapter.patch.config.PropertyContainerTemplate;
import org.sinytra.adapter.patch.config.key.ControlKeys;
import org.sinytra.adapter.patch.config.key.MixinKeys;
import org.sinytra.adapter.patch.processor.ParametersProcessor;
import org.sinytra.adapter.patch.processor.Processors;
import org.sinytra.adapter.patch.processor.redirect.DivertRedirectProcessor;
import org.sinytra.adapter.patch.processor.redirect.ParameterUsageProcessor;
import org.sinytra.adapter.patch.resolver.Resolvers;
import org.sinytra.adapter.patch.resolver.injection.InjectionPointResolver;
import org.sinytra.adapter.patch.resolver.special.ResolverSyntheticInstanceof;
import org.sinytra.adapter.util.MethodQualifier;

public class RedirectMixin implements MixinType {
   private static final PropertyContainerTemplate TEMPLATE = ConfigurationTemplates.MIXIN_AT.extend().pluralKeys(MixinKeys.TARGET_METHOD).build();

   @Override
   public PropertyContainerTemplate getConfigurationTemplate() {
      return TEMPLATE;
   }

   @Override
   public Set<MixinFlag> getFlags() {
      return EnumSet.of(MixinFlag.AT_TARGET_SENSITIVE, MixinFlag.ACCEPTS_INSTANCE);
   }

   @Override
   public TxResult preProcess(MixinContext context, MutableConfiguration clean, Resolvers resolvers, Processors processors) {
      resolvers.addBefore(InjectionPointResolver.class, new ResolverSyntheticInstanceof(false));
      processors.addAfter(ParametersProcessor.class, new DivertRedirectProcessor()).addAfter(ParametersProcessor.class, new ParameterUsageProcessor());
      if (clean.getAtData() != null && "INVOKE".equals(clean.getAtData().getValue())) {
         MethodQualifier targetDesc = clean.getAtData().getTarget().flatMap(MethodQualifier::parse).orElse(null);
         if (targetDesc == null) {
            return TxResult.FAIL;
         }

         List<Type> methodParams = Parameters.getParameterTypes(context.methodNode().desc);
         List<Type> callTypes = Parameters.getParameterTypes(targetDesc.desc());
         boolean isStatic = this.isStaticRedirect(context, targetDesc, methodParams, callTypes);
         if (!isStatic) {
            callTypes.addFirst(methodParams.getFirst());
         }

         MethodParameters capturedMethodParams = MethodParameters.create(
            Parameters.parse(context.methodNode(), callTypes.size(), -1),
            List.of(MethodParameters.ParamGroup.CAPTURED_PARAMS, MethodParameters.ParamGroup.LOCALS)
         );
         MethodParameters params = MethodParameters.builder()
            .putTypes(MethodParameters.ParamGroup.METHOD_PARAMS, callTypes)
            .putTypes(MethodParameters.ParamGroup.CAPTURED_PARAMS, capturedMethodParams.getTypes(MethodParameters.ParamGroup.CAPTURED_PARAMS))
            .putTypes(MethodParameters.ParamGroup.LOCALS, capturedMethodParams.getTypes(MethodParameters.ParamGroup.LOCALS))
            .build();
         clean.setParameters(params);
         return TxResult.SUCCESS;
      } else {
         return TxResult.FAIL;
      }
   }

   @Override
   public TxResult postProcess(MixinContext context, Configuration clean, MutableConfiguration dirty, Recipe recipe) {
      if (dirty.getTargetMethod() == null) {
         return TxResult.FAIL;
      }

      MethodQualifier targetDesc = dirty.getAtData().getTarget().flatMap(MethodQualifier::parse).orElse(null);
      if (targetDesc == null) {
         return TxResult.FAIL;
      }

      TargetPair dirtyTarget = context.methods().findOwnMethodPair(context.dirtyLookup(), targetDesc);
      if (dirtyTarget == null) {
         return TxResult.FAIL;
      }

      List<Type> dirtyCaptured = context.methods().resolveCapturedMethodParams(recipe.clean(), recipe.dirty());
      List<Type> callTypes = Parameters.getParameterTypes(targetDesc.desc());
      if (!MethodHelper.isStatic(dirtyTarget.methodNode())) {
         Type owner = Type.getObjectType(dirtyTarget.classNode().name);
         callTypes.addFirst(owner);
      }

      dirty.inheritProperyIfAbsent(ControlKeys.PARAMETERS);
      MethodParameters params = (dirty.getParameters() != null ? dirty.getParameters().mutableCopy() : MethodParameters.builder())
         .putTypes(MethodParameters.ParamGroup.METHOD_PARAMS, callTypes)
         .putTypes(MethodParameters.ParamGroup.CAPTURED_PARAMS, dirtyCaptured)
         .build();
      dirty.setParameters(params);
      dirty.setReturnType(Type.getReturnType(targetDesc.desc()));
      return TxResult.SUCCESS;
   }

   private boolean isStaticRedirect(MixinContext context, MethodQualifier targetDesc, List<Type> methodParams, List<Type> callTypes) {
      TargetPair cleanTarget = context.methods().findOwnMethodPair(context.cleanLookup(), targetDesc);
      return cleanTarget != null
         ? MethodHelper.isStatic(cleanTarget.methodNode())
         : methodParams.size() >= callTypes.size() && methodParams.subList(0, callTypes.size()).equals(callTypes);
   }
}
