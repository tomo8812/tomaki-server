package org.sinytra.adapter.env.ctx;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.Pair;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.analysis.MixinGroups;
import org.sinytra.adapter.patch.config.Configuration;
import org.sinytra.adapter.patch.config.key.MixinKeys;
import org.sinytra.adapter.util.AdapterUtil;
import org.slf4j.Logger;

public class AuditTrailImpl implements AuditTrail {
   private static final DecimalFormat FORMAT = new DecimalFormat("##.00");
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Map<AuditTrail.Candidate, AuditTrail.AuditLog> auditTrail = new LinkedHashMap<>();
   private final Map<AuditTrail.Candidate, AuditTrail.Match> candidates = new ConcurrentHashMap<>();

   @Override
   public void prepareMethod(MixinContext context) {
      AuditTrail.Candidate candidate = new AuditTrail.Candidate(context.classNode(), context.methodNode());
      synchronized (this.auditTrail) {
         this.auditTrail.put(candidate, AuditTrail.AuditLog.create(context));
      }
   }

   @Override
   public void recordAudit(Object transform, ClassNode classNode, String message, Object... args) {
      AuditTrail.Candidate candidate = new AuditTrail.Candidate(classNode, null);
      this.recordAudit(transform, null, candidate, message.formatted(args));
   }

   @Override
   public void recordAudit(Object transform, MixinContext context, String message, Object... args) {
      AuditTrail.Candidate candidate = new AuditTrail.Candidate(context.classNode(), context.methodNode());
      this.recordAudit(transform, context.methodNode(), candidate, message.formatted(args));
   }

   private void recordAudit(Object transform, @Nullable MethodNode methodNode, AuditTrail.Candidate candidate, String message) {
      synchronized (this.auditTrail) {
         AuditTrail.AuditLog auditLog = this.auditTrail
            .computeIfAbsent(candidate, k -> new AuditTrail.AuditLog(methodNode != null ? methodNode.name + methodNode.desc : null, new ArrayList<>()));
         List<Pair<Object, StringBuilder>> entries = auditLog.entries();
         StringBuilder builder;
         if (!entries.isEmpty() && entries.getLast().left() == transform) {
            builder = (StringBuilder)entries.getLast().right();
         } else {
            entries.add(Pair.of(transform, builder = new StringBuilder()));
            builder.append("\n  >> Using ").append(transform.getClass().getName());
         }

         builder.append("\n     - ").append(message);
         LOGGER.info(AdapterUtil.MIXINPATCH, "Applying [{}] {}", transform.getClass().getSimpleName(), message);
      }
   }

   @Override
   public void recordResult(MixinContext context, Configuration configuration, AuditTrail.Match match) {
      AuditTrail.Candidate candidate = new AuditTrail.Candidate(context.classNode(), context.methodNode());
      this.candidates.compute(candidate, (key, prev) -> {
         boolean isNotRequired = configuration.getProperty(MixinKeys.REQUIRE).map(i -> i == 0).orElse(false);
         AuditTrail.Match maybeIgnore = match == AuditTrail.Match.NONE && isNotRequired ? AuditTrail.Match.IGNORED : match;
         return prev == null ? maybeIgnore : prev.or(maybeIgnore);
      });
   }

   @Override
   public String getCompleteReport() {
      StringBuilder builder = new StringBuilder();
      this.getSummaryLines().forEach(l -> builder.append(l).append("\n"));
      List<Entry<AuditTrail.Candidate, AuditTrail.Match>> failed = this.candidates
         .entrySet()
         .stream()
         .filter(m -> m.getValue() == AuditTrail.Match.NONE || m.getValue() == AuditTrail.Match.IGNORED)
         .toList();
      if (!failed.isEmpty()) {
         builder.append("\n=============== Failed mixins ===============");
         failed.forEach(
            e -> builder.append("\n")
               .append(e.getValue() == AuditTrail.Match.IGNORED ? "(ignored) " : "")
               .append(e.getKey().classNode().name)
               .append(" ")
               .append(e.getKey().methodNode().name)
               .append(e.getKey().methodNode().desc)
         );
         builder.append("\n=============================================\n\n");
      } else {
         builder.append("\n");
      }

      this.auditTrail.forEach((candidate, auditLog) -> {
         if (!auditLog.entries().isEmpty()) {
            if (auditLog.originalMethod() == null) {
               builder.append("Mixin class ").append(candidate.classNode().name);
            } else {
               builder.append("Mixin method ").append(candidate.classNode().name).append(" ").append(auditLog.originalMethod());
            }

            for (Pair<Object, StringBuilder> record : auditLog.entries()) {
               builder.append((CharSequence)record.right());
            }

            builder.append("\n\n");
         }
      });
      return builder.toString();
   }

   @Override
   public List<AuditTrail.Candidate> getFailingMixins() {
      return this.candidates.entrySet().stream().filter(m -> m.getValue() == AuditTrail.Match.NONE).map(Entry::getKey).toList();
   }

   @Override
   public Map<AuditTrail.Candidate, AuditTrail.AuditLog> getAuditTrail() {
      return this.auditTrail;
   }

   @Override
   public Map<AuditTrail.Candidate, AuditTrail.Match> getCandidates() {
      return this.candidates;
   }

   @Nullable
   @Override
   public AuditTrail.Match getMatch(MixinContext context) {
      AuditTrail.Candidate candidate = new AuditTrail.Candidate(context.classNode(), context.methodNode());
      return this.candidates.get(candidate);
   }

   @Override
   public void merge(AuditTrail other) {
      synchronized (this.auditTrail) {
         this.auditTrail.putAll(other.getAuditTrail());
      }

      this.candidates.putAll(other.getCandidates());
   }

   @Override
   public void silenceClasses(Set<String> classes) {
      synchronized (this.candidates) {
         for (AuditTrail.Candidate candidate : Set.copyOf(this.candidates.keySet())) {
            if (classes.contains(candidate.classNode().name)) {
               this.candidates.put(candidate, AuditTrail.Match.IGNORED);
            }
         }
      }
   }

   @Override
   public void processGroups(ClassNode classNode, MixinGroups groups) {
      synchronized (this.candidates) {
         for (Entry<AuditTrail.Candidate, AuditTrail.Match> entry : Set.copyOf(this.candidates.entrySet())) {
            if (entry.getValue() == AuditTrail.Match.NONE) {
               AuditTrail.Candidate candidate = entry.getKey();
               MixinGroups.GroupInfo group = groups.getGroupInfo(candidate.methodNode());
               if (group != null && this.isGroupSatisfied(classNode, group)) {
                  this.candidates.put(candidate, AuditTrail.Match.IGNORED);
               }
            }
         }
      }
   }

   private boolean isGroupSatisfied(ClassNode classNode, MixinGroups.GroupInfo info) {
      int passing = 0;

      for (MethodNode method : info.members()) {
         AuditTrail.Candidate candidate = new AuditTrail.Candidate(classNode, method);
         if (this.auditTrail.containsKey(candidate) && this.candidates.get(candidate) != AuditTrail.Match.NONE) {
            passing++;
         }
      }

      return passing >= info.minRequired();
   }

   private List<String> getSummaryLines() {
      int total = this.candidates.size();
      int successful = (int)this.candidates.values().stream().filter(m -> m == AuditTrail.Match.FULL).count();
      int partial = (int)this.candidates.values().stream().filter(m -> m == AuditTrail.Match.PARTIAL).count();
      int failed = (int)this.candidates.values().stream().filter(m -> m == AuditTrail.Match.NONE).count();
      int silenced = (int)this.candidates.values().stream().filter(m -> m == AuditTrail.Match.IGNORED).count();
      double rate = (double)(successful + partial) / total * 100.0;
      double accuracy = ((double)successful / total + (double)partial / total / 2.0) * 100.0;
      return List.of(
         "==== Connector Mixin Patch Audit Summary ====",
         "Successful: %s".formatted(successful),
         "Partial: %s".formatted(partial),
         "Failed: %s%s".formatted(failed, silenced > 0 ? " (%s ignored)".formatted(silenced) : ""),
         "Success rate: %s%%        Accuracy: %s%%".formatted(FORMAT.format(rate), FORMAT.format(accuracy)),
         "============================================="
      );
   }

   @Override
   public boolean hasFailingMixins() {
      return !this.getFailingMixins().isEmpty();
   }
}
