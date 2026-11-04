package org.valkyrienskies.core.impl.shadow;

enum vm {
   UNINITIALIZED {
      @Override
      protected final double a(double var1) {
         return 0.0;
      }
   },
   PLUS {
      @Override
      protected final double a(double var1) {
         return var1;
      }
   },
   MINUS {
      @Override
      protected final double a(double var1) {
         return -var1;
      }
   },
   MIN {
      @Override
      protected final double a(double var1) {
         return BY.d(-Cq.b, BY.d(-var1, var1));
      }
   },
   MAX {
      @Override
      protected final double a(double var1) {
         return BY.e(Cq.b, BY.e(-var1, var1));
      }
   };

   vm() {
   }

   protected abstract double a(double var1);

   // $VF: synthetic method
   vm(byte var3) {
      this();
   }
}
