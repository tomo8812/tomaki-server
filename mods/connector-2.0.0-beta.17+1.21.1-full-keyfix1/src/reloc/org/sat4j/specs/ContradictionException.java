package reloc.org.sat4j.specs;

public class ContradictionException extends Exception {
   private static final long serialVersionUID = 1L;

   public ContradictionException() {
   }

   public ContradictionException(String message) {
      super(message);
   }

   public ContradictionException(Throwable cause) {
      super(cause);
   }

   public ContradictionException(String message, Throwable cause) {
      super(message, cause);
   }
}
