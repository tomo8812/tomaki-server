package dev.emi.emi.config;

import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;

public class Margins extends IntGroup {
   public Margins(int top, int right, int bottom, int left) {
      super("emi.sidebar.margins.", List.of("top", "right", "bottom", "left"), IntList.of(new int[]{top, right, bottom, left}));
   }

   public int top() {
      return this.values.getInt(0);
   }

   public int right() {
      return this.values.getInt(1);
   }

   public int bottom() {
      return this.values.getInt(2);
   }

   public int left() {
      return this.values.getInt(3);
   }
}
