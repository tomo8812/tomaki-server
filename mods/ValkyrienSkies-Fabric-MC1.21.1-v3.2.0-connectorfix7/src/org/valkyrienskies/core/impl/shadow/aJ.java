package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.classmate.AnnotationInclusion;
import com.fasterxml.classmate.AnnotationConfiguration.StdConfiguration;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.Type;

public final class aJ {
   private final aK a;
   private final aU b;

   private aJ(aK var1) {
      aK var3;
      aK var10001 = var3 = var1;
      aK var2 = var3;
      AnnotationInclusion var4 = AnnotationInclusion.INCLUDE_AND_INHERIT_IF_INHERITED;
      StdConfiguration var10002 = new StdConfiguration(var4);
      var2 = var2;
      StdConfiguration var5 = var10002;
      this(var10001, new aU(var5, var2));
   }

   private aJ(aK var1, aU var2) {
      this.a = var1;
      this.b = var2;
   }

   private ObjectNode a(Type var1, Type... var2) {
      return aG.a(this.a, this.b, var1, var2);
   }

   private aG a() {
      aU var2 = this.b;
      aK var1 = this.a;
      return new aG(var1, var2);
   }
}
