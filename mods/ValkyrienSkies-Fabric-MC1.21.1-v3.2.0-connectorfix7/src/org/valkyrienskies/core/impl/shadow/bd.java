package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.classmate.AnnotationConfiguration;
import com.fasterxml.classmate.AnnotationInclusion;
import com.fasterxml.classmate.AnnotationConfiguration.StdConfiguration;

public final class bd {
   private static aU a() {
      AnnotationInclusion var0 = AnnotationInclusion.INCLUDE_AND_INHERIT_IF_INHERITED;
      StdConfiguration var1 = new StdConfiguration(var0);
      return new aU(var1);
   }

   public static aU a(aK var0) {
      return a(AnnotationInclusion.INCLUDE_AND_INHERIT_IF_INHERITED, var0);
   }

   private static aU a(AnnotationInclusion var0) {
      return a(new StdConfiguration(var0));
   }

   private static aU a(AnnotationInclusion var0, aK var1) {
      return a(new StdConfiguration(var0), var1);
   }

   private static aU a(AnnotationConfiguration var0) {
      return new aU(var0);
   }

   private static aU a(AnnotationConfiguration var0, aK var1) {
      return new aU(var0, var1);
   }
}
