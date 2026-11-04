package reloc.org.sat4j.reader;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import reloc.org.sat4j.annotations.Feature;
import reloc.org.sat4j.specs.ContradictionException;
import reloc.org.sat4j.specs.IProblem;
import reloc.org.sat4j.specs.ISolver;

@Feature(value = "reader", parent = "expert")
public class InstanceReader extends Reader {
   private AAGReader aag;
   private AIGReader aig;
   private DimacsReader ezdimacs;
   private LecteurDimacs dimacs;
   private Reader reader = null;
   private final ISolver solver;

   public InstanceReader(ISolver solver, Reader reader) {
      this.solver = solver;
      this.reader = reader;
   }

   public InstanceReader(ISolver solver) {
      this.solver = solver;
   }

   private Reader getDefaultSATReader() {
      if (this.dimacs == null) {
         this.dimacs = new LecteurDimacs(this.solver);
      }

      return this.dimacs;
   }

   private Reader getEZSATReader() {
      if (this.ezdimacs == null) {
         this.ezdimacs = new DimacsReader(this.solver);
      }

      return this.ezdimacs;
   }

   private Reader getAIGReader() {
      if (this.aig == null) {
         this.aig = new AIGReader(this.solver);
      }

      return this.aig;
   }

   private Reader getAAGReader() {
      if (this.aag == null) {
         this.aag = new AAGReader(this.solver);
      }

      return this.aag;
   }

   protected Collection<String> getReservedPrefixes() {
      Collection<String> prefixes = new ArrayList<>();
      prefixes.add("EZCNF");
      return prefixes;
   }

   @Override
   public IProblem parseInstance(String filename) throws ParseFormatException, IOException, ContradictionException {
      String prefix = "";
      if (filename.startsWith("http://")) {
         filename = filename.substring(filename.lastIndexOf(47) + 1);
      }

      for (String reservedPrefix : this.getReservedPrefixes()) {
         if (filename.startsWith(reservedPrefix + ":")) {
            filename = filename.substring(reservedPrefix.length() + 1);
            prefix = reservedPrefix;
         }
      }

      String fname;
      if (!filename.endsWith(".gz") && !filename.endsWith(".bz2")) {
         fname = filename;
      } else {
         fname = filename.substring(0, filename.lastIndexOf(46));
      }

      if (this.reader == null) {
         this.reader = this.handleFileName(fname, prefix);
         this.reader.setUseMapping(this.isUsingMapping());
      }

      return this.reader.parseInstance(filename);
   }

   protected Reader handleFileName(String fname, String prefix) {
      if ("EZCNF".equals(prefix)) {
         return this.getEZSATReader();
      } else if (fname.endsWith(".aag")) {
         return this.getAAGReader();
      } else {
         return fname.endsWith(".aig") ? this.getAIGReader() : this.getDefaultSATReader();
      }
   }

   @Deprecated
   @Override
   public String decode(int[] model) {
      return this.reader.decode(model);
   }

   @Override
   public void decode(int[] model, PrintWriter out) {
      this.reader.decode(model, out);
   }

   @Override
   public IProblem parseInstance(InputStream in) throws ParseFormatException, ContradictionException, IOException {
      throw new UnsupportedOperationException("Use a domain specific Reader (LecteurDimacs, AIGReader, etc.) for stream input ");
   }

   @Override
   public boolean hasAMapping() {
      return this.reader.hasAMapping();
   }

   @Override
   public Map<Integer, String> getMapping() {
      return this.reader.getMapping();
   }
}
