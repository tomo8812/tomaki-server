package org.sinytra.adapter.analysis.locals;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;
import org.sinytra.adapter.util.AdapterUtil;
import org.sinytra.adapter.util.SingleValueHandle;

public class LVTSnapshot {
   private final List<LVTSnapshot.LocalVar> locals;
   private final int[] vars;

   public LVTSnapshot(List<LVTSnapshot.LocalVar> locals, int[] vars) {
      this.locals = locals;
      this.vars = vars;
   }

   public void applyDifference(MethodNode newNode) {
      LVTSnapshot newLVT = take(newNode);
      List<LVTSnapshot.LocalVar> removed = new ArrayList<>(this.locals);
      removed.removeIf(newLVT.locals::contains);
      int[] newVars = Arrays.copyOf(this.vars, this.vars.length);

      for (LVTSnapshot.LocalVar local : removed) {
         for (int i = 0; i < newVars.length; i++) {
            if (newVars[i] > local.index) {
               newVars[i] -= local.desc.getSize();
            }
         }

         newNode.localVariables.forEach(node -> {
            if (node.index > local.index) {
               node.index = node.index - local.desc.getSize();
            }
         });
      }

      List<LVTSnapshot.LocalVar> added = new ArrayList<>(newLVT.locals);
      added.removeIf(this.locals::contains);

      for (LVTSnapshot.LocalVar local : added) {
         for (int i = 0; i < newVars.length; i++) {
            if (newVars[i] >= local.index) {
               newVars[i] += local.desc.getSize();
            }
         }

         newNode.localVariables.forEach(node -> {
            if (!node.name.equals(local.name) && node.index >= local.index) {
               node.index = node.index + local.desc.getSize();
            }
         });
      }

      Int2IntMap old2New = new Int2IntArrayMap();

      for (int i = 0; i < this.vars.length; i++) {
         old2New.put(this.vars[i], newVars[i]);
      }

      newNode.instructions.forEach(insn -> {
         SingleValueHandle<Integer> handle = AdapterUtil.handleLocalVarInsnValue(insn);
         if (handle != null) {
            int idx = handle.get();
            handle.set(old2New.getOrDefault(idx, idx));
         }
      });
   }

   public static LVTSnapshot take(MethodNode node) {
      List<LVTSnapshot.LocalVar> locals = new ArrayList<>();
      IntSet vars = new IntArraySet();
      node.localVariables.forEach(local -> locals.add(new LVTSnapshot.LocalVar(local.name, Type.getType(local.desc), local.index)));
      node.instructions.forEach(insn -> {
         SingleValueHandle<Integer> handle = AdapterUtil.handleLocalVarInsnValue(insn);
         if (handle != null) {
            vars.add(handle.get());
         }
      });
      int[] varsArray = vars.toIntArray();
      Arrays.sort(varsArray);
      Collections.sort(locals);
      return new LVTSnapshot(locals, varsArray);
   }

   public static void with(MethodNode methodNode, Runnable action) {
      LVTSnapshot snapshot = take(methodNode);
      action.run();
      snapshot.applyDifference(methodNode);
   }

   public record LocalVar(String name, Type desc, int index) implements Comparable<LVTSnapshot.LocalVar> {
      public int compareTo(@NotNull LVTSnapshot.LocalVar o) {
         return Comparator.<Integer>naturalOrder().compare(this.index, o.index);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public int compareTo(@NotNull Object param1) {
         return this.compareTo((LVTSnapshot.LocalVar)var1);
      }
   }
}
