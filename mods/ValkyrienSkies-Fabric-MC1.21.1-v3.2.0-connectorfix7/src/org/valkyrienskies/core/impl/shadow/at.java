package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class at {
   private static at.b b;
   private static at.a c;
   private static at.a d;
   public final ObjectNode a;
   private final at.b e;
   private final at.a f;

   public at(ObjectNode var1) {
      this(var1, at.b.STANDARD, at.a.YES);
   }

   public at(ObjectNode var1, boolean var2) {
      this(var1, at.b.INLINE, at.a.YES);
   }

   public at(ObjectNode var1, at.b var2, at.a var3) {
      this.a = var1;
      this.e = var2;
      this.f = var3;
   }

   public final ObjectNode a() {
      return this.a;
   }

   private at.b d() {
      return this.e;
   }

   public final boolean b() {
      return this.e == at.b.INLINE;
   }

   private at.a e() {
      return this.f;
   }

   public final boolean c() {
      return this.f == at.a.YES;
   }

   public enum a {
      YES,
      NO;
   }

   public enum b {
      INLINE,
      STANDARD;
   }
}
