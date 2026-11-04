package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class cT {
   private boolean b;
   private boolean c;
   private bS d;
   private boolean e;
   private boolean f;
   private boolean g;
   private boolean h = false;
   private Map<String, String> i = new HashMap<>();
   private boolean j = true;
   private boolean k = true;
   public static final String a = "com.networknt.AllKeywordWalkListener";
   private final Map<String, List<dK>> l = new HashMap<>();
   private final List<dK> m = new ArrayList<>();
   private final List<dK> n = new ArrayList<>();
   private bV o;
   private boolean p = true;

   public boolean a() {
      return this.b;
   }

   public void a(boolean var1) {
      this.b = var1;
   }

   public void b(boolean var1) {
      this.c = var1;
   }

   public boolean b() {
      return this.c;
   }

   public void a(bS var1) {
      this.d = var1;
   }

   public bS c() {
      return this.d;
   }

   public Map<String, String> d() {
      return new HashMap<>(this.i);
   }

   public void a(Map<String, String> var1) {
      this.i = var1;
   }

   public boolean e() {
      return this.j;
   }

   public void c(boolean var1) {
      this.j = var1;
   }

   public boolean f() {
      return this.e;
   }

   public void d(boolean var1) {
      this.e = var1;
   }

   public boolean g() {
      return this.f;
   }

   public void e(boolean var1) {
      this.f = var1;
   }

   public void a(dK var1) {
      if (this.l.get("com.networknt.AllKeywordWalkListener") == null) {
         ArrayList var2 = new ArrayList();
         this.l.put("com.networknt.AllKeywordWalkListener", var2);
      }

      this.l.get("com.networknt.AllKeywordWalkListener").add(var1);
   }

   public void a(String var1, dK var2) {
      if (this.l.get(var1) == null) {
         ArrayList var3 = new ArrayList();
         this.l.put(var1, var3);
      }

      this.l.get(var1).add(var2);
   }

   public void a(List<dK> var1) {
      if (this.l.get("com.networknt.AllKeywordWalkListener") == null) {
         ArrayList var2 = new ArrayList();
         this.l.put("com.networknt.AllKeywordWalkListener", var2);
      }

      this.l.get("com.networknt.AllKeywordWalkListener").addAll(var1);
   }

   public void a(String var1, List<dK> var2) {
      if (this.l.get(var1) == null) {
         ArrayList var3 = new ArrayList();
         this.l.put(var1, var3);
      }

      this.l.get(var1).addAll(var2);
   }

   public void b(List<dK> var1) {
      this.m.addAll(var1);
   }

   public void b(dK var1) {
      this.m.add(var1);
   }

   public void c(dK var1) {
      this.n.add(var1);
   }

   public void c(List<dK> var1) {
      this.n.addAll(var1);
   }

   public List<dK> h() {
      return this.m;
   }

   public Map<String, List<dK>> i() {
      return this.l;
   }

   public List<dK> j() {
      return this.n;
   }

   public bV k() {
      return this.o;
   }

   public void a(bV var1) {
      this.o = var1;
   }

   public boolean l() {
      return this.g;
   }

   public void f(boolean var1) {
      this.g = var1;
   }

   public boolean m() {
      return this.h;
   }

   public void g(boolean var1) {
      this.h = var1;
   }

   public void h(boolean var1) {
      this.p = var1;
   }

   public boolean n() {
      return this.p;
   }

   public boolean o() {
      return this.k;
   }

   public void i(boolean var1) {
      this.k = var1;
   }
}
