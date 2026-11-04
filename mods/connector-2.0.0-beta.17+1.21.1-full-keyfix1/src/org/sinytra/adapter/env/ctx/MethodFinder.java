package org.sinytra.adapter.env.ctx;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.util.MethodQualifier;
import org.sinytra.adapter.util.provider.ClassLookup;
import org.slf4j.Logger;

public class MethodFinder {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final PatchEnvironment environment;
   @Nullable
   private final String fallbackOwner;

   public MethodFinder(PatchEnvironment environment, String fallbackOwner) {
      this.environment = environment;
      this.fallbackOwner = fallbackOwner;
   }

   @Nullable
   public TargetPair findInheritedMethod(ClassLookup lookup, MethodQualifier qualifier) {
      ClassNode node = lookup.getClass(qualifier.internalOwnerName()).orElse(null);
      if (node == null) {
         return null;
      }

      Collection<String> parents = this.environment.inheritanceHandler(lookup).getClassParents(node.name);
      return Stream.concat(Stream.of(node.name), parents.stream())
         .flatMap(cls -> lookup.findMethod(cls, qualifier.name(), qualifier.desc()).stream().map(m -> new TargetPair(lookup.getClass(cls).orElseThrow(), m)))
         .findFirst()
         .orElse(null);
   }

   @Nullable
   public TargetPair findMethod(ClassLookup lookup, MethodQualifier qualifier, int flags) {
      Pair<ClassNode, List<MethodNode>> pair = this.findMethods(lookup, qualifier, flags);
      if (pair == null) {
         return null;
      } else if (((List)pair.getSecond()).isEmpty()) {
         LOGGER.debug("Target method not found: {}{}{}", new Object[]{qualifier.owner(), qualifier.name(), qualifier.desc()});
         return null;
      } else if (((List)pair.getSecond()).size() > 1) {
         LOGGER.debug("Multiple candidates found for method: {}{}{}", new Object[]{qualifier.owner(), qualifier.name(), qualifier.desc()});
         return null;
      } else {
         return new TargetPair((ClassNode)pair.getFirst(), (MethodNode)((List)pair.getSecond()).getFirst());
      }
   }

   @Nullable
   public Pair<ClassNode, List<MethodNode>> findMethods(ClassLookup lookup, MethodQualifier qualifier, int flags) {
      if (qualifier != null && qualifier.name() != null) {
         String owner;
         if (qualifier.internalOwnerName() != null) {
            owner = qualifier.internalOwnerName();
         } else {
            if (!hasFlag(flags, 2) || this.fallbackOwner == null) {
               return null;
            }

            owner = this.fallbackOwner;
         }

         ClassNode targetClass = lookup.getClass(owner).orElse(null);
         if (targetClass == null) {
            return null;
         }

         String desc = qualifier.desc();
         List<MethodNode> candidates = targetClass.methods
            .stream()
            .filter(mtd -> mtd.name.equals(qualifier.name()) && (hasFlag(flags, 1) || desc == null || mtd.desc.equals(desc)))
            .toList();
         if (candidates.size() > 1 && desc == null) {
            candidates = candidates.stream().filter(mtd -> (mtd.access & 4096) == 0 && (mtd.access & 64) == 0).toList();
         }

         return candidates.isEmpty() && targetClass.superName != null
            ? this.findMethods(
               lookup, new MethodQualifier(Type.getObjectType(targetClass.superName).getDescriptor(), qualifier.name(), qualifier.desc()), flags
            )
            : Pair.of(targetClass, candidates);
      } else {
         return null;
      }
   }

   private static boolean hasFlag(int flags, int flag) {
      return (flags & flag) != 0;
   }

   public static class Flags {
      public static final int IGNORE_DESC = 1;
      public static final int FALLBACK_OWNER = 2;
   }
}
