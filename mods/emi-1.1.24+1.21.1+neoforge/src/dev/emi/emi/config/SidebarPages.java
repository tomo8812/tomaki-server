package dev.emi.emi.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;

public class SidebarPages {
   public final SidebarSettings settings;
   public List<SidebarPages.SidebarPage> pages = Lists.newArrayList();

   public SidebarPages(List<SidebarPages.SidebarPage> pages, SidebarSettings settings) {
      this.pages.addAll(pages);
      this.settings = settings;
      this.unique();
   }

   public void unique() {
      Set<SidebarType> types = Sets.newHashSet();

      for (int i = 0; i < this.pages.size(); i++) {
         if (this.pages.get(i).type != SidebarType.NONE && !types.contains(this.pages.get(i).type)) {
            types.add(this.pages.get(i).type);
         } else {
            this.pages.remove(i--);
         }
      }

      if (this.settings.subpanels() != null) {
         List<SidebarSubpanels.Subpanel> subpanels = this.settings.subpanels().subpanels;

         for (int i = 0; i < subpanels.size(); i++) {
            if (subpanels.get(i).type != SidebarType.NONE && !types.contains(subpanels.get(i).type)) {
               types.add(subpanels.get(i).type);
            } else {
               subpanels.remove(i--);
            }
         }
      }
   }

   public boolean canShowChess() {
      return this.settings.size().values.getInt(0) == 8 && this.settings.size().values.getInt(1) == 8 && this.settings.theme() == SidebarTheme.MODERN;
   }

   public static class SidebarPage {
      public SidebarType type;

      public SidebarPage(SidebarType type) {
         this.type = type;
      }
   }
}
