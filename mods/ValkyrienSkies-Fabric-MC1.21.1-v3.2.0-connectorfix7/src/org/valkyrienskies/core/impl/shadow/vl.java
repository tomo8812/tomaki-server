package org.valkyrienskies.core.impl.shadow;

public enum vl {
   TRIGGER_ONLY_DECREASING_EVENTS {
      @Override
      protected final boolean a() {
         return false;
      }

      @Override
      protected final vm a(vm var1, double var2, boolean var4) {
         if (var4) {
            switch (var1) {
               case UNINITIALIZED:
                  if (var2 > 0.0) {
                     return vm.MAX;
                  } else {
                     if (var2 < 0.0) {
                        return vm.PLUS;
                     }

                     return vm.UNINITIALIZED;
                  }
               case PLUS:
                  if (var2 >= 0.0) {
                     return vm.MIN;
                  }

                  return var1;
               case MINUS:
                  if (var2 >= 0.0) {
                     return vm.MAX;
                  }

                  return var1;
               case MIN:
                  if (var2 <= 0.0) {
                     return vm.MINUS;
                  }

                  return var1;
               case MAX:
                  if (var2 <= 0.0) {
                     return vm.PLUS;
                  }

                  return var1;
               default:
                  throw new oY();
            }
         } else {
            switch (var1) {
               case UNINITIALIZED:
                  if (var2 > 0.0) {
                     return vm.MINUS;
                  } else {
                     if (var2 < 0.0) {
                        return vm.MIN;
                     }

                     return vm.UNINITIALIZED;
                  }
               case PLUS:
                  if (var2 <= 0.0) {
                     return vm.MAX;
                  }

                  return var1;
               case MINUS:
                  if (var2 <= 0.0) {
                     return vm.MIN;
                  }

                  return var1;
               case MIN:
                  if (var2 >= 0.0) {
                     return vm.PLUS;
                  }

                  return var1;
               case MAX:
                  if (var2 >= 0.0) {
                     return vm.MINUS;
                  }

                  return var1;
               default:
                  throw new oY();
            }
         }
      }
   },
   TRIGGER_ONLY_INCREASING_EVENTS {
      @Override
      protected final boolean a() {
         return true;
      }

      @Override
      protected final vm a(vm var1, double var2, boolean var4) {
         if (var4) {
            switch (var1) {
               case UNINITIALIZED:
                  if (var2 > 0.0) {
                     return vm.PLUS;
                  } else {
                     if (var2 < 0.0) {
                        return vm.MIN;
                     }

                     return vm.UNINITIALIZED;
                  }
               case PLUS:
                  if (var2 <= 0.0) {
                     return vm.MAX;
                  }

                  return var1;
               case MINUS:
                  if (var2 <= 0.0) {
                     return vm.MIN;
                  }

                  return var1;
               case MIN:
                  if (var2 >= 0.0) {
                     return vm.PLUS;
                  }

                  return var1;
               case MAX:
                  if (var2 >= 0.0) {
                     return vm.MINUS;
                  }

                  return var1;
               default:
                  throw new oY();
            }
         } else {
            switch (var1) {
               case UNINITIALIZED:
                  if (var2 > 0.0) {
                     return vm.MAX;
                  } else {
                     if (var2 < 0.0) {
                        return vm.MINUS;
                     }

                     return vm.UNINITIALIZED;
                  }
               case PLUS:
                  if (var2 >= 0.0) {
                     return vm.MIN;
                  }

                  return var1;
               case MINUS:
                  if (var2 >= 0.0) {
                     return vm.MAX;
                  }

                  return var1;
               case MIN:
                  if (var2 <= 0.0) {
                     return vm.MINUS;
                  }

                  return var1;
               case MAX:
                  if (var2 <= 0.0) {
                     return vm.PLUS;
                  }

                  return var1;
               default:
                  throw new oY();
            }
         }
      }
   };

   vl() {
   }

   protected abstract boolean a();

   protected abstract vm a(vm var1, double var2, boolean var4);

   // $VF: synthetic method
   vl(byte var3) {
      this();
   }
}
