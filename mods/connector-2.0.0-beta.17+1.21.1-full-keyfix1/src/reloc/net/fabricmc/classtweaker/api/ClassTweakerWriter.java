package reloc.net.fabricmc.classtweaker.api;

import org.jetbrains.annotations.ApiStatus.NonExtendable;
import reloc.net.fabricmc.classtweaker.api.visitor.ClassTweakerVisitor;
import reloc.net.fabricmc.classtweaker.writer.ClassTweakerWriterImpl;

@NonExtendable
public interface ClassTweakerWriter extends ClassTweakerVisitor {
   static ClassTweakerWriter create(int version) {
      return new ClassTweakerWriterImpl(version);
   }

   byte[] getOutput();

   String getOutputAsString();
}
