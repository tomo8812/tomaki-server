package reloc.net.fabricmc.classtweaker.visitors;

import org.jetbrains.annotations.Nullable;
import reloc.net.fabricmc.classtweaker.api.visitor.AccessWidenerVisitor;
import reloc.net.fabricmc.classtweaker.api.visitor.ClassTweakerVisitor;

public final class TransitiveOnlyFilter implements ClassTweakerVisitor {
   private final ClassTweakerVisitor delegate;

   public TransitiveOnlyFilter(ClassTweakerVisitor delegate) {
      this.delegate = delegate;
   }

   @Override
   public void visitHeader(String namespace) {
      this.delegate.visitHeader(namespace);
   }

   @Nullable
   @Override
   public AccessWidenerVisitor visitAccessWidener(String owner) {
      final AccessWidenerVisitor delegateAccessWidenerVisitor = this.delegate.visitAccessWidener(owner);
      return delegateAccessWidenerVisitor != null ? new AccessWidenerVisitor() {
         @Override
         public void visitClass(AccessWidenerVisitor.AccessType access, boolean transitive) {
            if (transitive) {
               delegateAccessWidenerVisitor.visitClass(access, transitive);
            }
         }

         @Override
         public void visitMethod(String name, String descriptor, AccessWidenerVisitor.AccessType access, boolean transitive) {
            if (transitive) {
               delegateAccessWidenerVisitor.visitMethod(name, descriptor, access, transitive);
            }
         }

         @Override
         public void visitField(String name, String descriptor, AccessWidenerVisitor.AccessType access, boolean transitive) {
            if (transitive) {
               delegateAccessWidenerVisitor.visitField(name, descriptor, access, transitive);
            }
         }
      } : null;
   }

   @Override
   public void visitInjectedInterface(String owner, String iface, boolean transitive) {
      if (transitive) {
         this.delegate.visitInjectedInterface(owner, iface, transitive);
      }
   }

   @Override
   public void visitEnumExtension(String owner, String addedConstant, boolean transitive) {
      if (transitive) {
         this.delegate.visitEnumExtension(owner, addedConstant, transitive);
      }
   }
}
