package org.valkyrienskies.core.impl.shadow;

@Deprecated
public final class sd<T extends Comparable<T>> {
   sd<T>.a a = null;

   private void a(T var1) {
      if (var1 != null) {
         if (this.a == null) {
            this.a = new sd.a((T)var1, null);
            return;
         }

         this.a.a((T)var1);
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private boolean b(T var1) {
      if (var1 != null) {
         Comparable var3 = var1;
         sd var2 = this;
         sd.a var4 = null;
         sd.a var8 = var2.a;

         sd.a var10000;
         while (true) {
            if (var8 == null) {
               var10000 = null;
               break;
            }

            if (var8.a.compareTo((T)var3) < 0) {
               if (var8.c == null) {
                  var10000 = var4;
                  break;
               }

               var8 = var8.c;
            } else {
               var4 = var8;
               if (var8.b == null) {
                  var10000 = var4;
                  break;
               }

               var8 = var8.b;
            }
         }

         sd.a var9 = var10000;

         while (var9 != null) {
            if (var9.a == var1) {
               sd.a var11;
               if ((var11 = var9).d == null && var11.b == null && var11.c == null) {
                  var11.a = null;
                  Object var15 = null;
                  var11.f.a = null;
               } else {
                  boolean var12;
                  sd.a var18;
                  if (var11.b == null && var11.c == null) {
                     var18 = var11;
                     var11.a = null;
                     var12 = var18 == var18.d.b;
                     var10000 = null;
                  } else {
                     var18 = var11.b != null ? var11.b.c() : var11.c.b();
                     var11.a = var18.a;
                     var12 = var18 == var18.d.b;
                     var10000 = var18.b != null ? var18.b : var18.c;
                  }

                  var4 = var10000;
                  sd.a var19 = var18.d;
                  if (var12) {
                     var19.b = var4;
                  } else {
                     var19.c = var4;
                  }

                  if (var4 != null) {
                     var4.d = var19;
                  }

                  while (true) {
                     if (var12) {
                        sd.a var6 = var19;
                        boolean var23;
                        switch (<unrepresentable>.a[var6.e - 1]) {
                           case 1:
                              var6.e = sd.b.BALANCED$7e36164e;
                              var23 = true;
                              break;
                           case 2:
                              if (var6.c.e == sd.b.RIGHT_HIGH$7e36164e) {
                                 var6.g();
                                 var6.e = sd.b.BALANCED$7e36164e;
                                 var6.b.e = sd.b.BALANCED$7e36164e;
                                 var23 = true;
                              } else if (var6.c.e == sd.b.BALANCED$7e36164e) {
                                 var6.g();
                                 var6.e = sd.b.LEFT_HIGH$7e36164e;
                                 var6.b.e = sd.b.RIGHT_HIGH$7e36164e;
                                 var23 = false;
                              } else {
                                 int var13 = var6.c.b.e;
                                 var6.c.f();
                                 var6.g();
                                 switch (<unrepresentable>.a[var13 - 1]) {
                                    case 1:
                                       var6.b.e = sd.b.BALANCED$7e36164e;
                                       var6.c.e = sd.b.RIGHT_HIGH$7e36164e;
                                       break;
                                    case 2:
                                       var6.b.e = sd.b.LEFT_HIGH$7e36164e;
                                       var6.c.e = sd.b.BALANCED$7e36164e;
                                       break;
                                    default:
                                       var6.b.e = sd.b.BALANCED$7e36164e;
                                       var6.c.e = sd.b.BALANCED$7e36164e;
                                 }

                                 var6.e = sd.b.BALANCED$7e36164e;
                                 var23 = true;
                              }
                              break;
                           default:
                              var6.e = sd.b.RIGHT_HIGH$7e36164e;
                              var23 = false;
                        }

                        if (!var23) {
                           break;
                        }
                     } else {
                        sd.a var7 = var19;
                        boolean var24;
                        switch (<unrepresentable>.a[var7.e - 1]) {
                           case 1:
                              if (var7.b.e == sd.b.LEFT_HIGH$7e36164e) {
                                 var7.f();
                                 var7.e = sd.b.BALANCED$7e36164e;
                                 var7.c.e = sd.b.BALANCED$7e36164e;
                                 var24 = true;
                              } else if (var7.b.e == sd.b.BALANCED$7e36164e) {
                                 var7.f();
                                 var7.e = sd.b.RIGHT_HIGH$7e36164e;
                                 var7.c.e = sd.b.LEFT_HIGH$7e36164e;
                                 var24 = false;
                              } else {
                                 int var14 = var7.b.c.e;
                                 var7.b.g();
                                 var7.f();
                                 switch (<unrepresentable>.a[var14 - 1]) {
                                    case 1:
                                       var7.b.e = sd.b.BALANCED$7e36164e;
                                       var7.c.e = sd.b.RIGHT_HIGH$7e36164e;
                                       break;
                                    case 2:
                                       var7.b.e = sd.b.LEFT_HIGH$7e36164e;
                                       var7.c.e = sd.b.BALANCED$7e36164e;
                                       break;
                                    default:
                                       var7.b.e = sd.b.BALANCED$7e36164e;
                                       var7.c.e = sd.b.BALANCED$7e36164e;
                                 }

                                 var7.e = sd.b.BALANCED$7e36164e;
                                 var24 = true;
                              }
                              break;
                           case 2:
                              var7.e = sd.b.BALANCED$7e36164e;
                              var24 = true;
                              break;
                           default:
                              var7.e = sd.b.LEFT_HIGH$7e36164e;
                              var24 = false;
                        }

                        if (!var24) {
                           break;
                        }
                     }

                     if (var19.d == null) {
                        break;
                     }

                     var12 = var19 == var19.d.b;
                     var19 = var19.d;
                  }
               }

               return true;
            }

            if (var9.a.compareTo((T)var1) > 0) {
               return false;
            }

            sd.a var10;
            sd.a var16;
            if ((var10 = var9).c != null && (var16 = var10.c.b()) != null) {
               var10000 = var16;
            } else {
               sd.a var17 = var10;

               while (true) {
                  if (var17.d == null) {
                     var10000 = null;
                     break;
                  }

                  if (var17 != var17.d.c) {
                     var10000 = var17.d;
                     break;
                  }

                  var17 = var17.d;
               }
            }

            var9 = var10000;
         }
      }

      return false;
   }

   private boolean a() {
      return this.a == null;
   }

   private int b() {
      return this.a == null ? 0 : this.a.a();
   }

   private sd<T>.a c() {
      return this.a == null ? null : this.a.b();
   }

   private sd<T>.a d() {
      return this.a == null ? null : this.a.c();
   }

   private sd<T>.a c(T var1) {
      sd.a var2 = null;
      sd.a var3 = this.a;

      while (var3 != null) {
         if (var3.a.compareTo((T)var1) < 0) {
            if (var3.c == null) {
               return var2;
            }

            var3 = var3.c;
         } else {
            var2 = var3;
            if (var3.b == null) {
               return var2;
            }

            var3 = var3.b;
         }
      }

      return null;
   }

   private sd<T>.a d(T var1) {
      sd.a var2 = null;
      sd.a var3 = this.a;

      while (var3 != null) {
         if (var3.a.compareTo((T)var1) > 0) {
            if (var3.b == null) {
               return var2;
            }

            var3 = var3.b;
         } else {
            var2 = var3;
            if (var3.c == null) {
               return var2;
            }

            var3 = var3.c;
         }
      }

      return null;
   }

   // $VF: synthetic method
   static sd.a a(sd var0, sd.a var1) {
      return var0.a = null;
   }

   public class a {
      T a;
      sd<T>.a b;
      sd<T>.a c;
      sd<T>.a d;
      int e;

      a(T var2, sd<T>.a var3) {
         this.a = (T)var2;
         this.b = null;
         this.c = null;
         this.d = var3;
         this.e = sd.b.BALANCED$7e36164e;
      }

      private T h() {
         return this.a;
      }

      final int a() {
         return 1 + (this.b == null ? 0 : this.b.a()) + (this.c == null ? 0 : this.c.a());
      }

      final sd<T>.a b() {
         sd.a var1 = this;

         while (var1.b != null) {
            var1 = var1.b;
         }

         return var1;
      }

      final sd<T>.a c() {
         sd.a var1 = this;

         while (var1.c != null) {
            var1 = var1.c;
         }

         return var1;
      }

      private sd<T>.a i() {
         sd.a var1;
         if (this.b != null && (var1 = this.b.c()) != null) {
            return var1;
         }

         for (sd.a var2 = this; var2.d != null; var2 = var2.d) {
            if (var2 != var2.d.b) {
               return var2.d;
            }
         }

         return null;
      }

      public final sd<T>.a d() {
         sd.a var1;
         if (this.c != null && (var1 = this.c.b()) != null) {
            return var1;
         }

         for (sd.a var2 = this; var2.d != null; var2 = var2.d) {
            if (var2 != var2.d.c) {
               return var2.d;
            }
         }

         return null;
      }

      final boolean a(T var1) {
         if (var1.compareTo(this.a) < 0) {
            if (this.b == null) {
               this.b = sd.this.new a((T)var1, this);
               return this.j();
            } else {
               return this.b.a((T)var1) ? this.j() : false;
            }
         } else if (this.c == null) {
            this.c = sd.this.new a((T)var1, this);
            return this.k();
         } else {
            return this.c.a((T)var1) ? this.k() : false;
         }
      }

      public final void e() {
         if (this.d == null && this.b == null && this.c == null) {
            this.a = null;
            Object var5 = null;
            var5 = sd.this;
            sd.this.a = null;
         } else {
            sd.a var1;
            boolean var3;
            sd.a var10000;
            if (this.b == null && this.c == null) {
               var1 = this;
               this.a = null;
               var3 = var1 == var1.d.b;
               var10000 = null;
            } else {
               var1 = this.b != null ? this.b.c() : this.c.b();
               this.a = var1.a;
               var3 = var1 == var1.d.b;
               var10000 = var1.b != null ? var1.b : var1.c;
            }

            sd.a var2 = var10000;
            var1 = var1.d;
            if (var3) {
               var1.b = var2;
            } else {
               var1.c = var2;
            }

            if (var2 != null) {
               var2.d = var1;
            }

            while (var3 ? var1.l() : var1.m()) {
               if (var1.d == null) {
                  return;
               }

               var3 = var1 == var1.d.b;
               var1 = var1.d;
            }
         }
      }

      // $VF: Unable to simplify switch on enum
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      private boolean j() {
         switch (<unrepresentable>.a[this.e - 1]) {
            case 1:
               if (this.b.e == sd.b.LEFT_HIGH$7e36164e) {
                  this.f();
                  this.e = sd.b.BALANCED$7e36164e;
                  this.c.e = sd.b.BALANCED$7e36164e;
               } else {
                  int var1 = this.b.c.e;
                  this.b.g();
                  this.f();
                  switch (<unrepresentable>.a[var1 - 1]) {
                     case 1:
                        this.b.e = sd.b.BALANCED$7e36164e;
                        this.c.e = sd.b.RIGHT_HIGH$7e36164e;
                        break;
                     case 2:
                        this.b.e = sd.b.LEFT_HIGH$7e36164e;
                        this.c.e = sd.b.BALANCED$7e36164e;
                        break;
                     default:
                        this.b.e = sd.b.BALANCED$7e36164e;
                        this.c.e = sd.b.BALANCED$7e36164e;
                  }

                  this.e = sd.b.BALANCED$7e36164e;
               }

               return false;
            case 2:
               this.e = sd.b.BALANCED$7e36164e;
               return false;
            default:
               this.e = sd.b.LEFT_HIGH$7e36164e;
               return true;
         }
      }

      // $VF: Unable to simplify switch on enum
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      private boolean k() {
         switch (<unrepresentable>.a[this.e - 1]) {
            case 1:
               this.e = sd.b.BALANCED$7e36164e;
               return false;
            case 2:
               if (this.c.e == sd.b.RIGHT_HIGH$7e36164e) {
                  this.g();
                  this.e = sd.b.BALANCED$7e36164e;
                  this.b.e = sd.b.BALANCED$7e36164e;
               } else {
                  int var1 = this.c.b.e;
                  this.c.f();
                  this.g();
                  switch (<unrepresentable>.a[var1 - 1]) {
                     case 1:
                        this.b.e = sd.b.BALANCED$7e36164e;
                        this.c.e = sd.b.RIGHT_HIGH$7e36164e;
                        break;
                     case 2:
                        this.b.e = sd.b.LEFT_HIGH$7e36164e;
                        this.c.e = sd.b.BALANCED$7e36164e;
                        break;
                     default:
                        this.b.e = sd.b.BALANCED$7e36164e;
                        this.c.e = sd.b.BALANCED$7e36164e;
                  }

                  this.e = sd.b.BALANCED$7e36164e;
               }

               return false;
            default:
               this.e = sd.b.RIGHT_HIGH$7e36164e;
               return true;
         }
      }

      // $VF: Unable to simplify switch on enum
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      private boolean l() {
         switch (<unrepresentable>.a[this.e - 1]) {
            case 1:
               this.e = sd.b.BALANCED$7e36164e;
               return true;
            case 2:
               if (this.c.e == sd.b.RIGHT_HIGH$7e36164e) {
                  this.g();
                  this.e = sd.b.BALANCED$7e36164e;
                  this.b.e = sd.b.BALANCED$7e36164e;
                  return true;
               } else {
                  if (this.c.e == sd.b.BALANCED$7e36164e) {
                     this.g();
                     this.e = sd.b.LEFT_HIGH$7e36164e;
                     this.b.e = sd.b.RIGHT_HIGH$7e36164e;
                     return false;
                  }

                  int var1 = this.c.b.e;
                  this.c.f();
                  this.g();
                  switch (<unrepresentable>.a[var1 - 1]) {
                     case 1:
                        this.b.e = sd.b.BALANCED$7e36164e;
                        this.c.e = sd.b.RIGHT_HIGH$7e36164e;
                        break;
                     case 2:
                        this.b.e = sd.b.LEFT_HIGH$7e36164e;
                        this.c.e = sd.b.BALANCED$7e36164e;
                        break;
                     default:
                        this.b.e = sd.b.BALANCED$7e36164e;
                        this.c.e = sd.b.BALANCED$7e36164e;
                  }

                  this.e = sd.b.BALANCED$7e36164e;
                  return true;
               }
            default:
               this.e = sd.b.RIGHT_HIGH$7e36164e;
               return false;
         }
      }

      // $VF: Unable to simplify switch on enum
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      private boolean m() {
         switch (<unrepresentable>.a[this.e - 1]) {
            case 1:
               if (this.b.e == sd.b.LEFT_HIGH$7e36164e) {
                  this.f();
                  this.e = sd.b.BALANCED$7e36164e;
                  this.c.e = sd.b.BALANCED$7e36164e;
                  return true;
               } else {
                  if (this.b.e == sd.b.BALANCED$7e36164e) {
                     this.f();
                     this.e = sd.b.RIGHT_HIGH$7e36164e;
                     this.c.e = sd.b.LEFT_HIGH$7e36164e;
                     return false;
                  }

                  int var1 = this.b.c.e;
                  this.b.g();
                  this.f();
                  switch (<unrepresentable>.a[var1 - 1]) {
                     case 1:
                        this.b.e = sd.b.BALANCED$7e36164e;
                        this.c.e = sd.b.RIGHT_HIGH$7e36164e;
                        break;
                     case 2:
                        this.b.e = sd.b.LEFT_HIGH$7e36164e;
                        this.c.e = sd.b.BALANCED$7e36164e;
                        break;
                     default:
                        this.b.e = sd.b.BALANCED$7e36164e;
                        this.c.e = sd.b.BALANCED$7e36164e;
                  }

                  this.e = sd.b.BALANCED$7e36164e;
                  return true;
               }
            case 2:
               this.e = sd.b.BALANCED$7e36164e;
               return true;
            default:
               this.e = sd.b.LEFT_HIGH$7e36164e;
               return false;
         }
      }

      void f() {
         Comparable var1 = this.a;
         this.a = this.b.a;
         this.b.a = (T)var1;
         sd.a var2 = this.b;
         this.b = var2.b;
         var2.b = var2.c;
         var2.c = this.c;
         this.c = var2;
         if (this.b != null) {
            this.b.d = this;
         }

         if (this.c.c != null) {
            this.c.c.d = this.c;
         }
      }

      void g() {
         Comparable var1 = this.a;
         this.a = this.c.a;
         this.c.a = (T)var1;
         sd.a var2 = this.c;
         this.c = var2.c;
         var2.c = var2.b;
         var2.b = this.b;
         this.b = var2;
         if (this.c != null) {
            this.c.d = this;
         }

         if (this.b.b != null) {
            this.b.b.d = this.b;
         }
      }

      // $VF: synthetic method
      static Comparable a(sd.a var0) {
         return var0.a;
      }

      // $VF: synthetic method
      static sd.a b(sd.a var0) {
         return var0.c;
      }

      // $VF: synthetic method
      static sd.a c(sd.a var0) {
         return var0.b;
      }
   }

   enum b {
      public static final int LEFT_HIGH$7e36164e = 1;
      public static final int RIGHT_HIGH$7e36164e = 2;
      public static final int BALANCED$7e36164e = 3;
      // $VF: synthetic field
      private static final int[] $VALUES$313b9e6d = new int[]{LEFT_HIGH$7e36164e, RIGHT_HIGH$7e36164e, BALANCED$7e36164e};

      public static int[] values$2eacb26c() {
         return (int[])$VALUES$313b9e6d.clone();
      }

      private b(String var1, int var2) {
         super(var1, var2);
      }
   }
}
