package reloc.net.fabricmc.classtweaker.api;

import java.io.BufferedReader;
import java.io.IOException;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import reloc.net.fabricmc.classtweaker.api.visitor.ClassTweakerVisitor;
import reloc.net.fabricmc.classtweaker.reader.ClassTweakerReaderImpl;

@NonExtendable
public interface ClassTweakerReader {
   static ClassTweakerReader create(ClassTweakerVisitor visitor) {
      return new ClassTweakerReaderImpl(visitor);
   }

   void read(byte[] var1);

   void read(byte[] var1, String var2);

   void read(BufferedReader var1) throws IOException;

   void read(BufferedReader var1, String var2) throws IOException;

   static int readVersion(byte[] content) {
      return ClassTweakerReaderImpl.readVersion(content);
   }

   static int readVersion(BufferedReader bufferedReader) throws IOException {
      return ClassTweakerReaderImpl.readVersion(bufferedReader);
   }

   static ClassTweakerReader.Header readHeader(byte[] content) {
      return ClassTweakerReaderImpl.readHeader(content);
   }

   static ClassTweakerReader.Header readHeader(BufferedReader reader) throws IOException {
      return ClassTweakerReaderImpl.readHeader(reader);
   }

   interface Header {
      int getVersion();

      String getNamespace();
   }
}
