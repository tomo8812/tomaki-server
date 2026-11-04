package org.sinytra.connector.service.hacks;

import cpw.mods.modlauncher.serviceapi.ILaunchPluginService.Phase;
import java.util.List;
import net.neoforged.fml.common.asm.enumextension.RuntimeEnumExtender;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class LenientRuntimeEnumExtender extends RuntimeEnumExtender {
   public int processClassWithFlags(Phase phase, ClassNode classNode, Type classType, String reason) {
      if ((classNode.access & 16384) == 0) {
         return 0;
      }

      int flags = 4106;
      Type array = Type.getType("[" + classType.getDescriptor());
      List<FieldNode> values = classNode.fields.stream().filter(f -> f.desc.contentEquals(array.getDescriptor()) && (f.access & flags) == flags).toList();
      if (values.size() == 1) {
         FieldNode node = values.getFirst();
         if ((node.access & 16) == 0) {
            node.access |= 16;
            int result = super.processClassWithFlags(phase, classNode, classType, reason);
            node.access &= -17;
            return result;
         }
      }

      return super.processClassWithFlags(phase, classNode, classType, reason);
   }
}
