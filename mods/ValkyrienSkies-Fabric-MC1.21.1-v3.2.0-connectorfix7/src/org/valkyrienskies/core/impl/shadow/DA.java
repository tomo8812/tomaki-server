package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class DA {
   private static final int b = 2;
   private static final int c = 1073741824;
   private final Dy d;
   private DF e;
   public Map<DB, DG> a = new HashMap<>();
   private int f;
   private int g;

   public DA() {
      this.d = null;
      this.e = null;
   }

   private DA(Dy var1) {
      this.d = var1;
      this.e = null;
   }

   private DA(DF var1) {
      this.d = null;
      this.e = var1;
   }

   public DA(Dy var1, DF var2) {
      this.d = var1;
      this.e = var2;
   }

   private void a() {
      if (this.d == null) {
         throw new RuntimeException(
            "You may only call augmentation-related methods on ConnGraph if the graph is augmented, i.e. if an Augmentation was passed to the constructor"
         );
      }
   }

   private DG e(DB var1) {
      DG var2;
      if ((var2 = this.a.get(var1)) != null) {
         return var2;
      }

      if (this.a.size() == 1073741824) {
         throw new RuntimeException(
            "Sorry, ConnGraph has too many vertices to perform this operation. ConnGraph does not support storing more than ~2^30 vertices at a time."
         );
      }

      DE var4 = new DE();
      DD var3 = new DD(var4, this.d);
      var4.c = var3;
      var3.j = (N)DD.a;
      var3.k = (N)DD.a;
      var3.b();
      var2 = new DG(var4);
      this.a.put(var1, var2);
      if (this.a.size() > 1 << this.f) {
         this.f++;
      }

      this.g = Math.max(this.g, this.a.size());
      return var2;
   }

   private void f(DB var1) {
      this.a.remove(var1);
      if (4 * this.a.size() <= this.g && this.g > 12) {
         this.a = new HashMap<>(this.a);
         this.g = this.a.size();
      }

      if (this.a.size() << 2 <= 1 << this.f) {
         this.b();
      }
   }

   private static Dz a(Dz var0, Dz var1, DE var2, DE var3) {
      if (var1 == null) {
         return var0;
      }

      Dz var4 = null;
      Dz var5 = var1;

      while (var5 != null) {
         var4 = var5;
         if (var5.a == var3) {
            var5.a = var2;
            var5 = var5.e;
         } else {
            var5.b = var2;
            var5 = var5.g;
         }
      }

      if (var4.a == var2) {
         var4.e = var0;
      } else {
         var4.g = var0;
      }

      if (var0 != null) {
         if (var0.a == var2) {
            var0.d = var4;
         } else {
            var0.f = var4;
         }
      }

      return var1;
   }

   private static void a(DD var0) {
      var0 = var0;

      while (var0 != null && var0.b()) {
         var0 = (DD)var0.i;
      }
   }

   private static void b(DD var0) {
      var0 = var0;

      while (var0 != null && var0.a()) {
         var0 = (DD)var0.i;
      }
   }

   private void b() {
      if (this.a.isEmpty()) {
         this.f = 0;
      } else {
         int var1;
         for (var1 = 0; 2 * this.a.size() <= 1 << this.f; var1++) {
            this.f--;
         }

         if (var1 != 0) {
            Iterator var2 = this.a.values().iterator();

            while (var2.hasNext()) {
               DE var3;
               DE var4 = var3 = ((DG)var2.next()).a;

               for (int var5 = 0; var5 < var1 && (var4 = var4.a) != null; var5++) {
                  var3.d = a(var3.d, var4.d, var3, var4);
                  if (var4.e != null) {
                     Dz var6 = var4.e;

                     while (var6 != null) {
                        if (var6.a == var4) {
                           var6 = var6.e;
                        } else {
                           DC var7 = var6.c.c;

                           for (int var8 = 0; var8 < var5; var8++) {
                              var7 = var7.c;
                           }

                           var6.c = var7;
                           var6 = var6.g;
                        }
                     }

                     var3.e = a(var3.e, var4.e, var3, var4);
                  }
               }

               if (var4 != null) {
                  var4 = var4.a;
               }

               var3.a = var4;
               if (var4 != null) {
                  var4.b = var3;
               }

               b(var3.c);
            }
         }
      }
   }

   private static void a(Dz var0) {
      var0.d = null;
      var0.e = var0.a.d;
      if (var0.e != null) {
         if (var0.e.a == var0.a) {
            var0.e.d = var0;
         } else {
            var0.e.f = var0;
         }
      }

      var0.a.d = var0;
   }

   private static void b(Dz var0) {
      var0.f = null;
      var0.g = var0.b.d;
      if (var0.g != null) {
         if (var0.g.a == var0.b) {
            var0.g.d = var0;
         } else {
            var0.g.f = var0;
         }
      }

      var0.b.d = var0;
   }

   private void c(Dz var1) {
      a(var1);
      b(var1);
   }

   private static void d(Dz var0) {
      var0.d = null;
      var0.e = var0.a.e;
      if (var0.e != null) {
         if (var0.e.a == var0.a) {
            var0.e.d = var0;
         } else {
            var0.e.f = var0;
         }
      }

      var0.a.e = var0;
      var0.f = null;
      var0.g = var0.b.e;
      if (var0.g != null) {
         if (var0.g.a == var0.b) {
            var0.g.d = var0;
         } else {
            var0.g.f = var0;
         }
      }

      var0.b.e = var0;
   }

   private static void e(Dz var0) {
      if (var0.d != null) {
         if (var0.d.a == var0.a) {
            var0.d.e = var0.e;
         } else {
            var0.d.g = var0.e;
         }
      } else if (var0 == var0.a.d) {
         var0.a.d = var0.e;
      } else {
         var0.a.e = var0.e;
      }

      if (var0.e != null) {
         if (var0.e.a == var0.a) {
            var0.e.d = var0.d;
            return;
         }

         var0.e.f = var0.d;
      }
   }

   private static void f(Dz var0) {
      if (var0.f != null) {
         if (var0.f.a == var0.b) {
            var0.f.e = var0.g;
         } else {
            var0.f.g = var0.g;
         }
      } else if (var0 == var0.b.d) {
         var0.b.d = var0.g;
      } else {
         var0.b.e = var0.g;
      }

      if (var0.g != null) {
         if (var0.g.a == var0.b) {
            var0.g.d = var0.f;
            return;
         }

         var0.g.f = var0.f;
      }
   }

   private void g(Dz var1) {
      e(var1);
      f(var1);
   }

   private DC a(DE var1, DE var2) {
      DD var3;
      DD var4;
      if ((var4 = (DD)(var3 = (DD)var2.c.e()).g()).b != var2) {
         var3 = (DD)var4.l();
         if (var4.b.c == var4) {
            DD var5 = (DD)var3.f();
            var4.b.c = var5;
            a(var5);
         }

         DD[] var11;
         var3 = (var11 = var3.a((DD)var2.c))[1].a(var11[0]);
         DD var7;
         (var7 = new DD(var2, var3.f)).j = (N)DD.a;
         var7.k = (N)DD.a;
         var7.l = true;
         DD var12;
         (var12 = (DD)var3.g()).k = (N)var7;
         var7.i = (N)var12;
         var3 = (DD)var7.j();
         var4 = var7;
      }

      DD[] var13;
      DD var8 = (var13 = var1.c.e().a((DD)var1.c))[0];
      DD var14 = var13[1];
      DD var6 = new DD(var1, var3.f);
      var8.a(var3, var6).a(var14);
      return new DC(var6, var4);
   }

   private void a(DC var1) {
      DD var2;
      DD var5;
      if (var1.a.b(var1.b) < 0) {
         var2 = var1.a;
         var5 = var1.b;
      } else {
         var2 = var1.b;
         var5 = var1.a;
      }

      DD var6 = (DD)var5.i();
      if (var2.b.c == var2) {
         DD var3 = var6;
         var2.b.c = var3;
         a(var2);
         a(var3);
      }

      DD[] var8;
      DD var4 = (var8 = var2.e().a((DD)var2))[0];
      DD[] var7 = var8[1].a((DD)var6);
      var4.a(var7[1]);
      var2.k();
   }

   private static void a(Dz var0, DG var1, DB var2) {
      var1.b.put(var2, var0);
      if (var1.b.size() > var1.c) {
         var1.c = var1.b.size();
      }
   }

   public final boolean a(DB var1, DB var2) {
      if (var1 == var2) {
         throw new IllegalArgumentException("Self-loops are not allowed");
      }

      if (this.a.size() >= 1073741823) {
         throw new RuntimeException(
            "Sorry, ConnGraph has too many vertices to perform this operation. ConnGraph does not support storing more than ~2^30 vertices at a time."
         );
      }

      DG var3;
      if ((var3 = this.e(var1)).b.containsKey(var2)) {
         return false;
      }

      DG var4 = this.e(var2);
      boolean var5 = false;
      DD var6 = (DD)var3.a.c.e();
      DD var7 = (DD)var4.a.c.e();
      DE var8 = var3.a;
      DE var9 = var4.a;
      if (this.e != null) {
         this.e.a(var1, var2, var6, var7);
      }

      Dz var10 = new Dz(var8, var9);
      if (var8.c.e() == var9.c.e()) {
         this.c(var10);
         b(var8.c);
         b(var9.c);
      } else {
         d(var10);
         b(var8.c);
         b(var9.c);
         var10.c = this.a(var8, var9);
         var5 = true;
      }

      a(var10, var3, var2);
      a(var10, var4, var1);
      if (var5 && this.e != null) {
         this.e.a(var1, var2, var6, var7, (DD)var3.a.c.e());
      }

      return true;
   }

   private static DE a(DE var0) {
      DE var1 = var0.a;
      if (var0.a == null) {
         var1 = new DE();
         DD var2 = new DD(var1, null);
         var1.c = var2;
         var0.a = var1;
         var1.b = var0;
         var2.j = (N)DD.a;
         var2.k = (N)DD.a;
         var2.b();
      }

      return var1;
   }

   private void c(DD var1) {
      if (var1.e && var1.c != 1) {
         var1 = var1;

         while (((DD)var1.j).e) {
            var1 = (DD)var1.j;
         }

         while (var1 != null) {
            DE var2 = var1.b;
            Dz var3 = var1.b.e;
            if (var1.b.e != null) {
               DE var4 = a(var2);
               Dz var5 = null;

               while (var3 != null) {
                  if (var3.b != var2 && var3.b != var4) {
                     var3.a = var4;
                     var3.b = a(var3.b);
                     DC var7;
                     (var7 = this.a(var3.a, var3.b)).c = var3.c;
                     var3.c = var7;
                     var5 = var3;
                     var3 = var3.e;
                  } else {
                     var5 = var3;
                     var3 = var3.g;
                  }
               }

               if (var5.a == var4) {
                  var5.e = var4.e;
               } else {
                  var5.g = var4.e;
               }

               if (var4.e != null) {
                  if (var4.e.a == var4) {
                     var4.e.d = var5;
                  } else {
                     var4.e.f = var5;
                  }
               }

               var4.e = var2.e;
               var2.e = null;
               b(var4.c);
            }

            if (((DD)var1.k).e) {
               var1 = (DD)var1.k;

               while (((DD)var1.j).e) {
                  var1 = (DD)var1.j;
               }
            } else {
               while (true) {
                  var1.e = false;
                  if (var1.i == null || ((DD)var1.i).k != var1) {
                     var1 = (DD)var1.i;
                     break;
                  }

                  var1 = (DD)var1.i;
               }
            }
         }
      }
   }

   private Dz d(DD var1) {
      if (!var1.d) {
         return null;
      }

      DD var2 = var1;

      while (((DD)var2.j).d) {
         var2 = (DD)var2.j;
      }

      while (var2 != null) {
         DE var3 = var2.b;
         Dz var4 = var2.b.d;
         if (var2.b.d != null) {
            Dz var5 = null;
            Dz var6 = null;

            while (var4 != null) {
               DE var7;
               Dz var8;
               if (var4.a == var3) {
                  var7 = var4.b;
                  var8 = var4.e;
               } else {
                  var7 = var4.a;
                  var8 = var4.g;
               }

               if (var7.c.e() != var1) {
                  var5 = var4;
                  break;
               }

               if (var4.a == var7) {
                  e(var4);
               } else {
                  f(var4);
               }

               b(var7.c);
               var4.a = a(var4.a);
               var4.b = a(var4.b);
               if (var4.a != var3.a) {
                  a(var4);
               } else {
                  b(var4);
               }

               b(var7.a.c);
               var6 = var4;
               var4 = var8;
            }

            if (var6 != null) {
               DE var9 = var3.a;
               if (var6.a == var9) {
                  var6.e = var9.d;
               } else {
                  var6.g = var9.d;
               }

               if (var9.d != null) {
                  if (var9.d.a == var9) {
                     var9.d.d = var6;
                  } else {
                     var9.d.f = var6;
                  }
               }

               var9.d = var3.d;
               b(var9.c);
            }

            var3.d = var4;
            if (var4 == null) {
               b(var3.c);
            } else if (var4.a == var3) {
               var4.d = null;
            } else {
               var4.f = null;
            }

            if (var5 != null) {
               return var5;
            }
         }

         if (((DD)var2.k).d) {
            var2 = (DD)var2.k;

            while (((DD)var2.j).d) {
               var2 = (DD)var2.j;
            }
         } else {
            while (var2.i != null && (((DD)var2.i).k == var2 || !((DD)var2.i).d)) {
               var2 = (DD)var2.i;
            }

            var2 = (DD)var2.i;
         }
      }

      return null;
   }

   private static Dz a(DG var0, DB var1) {
      Dz var2;
      if ((var2 = var0.b.remove(var1)) != null && 4 * var0.b.size() <= var0.c && var0.c > 6) {
         var0.b = new HashMap<>(var0.b);
         var0.c = var0.b.size();
      }

      return var2;
   }

   public final boolean b(DB var1, DB var2) {
      if (var1 == var2) {
         throw new IllegalArgumentException("Self-loops are not allowed");
      }

      DG var3;
      if ((var3 = this.a.get(var1)) == null) {
         return false;
      }

      DD var4 = (DD)var3.a.c.e();
      if (this.e != null) {
         this.e.a(var1, var2, var4);
      }

      Dz var5;
      if ((var5 = a(var3, var2)) == null) {
         return false;
      }

      DG var6;
      a(var6 = this.a.get(var2), var1);
      this.g(var5);
      b(var5.a.c);
      b(var5.b.c);
      if (var5.c != null) {
         for (DC var7 = var5.c; var7 != null; var7 = var7.c) {
            DC var13 = var7;
            DD var14;
            DD var15;
            if (var13.a.b(var13.b) < 0) {
               var14 = var13.a;
               var15 = var13.b;
            } else {
               var14 = var13.b;
               var15 = var13.a;
            }

            DD var16 = (DD)var15.i();
            if (var14.b.c == var14) {
               DD var17 = var16;
               var14.b.c = var17;
               a(var14);
               a(var17);
            }

            DD[] var18;
            DD var19 = (var18 = var14.e().a((DD)var14))[0];
            DD[] var11 = var18[1].a((DD)var16);
            var19.a(var11[1]);
            var14.k();
         }

         var5.c = null;
         Dz var22 = null;
         DE var8 = var5.a;

         for (DE var21 = var5.b; var8 != null; var21 = var21.b) {
            DD var9 = (DD)var8.c.e();
            DD var10 = (DD)var21.c.e();
            if (var9.d && var10.d) {
               DD var25;
               if (var9.c < var10.c) {
                  var25 = var9;
               } else {
                  var25 = var10;
               }

               DD var28 = var25;
               DA var12 = this;
               if (var28.e && var28.c != 1) {
                  DD var30 = var28;

                  while (((DD)var30.j).e) {
                     var30 = (DD)var30.j;
                  }

                  while (var30 != null) {
                     DE var32 = var30.b;
                     Dz var34 = var30.b.e;
                     if (var30.b.e != null) {
                        DE var36 = a(var32);
                        Dz var38 = null;

                        while (var34 != null) {
                           if (var34.b != var32 && var34.b != var36) {
                              var34.a = var36;
                              var34.b = a(var34.b);
                              DC var40;
                              (var40 = var12.a(var34.a, var34.b)).c = var34.c;
                              var34.c = var40;
                              var38 = var34;
                              var34 = var34.e;
                           } else {
                              var38 = var34;
                              var34 = var34.g;
                           }
                        }

                        if (var38.a == var36) {
                           var38.e = var36.e;
                        } else {
                           var38.g = var36.e;
                        }

                        if (var36.e != null) {
                           if (var36.e.a == var36) {
                              var36.e.d = var38;
                           } else {
                              var36.e.f = var38;
                           }
                        }

                        var36.e = var32.e;
                        var32.e = null;
                        b(var36.c);
                     }

                     if (((DD)var30.k).e) {
                        var30 = (DD)var30.k;

                        while (((DD)var30.j).e) {
                           var30 = (DD)var30.j;
                        }
                     } else {
                        while (true) {
                           var30.e = false;
                           if (var30.i == null || ((DD)var30.i).k != var30) {
                              var30 = (DD)var30.i;
                              break;
                           }

                           var30 = (DD)var30.i;
                        }
                     }
                  }
               }

               Dz var10000;
               label234: {
                  var28 = var25;
                  if (var28.d) {
                     DD var31 = var28;

                     while (((DD)var31.j).d) {
                        var31 = (DD)var31.j;
                     }

                     while (var31 != null) {
                        DE var33 = var31.b;
                        Dz var35 = var31.b.d;
                        if (var31.b.d != null) {
                           Dz var37 = null;
                           Dz var39 = null;

                           while (var35 != null) {
                              Dz var26;
                              DE var41;
                              if (var35.a == var33) {
                                 var41 = var35.b;
                                 var26 = var35.e;
                              } else {
                                 var41 = var35.a;
                                 var26 = var35.g;
                              }

                              if (var41.c.e() != var28) {
                                 var37 = var35;
                                 break;
                              }

                              if (var35.a == var41) {
                                 e(var35);
                              } else {
                                 f(var35);
                              }

                              b(var41.c);
                              var35.a = a(var35.a);
                              var35.b = a(var35.b);
                              if (var35.a != var33.a) {
                                 a(var35);
                              } else {
                                 b(var35);
                              }

                              b(var41.a.c);
                              var39 = var35;
                              var35 = var26;
                           }

                           if (var39 != null) {
                              DE var42 = var33.a;
                              if (var39.a == var42) {
                                 var39.e = var42.d;
                              } else {
                                 var39.g = var42.d;
                              }

                              if (var42.d != null) {
                                 if (var42.d.a == var42) {
                                    var42.d.d = var39;
                                 } else {
                                    var42.d.f = var39;
                                 }
                              }

                              var42.d = var33.d;
                              b(var42.c);
                           }

                           var33.d = var35;
                           if (var35 == null) {
                              b(var33.c);
                           } else if (var35.a == var33) {
                              var35.d = null;
                           } else {
                              var35.f = null;
                           }

                           if (var37 != null) {
                              var10000 = var37;
                              break label234;
                           }
                        }

                        if (((DD)var31.k).d) {
                           var31 = (DD)var31.k;

                           while (((DD)var31.j).d) {
                              var31 = (DD)var31.j;
                           }
                        } else {
                           while (var31.i != null && (((DD)var31.i).k == var31 || !((DD)var31.i).d)) {
                              var31 = (DD)var31.i;
                           }

                           var31 = (DD)var31.i;
                        }
                     }
                  }

                  var10000 = null;
               }

               var22 = var10000;
               if (var10000 != null) {
                  break;
               }
            }

            if (var9.c == 1 && var8.b != null) {
               var8.b.a = null;
            }

            if (var10.c == 1 && var21.b != null) {
               var21.b.a = null;
            }

            var8 = var8.b;
         }

         if (var22 != null) {
            this.g(var22);
            d(var22);
            DE var23 = var22.a;
            DE var24 = var22.b;
            b(var23.c);
            b(var24.c);
            DC var27 = null;

            while (var23 != null) {
               DC var20 = this.a(var23, var24);
               if (var27 == null) {
                  var22.c = var20;
               } else {
                  var27.c = var20;
               }

               var27 = var20;
               var23 = var23.b;
               var24 = var24.b;
            }
         } else if (this.e != null && var4 != null) {
            this.e.b(var1, var2, (DD)var3.a.c.e(), (DD)var6.a.c.e(), var4);
         }
      }

      if (var3.b.isEmpty() && !var3.a.g) {
         this.f(var1);
      }

      if (var6.b.isEmpty() && !var6.a.g) {
         this.f(var2);
      }

      return true;
   }

   public final boolean c(DB var1, DB var2) {
      if (var1 == var2) {
         return true;
      }

      DG var3;
      DG var4;
      return (var3 = this.a.get(var1)) == null ? false : (var4 = this.a.get(var2)) != null && var3.a.c.e() == var4.a.c.e();
   }

   public final Collection<DB> a(DB var1) {
      DG var2;
      return (var2 = this.a.get(var1)) != null ? new ArrayList<>(var2.b.keySet()) : Collections.emptyList();
   }

   public final Object a(DB var1, Object var2) {
      this.a();
      DE var4;
      Object var3 = (var4 = this.e(var1).a).f;
      if (!var4.g || (var2 != null ? !var2.equals(var3) : var3 != null)) {
         var4.f = var2;
         var4.g = true;
         DD var5 = var4.c;

         while (var5 != null && var5.b()) {
            var5 = (DD)var5.i;
         }
      }

      return var3;
   }

   public final Object b(DB var1) {
      this.a();
      DG var2;
      if ((var2 = this.a.get(var1)) == null) {
         return null;
      }

      DE var3 = var2.a;
      Object var4 = var2.a.f;
      if (var2.b.isEmpty()) {
         this.f(var1);
      } else if (var3.g) {
         var3.f = null;
         var3.g = false;
         DD var5 = var3.c;

         while (var5 != null && var5.b()) {
            var5 = (DD)var5.i;
         }
      }

      return var4;
   }

   public final Object c(DB var1) {
      this.a();
      DG var2;
      return (var2 = this.a.get(var1)) != null ? var2.a.f : null;
   }

   public final Object d(DB var1) {
      this.a();
      DG var2;
      return (var2 = this.a.get(var1)) != null ? ((DD)var2.a.c.e()).g : null;
   }

   private boolean g(DB var1) {
      this.a();
      DG var2;
      return (var2 = this.a.get(var1)) != null ? var2.a.g : false;
   }

   private boolean h(DB var1) {
      this.a();
      DG var2;
      return (var2 = this.a.get(var1)) != null ? ((DD)var2.a.c.e()).h : false;
   }

   private void c() {
      this.a = new HashMap<>();
      this.f = 0;
      this.g = 0;
   }

   private void d() {
      for (DG var2 : this.a.values()) {
         int var3 = this.f;

         for (var11 = var2.a; var11.a != null; var11 = var11.a) {
            var3--;
         }

         while (var11 != null) {
            DD var4 = var11.c;
            Dz var5 = var11.e;

            while (var5 != null) {
               if (var11 == var5.b) {
                  var5 = var5.g;
               } else {
                  Dz var6 = var5.e;
                  DE var7 = var11;
                  DE var8 = var5.b;
                  int var9 = var3 - 1;

                  while (true) {
                     if (var9 > 0) {
                        int var10 = 1;
                        if (var7.a != null) {
                           var10 = 1 + ((DD)var7.a.c.e()).c;
                        } else {
                           var10++;
                        }

                        if (var8.a != null) {
                           var10 += ((DD)var8.a.c.e()).c;
                        } else {
                           var10++;
                        }

                        if (var10 <= 2 * (1 << var9) - 1) {
                           var7 = a(var7);
                           var8 = a(var8);
                           DC var14;
                           (var14 = this.a(var7, var8)).c = var5.c;
                           var5.c = var14;
                           var9--;
                           continue;
                        }
                     }

                     if (var7 != var11) {
                        this.g(var5);
                        b(var4);
                        b(var5.b.c);
                        var5.a = var7;
                        var5.b = var8;
                        d(var5);
                        b(var7.c);
                        b(var8.c);
                     }

                     var5 = var6;
                     break;
                  }
               }
            }

            var11 = var11.b;
            var3++;
         }
      }
   }

   private void e() {
      Iterator var1 = this.a.values().iterator();

      while (var1.hasNext()) {
         DE var2 = ((DG)var1.next()).a;

         while (var2.a != null) {
            var2 = var2.a;
         }

         for (; var2 != null; var2 = var2.b) {
            DD var3 = var2.c;
            Dz var4 = var2.d;

            while (var4 != null) {
               if (var2 == var4.b) {
                  var4 = var4.g;
               } else {
                  Dz var5 = var4.e;
                  int var6 = 0;
                  DE var7 = var2.a;

                  for (DE var8 = var4.b.a; var7 != null && var8 != null; var8 = var8.a) {
                     var6++;
                     var7 = var7.a;
                  }

                  DE var9 = var2;
                  DE var10 = var4.b;

                  while (var6 > 0) {
                     int var11 = (var6 + 1) / 2;
                     var7 = var9;
                     DE var14 = var10;

                     for (int var12 = 0; var12 < var11; var12++) {
                        var7 = var7.a;
                        var14 = var14.a;
                     }

                     if (var7.c.e() != var14.c.e()) {
                        var6 = var11 - 1;
                     } else {
                        var9 = var7;
                        var10 = var14;
                        var6 -= var11;
                     }
                  }

                  if (var9 != var2) {
                     this.g(var4);
                     b(var3);
                     b(var4.b.c);
                     var4.a = var9;
                     var4.b = var10;
                     this.c(var4);
                     b(var9.c);
                     b(var10.c);
                  }

                  var4 = var5;
               }
            }
         }
      }
   }

   private void f() {
      this.b();
      DA var1 = this;

      for (DG var3 : this.a.values()) {
         int var4 = var1.f;

         for (var16 = var3.a; var16.a != null; var16 = var16.a) {
            var4--;
         }

         while (var16 != null) {
            DD var5 = var16.c;
            Dz var6 = var16.e;

            while (var6 != null) {
               if (var16 == var6.b) {
                  var6 = var6.g;
               } else {
                  Dz var7 = var6.e;
                  DE var8 = var16;
                  DE var9 = var6.b;
                  int var10 = var4 - 1;

                  while (true) {
                     if (var10 > 0) {
                        int var11 = 1;
                        if (var8.a != null) {
                           var11 = 1 + ((DD)var8.a.c.e()).c;
                        } else {
                           var11++;
                        }

                        if (var9.a != null) {
                           var11 += ((DD)var9.a.c.e()).c;
                        } else {
                           var11++;
                        }

                        if (var11 <= 2 * (1 << var10) - 1) {
                           var8 = a(var8);
                           var9 = a(var9);
                           DC var12;
                           (var12 = var1.a(var8, var9)).c = var6.c;
                           var6.c = var12;
                           var10--;
                           continue;
                        }
                     }

                     if (var8 != var16) {
                        var1.g(var6);
                        b(var5);
                        b(var6.b.c);
                        var6.a = var8;
                        var6.b = var9;
                        d(var6);
                        b(var8.c);
                        b(var9.c);
                     }

                     var6 = var7;
                     break;
                  }
               }
            }

            var16 = var16.b;
            var4++;
         }
      }

      var1 = this;
      Iterator var15 = this.a.values().iterator();

      while (var15.hasNext()) {
         DE var18 = ((DG)var15.next()).a;

         while (var18.a != null) {
            var18 = var18.a;
         }

         for (; var18 != null; var18 = var18.b) {
            DD var17 = var18.c;
            Dz var19 = var18.d;

            while (var19 != null) {
               if (var18 == var19.b) {
                  var19 = var19.g;
               } else {
                  Dz var20 = var19.e;
                  int var21 = 0;
                  DE var22 = var18.a;

                  for (DE var24 = var19.b.a; var22 != null && var24 != null; var24 = var24.a) {
                     var21++;
                     var22 = var22.a;
                  }

                  DE var26 = var18;
                  DE var29 = var19.b;

                  while (var21 > 0) {
                     int var30 = (var21 + 1) / 2;
                     var22 = var26;
                     DE var25 = var29;

                     for (int var13 = 0; var13 < var30; var13++) {
                        var22 = var22.a;
                        var25 = var25.a;
                     }

                     if (var22.c.e() != var25.c.e()) {
                        var21 = var30 - 1;
                     } else {
                        var26 = var22;
                        var29 = var25;
                        var21 -= var30;
                     }
                  }

                  if (var26 != var18) {
                     var1.g(var19);
                     b(var17);
                     b(var19.b.c);
                     var19.a = var26;
                     var19.b = var29;
                     var1.c(var19);
                     b(var26.c);
                     b(var29.c);
                  }

                  var19 = var20;
               }
            }
         }
      }
   }
}
