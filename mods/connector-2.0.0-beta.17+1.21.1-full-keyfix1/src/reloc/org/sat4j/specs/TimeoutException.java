package reloc.org.sat4j.specs;

public class TimeoutException extends Exception {
   private static final long serialVersionUID = 1L;

   public TimeoutException() {
   }

   public TimeoutException(String message) {
      super(message);
   }

   public TimeoutException(String message, Throwable cause) {
      super(message, cause);
   }

   public TimeoutException(Throwable cause) {
      super(cause);
   }
}
