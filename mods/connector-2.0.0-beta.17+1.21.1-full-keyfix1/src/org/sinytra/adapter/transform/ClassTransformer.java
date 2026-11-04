package org.sinytra.adapter.transform;

import org.objectweb.asm.tree.ClassNode;
import org.sinytra.adapter.env.ann.ClassTarget;
import org.sinytra.adapter.env.ctx.PatchContext;
import org.sinytra.adapter.env.ctx.PatchResult;

public interface ClassTransformer {
   PatchResult apply(ClassNode var1, ClassTarget var2, PatchContext var3);
}
