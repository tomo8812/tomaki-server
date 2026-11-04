package net.caffeinemc.mods.lithium.common.entity.pushable;

import java.util.function.Predicate;

public abstract class EntityPushablePredicate<S> implements Predicate<S> {
   public static <T> Predicate<T> and(final Predicate<? super T> first, final Predicate<? super T> second) {
      return new EntityPushablePredicate<T>() {
         @Override
         public boolean test(T t) {
            return first.test(t) && second.test(t);
         }
      };
   }
}
