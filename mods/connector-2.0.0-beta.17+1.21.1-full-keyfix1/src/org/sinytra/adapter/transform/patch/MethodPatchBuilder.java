package org.sinytra.adapter.transform.patch;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.commons.InstructionAdapter;
import org.sinytra.adapter.env.param.MethodParameters;
import org.sinytra.adapter.transform.MethodTransformer;

public interface MethodPatchBuilder {
   MethodPatchBuilder targetMixinType(String var1);

   MethodPatchBuilder targetClass(String... var1);

   MethodPatchBuilder targetMethod(String... var1);

   MethodPatchBuilder targetInjectionPoint(String var1);

   MethodPatchBuilder targetInjectionPoint(String var1, @Nullable String var2);

   MethodPatchBuilder targetConstant(double var1);

   MethodPatchBuilder targetField(String var1);

   MethodPatchBuilder extractMixin(String var1);

   MethodPatchBuilder modifyTarget(String var1);

   MethodPatchBuilder modifyInjectionPoint(String var1);

   MethodPatchBuilder modifyInjectionPoint(String var1, String var2);

   MethodPatchBuilder replaceInjectionPoint(String var1, String var2);

   MethodPatchBuilder modifyParams(UnaryOperator<MethodParameters> var1);

   MethodPatchBuilder modifyStatic(boolean var1);

   MethodPatchBuilder modifyMixinType(String var1);

   MethodPatchBuilder divertRedirector(Consumer<InstructionAdapter> var1);

   MethodPatchBuilder disable();

   MethodPatchBuilder transform(MethodTransformer var1);

   MethodPatch build();
}
