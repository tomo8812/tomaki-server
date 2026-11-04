package reloc.net.minecraftforge.srgutils;

public interface IMappingBuilder {
   static IMappingBuilder create(String... names) {
      return new NamedMappingFile(names != null && names.length != 0 ? names : new String[]{"left", "right"});
   }

   IMappingBuilder.IPackage addPackage(String... var1);

   IMappingBuilder.IClass addClass(String... var1);

   INamedMappingFile build();

   interface IClass {
      IMappingBuilder.IField field(String... var1);

      IMappingBuilder.IMethod method(String var1, String... var2);

      IMappingBuilder.IClass meta(String var1, String var2);

      IMappingBuilder build();
   }

   interface IField {
      IMappingBuilder.IField descriptor(String var1);

      IMappingBuilder.IField meta(String var1, String var2);

      IMappingBuilder.IClass build();
   }

   interface IMethod {
      IMappingBuilder.IParameter parameter(int var1, String... var2);

      IMappingBuilder.IMethod meta(String var1, String var2);

      IMappingBuilder.IClass build();
   }

   interface IPackage {
      IMappingBuilder.IPackage meta(String var1, String var2);

      IMappingBuilder build();
   }

   interface IParameter {
      IMappingBuilder.IParameter meta(String var1, String var2);

      IMappingBuilder.IMethod build();
   }
}
