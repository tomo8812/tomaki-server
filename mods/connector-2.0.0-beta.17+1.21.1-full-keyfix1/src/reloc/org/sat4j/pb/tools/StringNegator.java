package reloc.org.sat4j.pb.tools;

public final class StringNegator implements INegator {
   public static final INegator INSTANCE = new StringNegator();

   private StringNegator() {
   }

   @Override
   public boolean isNegated(Object thing) {
      return thing instanceof String ? ((String)thing).startsWith("-") : false;
   }

   @Override
   public Object unNegate(Object thing) {
      return this.isNegated(thing) ? ((String)thing).substring(1) : thing;
   }
}
