package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import java.lang.annotation.Annotation;
import kotlin.jvm.internal.Intrinsics;

public final class GE extends JacksonAnnotationIntrospector {
   private final Class<? extends Annotation>[] a;

   public GE(Class<? extends Annotation>... var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      super();
      this.a = var1;
   }

   @Override
   public final boolean hasIgnoreMarker(AnnotatedMember m) {
      Intrinsics.checkNotNullParameter(m, "");
      return m.hasOneOf(this.a) || super.hasIgnoreMarker(m);
   }
}
