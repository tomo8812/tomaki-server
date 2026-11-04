package com.archiangel.quadhotbar;

public record QuadHotbarPolicy(
   boolean allowFreedom,
   boolean overviewAllowed,
   boolean showOverviewButton,
   boolean containerOverviewAllowed,
   QuadHotbarServerConfig.OverviewLayout overviewLayout,
   int pageLimit,
   int rowLimit,
   int maxOverviewWidth,
   int maxOverviewHeight
) {
   public static final QuadHotbarPolicy UNRESTRICTED = new QuadHotbarPolicy(
      true, true, true, true, QuadHotbarServerConfig.OverviewLayout.CLIENT, 100, 8, 27, 50
   );

   public int maxInventoryPages() {
      return this.allowFreedom ? this.pageLimit : 1;
   }

   public int maxHotbarRows() {
      return Math.min(this.rowLimit, this.allowFreedom ? 8 : 4);
   }

   public boolean allowPageOverview() {
      return this.allowFreedom && this.overviewAllowed;
   }

   public boolean allowContainerOverview() {
      return this.allowPageOverview() && this.containerOverviewAllowed;
   }
}
