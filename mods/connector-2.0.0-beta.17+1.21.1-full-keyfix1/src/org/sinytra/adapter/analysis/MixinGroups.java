package org.sinytra.adapter.analysis;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.struct.InjectorGroupInfo;
import org.spongepowered.asm.util.Annotations;

public class MixinGroups {
   private final Map<String, MixinGroups.GroupInfo> groups;

   private MixinGroups(Map<String, MixinGroups.GroupInfo> groups) {
      this.groups = Map.copyOf(groups);
   }

   @Nullable
   public MixinGroups.GroupInfo getGroupInfo(MethodNode methodNode) {
      AnnotationNode node = Annotations.getInvisible(methodNode, Group.class);
      if (node == null) {
         return null;
      }

      String name = (String)Annotations.getValue(node, "name");
      return name != null && !name.isEmpty() ? this.groups.get(name) : null;
   }

   public static MixinGroups create(List<MethodNode> methods) {
      org.spongepowered.asm.mixin.injection.struct.InjectorGroupInfo.Map infos = new org.spongepowered.asm.mixin.injection.struct.InjectorGroupInfo.Map();
      Multimap<String, MethodNode> members = HashMultimap.create();

      for (MethodNode method : methods) {
         InjectorGroupInfo info = infos.parseGroup(method, "default");
         if (!info.isDefault() && !info.getName().equals("default")) {
            members.put(info.getName(), method);
         }
      }

      Map<String, MixinGroups.GroupInfo> groups = new HashMap<>();
      members.asMap().forEach((name, mems) -> {
         InjectorGroupInfo infox = infos.forName(name);
         groups.put(name, new MixinGroups.GroupInfo((Collection<MethodNode>)mems, infox.getMinRequired()));
      });
      return new MixinGroups(groups);
   }

   public record GroupInfo(Collection<MethodNode> members, int minRequired) {
   }
}
