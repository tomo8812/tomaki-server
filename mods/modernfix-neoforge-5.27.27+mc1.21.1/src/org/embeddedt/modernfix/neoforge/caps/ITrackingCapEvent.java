package org.embeddedt.modernfix.neoforge.caps;

import java.util.Set;
import net.neoforged.neoforge.capabilities.BaseCapability;

public interface ITrackingCapEvent {
   Set<BaseCapability<?, ?>> mfix$getTrackedCaps();
}
