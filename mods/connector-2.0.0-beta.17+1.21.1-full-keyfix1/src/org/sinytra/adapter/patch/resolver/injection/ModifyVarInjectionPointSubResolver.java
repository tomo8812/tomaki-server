package org.sinytra.adapter.patch.resolver.injection;

import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.analysis.InstructionMatcher;
import org.sinytra.adapter.analysis.locals.LocalVarAnalyzer;
import org.sinytra.adapter.analysis.locals.LocalVariableLookup;
import org.sinytra.adapter.env.ctx.MixinContext;
import org.sinytra.adapter.env.ctx.TargetPair;
import org.sinytra.adapter.patch.Recipe;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.patch.config.key.MixinKeys;
import org.sinytra.adapter.patch.resolver.SubResolver;

public class ModifyVarInjectionPointSubResolver implements SubResolver {
   @Nullable
   @Override
   public Configuration resolve(MixinContext context, Recipe recipe) {
      if (!recipe.clean().getAtData().getValue().equals("STORE")) {
         return null;
      }

      TargetPair dirtyPair = recipe.getDirtyTarget();
      if (dirtyPair == null) {
         return null;
      }

      Integer ordinal = recipe.clean().getProperty(MixinKeys.ORDINAL).orElse(null);
      if (ordinal == null) {
         return null;
      }

      Type varType = Type.getReturnType(context.methodNode().desc);
      TargetPair cleanPair = recipe.getCleanTarget();
      LocalVariableLookup lookup = new LocalVariableLookup(cleanPair.methodNode());
      LocalVariableNode desired = lookup.getByTypedOrdinal(varType, ordinal).orElseThrow();
      InsnList desiredInitializerInsns = LocalVarAnalyzer.findInitializerInsns(cleanPair.methodNode(), desired.index);

      for (MethodNode method : dirtyPair.classNode().methods) {
         LocalVariableLookup dirtyLookup = new LocalVariableLookup(method);
         List<LocalVariableNode> lvs = method.localVariables.stream().filter(lvn -> desired.desc.equals(lvn.desc)).filter(lvn -> {
            InsnList insns = LocalVarAnalyzer.findInitializerInsns(method, lvn.index);
            return InstructionMatcher.test(desiredInitializerInsns, insns);
         }).toList();
         if (lvs.size() == 1) {
            int dirtyOrdinal = dirtyLookup.getOrdinal(lvs.getFirst());
            return recipe.dirty().copyClean().setProperty(MixinKeys.ORDINAL, dirtyOrdinal).setTargetMethod(method).inheritAtData();
         }
      }

      return null;
   }
}
