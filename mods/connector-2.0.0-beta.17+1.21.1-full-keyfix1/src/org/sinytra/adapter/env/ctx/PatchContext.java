package org.sinytra.adapter.env.ctx;

import java.util.List;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

public interface PatchContext extends RefMapper {
   static PatchContext create(ClassNode classNode, List<Type> targetTypes, PatchEnvironment environment) {
      return new PatchContextImpl(classNode, targetTypes, environment);
   }

   ClassNode classNode();

   List<Type> targetTypes();

   PatchEnvironment environment();

   void postApply(Runnable var1);
}
