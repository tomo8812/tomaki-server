package reloc.net.minecraftforge.srgutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

public interface IMappingFile {
   static IMappingFile load(File path) throws IOException {
      try (InputStream in = new FileInputStream(path)) {
         return load(in);
      }
   }

   static IMappingFile load(InputStream in) throws IOException {
      return InternalUtils.load(in);
   }

   Collection<? extends IMappingFile.IPackage> getPackages();

   IMappingFile.IPackage getPackage(String var1);

   Collection<? extends IMappingFile.IClass> getClasses();

   IMappingFile.IClass getClass(String var1);

   String remapPackage(String var1);

   String remapClass(String var1);

   String remapDescriptor(String var1);

   void write(Path var1, IMappingFile.Format var2, boolean var3) throws IOException;

   IMappingFile reverse();

   IMappingFile rename(IRenamer var1);

   IMappingFile chain(IMappingFile var1);

   IMappingFile merge(IMappingFile var1);

   enum Format {
      SRG(false, false, false),
      XSRG(false, true, false),
      CSRG(false, false, false),
      TSRG(true, false, false),
      TSRG2(true, true, true),
      PG(true, true, false),
      TINY1(false, true, true),
      TINY(true, true, false);

      private final boolean ordered;
      private final boolean hasFieldTypes;
      private final boolean hasNames;

      Format(boolean ordered, boolean hasFieldTypes, boolean hasNames) {
         this.ordered = ordered;
         this.hasFieldTypes = hasFieldTypes;
         this.hasNames = hasNames;
      }

      public boolean isOrdered() {
         return this.ordered;
      }

      public boolean hasFieldTypes() {
         return this.hasFieldTypes;
      }

      public boolean hasNames() {
         return this.hasNames;
      }

      public static IMappingFile.Format get(String name) {
         name = name.toUpperCase(Locale.ENGLISH);

         for (IMappingFile.Format value : values()) {
            if (value.name().equals(name)) {
               return value;
            }
         }

         return null;
      }
   }

   interface IClass extends IMappingFile.INode {
      Collection<? extends IMappingFile.IField> getFields();

      Collection<? extends IMappingFile.IMethod> getMethods();

      String remapField(String var1);

      String remapMethod(String var1, String var2);

      @Nullable
      IMappingFile.IField getField(String var1);

      @Nullable
      IMappingFile.IMethod getMethod(String var1, String var2);
   }

   interface IField extends IMappingFile.IOwnedNode<IMappingFile.IClass> {
      @Nullable
      String getDescriptor();

      @Nullable
      String getMappedDescriptor();
   }

   interface IMethod extends IMappingFile.IOwnedNode<IMappingFile.IClass> {
      String getDescriptor();

      String getMappedDescriptor();

      Collection<? extends IMappingFile.IParameter> getParameters();

      String remapParameter(int var1, String var2);

      @Nullable
      IMappingFile.IParameter getParameter(int var1);
   }

   interface INode {
      String getOriginal();

      String getMapped();

      @Nullable
      String write(IMappingFile.Format var1, boolean var2);

      Map<String, String> getMetadata();
   }

   interface IOwnedNode<T> extends IMappingFile.INode {
      T getParent();
   }

   interface IPackage extends IMappingFile.INode {
   }

   interface IParameter extends IMappingFile.IOwnedNode<IMappingFile.IMethod> {
      int getIndex();
   }
}
