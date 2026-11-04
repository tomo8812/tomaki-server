package reloc.org.sat4j.reader;

public class ParseFormatException extends Exception {
   public static final String PARSING_ERROR = "Parsing Error";
   private static final long serialVersionUID = 1L;

   public ParseFormatException() {
      super("Parsing Error");
   }

   public ParseFormatException(String message) {
      super("Parsing Error" + message);
   }

   public ParseFormatException(String message, Throwable cause) {
      super("Parsing Error" + message, cause);
   }

   public ParseFormatException(Throwable cause) {
      super(cause);
   }
}
