package reloc.net.minecraftforge.fart.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import reloc.net.minecraftforge.fart.api.SignatureStripperConfig;
import reloc.net.minecraftforge.fart.api.Transformer;

public class SignatureStripperTransformer implements Transformer {
   private final Consumer<String> log;
   private final SignatureStripperConfig config;

   public SignatureStripperTransformer(Consumer<String> log, SignatureStripperConfig config) {
      this.log = log;
      this.config = config;
   }

   @Override
   public Transformer.ManifestEntry process(Transformer.ManifestEntry entry) {
      try {
         Manifest manifest = new Manifest(new ByteArrayInputStream(entry.getData()));
         boolean found = false;
         Iterator<Map.Entry<String, Attributes>> it = manifest.getEntries().entrySet().iterator();

         while (it.hasNext()) {
            Map.Entry<String, Attributes> section = it.next();
            Iterator<Map.Entry<Object, Object>> attrIter = section.getValue().entrySet().iterator();

            while (attrIter.hasNext()) {
               Map.Entry<Object, Object> attribute = attrIter.next();
               String key = attribute.getKey().toString().toLowerCase(Locale.ROOT);
               if (key.endsWith("-digest")) {
                  if (this.config == SignatureStripperConfig.ALL) {
                     attrIter.remove();
                  }

                  found = true;
               }
            }

            if (section.getValue().isEmpty()) {
               it.remove();
            }
         }

         if (found) {
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
               manifest.write(os);
               return Transformer.ManifestEntry.create(entry.getTime(), os.toByteArray());
            }
         }
      } catch (IOException ex) {
         this.log.accept("Failed to remove signature entries from manifest: " + ex);
      }

      return entry;
   }

   @Override
   public Transformer.ResourceEntry process(Transformer.ResourceEntry entry) {
      return !entry.getName().startsWith("META-INF/")
            || !entry.getName().endsWith(".RSA") && !entry.getName().endsWith(".SF") && !entry.getName().endsWith(".DSA") && !entry.getName().endsWith(".EC")
         ? entry
         : null;
   }
}
