package reloc.net.fabricmc.classtweaker.api.visitor;

import net.fabricmc.tinyremapper.api.TrEnvironment;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.commons.Remapper;
import reloc.net.fabricmc.classtweaker.api.ProblemSink;
import reloc.net.fabricmc.classtweaker.validator.ClassTweakerValidatingVisitor;
import reloc.net.fabricmc.classtweaker.visitors.ClassTweakerRemapperVisitor;
import reloc.net.fabricmc.classtweaker.visitors.ForwardingVisitor;
import reloc.net.fabricmc.classtweaker.visitors.TransitiveOnlyFilter;

public interface ClassTweakerVisitor {
   default void visitHeader(String namespace) {
   }

   @Nullable
   default AccessWidenerVisitor visitAccessWidener(String owner) {
      return null;
   }

   default void visitInjectedInterface(String owner, String iface, boolean transitive) {
   }

   default void visitEnumExtension(String owner, String addedConstant, boolean transitive) {
   }

   default void visitLineNumber(int lineNumber) {
   }

   static ClassTweakerVisitor remap(ClassTweakerVisitor delegate, Remapper remapper, String fromNamespace, String toNamespace) {
      return new ClassTweakerRemapperVisitor(delegate, remapper, fromNamespace, toNamespace);
   }

   static ClassTweakerVisitor forward(ClassTweakerVisitor... visitors) {
      return new ForwardingVisitor(visitors);
   }

   static ClassTweakerVisitor transitiveOnly(ClassTweakerVisitor delegate) {
      return new TransitiveOnlyFilter(delegate);
   }

   static ClassTweakerVisitor validate(TrEnvironment environment, ProblemSink sink) {
      return new ClassTweakerValidatingVisitor(environment, sink);
   }
}
