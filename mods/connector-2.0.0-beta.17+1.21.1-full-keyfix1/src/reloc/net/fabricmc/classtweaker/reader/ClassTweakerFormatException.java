package reloc.net.fabricmc.classtweaker.reader;

public class ClassTweakerFormatException extends RuntimeException {
   private final int lineNumber;

   public ClassTweakerFormatException(int lineNumber, String message) {
      super(message);
      this.lineNumber = lineNumber;
   }

   public int getLineNumber() {
      return this.lineNumber;
   }

   @Override
   public String toString() {
      return this.getClass().getName() + ": line " + this.lineNumber + ": " + this.getMessage();
   }
}
