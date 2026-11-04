package org.sinytra.adapter.util;

import java.util.List;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

public class OpcodeUtil {
   public static final List<Integer> INSN_TYPE_OFFSETS = List.of(7, 6, 8, 10);
   public static final List<Integer> INSN_TYPE_OFFSETS_EXTENDED = List.of(7, 6, 8, 10, 0);

   public static boolean isStoreOpcode(int opcode) {
      return opcode >= 54 && opcode <= 58;
   }

   public static boolean isLoadOpcode(int opcode) {
      return opcode >= 21 && opcode <= 25;
   }

   public static boolean isReturnOpcode(int opcode) {
      return opcode >= 172 && opcode <= 177;
   }

   public static int getLoadOpcode(int sort) {
      return 21 + INSN_TYPE_OFFSETS.indexOf(sort) + 1;
   }

   public static int getStoreOpcode(int sort) {
      return 54 + INSN_TYPE_OFFSETS.indexOf(sort) + 1;
   }

   public static int getReturnOpcode(MethodNode methodNode) {
      return getReturnOpcode(Type.getReturnType(methodNode.desc).getSort());
   }

   public static int getReturnOpcode(int sort) {
      return 172 + INSN_TYPE_OFFSETS_EXTENDED.indexOf(sort) + 1;
   }

   public static int getAccessVisibility(int access) {
      return access & 7;
   }

   public static int setAccessVisibility(int access, int visibility) {
      return access & -8 | visibility;
   }
}
