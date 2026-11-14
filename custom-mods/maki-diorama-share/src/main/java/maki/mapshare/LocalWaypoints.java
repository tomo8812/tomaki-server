package maki.mapshare;

import dev.diorama.waypoint.Waypoint;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

/** The native store's two partitions contain one logical collection for synchronization. */
final class LocalWaypoints {
    private final List<Waypoint> active;
    private final List<Waypoint> elsewhere;

    LocalWaypoints(List<Waypoint> active, List<Waypoint> elsewhere) {
        this.active = active;
        this.elsewhere = elsewhere;
    }

    List<Waypoint> all() {
        List<Waypoint> result = new ArrayList<>(active);
        result.addAll(elsewhere);
        return result;
    }

    Map<UUID, ShareState.Mark> own(Function<Waypoint, ShareState.Mark> draft) {
        Map<UUID, ShareState.Mark> result = new LinkedHashMap<>();
        for (Waypoint waypoint : all()) {
            if (waypoint.kind == Waypoint.Kind.MANUAL) result.put(waypoint.id, draft.apply(waypoint));
        }
        return result;
    }

    Waypoint findManual(UUID id) {
        return all().stream().filter(w -> w.kind == Waypoint.Kind.MANUAL && w.id.equals(id)).findFirst().orElse(null);
    }

    boolean removeMissing(Set<UUID> known, Set<UUID> serverIds, Set<UUID> unsentLocal) {
        boolean changed = false;
        for (List<Waypoint> partition : List.of(active, elsewhere)) {
            changed |= partition.removeIf(w -> w.kind == Waypoint.Kind.MANUAL
                    && known.contains(w.id) && !serverIds.contains(w.id) && !unsentLocal.contains(w.id));
        }
        return changed;
    }
}
