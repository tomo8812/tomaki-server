package org.valkyrienskies.core.impl.shadow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class hB {
   public static <T> fh<T> a(T var0) {
      if (var0 == null) {
         return gU.a(null);
      }

      try {
         Method var1 = var0.getClass().getMethod("clone", null);
         return new hB.a<>(var0, var1, (byte)0);
      } catch (NoSuchMethodException var3) {
         try {
            var0.getClass().getConstructor(var0.getClass());
            return new hk<>((Class<T>)var0.getClass(), new Class[]{var0.getClass()}, new Object[]{var0});
         } catch (NoSuchMethodException var2) {
            if (var0 instanceof Serializable) {
               return new hB.b((Serializable)var0, (byte)0);
            } else {
               throw new IllegalArgumentException("The prototype must be cloneable via a public clone method");
            }
         }
      }
   }

   private hB() {
   }

   static class a<T> implements fh<T> {
      private final T a;
      private transient Method b;

      private a(T var1, Method var2) {
         this.a = (T)var1;
         this.b = var2;
      }

      private void b() {
         try {
            this.b = this.a.getClass().getMethod("clone", null);
         } catch (NoSuchMethodException var1) {
            throw new IllegalArgumentException("PrototypeCloneFactory: The clone method must exist and be public ");
         }
      }

      @Override
      public final T a() {
         if (this.b == null) {
            hB.a var1 = this;

            try {
               var1.b = var1.a.getClass().getMethod("clone", null);
            } catch (NoSuchMethodException var4) {
               throw new IllegalArgumentException("PrototypeCloneFactory: The clone method must exist and be public ");
            }
         }

         try {
            return (T)this.b.invoke(this.a, null);
         } catch (IllegalAccessException var2) {
            throw new fk("PrototypeCloneFactory: Clone method must be public", var2);
         } catch (InvocationTargetException var3) {
            throw new fk("PrototypeCloneFactory: Clone method threw an exception", var3);
         }
      }

      // $VF: synthetic method
      a(Object var1, Method var2, byte var3) {
         this((T)var1, var2);
      }
   }

   static class b<T extends Serializable> implements fh<T> {
      private final T a;

      private b(T var1) {
         this.a = (T)var1;
      }

      // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      private T b() {
         ByteArrayOutputStream var1 = new ByteArrayOutputStream(512);
         ByteArrayInputStream var2 = null;
         boolean var11 = false /* VF: Semaphore variable */;

         Serializable var3;
         try {
            var11 = true;
            new ObjectOutputStream(var1).writeObject(this.a);
            var2 = new ByteArrayInputStream(var1.toByteArray());
            var3 = (Serializable)new ObjectInputStream(var2).readObject();
            var11 = false;
         } catch (ClassNotFoundException var16) {
            throw new fk(var16);
         } catch (IOException var17) {
            throw new fk(var17);
         } finally {
            if (var11) {
               try {
                  if (var2 != null) {
                     var2.close();
                  }
               } catch (IOException var13) {
               }

               try {
                  var1.close();
               } catch (IOException var12) {
               }
            }
         }

         try {
            var2.close();
         } catch (IOException var15) {
         }

         try {
            var1.close();
         } catch (IOException var14) {
         }

         return (T)var3;
      }

      // $VF: synthetic method
      @Override
      public final Object a() {
         return this.b();
      }

      // $VF: synthetic method
      b(Serializable var1, byte var2) {
         this((T)var1);
      }
   }
}
