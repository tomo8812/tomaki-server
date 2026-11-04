package org.sinytra.adapter.types;

import java.util.function.BiConsumer;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

public record SimpleTypeAdapter(Type from, Type to, SimpleTypeAdapter.TypePatch adapter) implements TypeAdapter {
   @Override
   public void apply(InsnList list, AbstractInsnNode target) {
      this.adapter.apply(list, target);
   }

   @Override
   public TypeAdapter andThen(BiConsumer<InsnList, AbstractInsnNode> consumer) {
      return new SimpleTypeAdapter(this.from, this.to, (l, t) -> {
         consumer.accept(l, t);
         this.adapter.apply(l, t);
      });
   }

   public interface TypePatch {
      void apply(InsnList var1, AbstractInsnNode var2);
   }
}
