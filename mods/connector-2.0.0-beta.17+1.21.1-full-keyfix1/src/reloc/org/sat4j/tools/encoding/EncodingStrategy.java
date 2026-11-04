package reloc.org.sat4j.tools.encoding;

public enum EncodingStrategy {
   BINARY("Binary"),
   BINOMIAL("Binomial"),
   COMMANDER("Commander"),
   LADDER("Ladder"),
   PRODUCT("Product"),
   SEQUENTIAL("Sequential"),
   NATIVE("Native");

   private String name;

   EncodingStrategy(String name) {
      this.name = name;
   }

   @Override
   public String toString() {
      return this.name;
   }
}
