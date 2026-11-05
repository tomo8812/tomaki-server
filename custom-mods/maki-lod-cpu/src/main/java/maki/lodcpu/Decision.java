package maki.lodcpu;

/** Pure decision policy, independent of Minecraft. Percentages use the allocated CPU budget. */
public final class Decision {
    public int cap = 2;
    private int healthy, overloaded;
    public String reason = "warmup";

    public int idle(Settings s) {
        healthy = overloaded = 0;
        cap = Math.max(s.min,Math.min(s.max,2));reason="generation-idle";
        return cap;
    }

    public int update(double cpu, double mspt, double heap, int players, Settings s) {
        cap = Math.max(s.min, Math.min(s.max, cap));
        if (players > 0) {
            healthy = overloaded = 0;
            boolean busy=!Double.isFinite(cpu)||!Double.isFinite(mspt)||cpu>=s.highCpu||mspt>=30||heap>=s.highHeap-5;
            cap=busy?1:Math.max(1,Math.min(s.onlineMax,s.max));
            reason=busy?"online-busy":"online-two-slots";
            if(!s.generateWhileOnline){cap=1;reason="players-online-paused";}
        } else if (!Double.isFinite(cpu) || cpu < 0 || !Double.isFinite(mspt)) {
            healthy = overloaded = 0; cap = s.min; reason = "measurement-unavailable";
        } else if (cpu >= s.emergencyCpu || mspt >= s.emergencyMspt || heap >= s.highHeap) {
            healthy = overloaded = 0; cap = Math.max(s.min, cap - 2); reason = "overload";
        } else if (cpu >= s.highCpu || mspt >= s.highMspt) {
            healthy = 0;
            if (++overloaded >= s.downSamples) {
                cap = Math.max(s.min, cap - 1); overloaded = 0;
            }
            reason = "busy";
        } else if (cpu < s.lowCpu && mspt < s.lowMspt && heap < s.highHeap - 5) {
            overloaded = 0;
            if (++healthy >= s.upSamples) {
                cap = Math.min(s.max, cap + 1); healthy = 0;
            }
            reason = "headroom";
        } else {
            healthy = overloaded = 0; reason = "steady";
        }
        return cap;
    }
}
