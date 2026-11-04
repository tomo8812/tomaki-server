package reloc.org.sat4j;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class Messages {
   private static final String BUNDLE_NAME = "reloc.org.sat4j.messages";
   private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("reloc.org.sat4j.messages");

   private Messages() {
   }

   public static String getString(String key) {
      try {
         return RESOURCE_BUNDLE.getString(key);
      } catch (MissingResourceException e) {
         return '!' + key + '!';
      }
   }
}
