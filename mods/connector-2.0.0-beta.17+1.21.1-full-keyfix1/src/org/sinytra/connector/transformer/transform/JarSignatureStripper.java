package org.sinytra.connector.transformer.transform;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;
import reloc.net.minecraftforge.fart.api.Transformer;

public class JarSignatureStripper implements Transformer {
   @Override
   public Transformer.ResourceEntry process(Transformer.ResourceEntry entry) {
      String name = entry.getName();
      return !name.startsWith("META-INF/") || !name.endsWith(".RSA") && !name.endsWith(".SF") ? entry : null;
   }

   @Override
   public Transformer.ManifestEntry process(Transformer.ManifestEntry entry) {
      Manifest manifest = new Manifest();

      try (InputStream is = new ByteArrayInputStream(entry.getData())) {
         manifest.read(is);
         processManifest(manifest);
         ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
         manifest.write(byteStream);
         return Transformer.ManifestEntry.create(entry.getTime(), byteStream.toByteArray());
      } catch (IOException e) {
         throw new UncheckedIOException("Error writing manifest", e);
      }
   }

   private static void processManifest(Manifest manifest) {
      manifest.getEntries().clear();
      manifest.getMainAttributes().remove(new Name("Automatic-Module-Name"));
   }
}
