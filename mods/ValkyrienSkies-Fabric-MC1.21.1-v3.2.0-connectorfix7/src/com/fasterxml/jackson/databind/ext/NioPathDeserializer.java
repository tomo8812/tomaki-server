package com.fasterxml.jackson.databind.ext;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.util.ServiceLoader;

public class NioPathDeserializer extends StdScalarDeserializer<Path> {
   private static final long serialVersionUID = 1L;
   private static final boolean areWindowsFilePathsSupported;

   public NioPathDeserializer() {
      super(Path.class);
   }

   public Path deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (!p.hasToken(JsonToken.VALUE_STRING)) {
         return (Path)ctxt.handleUnexpectedToken(Path.class, p);
      }

      String value = p.getText();
      if (value.indexOf(58) < 0) {
         return Paths.get(value);
      }

      if (areWindowsFilePathsSupported && value.length() >= 2 && Character.isLetter(value.charAt(0)) && value.charAt(1) == ':') {
         return Paths.get(value);
      }

      URI uri;
      try {
         uri = new URI(value);
      } catch (URISyntaxException e) {
         return (Path)ctxt.handleInstantiationProblem(this.handledType(), value, e);
      }

      try {
         return Paths.get(uri);
      } catch (FileSystemNotFoundException var10) {
         FileSystemNotFoundException cause = var10;

         try {
            String scheme = uri.getScheme();

            for (FileSystemProvider provider : ServiceLoader.load(FileSystemProvider.class)) {
               if (provider.getScheme().equalsIgnoreCase(scheme)) {
                  return provider.getPath(uri);
               }
            }

            return (Path)ctxt.handleInstantiationProblem(this.handledType(), value, cause);
         } catch (Throwable e) {
            e.addSuppressed(var10);
            return (Path)ctxt.handleInstantiationProblem(this.handledType(), value, e);
         }
      } catch (Throwable e) {
         return (Path)ctxt.handleInstantiationProblem(this.handledType(), value, e);
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object deserialize(JsonParser var1, DeserializationContext var2) throws IOException, JacksonException {
      return this.deserialize(var1, var2);
   }

   static {
      boolean isWindowsRootFound = false;

      for (File file : File.listRoots()) {
         String path = file.getPath();
         if (path.length() >= 2 && Character.isLetter(path.charAt(0)) && path.charAt(1) == ':') {
            isWindowsRootFound = true;
            break;
         }
      }

      areWindowsFilePathsSupported = isWindowsRootFound;
   }
}
