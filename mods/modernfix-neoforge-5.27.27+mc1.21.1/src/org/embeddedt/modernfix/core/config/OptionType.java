package org.embeddedt.modernfix.core.config;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public abstract class OptionType<T> {
   public static final OptionType<Boolean> BOOLEAN = new OptionType<Boolean>() {
      public Boolean parse(String s) {
         if (s.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
         } else if (s.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
         } else {
            throw new IllegalArgumentException(s);
         }
      }

      public String serialize(Boolean value) {
         return value.toString();
      }

      @Override
      public Class<Boolean> type() {
         return Boolean.class;
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public String serialize(Object param1) {
         return this.serialize((Boolean)var1);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Object parse(String param1) throws IllegalArgumentException {
         return this.parse(var1);
      }
   };
   private static final ConcurrentHashMap<Class<? extends Enum<?>>, OptionType<? extends Enum<?>>> ENUM_TYPES = new ConcurrentHashMap<>();

   private OptionType() {
   }

   public abstract Class<T> type();

   public abstract T parse(String var1) throws IllegalArgumentException;

   public abstract String serialize(T var1);

   private static <T extends Enum<T>> OptionType<T> createEnumType(final Class<T> enumClass) {
      return new OptionType<T>() {
         public T parse(String s) throws IllegalArgumentException {
            return Enum.valueOf(enumClass, s.toUpperCase(Locale.ROOT));
         }

         public String serialize(T value) {
            return value.name();
         }

         @Override
         public Class<T> type() {
            return enumClass;
         }

         // $VF: synthetic method
         // $VF: bridge method
         @Override
         public String serialize(Object param1) {
            return this.serialize((T)var1);
         }

         // $VF: synthetic method
         // $VF: bridge method
         @Override
         public Object parse(String param1) throws IllegalArgumentException {
            return this.parse(var1);
         }
      };
   }

   public static <T extends Enum<T>> OptionType<T> enumType(Class<T> enumClass) {
      return (OptionType<T>)ENUM_TYPES.computeIfAbsent(enumClass, k -> (OptionType<? extends Enum<?>>)createEnumType((Class<?>)k));
   }
}
