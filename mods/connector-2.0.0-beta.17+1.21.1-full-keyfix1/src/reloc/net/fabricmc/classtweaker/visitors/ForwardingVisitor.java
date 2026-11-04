package reloc.net.fabricmc.classtweaker.visitors;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import reloc.net.fabricmc.classtweaker.api.visitor.AccessWidenerVisitor;
import reloc.net.fabricmc.classtweaker.api.visitor.ClassTweakerVisitor;

public class ForwardingVisitor implements ClassTweakerVisitor {
   private final ClassTweakerVisitor[] visitors;

   public ForwardingVisitor(ClassTweakerVisitor... visitors) {
      this.visitors = (ClassTweakerVisitor[])visitors.clone();
   }

   @Override
   public void visitHeader(String namespace) {
      for (ClassTweakerVisitor visitor : this.visitors) {
         visitor.visitHeader(namespace);
      }
   }

   @Nullable
   @Override
   public AccessWidenerVisitor visitAccessWidener(String owner) {
      List<AccessWidenerVisitor> accessWidenerVisitors = new ArrayList<>(this.visitors.length);

      for (ClassTweakerVisitor visitor : this.visitors) {
         AccessWidenerVisitor next = visitor.visitAccessWidener(owner);
         if (next != null) {
            accessWidenerVisitors.add(next);
         }
      }

      return new ForwardingVisitor.ForwardingAccessWidenerVisitor(accessWidenerVisitors.toArray(new AccessWidenerVisitor[0]));
   }

   @Override
   public void visitInjectedInterface(String owner, String iface, boolean transitive) {
      for (ClassTweakerVisitor visitor : this.visitors) {
         visitor.visitInjectedInterface(owner, iface, transitive);
      }
   }

   @Override
   public void visitEnumExtension(String owner, String addedConstant, boolean transitive) {
      for (ClassTweakerVisitor visitor : this.visitors) {
         visitor.visitEnumExtension(owner, addedConstant, transitive);
      }
   }

   @Override
   public void visitLineNumber(int lineNumber) {
      for (ClassTweakerVisitor visitor : this.visitors) {
         visitor.visitLineNumber(lineNumber);
      }
   }

   private static class ForwardingAccessWidenerVisitor implements AccessWidenerVisitor {
      private final AccessWidenerVisitor[] visitors;

      ForwardingAccessWidenerVisitor(AccessWidenerVisitor[] visitors) {
         this.visitors = visitors;
      }

      @Override
      public void visitClass(AccessWidenerVisitor.AccessType access, boolean transitive) {
         for (AccessWidenerVisitor visitor : this.visitors) {
            visitor.visitClass(access, transitive);
         }
      }

      @Override
      public void visitMethod(String name, String descriptor, AccessWidenerVisitor.AccessType access, boolean transitive) {
         for (AccessWidenerVisitor visitor : this.visitors) {
            visitor.visitMethod(name, descriptor, access, transitive);
         }
      }

      @Override
      public void visitField(String name, String descriptor, AccessWidenerVisitor.AccessType access, boolean transitive) {
         for (AccessWidenerVisitor visitor : this.visitors) {
            visitor.visitField(name, descriptor, access, transitive);
         }
      }
   }
}
