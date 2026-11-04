package reloc.org.sat4j.reader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IProblem;

@Feature(value = "reader", parent = "expert")
public abstract class Reader {
   private boolean verbose = false;
   private boolean useMapping = false;

   public IProblem parseInstance(String filename) throws ParseFormatException, IOException, ContradictionException {
      InputStream in = null;

      try {
         in = getInputStreamFromFilename(filename);
         return this.parseInstance(in);
      } catch (IllegalStateException e) {
         if (e.getCause() instanceof ContradictionException) {
            throw (ContradictionException)e.getCause();
         } else {
            throw e;
         }
      } finally {
         if (in != null) {
            in.close();
         }
      }
   }

   public static InputStream getInputStreamFromFilename(String filename) throws IOException, MalformedURLException {
      InputStream in;
      if (filename.startsWith("http://")) {
         in = new URL(filename).openStream();
      } else {
         in = new FileInputStream(filename);
      }

      if (filename.endsWith(".gz")) {
         in = new GZIPInputStream(in);
      } else if (filename.endsWith(".bz2")) {
         in.close();
         in = Runtime.getRuntime().exec("bunzip2 -c " + filename).getInputStream();
      } else if (filename.endsWith(".lzma")) {
         in.close();
         in = Runtime.getRuntime().exec("lzma -d -c " + filename).getInputStream();
      }

      return in;
   }

   public abstract IProblem parseInstance(InputStream var1) throws ParseFormatException, ContradictionException, IOException;

   @Deprecated
   public IProblem parseInstance(java.io.Reader in) throws ParseFormatException, ContradictionException, IOException {
      throw new UnsupportedOperationException("Use #parseInstance(InputStream) instead");
   }

   @Deprecated
   public abstract String decode(int[] var1);

   public abstract void decode(int[] var1, PrintWriter var2);

   public boolean isVerbose() {
      return this.verbose;
   }

   public void setVerbosity(boolean b) {
      this.verbose = b;
   }

   public boolean hasAMapping() {
      return false;
   }

   public Map<Integer, String> getMapping() {
      return null;
   }

   public boolean isUsingMapping() {
      return this.useMapping;
   }

   public void setUseMapping(boolean b) {
      this.useMapping = b;
   }
}
