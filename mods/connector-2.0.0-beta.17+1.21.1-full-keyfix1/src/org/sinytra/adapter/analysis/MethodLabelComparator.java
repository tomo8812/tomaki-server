package org.sinytra.adapter.analysis;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.env.ctx.TargetPair;
import org.sinytra.adapter.patch.Recipe;

public class MethodLabelComparator {
   @Nullable
   public static MethodLabelComparator.ComparisonResult findPatchedLabels(AbstractInsnNode cleanInjectionInsn, Recipe recipe) {
      TargetPair cleanTarget = recipe.getCleanTarget();
      if (cleanTarget == null) {
         return null;
      }

      TargetPair dirtyTarget = recipe.getDirtyTarget();
      if (dirtyTarget == null) {
         return null;
      }

      List<List<AbstractInsnNode>> cleanLabels = getLabelsInMethod(cleanTarget.methodNode());
      List<List<AbstractInsnNode>> cleanLabelsOriginal = List.copyOf(cleanLabels);
      List<List<AbstractInsnNode>> cleanMatchedLabels = cleanLabels.stream().filter(insns -> insns.contains(cleanInjectionInsn)).toList();
      if (cleanMatchedLabels.size() != 1) {
         return null;
      }

      List<AbstractInsnNode> cleanLabel = cleanMatchedLabels.getFirst();
      List<List<AbstractInsnNode>> dirtyLabels = getLabelsInMethod(dirtyTarget.methodNode());
      List<List<AbstractInsnNode>> dirtyLabelsOriginal = List.copyOf(dirtyLabels);
      Map<List<AbstractInsnNode>, List<AbstractInsnNode>> matchedLabels = new LinkedHashMap<>();

      for (List<AbstractInsnNode> cleanInsns : cleanLabelsOriginal) {
         List<List<AbstractInsnNode>> candidates = new ArrayList<>();

         for (List<AbstractInsnNode> dirtyInsns : dirtyLabels) {
            if (InstructionMatcher.test(cleanInsns, dirtyInsns, 17)) {
               candidates.add(dirtyInsns);
            }
         }

         if (candidates.size() == 1) {
            List<AbstractInsnNode> dirtyInsns = candidates.getFirst();
            matchedLabels.put(cleanInsns, dirtyInsns);
            cleanLabels.remove(cleanInsns);
            dirtyLabels.remove(dirtyInsns);
         }
      }

      Pair<List<AbstractInsnNode>, List<AbstractInsnNode>> patchRange = findPatchHunkRange(cleanLabel, cleanLabelsOriginal, matchedLabels);
      if (patchRange == null) {
         return null;
      }

      int to = dirtyLabelsOriginal.indexOf(patchRange.getSecond());
      List<List<AbstractInsnNode>> patchedLabels;
      if (patchRange.getFirst() == null) {
         patchedLabels = dirtyLabelsOriginal.subList(0, to);
      } else {
         int from = dirtyLabelsOriginal.indexOf(patchRange.getFirst()) + 1;
         if (from >= to) {
            return null;
         }

         patchedLabels = dirtyLabelsOriginal.subList(dirtyLabelsOriginal.indexOf(patchRange.getFirst()) + 1, to);
      }

      return new MethodLabelComparator.ComparisonResult(patchedLabels, cleanLabel);
   }

   @Nullable
   private static Pair<List<AbstractInsnNode>, List<AbstractInsnNode>> findPatchHunkRange(
      List<AbstractInsnNode> cleanLabel, List<List<AbstractInsnNode>> cleanLabels, Map<List<AbstractInsnNode>, List<AbstractInsnNode>> matchedLabels
   ) {
      int cleanLabelOrdinal = cleanLabels.indexOf(cleanLabel);
      List<AbstractInsnNode> dirtyLabelBefore;
      if (cleanLabelOrdinal == 0) {
         dirtyLabelBefore = null;
      } else {
         dirtyLabelBefore = Stream.<Integer>iterate(cleanLabelOrdinal, i -> i >= 0, i -> i - 1)
            .map(i -> matchedLabels.get(cleanLabels.get(i)))
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
         if (dirtyLabelBefore == null) {
            return null;
         }
      }

      List<AbstractInsnNode> dirtyLabelAfter = Stream.<Integer>iterate(cleanLabels.indexOf(cleanLabel), i -> i < cleanLabels.size(), i -> i + 1)
         .map(i -> matchedLabels.get(cleanLabels.get(i)))
         .filter(Objects::nonNull)
         .findFirst()
         .orElse(null);
      return dirtyLabelAfter == null ? null : Pair.of(dirtyLabelBefore, dirtyLabelAfter);
   }

   private static List<List<AbstractInsnNode>> getLabelsInMethod(MethodNode methodNode) {
      List<List<AbstractInsnNode>> list = new ArrayList<>();
      List<AbstractInsnNode> workingList = null;

      for (AbstractInsnNode insn : methodNode.instructions) {
         if (!(insn instanceof FrameNode)) {
            if (insn instanceof LabelNode) {
               if (workingList != null) {
                  list.add(workingList);
               }

               workingList = new ArrayList<>();
            }

            workingList.add(insn);
         }
      }

      return list;
   }

   public record ComparisonResult(List<List<AbstractInsnNode>> patchedLabels, List<AbstractInsnNode> cleanLabel) {
   }
}
