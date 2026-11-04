package org.sinytra.adapter.env.param;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.objectweb.asm.Type;

public record Parameter(Type type, List<Annotation> annotations) {
   public Parameter(Type type, List<Annotation> annotations) {
      this.type = type;
      this.annotations = ImmutableList.copyOf(annotations);
   }

   public boolean isLocalOrShare() {
      return this.isLocal() || this.isShare();
   }

   public boolean isLocal() {
      return this.hasAnnotation("Lcom/llamalad7/mixinextras/sugar/Local;");
   }

   public boolean isShare() {
      return this.hasAnnotation("Lcom/llamalad7/mixinextras/sugar/Share;");
   }

   public boolean hasAnnotation(String desc) {
      return this.annotations.stream().anyMatch(annotation -> annotation.getDesc().equals(desc));
   }

   public Parameter.Builder extend() {
      Parameter.Builder builder = builder(this.type);
      this.annotations.forEach(builder::annotate);
      return builder;
   }

   public static Parameter simple(Type type) {
      return new Parameter(type, List.of());
   }

   public static Parameter.Builder builder(String typeDesc) {
      return builder(Type.getType(typeDesc));
   }

   public static Parameter.Builder builder(Type type) {
      return new Parameter.Builder(type);
   }

   public static class Builder {
      private final Type type;
      private final List<Annotation> annotations = new ArrayList<>();

      public Builder(Type type) {
         this.type = type;
      }

      public Parameter.Builder annotate(String desc, Consumer<Annotation.Builder> consumer) {
         Annotation.Builder builder = Annotation.builder(desc);
         consumer.accept(builder);
         return this.annotate(builder.build());
      }

      public Parameter.Builder annotate(Annotation annotation) {
         this.annotations.add(annotation);
         return this;
      }

      public Parameter build() {
         return new Parameter(this.type, this.annotations);
      }
   }
}
