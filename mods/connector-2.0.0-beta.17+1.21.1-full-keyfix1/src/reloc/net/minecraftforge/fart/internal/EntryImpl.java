package reloc.net.minecraftforge.fart.internal;

import reloc.net.minecraftforge.fart.api.Transformer;

public abstract class EntryImpl implements Transformer.Entry {
   private final String name;
   private final long time;
   private final byte[] data;

   protected EntryImpl(String name, long time, byte[] data) {
      this.name = name;
      this.time = time;
      this.data = data;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public long getTime() {
      return this.time;
   }

   @Override
   public byte[] getData() {
      return this.data;
   }

   public static class ClassEntry extends EntryImpl implements Transformer.ClassEntry {
      private static final String VERSION_PREFIX = "META-INF/versions/";
      private final int release;
      private final String className;

      public ClassEntry(String name, long time, byte[] data) {
         super(name, time, data);
         if (name.startsWith("META-INF/versions/")) {
            int start = "META-INF/versions/".length();
            int idx = name.indexOf(47, start);
            if (idx == -1) {
               throw new IllegalArgumentException("Invalid versioned class entry: " + name);
            }

            this.release = Integer.parseInt(name.substring(start, idx));
            name = name.substring(idx + 1);
         } else {
            this.release = -1;
         }

         this.className = name.substring(0, name.length() - 6);
      }

      public Transformer.ClassEntry process(Transformer transformer) {
         return transformer.process(this);
      }

      @Override
      public String getClassName() {
         return this.className;
      }

      @Override
      public boolean isMultiRelease() {
         return this.release != -1;
      }

      @Override
      public int getVersion() {
         return this.release;
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Transformer.Entry process(Transformer var1) {
         return this.process(var1);
      }
   }

   public static class ManifestEntry extends EntryImpl implements Transformer.ManifestEntry {
      public ManifestEntry(long time, byte[] data) {
         super("META-INF/MANIFEST.MF", time, data);
      }

      public Transformer.ManifestEntry process(Transformer transformer) {
         return transformer.process(this);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Transformer.Entry process(Transformer var1) {
         return this.process(var1);
      }
   }

   public static class ResourceEntry extends EntryImpl implements Transformer.ResourceEntry {
      public ResourceEntry(String name, long time, byte[] data) {
         super(name, time, data);
      }

      public Transformer.ResourceEntry process(Transformer transformer) {
         return transformer.process(this);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Transformer.Entry process(Transformer var1) {
         return this.process(var1);
      }
   }
}
