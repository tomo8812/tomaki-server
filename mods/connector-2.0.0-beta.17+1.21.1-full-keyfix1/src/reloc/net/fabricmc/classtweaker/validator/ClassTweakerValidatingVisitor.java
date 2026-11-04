package reloc.net.fabricmc.classtweaker.validator;

import net.fabricmc.tinyremapper.api.TrClass;
import net.fabricmc.tinyremapper.api.TrEnvironment;
import org.jetbrains.annotations.Nullable;
import reloc.net.fabricmc.classtweaker.api.ProblemSink;
import reloc.net.fabricmc.classtweaker.api.visitor.AccessWidenerVisitor;
import reloc.net.fabricmc.classtweaker.api.visitor.ClassTweakerVisitor;

public class ClassTweakerValidatingVisitor implements ClassTweakerVisitor {
   private final TrEnvironment environment;
   private final ProblemSink sink;
   private int lineNumber;

   public ClassTweakerValidatingVisitor(TrEnvironment environment, ProblemSink sink) {
      this.environment = environment;
      this.sink = sink;
   }

   @Nullable
   @Override
   public AccessWidenerVisitor visitAccessWidener(String owner) {
      return new AccessWidenerValidatingVisitor(this.environment, this.sink, owner, this.lineNumber);
   }

   @Override
   public void visitInjectedInterface(String owner, String iface, boolean transitive) {
      if (this.environment.getClass(owner) == null) {
         this.sink.addProblem(this.lineNumber, String.format("Could not find target class (%s)", owner));
      }
   }

   @Override
   public void visitEnumExtension(String owner, String addedConstant, boolean transitive) {
      TrClass clazz = this.environment.getClass(owner);
      if (clazz == null) {
         this.sink.addProblem(this.lineNumber, String.format("Could not find target class (%s)", owner));
      } else if (!clazz.isEnum() || !clazz.getSuperName().equals("java/lang/Enum")) {
         this.sink.addProblem(this.lineNumber, String.format("Class (%s) is not an enum", owner));
      }
   }

   @Override
   public void visitLineNumber(int lineNumber) {
      this.lineNumber = lineNumber;
   }
}
