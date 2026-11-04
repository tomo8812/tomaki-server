package org.valkyrienskies.core.impl.shadow;

final class rW<S extends qN> {
   private final rY<S> c;
   boolean a;
   boolean b;

   rW(rY<S> var1) {
      this.c = var1;
      this.a = false;
      this.b = false;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void a(rM<S> var1, sb<S> var2) {
      if (var1.a == null) {
         if ((Boolean)var1.e) {
            this.a = true;
            this.b = true;
         }
      } else {
         label89: {
            rV var3 = var1.a.c();
            sb.a var5 = var2.a(var3);
            switch (<unrepresentable>.a[var5.c() - 1]) {
               case 1:
                  if (var1.a.a(var2.c()).c() == sa.PLUS$7c70dd6b) {
                     if (!this.c.c(var1.c)) {
                        this.a = true;
                     }
                  } else if (!this.c.c(var1.c)) {
                     this.b = true;
                  }

                  if (!this.a || !this.b) {
                     this.a(var1.b, var2);
                     return;
                  }
                  break;
               case 2:
                  if (var1.a.a(var2.c()).c() == sa.PLUS$7c70dd6b) {
                     if (!this.c.c(var1.b)) {
                        this.a = true;
                     }
                  } else if (!this.c.c(var1.b)) {
                     this.b = true;
                  }

                  if (!this.a || !this.b) {
                     this.a(var1.c, var2);
                     return;
                  }
                  break;
               case 3:
                  this.a(var1.b, var5.a);
                  if (!this.a || !this.b) {
                     this.a(var1.c, var5.b);
                     return;
                  }
                  break;
               default:
                  if (!var1.a.c().a(var2.c())) {
                     label58: {
                        rM var9 = var1.b;
                        if (var1.b.a == null) {
                           var9 = var1.b;
                           if (!(Boolean)var1.b.e) {
                              break label58;
                           }
                        }

                        this.b = true;
                     }

                     rM var11 = var1.c;
                     if (var1.c.a == null) {
                        var11 = var1.c;
                        if (!(Boolean)var1.c.e) {
                           return;
                        }
                     }

                     this.a = true;
                  } else {
                     label85: {
                        rM var4 = var1.b;
                        if (var1.b.a == null) {
                           var4 = var1.b;
                           if (!(Boolean)var1.b.e) {
                              break label85;
                           }
                        }

                        this.a = true;
                     }

                     rM var7 = var1.c;
                     if (var1.c.a != null) {
                        break label89;
                     }

                     var7 = var1.c;
                     if ((Boolean)var1.c.e) {
                        break label89;
                     }
                  }
            }

            return;
         }

         this.b = true;
      }
   }

   public final boolean a() {
      return this.a;
   }

   public final boolean b() {
      return this.b;
   }
}
