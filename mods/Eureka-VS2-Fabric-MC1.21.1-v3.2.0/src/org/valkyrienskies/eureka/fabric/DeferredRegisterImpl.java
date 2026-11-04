package org.valkyrienskies.eureka.fabric;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.jvm.functions.Function0;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_7923;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.eureka.registry.DeferredRegister;
import org.valkyrienskies.eureka.registry.RegistrySupplier;

public class DeferredRegisterImpl<T> implements DeferredRegister<T> {
   private final String modId;
   private final class_2378<T> registry;
   private final List<RegistrySupplier<T>> everMade = new ArrayList<>();

   public DeferredRegisterImpl(String modId, class_5321<class_2378<T>> registry) {
      this.modId = modId;
      this.registry = (class_2378<T>)class_7923.field_41167.method_10223(registry.method_29177());
   }

   @NotNull
   @Override
   public <I extends T> RegistrySupplier<I> register(@NotNull String name, @NotNull Function0<? extends I> builder) {
      final I result = (I)class_2378.method_10230(this.registry, class_2960.method_60655(this.modId, name), builder.invoke());
      RegistrySupplier<I> r = new RegistrySupplier<I>() {
         @NotNull
         @Override
         public String getName() {
            return name;
         }

         @Override
         public I get() {
            return result;
         }
      };
      this.everMade.add(r);
      return r;
   }

   @Override
   public void applyAll() {
   }

   @NotNull
   @Override
   public Iterator<RegistrySupplier<T>> iterator() {
      return this.everMade.iterator();
   }
}
