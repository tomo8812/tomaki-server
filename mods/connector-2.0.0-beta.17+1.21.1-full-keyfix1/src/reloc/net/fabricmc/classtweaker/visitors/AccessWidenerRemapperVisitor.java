package reloc.net.fabricmc.classtweaker.visitors;

import org.objectweb.asm.commons.Remapper;
import reloc.net.fabricmc.classtweaker.api.visitor.AccessWidenerVisitor;

final class AccessWidenerRemapperVisitor implements AccessWidenerVisitor {
   private final AccessWidenerVisitor delegate;
   private final Remapper remapper;
   private final String owner;

   AccessWidenerRemapperVisitor(AccessWidenerVisitor delegate, Remapper remapper, String owner) {
      this.delegate = delegate;
      this.remapper = remapper;
      this.owner = owner;
   }

   @Override
   public void visitClass(AccessWidenerVisitor.AccessType access, boolean transitive) {
      this.delegate.visitClass(access, transitive);
   }

   @Override
   public void visitMethod(String name, String descriptor, AccessWidenerVisitor.AccessType access, boolean transitive) {
      this.delegate.visitMethod(this.remapper.mapMethodName(this.owner, name, descriptor), this.remapper.mapDesc(descriptor), access, transitive);
   }

   @Override
   public void visitField(String name, String descriptor, AccessWidenerVisitor.AccessType access, boolean transitive) {
      this.delegate.visitField(this.remapper.mapFieldName(this.owner, name, descriptor), this.remapper.mapDesc(descriptor), access, transitive);
   }
}
