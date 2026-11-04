package reloc.org.sat4j.pb.orders;

import reloc.org.sat4j.minisat.core.IOrder;
import reloc.org.sat4j.pb.constraints.pb.PBConstr;

public enum BumpStrategy {
   ALWAYS_ONE {
      @Override
      public void varBumpActivity(IOrder order, PBConstr constr, int i) {
         order.updateVar(constr.get(i));
      }
   },
   DEGREE {
      @Override
      public void varBumpActivity(IOrder order, PBConstr constr, int i) {
         double value = constr.getDegree().doubleValue();
         if (value >= 2.147483647E9) {
            value = 2.147483647E9;
         }

         order.updateVar(constr.get(i), value);
      }
   },
   COEFFICIENT {
      @Override
      public void varBumpActivity(IOrder order, PBConstr constr, int i) {
         double value = constr.getCoef(i).doubleValue();
         if (value >= 2.147483647E9) {
            value = 2.147483647E9;
         }

         order.updateVar(constr.get(i), value);
      }
   },
   RATIO_DC {
      @Override
      public void varBumpActivity(IOrder order, PBConstr constr, int i) {
         double degree = constr.getDegree().doubleValue();
         double coefficient = constr.getCoef(i).doubleValue();
         double value = 1.0;
         if (Double.isFinite(degree) || Double.isFinite(coefficient)) {
            value = degree / coefficient;
         }

         order.updateVar(constr.get(i), value);
      }
   },
   RATIO_CD {
      @Override
      public void varBumpActivity(IOrder order, PBConstr constr, int i) {
         double degree = constr.getDegree().doubleValue();
         double coefficient = constr.getCoef(i).doubleValue();
         double value = 1.0;
         if (Double.isFinite(degree) || Double.isFinite(coefficient)) {
            value = coefficient / degree;
         }

         order.updateVar(constr.get(i), value);
      }
   };

   BumpStrategy() {
   }

   public abstract void varBumpActivity(IOrder var1, PBConstr var2, int var3);
}
