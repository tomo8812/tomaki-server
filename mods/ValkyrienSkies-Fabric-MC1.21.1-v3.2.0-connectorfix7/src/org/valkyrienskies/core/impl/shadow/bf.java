package org.valkyrienskies.core.impl.shadow;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

public final class bf implements aD {
   private static List<?> a(ay var0) {
      if (var0.a.isStatic() && var0.a.isFinal() && !((Field)var0.a.getRawMember()).isEnumConstant() && !var0.c) {
         Field var4 = (Field)var0.a.getRawMember();

         try {
            return Collections.singletonList(var4.get(null));
         } catch (IllegalAccessException var3) {
            try {
               var4.setAccessible(true);
               return Collections.singletonList(var4.get(null));
            } catch (SecurityException | IllegalAccessException var2) {
            }
         }
      }

      return null;
   }

   private static Boolean b(ay var0) {
      List var1;
      return (var1 = a(var0)) == null ? null : var1.get(0) == null;
   }

   @Override
   public final void a(aL var1) {
      var1.c.g(bf::a).a(bf::b);
   }
}
