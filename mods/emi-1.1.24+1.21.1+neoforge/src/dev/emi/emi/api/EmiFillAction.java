package dev.emi.emi.api;

public enum EmiFillAction {
   FILL(0),
   CURSOR(1),
   QUICK_MOVE(2);

   public final int id;

   EmiFillAction(int id) {
      this.id = id;
   }

   // $VF: synthetic method
   private static EmiFillAction[] $values() {
      return new EmiFillAction[]{FILL, CURSOR, QUICK_MOVE};
   }
}
