package de.maxhenkel.gravestone.corelib.tag;

import java.util.List;
import net.minecraft.resources.ResourceLocation;

public interface Tag<T> {
   ResourceLocation getName();

   boolean contains(T var1);

   List<T> getAll();
}
