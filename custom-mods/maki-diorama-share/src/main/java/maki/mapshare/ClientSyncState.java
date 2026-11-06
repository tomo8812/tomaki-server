package maki.mapshare;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Separates the three client-side synchronization states:
 * server-acknowledged state, last-sent state, and current native Diorama state.
 */
final class ClientSyncState {
    private final Map<UUID, ShareState.Mark> sent = new LinkedHashMap<>();
    private final Map<UUID, ShareState.Mark> acknowledged = new LinkedHashMap<>();
    private long acknowledgedRevision;

    void clear() {
        sent.clear();
        acknowledged.clear();
        acknowledgedRevision = 0L;
    }

    Map<UUID, ShareState.Mark> sent() {
        return new LinkedHashMap<>(sent);
    }

    Map<UUID, ShareState.Mark> acknowledged() {
        return new LinkedHashMap<>(acknowledged);
    }

    long acknowledgedRevision() {
        return acknowledgedRevision;
    }

    void markSent(Map<UUID, ShareState.Mark> current) {
        sent.clear();
        sent.putAll(current);
    }

    void acceptSnapshot(Map<UUID, ShareState.Mark> serverOwn, long ackRevision) {
        if (ackRevision < acknowledgedRevision) {
            return;
        }
        acknowledged.clear();
        acknowledged.putAll(serverOwn);
        acknowledgedRevision = ackRevision;
    }

    boolean isAcknowledgedThrough(long revision) {
        return acknowledgedRevision >= revision;
    }

    Set<UUID> unsentChanges(Map<UUID, ShareState.Mark> current) {
        Set<UUID> ids = new HashSet<>(sent.keySet());
        ids.addAll(current.keySet());

        Set<UUID> changed = new HashSet<>();
        for (UUID id : ids) {
            if (!Objects.equals(sent.get(id), current.get(id))) {
                changed.add(id);
            }
        }
        return changed;
    }

    /**
     * Realign the transmitted baseline to the reconciled native store, except for
     * IDs that still represent unsent local intent. Those IDs must remain different
     * from the baseline so the next polling tick emits their put/delete operation.
     */
    void alignSent(Map<UUID, ShareState.Mark> reconciledLocal, Set<UUID> unsentLocalIds) {
        Set<UUID> ids = new HashSet<>(sent.keySet());
        ids.addAll(reconciledLocal.keySet());

        for (UUID id : ids) {
            if (unsentLocalIds.contains(id)) {
                continue;
            }
            ShareState.Mark mark = reconciledLocal.get(id);
            if (mark == null) {
                sent.remove(id);
            } else {
                sent.put(id, mark);
            }
        }
    }
}
