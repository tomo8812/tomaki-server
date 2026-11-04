package org.valkyrienskies.core.impl.shadow;

public final class ev {
   public static <T> T a(@Cx T reference) {
      if (reference == null) {
         throw new NullPointerException();
      } else {
         return (T)reference;
      }
   }

   public static <T> T a(T reference, String errorMessage) {
      if (reference == null) {
         throw new NullPointerException(errorMessage);
      } else {
         return (T)reference;
      }
   }

   public static <T> T b(T reference) {
      if (reference == null) {
         throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
      } else {
         return (T)reference;
      }
   }

   private static <T> T c(T reference) {
      if (reference == null) {
         throw new NullPointerException("Cannot return null from a non-@Nullable component method");
      } else {
         return (T)reference;
      }
   }

   private static <T> T a(T reference, String errorMessageTemplate, Object errorMessageArg) {
      if (reference == null) {
         if (!errorMessageTemplate.contains("%s")) {
            throw new IllegalArgumentException("errorMessageTemplate has no format specifiers");
         }

         if (errorMessageTemplate.indexOf("%s") != errorMessageTemplate.lastIndexOf("%s")) {
            throw new IllegalArgumentException("errorMessageTemplate has more than one format specifier");
         }

         reference = String.valueOf(errorMessageArg instanceof Class ? ((Class)errorMessageArg).getCanonicalName() : errorMessageArg);
         throw new NullPointerException(errorMessageTemplate.replace("%s", (CharSequence)reference));
      } else {
         return (T)reference;
      }
   }

   public static <T> void a(T requirement, Class<T> clazz) {
      if (requirement == null) {
         throw new IllegalStateException(clazz.getCanonicalName() + " must be set");
      }
   }

   private ev() {
   }
}
