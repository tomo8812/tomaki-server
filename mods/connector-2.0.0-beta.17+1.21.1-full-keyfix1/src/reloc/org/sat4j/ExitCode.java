package reloc.org.sat4j;

public enum ExitCode {
   OPTIMUM_FOUND(30, "OPTIMUM FOUND"),
   UPPER_BOUND(30, "UPPER BOUND"),
   SATISFIABLE(10, "SATISFIABLE"),
   UNKNOWN(0, "UNKNOWN"),
   UNSATISFIABLE(20, "UNSATISFIABLE");

   private final int value;
   private final String str;

   ExitCode(int i, String str) {
      this.value = i;
      this.str = str;
   }

   public int value() {
      return this.value;
   }

   @Override
   public String toString() {
      return this.str;
   }
}
