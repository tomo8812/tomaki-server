package reloc.net.fabricmc.classtweaker.validator;

import net.fabricmc.tinyremapper.api.TrEnvironment;
import reloc.net.fabricmc.classtweaker.api.ProblemSink;
import reloc.net.fabricmc.classtweaker.api.visitor.AccessWidenerVisitor;

public class AccessWidenerValidatingVisitor implements AccessWidenerVisitor {
   private final TrEnvironment environment;
   private final ProblemSink sink;
   private final String owner;
   private final int lineNumber;

   public AccessWidenerValidatingVisitor(TrEnvironment environment, ProblemSink sink, String owner, int lineNumber) {
      this.environment = environment;
      this.sink = sink;
      this.owner = owner;
      this.lineNumber = lineNumber;
   }

   @Override
   public void visitClass(AccessWidenerVisitor.AccessType access, boolean transitive) {
      if (this.environment.getClass(this.owner) == null) {
         this.sink.addProblem(this.lineNumber, String.format("Could not find class (%s)", this.owner));
      }
   }

   @Override
   public void visitMethod(String name, String descriptor, AccessWidenerVisitor.AccessType access, boolean transitive) {
      if (this.environment.getMethod(this.owner, name, descriptor) == null) {
         this.sink.addProblem(this.lineNumber, String.format("Could not find method (%s%s) in class (%s)", name, descriptor, this.owner));
      }
   }

   @Override
   public void visitField(String name, String descriptor, AccessWidenerVisitor.AccessType access, boolean transitive) {
      if (this.environment.getField(this.owner, name, descriptor) == null) {
         this.sink.addProblem(this.lineNumber, String.format("Could not find field (%s:%s) in class (%s)", name, descriptor, this.owner));
      }
   }
}
