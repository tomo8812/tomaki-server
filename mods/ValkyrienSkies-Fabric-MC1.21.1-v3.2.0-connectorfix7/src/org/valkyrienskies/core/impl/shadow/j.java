package org.valkyrienskies.core.impl.shadow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.YearMonth;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

public class j implements l {
   private static String b;
   private final SortedSet<YearMonth> c = new TreeSet<>();
   private final YearMonth d;

   public j() {
      try {
         InputStream var1 = j.class.getClassLoader().getResourceAsStream("leap_second_dates.csv");

         try {
            BufferedReader var2 = new BufferedReader(
               new InputStreamReader(Objects.requireNonNull(var1, "leap_second_dates.csv was not found on the classpath"), StandardCharsets.UTF_8)
            );

            String var3;
            try {
               while ((var3 = var2.readLine()) != null) {
                  if (!var3.isEmpty()) {
                     this.c.add(YearMonth.parse(var3));
                  }
               }
            } catch (Throwable var6) {
               try {
                  var2.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }

               throw var6;
            }

            var2.close();
         } catch (Throwable var7) {
            if (var1 != null) {
               try {
                  var1.close();
               } catch (Throwable var4) {
                  var7.addSuppressed(var4);
               }
            }

            throw var7;
         }

         if (var1 != null) {
            var1.close();
         }
      } catch (IOException var8) {
         throw new UncheckedIOException(var8);
      }

      this.d = this.c.last();
   }

   @Override
   public final boolean a(YearMonth var1) {
      return this.c.contains(var1);
   }

   @Override
   public final YearMonth a() {
      return this.d;
   }
}
