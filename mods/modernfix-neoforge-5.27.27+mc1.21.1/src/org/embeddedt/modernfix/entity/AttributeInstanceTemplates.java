package org.embeddedt.modernfix.entity;

import it.unimi.dsi.fastutil.Hash.Strategy;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

public class AttributeInstanceTemplates {
   private static final ObjectOpenCustomHashSet<AttributeInstance> INTERNER = new ObjectOpenCustomHashSet(
      new Strategy<AttributeInstance>() {
         public int hashCode(AttributeInstance o) {
            if (o == null) {
               return 0;
            }

            int h = System.identityHashCode(o.getAttribute());
            h = 31 * h + Double.hashCode(o.getBaseValue());
            return 31 * h + o.getModifiers().hashCode();
         }

         public boolean equals(AttributeInstance a, AttributeInstance b) {
            if (a == b) {
               return true;
            } else {
               return a != null && b != null
                  ? a.getAttribute() == b.getAttribute() && a.getBaseValue() == b.getBaseValue() && a.getModifiers().equals(b.getModifiers())
                  : false;
            }
         }

         // $VF: synthetic method
         // $VF: bridge method
         public boolean equals(Object param1, Object nullx) {
            return this.equals((AttributeInstance)var1, (AttributeInstance)nullx);
         }

         // $VF: synthetic method
         // $VF: bridge method
         public int hashCode(Object param1) {
            return this.hashCode((AttributeInstance)var1);
         }
      }
   );

   public static AttributeInstance intern(AttributeInstance a) {
      if (a != null && a.getClass() == AttributeInstance.class) {
         synchronized (INTERNER) {
            return (AttributeInstance)INTERNER.addOrGet(a);
         }
      } else {
         return a;
      }
   }
}
