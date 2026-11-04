package org.sinytra.adapter.types;

import java.util.function.BiConsumer;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

public interface TypeAdapter {
   Type from();

   Type to();

   void apply(InsnList var1, AbstractInsnNode var2);

   TypeAdapter andThen(BiConsumer<InsnList, AbstractInsnNode> var1);
}
