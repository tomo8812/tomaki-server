package org.sinytra.adapter.env.ann;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.tree.AnnotationNode;
import org.sinytra.adapter.analysis.selector.AnnotationHandle;
import org.sinytra.adapter.env.ctx.RefMapper;

public class SliceData {
   @Nullable
   private final AtData from;
   @Nullable
   private final AtData to;

   public SliceData(@Nullable AtData from, @Nullable AtData to) {
      this.from = from;
      this.to = to;
   }

   @Nullable
   public AtData from() {
      return this.from;
   }

   @Nullable
   public AtData to() {
      return this.to;
   }

   public static SliceData parse(AnnotationHandle handle, RefMapper context) {
      AtData from = handle.getNested("from").flatMap(s -> AtData.parse(s, context)).orElse(null);
      AtData to = handle.getNested("to").flatMap(s -> AtData.parse(s, context)).orElse(null);
      return new SliceData(from, to);
   }

   public AnnotationNode toAnnotationNode() {
      AnnotationNode slice = new AnnotationNode("Lorg/spongepowered/asm/mixin/injection/Slice;");
      if (this.from != null) {
         AnnotationVisitor fromNode = slice.visitAnnotation("from", "Lorg/spongepowered/asm/mixin/injection/At;");
         this.from.toAnnotationNode().accept(fromNode);
      }

      if (this.to != null) {
         AnnotationVisitor fromNode = slice.visitAnnotation("to", "Lorg/spongepowered/asm/mixin/injection/At;");
         this.to.toAnnotationNode().accept(fromNode);
      }

      return slice;
   }
}
