package org.sinytra.adapter.transform.param;

import java.util.List;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.PatchResult;

public interface ParameterTransformer {
   PatchResult apply(ClassNode var1, MethodNode var2, MixinContext var3, List<Type> var4, int var5);
}
