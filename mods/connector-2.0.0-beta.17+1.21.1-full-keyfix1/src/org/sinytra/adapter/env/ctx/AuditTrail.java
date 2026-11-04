package org.sinytra.adapter.env.ctx;

import it.unimi.dsi.fastutil.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.analysis.MixinGroups;
import org.sinytra.adapter.patch.config.Configuration;

public interface AuditTrail {
   static AuditTrail create() {
      return new AuditTrailImpl();
   }

   void prepareMethod(MixinContext var1);

   void recordAudit(Object var1, ClassNode var2, String var3, Object... var4);

   void recordAudit(Object var1, MixinContext var2, String var3, Object... var4);

   void recordResult(MixinContext var1, Configuration var2, AuditTrail.Match var3);

   void processGroups(ClassNode var1, MixinGroups var2);

   String getCompleteReport();

   boolean hasFailingMixins();

   List<AuditTrail.Candidate> getFailingMixins();

   Map<AuditTrail.Candidate, AuditTrail.AuditLog> getAuditTrail();

   Map<AuditTrail.Candidate, AuditTrail.Match> getCandidates();

   @Nullable
   AuditTrail.Match getMatch(MixinContext var1);

   void merge(AuditTrail var1);

   void silenceClasses(Set<String> var1);

   record AuditLog(@Nullable String originalMethod, List<Pair<Object, StringBuilder>> entries) {
      public static AuditTrail.AuditLog create(MixinContext context) {
         return new AuditTrail.AuditLog(context.methodNode().name + context.methodNode().desc, new ArrayList<>());
      }
   }

   record Candidate(ClassNode classNode, MethodNode methodNode) {
   }

   enum Match {
      NONE,
      IGNORED,
      PARTIAL,
      FULL;

      public AuditTrail.Match or(AuditTrail.Match other) {
         if (this == NONE && other != NONE) {
            return other;
         } else if (this == IGNORED && other != NONE) {
            return other;
         } else {
            return this == PARTIAL && other == FULL ? FULL : this;
         }
      }

      // $VF: synthetic method
      private static AuditTrail.Match[] $values() {
         return new AuditTrail.Match[]{NONE, IGNORED, PARTIAL, FULL};
      }
   }
}
