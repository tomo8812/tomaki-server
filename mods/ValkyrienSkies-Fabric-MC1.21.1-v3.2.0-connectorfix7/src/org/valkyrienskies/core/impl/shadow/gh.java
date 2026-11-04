package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public final class gh<E> extends fW<E> implements Serializable, fN<E> {
   private static final long a = -7740146511091606676L;

   public gh() {
      super(new TreeMap<>());
   }

   private gh(Comparator<? super E> var1) {
      super(new TreeMap<>(var1));
   }

   private gh(Collection<? extends E> var1) {
      this();
      this.addAll(var1);
   }

   @Override
   public final boolean add(E object) {
      if (this.b() != null || object instanceof Comparable) {
         return super.add(object);
      } else if (object == null) {
         throw new NullPointerException();
      } else {
         throw new IllegalArgumentException(
            "Objects of type " + object.getClass() + " cannot be added to a naturally ordered TreeBag as it does not implement Comparable"
         );
      }
   }

   @Override
   public final E c() {
      return ((SortedMap)super.e()).firstKey();
   }

   @Override
   public final E d() {
      return ((SortedMap)super.e()).lastKey();
   }

   @Override
   public final Comparator<? super E> b() {
      return ((SortedMap)super.e()).comparator();
   }

   private SortedMap<E, fW.b> f() {
      return (SortedMap<E, fW.b>)super.e();
   }

   private void b(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      var1.writeObject(this.b());
      super.a(var1);
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      Comparator var2 = (Comparator)var1.readObject();
      super.a(new TreeMap<>(var2), var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   protected final Map e() {
      return super.e();
   }
}
