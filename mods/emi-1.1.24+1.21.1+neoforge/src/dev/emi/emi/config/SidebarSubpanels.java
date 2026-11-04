package dev.emi.emi.config;

import com.google.common.collect.Lists;
import java.util.List;

public class SidebarSubpanels {
   public final SidebarSettings settings;
   public List<SidebarSubpanels.Subpanel> subpanels = Lists.newArrayList();

   public SidebarSubpanels(List<SidebarSubpanels.Subpanel> subpanels, SidebarSettings settings) {
      this.subpanels.addAll(subpanels);
      this.settings = settings;
      this.unique();
   }

   public void unique() {
      this.settings.pages().unique();
   }

   public static class Subpanel {
      public SidebarType type;
      public int rows;

      public Subpanel(SidebarType type, int rows) {
         this.type = type;
         this.rows = rows;
      }

      public int rows() {
         return Math.max(1, this.rows);
      }
   }
}
