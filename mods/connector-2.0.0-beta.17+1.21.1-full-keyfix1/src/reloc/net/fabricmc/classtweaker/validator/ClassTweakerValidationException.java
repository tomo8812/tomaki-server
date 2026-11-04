package reloc.net.fabricmc.classtweaker.validator;

public class ClassTweakerValidationException extends RuntimeException {
   public ClassTweakerValidationException(String message, Object... args) {
      super(String.format(message, args));
   }
}
