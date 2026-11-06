package maki.lodcpu.onlinefix;

import net.neoforged.fml.common.Mod;
import org.slf4j.LoggerFactory;

@Mod("maki_lod_cpu_online_fix")
public final class MakiLodCpuOnlineFix {
   public MakiLodCpuOnlineFix() {
      LoggerFactory.getLogger("MakiLodCpuOnlineFix").info("Maki LOD CPU Online Fix initialized; online CPU policy compatibility is enabled.");
   }
}
