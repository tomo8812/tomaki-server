package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.util.Iterator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.joml.primitives.AABBi;
import org.valkyrienskies.core.api.ships.properties.ChunkClaim;
import org.valkyrienskies.core.api.util.functions.IntTernaryConsumer;
import org.valkyrienskies.core.api.world.LevelYRange;
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet;
import org.valkyrienskies.core.util.datastructures.IBlockPosSet;

@JsonDeserialize(using = Dr.a.class)
@JsonSerialize(using = Dr.b.class)
public final class Dr implements Dv {
   final IBlockPosSet a;
   final int b;
   final int c;
   final int d;
   final int e;
   final int f;
   final int g;
   private final Du h;
   private final Du i;
   private final Du j;

   private Dr(ChunkClaim var1, LevelYRange var2) {
      this(var1, var2, new DenseBlockPosSet());
   }

   private Dr(ChunkClaim var1, LevelYRange var2, DenseBlockPosSet var3) {
      Vector3i var4 = var1.getCenterBlockCoordinates(var2, new Vector3i());
      Vector3i var5 = var1.getBlockSize(var2, new Vector3i());
      this.a = var3;
      this.b = var4.x();
      this.c = var4.y();
      this.d = var4.z();
      this.e = var5.x();
      this.f = var5.y();
      this.g = var5.z();
      this.h = new Du(this.e);
      this.i = new Du(this.f);
      this.j = new Du(this.g);
   }

   private Dr(int var1, int var2, int var3, int var4, int var5, int var6) {
      this(new DenseBlockPosSet(), var1, var2, var3, var4, var5, var6);
   }

   Dr(IBlockPosSet var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = var4;
      this.e = var5;
      this.f = var6;
      this.g = var7;
      this.h = new Du(var5);
      this.i = new Du(var6);
      this.j = new Du(var7);
   }

   @Nullable
   @Override
   public final AABBi a() {
      if (this.a.isEmpty()) {
         return null;
      }

      Du var6 = this.h;
      int var1 = this.h.a - this.e / 2;
      var6 = this.h;
      int var2 = this.h.b - this.e / 2;
      var6 = this.i;
      int var3 = this.i.a - this.f / 2;
      var6 = this.i;
      int var4 = this.i.b - this.f / 2;
      var6 = this.j;
      int var5 = this.j.a - this.g / 2;
      var6 = this.j;
      int var17 = this.j.b - this.g / 2;
      var1 += this.b;
      var2 += this.b;
      var3 += this.c;
      var4 += this.c;
      var5 += this.d;
      int var18 = var17 + this.d;
      return new AABBi(var1, var3, var5, var2, var4, var18);
   }

   @Override
   public final boolean add(int x, int y, int z) {
      boolean var4;
      if (var4 = this.a.add(x, y, z)) {
         this.a(x, y, z);
      }

      return var4;
   }

   private void a(int var1, int var2, int var3) {
      this.h.a(var1 - this.b + this.e / 2);
      this.i.a(var2 - this.c + this.f / 2);
      this.j.a(var3 - this.d + this.g / 2);
   }

   @Override
   public final boolean remove(int x, int y, int z) {
      boolean var4;
      if (var4 = this.a.remove(x, y, z)) {
         int var5 = z;
         z = y;
         y = x;
         Dr var6 = this;
         this.h.b(y - var6.b + var6.e / 2);
         var6.i.b(z - var6.c + var6.f / 2);
         var6.j.b(var5 - var6.d + var6.g / 2);
      }

      return var4;
   }

   private void b(int var1, int var2, int var3) {
      this.h.b(var1 - this.b + this.e / 2);
      this.i.b(var2 - this.c + this.f / 2);
      this.j.b(var3 - this.d + this.g / 2);
   }

   @Override
   public final boolean contains(int x, int y, int z) {
      return this.a.contains(x, y, z);
   }

   @Override
   public final boolean canStore(int x, int y, int z) {
      return this.a.canStore(x, y, z);
   }

   @Override
   public final int size() {
      return this.a.size();
   }

   @Nonnull
   @Override
   public final Iterator<Vector3ic> iterator() {
      return this.a.iterator();
   }

   @Override
   public final void clear() {
      this.a.clear();
      this.h.a();
      this.i.a();
      this.j.a();
   }

   @Override
   public final void forEach(@Nonnull IntTernaryConsumer action) {
      this.a.forEach(action);
   }

   @Override
   public final boolean equals(Object other) {
      return other instanceof Dv ? ((Dv)other).size() == this.size() && ((Dv)other).containsAll(this) : false;
   }

   public static class a extends StdDeserializer<Dr> {
      private final ObjectMapper a = new ObjectMapper();

      public a() {
         super((Class<?>)null);
      }

      private Dr a(JsonParser var1) {
         JsonNode var8 = var1.getCodec().readTree(var1);
         Dx var2 = this.a.treeToValue(var8.get("blockPosSet"), Dx.class);
         int var3 = var8.get("centerX").asInt();
         int var4 = var8.get("centerY").asInt();
         int var5 = var8.get("centerZ").asInt();
         int var6 = var8.get("xSize").asInt();
         int var7 = var8.get("ySize").asInt();
         int var9 = var8.get("zSize").asInt();
         Dr var10 = new Dr(var2, var3, var4, var5, var6, var7, var9);
         var2.forEach(var10::a);
         return var10;
      }

      // $VF: synthetic method
      @Override
      public final Object deserialize(JsonParser var1, DeserializationContext var2) {
         JsonParser var10 = var1;
         Dr.a var8 = this;
         JsonNode var11 = var10.getCodec().readTree(var10);
         Dx var9 = var8.a.treeToValue(var11.get("blockPosSet"), Dx.class);
         int var3 = var11.get("centerX").asInt();
         int var4 = var11.get("centerY").asInt();
         int var5 = var11.get("centerZ").asInt();
         int var6 = var11.get("xSize").asInt();
         int var7 = var11.get("ySize").asInt();
         int var12 = var11.get("zSize").asInt();
         Dr var13 = new Dr(var9, var3, var4, var5, var6, var7, var12);
         var9.forEach(var13::a);
         return var13;
      }
   }

   public static class b extends StdSerializer<Dr> {
      public b() {
         super((Class<Dr>)null);
      }

      private static void a(Dr var0, JsonGenerator var1) {
         var1.writeStartObject();
         var1.writeObjectField("blockPosSet", var0.a);
         var1.writeNumberField("centerX", var0.b);
         var1.writeNumberField("centerY", var0.c);
         var1.writeNumberField("centerZ", var0.d);
         var1.writeNumberField("xSize", var0.e);
         var1.writeNumberField("ySize", var0.f);
         var1.writeNumberField("zSize", var0.g);
         var1.writeEndObject();
      }

      // $VF: synthetic method
      @Override
      public final void serialize(Object var1, JsonGenerator var2, SerializerProvider var3) {
         Dr var10000 = var1;
         var2 = var2;
         var1 = var10000;
         var2.writeStartObject();
         var2.writeObjectField("blockPosSet", var1.a);
         var2.writeNumberField("centerX", var1.b);
         var2.writeNumberField("centerY", var1.c);
         var2.writeNumberField("centerZ", var1.d);
         var2.writeNumberField("xSize", var1.e);
         var2.writeNumberField("ySize", var1.f);
         var2.writeNumberField("zSize", var1.g);
         var2.writeEndObject();
      }
   }
}
