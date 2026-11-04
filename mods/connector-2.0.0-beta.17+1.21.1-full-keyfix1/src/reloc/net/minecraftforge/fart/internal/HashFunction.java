package reloc.net.minecraftforge.fart.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

enum HashFunction {
   MD5("md5", 32),
   SHA1("SHA-1", 40),
   SHA256("SHA-256", 64),
   SHA512("SHA-512", 128);

   private final String algo;
   private final String pad;

   HashFunction(String algo, int length) {
      this.algo = algo;
      this.pad = String.format(Locale.ROOT, "%0" + length + "d", 0);
   }

   public String getExtension() {
      return this.name().toLowerCase(Locale.ROOT);
   }

   public MessageDigest get() {
      try {
         return MessageDigest.getInstance(this.algo);
      } catch (NoSuchAlgorithmException e) {
         throw new RuntimeException(e);
      }
   }

   public String hash(File file) throws IOException {
      return this.hash(Files.readAllBytes(file.toPath()));
   }

   public String hash(Iterable<File> files) throws IOException {
      MessageDigest hash = this.get();

      for (File file : files) {
         if (file.exists()) {
            hash.update(Files.readAllBytes(file.toPath()));
         }
      }

      return this.pad(new BigInteger(1, hash.digest()).toString(16));
   }

   public String hash(@Nullable String data) {
      return this.hash(data == null ? new byte[0] : data.getBytes(StandardCharsets.UTF_8));
   }

   public String hash(InputStream stream) throws IOException {
      return this.hash(Util.toByteArray(stream));
   }

   public String hash(byte[] data) {
      return this.pad(new BigInteger(1, this.get().digest(data)).toString(16));
   }

   public String pad(String hash) {
      return (this.pad + hash).substring(hash.length());
   }
}
