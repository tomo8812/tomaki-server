package org.valkyrienskies.core.impl.shadow;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class dB implements dx {
   public static final Set<String> a = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("http", "https", "ftp", "file", "jar")));

   @Override
   public final URI a(String var1) {
      try {
         return URI.create(var1);
      } catch (IllegalArgumentException var2) {
         throw new IllegalArgumentException("Unable to create URI.", var2);
      }
   }

   @Override
   public final URI a(URI var1, String var2) {
      try {
         return new URL(var1.toURL(), var2).toURI();
      } catch (MalformedURLException var3) {
         throw new IllegalArgumentException("Unable to create URI.", var3);
      } catch (URISyntaxException var4) {
         throw new IllegalArgumentException("Unable to create URI.", var4);
      }
   }
}
