package reloc.net.minecraftforge.fart.internal;

import java.io.File;
import java.io.FileInputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import reloc.net.minecraftforge.fart.api.Transformer;

public final class FFLineFixer implements Transformer {
   private final Map<String, NavigableMap<Integer, Integer>> classes = new HashMap<>();

   public FFLineFixer(Consumer<String> debug, File data) {
      try (
         FileInputStream fis = new FileInputStream(data);
         ZipInputStream zip = new ZipInputStream(fis);
      ) {
         ZipEntry entry = null;

         while ((entry = zip.getNextEntry()) != null) {
            byte[] extra = entry.getExtra();
            if (extra != null && entry.getName().endsWith(".java")) {
               ByteBuffer buf = ByteBuffer.wrap(extra);
               buf.order(ByteOrder.LITTLE_ENDIAN);

               while (buf.hasRemaining()) {
                  short id = buf.getShort();
                  short len = buf.getShort();
                  if (id == 17990) {
                     String cls = entry.getName().substring(0, entry.getName().length() - 5);
                     debug.accept("Lines: " + cls);
                     int ver = buf.get();
                     if (ver != 1) {
                        throw new IllegalStateException("Invalid FF code line version for " + entry.getName());
                     }

                     int count = (len - 1) / 4;
                     NavigableMap<Integer, Integer> lines = new TreeMap<>();

                     for (int x = 0; x < count; x++) {
                        int oline = buf.getShort();
                        int nline = buf.getShort();
                        debug.accept("  " + oline + ' ' + nline);
                        lines.put(oline, nline);
                     }

                     this.classes.put(cls, lines);
                  } else {
                     ((Buffer)buf).position(buf.position() + len);
                  }
               }
            }
         }
      } catch (Exception e) {
         throw new RuntimeException("Could not create FFLineFixer for file: " + data.getAbsolutePath(), e);
      }
   }

   @Override
   public Transformer.ClassEntry process(Transformer.ClassEntry entry) {
      String owner = entry.getClassName();
      int idx = owner.indexOf(36);
      if (idx != -1) {
         owner = owner.substring(0, idx);
      }

      NavigableMap<Integer, Integer> lines = this.classes.get(owner);
      if (lines == null) {
         return entry;
      }

      ClassReader reader = new ClassReader(entry.getData());
      ClassWriter writer = new ClassWriter(reader, 0);
      FFLineFixer.Fixer fixer = new FFLineFixer.Fixer(writer, lines);
      reader.accept(fixer, 0);
      return !fixer.madeChange() ? entry : Transformer.ClassEntry.create(entry.getName(), entry.getTime(), writer.toByteArray());
   }

   private static class Fixer extends ClassVisitor {
      private final NavigableMap<Integer, Integer> lines;
      private boolean madeChange = false;

      public Fixer(ClassVisitor parent, NavigableMap<Integer, Integer> lines) {
         super(589824, parent);
         this.lines = lines;
      }

      public boolean madeChange() {
         return this.madeChange;
      }

      public final MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
         MethodVisitor parent = super.visitMethod(access, name, descriptor, signature, exceptions);
         return new MethodVisitor(589824, parent) {
            public void visitLineNumber(int line, Label start) {
               Map.Entry<Integer, Integer> nline = Fixer.this.lines.ceilingEntry(line);
               if (nline != null) {
                  Fixer.this.madeChange = true;
                  super.visitLineNumber(nline.getValue(), start);
               } else {
                  super.visitLineNumber(line, start);
               }
            }
         };
      }
   }
}
