package com.kishku7.chunksmith.platform;

import java.nio.file.Path;

public interface Config {
   long DISPATCH_MAX_CONCURRENT_DEFAULT = 200L;
   long LOD_INDEX_BUDGET_MB_DEFAULT = 2048L;

   Path getDirectory();

   int getVersion();

   String getLanguage();

   boolean getContinueOnRestart();

   boolean isForceLoadExistingChunks();

   boolean isSilent();

   void setSilent(boolean var1);

   int getUpdateInterval();

   void setUpdateInterval(int var1);

   boolean isIoThrottleEnabled();

   double getThrottleTargetMspt();

   long getThrottleMaxChunkMillis();

   long getThrottleMaxQueuedWrites();

   long getThrottleMaxAddedChunks();

   long getThrottleMaxHeapPercent();

   long getThrottleTickBudgetMillis();

   long getThrottlePlayerReserveMillis();

   long getThrottleCeilingMillis();

   boolean isAutoPauseEnabled();

   int getAutoPauseGraceSeconds();

   LodMode getLodMode();

   long getThrottleMaxLodQueue();

   long getThrottleLodDrainTo();

   long getDispatchMaxConcurrent();

   boolean isPregenSettleEnabled();

   long getPregenSettleDelayTicks();

   int getPregenSettleRadius();

   long getPregenSettleMaxHeld();

   void setPregenSettleEnabled(boolean var1);

   void setPregenSettleDelayTicks(long var1);

   void setPregenSettleRadius(int var1);

   void setPregenSettleMaxHeld(long var1);

   default boolean isPregenSettleSupported() {
      return true;
   }

   boolean isLodDhOverrideEnabled();

   boolean isLodFromExploredChunksEnabled();

   boolean isLodBackgroundSendEnabled();

   int getLodBackchannelPort();

   long getLodIndexBudgetMb();

   String getLodBackchannelBindAddress();

   String getLodBackchannelHost();

   boolean isWorldEnterPregenEnabled();

   boolean isWorldEnterPregenSupported();

   long getWorldEnterPregenRadius();

   long getWorldEnterLodHorizon();

   String getWorldEnterPregenCenter();

   String getWorldEnterPregenShape();

   String getWorldEnterPregenDimensions();

   void setLanguage(String var1);

   void setContinueOnRestart(boolean var1);

   void setForceLoadExistingChunks(boolean var1);

   void setIoThrottleEnabled(boolean var1);

   void setThrottleTargetMspt(double var1);

   void setThrottleMaxChunkMillis(long var1);

   void setThrottleMaxQueuedWrites(long var1);

   void setThrottleMaxAddedChunks(long var1);

   void setThrottleMaxHeapPercent(long var1);

   void setThrottleTickBudgetMillis(long var1);

   void setThrottlePlayerReserveMillis(long var1);

   void setThrottleCeilingMillis(long var1);

   void setAutoPauseEnabled(boolean var1);

   void setAutoPauseGraceSeconds(int var1);

   void setThrottleMaxLodQueue(long var1);

   void setThrottleLodDrainTo(long var1);

   void setDispatchMaxConcurrent(long var1);

   void setLodMode(LodMode var1);

   void setLodDhOverrideEnabled(boolean var1);

   void setLodFromExploredChunksEnabled(boolean var1);

   void setLodBackgroundSendEnabled(boolean var1);

   void setLodBackchannelPort(int var1);

   void setLodIndexBudgetMb(long var1);

   void setLodBackchannelBindAddress(String var1);

   void setLodBackchannelHost(String var1);

   void setWorldEnterPregenEnabled(boolean var1);

   default LodMode getWorldEnterLodMode() {
      return LodMode.AUTO;
   }

   default void setWorldEnterLodMode(LodMode mode) {
   }

   void setWorldEnterPregenRadius(long var1);

   void setWorldEnterLodHorizon(long var1);

   void setWorldEnterPregenCenter(String var1);

   void setWorldEnterPregenShape(String var1);

   void setWorldEnterPregenDimensions(String var1);

   void reload();
}
