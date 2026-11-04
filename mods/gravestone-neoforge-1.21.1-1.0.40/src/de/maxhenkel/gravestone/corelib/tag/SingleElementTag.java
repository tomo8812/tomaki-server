package de.maxhenkel.gravestone.corelib.tag;

import java.util.Collections;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class SingleElementTag<T> implements Tag<T> {
   private final ResourceLocation name;
   private final T element;
   private final List<T> list;

   public SingleElementTag(ResourceLocation name, T element) {
      this.name = name;
      this.element = element;
      this.list = Collections.singletonList(element);
   }

   public T getElement() {
      return this.element;
   }

   @Override
   public ResourceLocation getName() {
      return this.name;
   }

   @Override
   public boolean contains(T t) {
      return this.element == t;
   }

   @Override
   public List<T> getAll() {
      return this.list;
   }
}
