package reloc.org.sat4j.annotations;

public @interface Feature {
   String value();

   String parent() default "user";
}
