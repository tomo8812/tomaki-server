package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.Iterator;
import javax.annotation.Nonnull;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.valkyrienskies.core.api.util.functions.IntTernaryConsumer;
import org.valkyrienskies.core.util.datastructures.IBlockPosSet;

@JsonDeserialize(using = Dx.b.class)
@JsonSerialize(using = Dx.c.class)
public class Dx implements IBlockPosSet {
   private static final int f = 4095;
   private static final int g = 255;
   @Nonnull
   final IntArrayList a = new IntArrayList();
   @Nonnull
   final Int2IntOpenHashMap b = new Int2IntOpenHashMap();
   final int c;
   final int d;
   final int e;

   public Dx(int var1, int var2, int var3) {
      this.c = var1;
      this.d = var2;
      this.e = var3;
   }

   @Override
   public boolean add(int x, int y, int z) {
      if (!this.canStore(x, y, z)) {
         throw new IllegalArgumentException(
            "Cannot store block position at <" + x + "," + y + "," + z + "> into SmallBlockPosSet centered at <" + this.c + "," + this.d + "," + this.e + ">"
         );
      }

      x = this.a(x, y, z);
      if (this.b.containsKey(x)) {
         return false;
      }

      this.a.add(x);
      this.b.put(x, this.a.size() - 1);
      return true;
   }

   @Override
   public boolean remove(int x, int y, int z) {
      if (!this.canStore(x, y, z)) {
         throw new IllegalArgumentException("Cannot remove block position at <" + x + "," + y + "," + z + ">");
      }

      x = this.a(x, y, z);
      if (!this.b.containsKey(x)) {
         return false;
      }

      if ((y = this.b.get(x)) == this.a.size() - 1) {
         this.a.removeInt(y);
      } else {
         z = this.a.removeInt(this.a.size() - 1);
         this.a.set(y, z);
         this.b.put(z, y);
      }

      this.b.remove(x);
      return true;
   }

   @Override
   public boolean contains(int x, int y, int z) {
      return !this.canStore(x, y, z) ? false : this.b.containsKey(this.a(x, y, z));
   }

   @Override
   public boolean canStore(int x, int y, int z) {
      x -= this.c;
      y -= this.d;
      z -= this.e;
      return !(y < -128 | y > 127 | x < -2048 | x > 2047 | z < -2048 | z > 2047);
   }

   @Override
   public int size() {
      return this.a.size();
   }

   @Nonnull
   @Override
   public Iterator<Vector3ic> iterator() {
      return new Dx.a(this.a.iterator());
   }

   @Override
   public void forEach(@Nonnull IntTernaryConsumer action) {
      IntListIterator var2 = this.a.iterator();

      while (var2.hasNext()) {
         int var3;
         int var4 = (var3 = var2.nextInt()) >> 20;
         int var5;
         if (((var5 = var3 >> 12 & 0xFF) & 128) != 0) {
            var5 |= -256;
         }

         var3 = (var3 & 4095) << 20 >> 20;
         action.accept(var3 + this.c, var5 + this.d, var4 + this.e);
      }
   }

   @Override
   public void clear() {
      this.a.clear();
      this.b.clear();
   }

   @Nonnull
   private Vector3i a(int var1) {
      return this.a(var1, new Vector3i());
   }

   Vector3i a(int var1, Vector3i var2) {
      int var3 = var1 >> 20;
      int var4;
      if (((var4 = var1 >> 12 & 0xFF) & 128) != 0) {
         var4 |= -256;
      }

      var1 = (var1 & 4095) << 20 >> 20;
      var2.set(var1 + this.c, var4 + this.d, var3 + this.e);
      return var2;
   }

   private int a(int var1, int var2, int var3) {
      var1 = var1 - this.c & 4095;
      var2 = var2 - this.d & 0xFF;
      var3 = var3 - this.e & 4095;
      return var1 | var2 << 12 | var3 << 20;
   }

   @Override
   public void forEachUnsafe(@Nonnull IntTernaryConsumer action) {
      int var2 = 0;
      Vector3i var3 = new Vector3i();

      while (this.a.size() >= var2) {
         try {
            int var4 = this.a.get(var2);
            var2++;
            this.a(var4, var3);
            action.accept(var3.x(), var3.y(), var3.z());
         } catch (Exception var5) {
            return;
         }
      }
   }

   private int a() {
      return this.c;
   }

   private int b() {
      return this.d;
   }

   private int c() {
      return this.e;
   }

   @Override
   public boolean equals(Object other) {
      return other instanceof IBlockPosSet ? ((IBlockPosSet)other).size() == this.size() && ((IBlockPosSet)other).containsAll(this) : false;
   }

   class a implements Iterator<Vector3ic> {
      private final IntIterator a;

      a(IntListIterator var2) {
         this.a = var2;
      }

      @Override
      public final boolean hasNext() {
         return this.a.hasNext();
      }

      private Vector3ic a() {
         Dx var10000 = Dx.this;
         int var2 = this.a.next();
         return var10000.a(var2, new Vector3i());
      }

      // $VF: synthetic method
      @Override
      public final Object next() {
         Dx.a var1 = this;
         Dx var10000 = Dx.this;
         int var2 = var1.a.next();
         return var10000.a(var2, new Vector3i());
      }
   }

   public static class b extends StdDeserializer<Dx> {
      public b() {
         super((Class<?>)null);
      }

      private static Dx a(JsonParser var0) {
         JsonNode var4;
         int var1 = (var4 = var0.getCodec().readTree(var0)).get("centerX").asInt();
         int var2 = var4.get("centerY").asInt();
         int var3 = var4.get("centerZ").asInt();
         Dx var6 = new Dx(var1, var2, var3);
         Iterator var5 = var4.get("positions").iterator();

         while (var5.hasNext()) {
            var2 = ((JsonNode)var5.next()).asInt();
            var6.a.add(var2);
            var6.b.put(var2, var6.a.size() - 1);
         }

         return var6;
      }

      // $VF: synthetic method
      @Override
      public final Object deserialize(JsonParser var1, DeserializationContext var2) {
         JsonParser var5;
         JsonNode var6;
         int var8 = (var6 = (var5 = var1).getCodec().readTree(var5)).get("centerX").asInt();
         int var3 = var6.get("centerY").asInt();
         int var4 = var6.get("centerZ").asInt();
         Dx var9 = new Dx(var8, var3, var4);
         Iterator var7 = var6.get("positions").iterator();

         while (var7.hasNext()) {
            var3 = ((JsonNode)var7.next()).asInt();
            var9.a.add(var3);
            var9.b.put(var3, var9.a.size() - 1);
         }

         return var9;
      }
   }

   public static class c extends StdSerializer<Dx> {
      public c() {
         super((Class<Dx>)null);
      }

      private static void a(Dx var0, JsonGenerator var1) {
         var1.writeStartObject();
         var1.writeFieldName("positions");
         var1.writeStartArray(var0.a, var0.a.size());
         IntListIterator var2 = var0.a.iterator();

         while (var2.hasNext()) {
            var1.writeNumber(var2.nextInt());
         }

         var1.writeEndArray();
         var1.writeNumberField("centerX", var0.c);
         var1.writeNumberField("centerY", var0.d);
         var1.writeNumberField("centerZ", var0.e);
         var1.writeEndObject();
      }

      // $VF: synthetic method
      @Override
      public final void serialize(Object var1, JsonGenerator var2, SerializerProvider var3) {
         Dx var10000 = var1;
         var2 = var2;
         var1 = var10000;
         var2.writeStartObject();
         var2.writeFieldName("positions");
         var2.writeStartArray(var1.a, var1.a.size());
         IntListIterator var6 = var1.a.iterator();

         while (var6.hasNext()) {
            var2.writeNumber(var6.nextInt());
         }

         var2.writeEndArray();
         var2.writeNumberField("centerX", var1.c);
         var2.writeNumberField("centerY", var1.d);
         var2.writeNumberField("centerZ", var1.e);
         var2.writeEndObject();
      }
   }
}
