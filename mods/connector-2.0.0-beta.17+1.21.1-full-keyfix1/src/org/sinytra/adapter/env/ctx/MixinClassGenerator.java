package org.sinytra.adapter.env.ctx;

import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;

public interface MixinClassGenerator {
   Map<String, MixinClassGenerator.GeneratedClass> getGeneratedMixinClasses();

   ClassNode getOrGenerateMixinClass(ClassNode var1, String var2, @Nullable String var3);

   record GeneratedClass(String originalName, String generatedName, ClassNode node) {
   }
}
