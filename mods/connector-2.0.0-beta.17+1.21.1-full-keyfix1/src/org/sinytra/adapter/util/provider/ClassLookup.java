package org.sinytra.adapter.util.provider;

import java.util.Optional;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public interface ClassLookup {
   Optional<ClassNode> getClass(String var1);

   default Optional<MethodNode> findMethod(String owner, String name, String desc) {
      return this.getClass(owner).stream().flatMap(cls -> cls.methods.stream()).filter(mtd -> mtd.name.equals(name) && mtd.desc.equals(desc)).findFirst();
   }

   default Optional<FieldNode> findField(String owner, String name) {
      return this.getClass(owner).stream().flatMap(cls -> cls.fields.stream()).filter(fd -> fd.name.equals(name)).findFirst();
   }
}
