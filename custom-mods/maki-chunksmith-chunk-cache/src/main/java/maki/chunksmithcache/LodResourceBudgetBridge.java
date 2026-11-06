package maki.chunksmithcache;

import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Fail-closed bridge to the required maki-lod-cpu shared resource budget API. */
final class LodResourceBudgetBridge {
    private static final Logger LOG=LoggerFactory.getLogger("MakiChunkCache");
    private static Method reserve;
    private static Method held;
    private static Method started;
    private static Method release;
    private static Method status;
    private static boolean initialized;
    private static boolean warned;

    private LodResourceBudgetBridge() {}

    static String reserve() {
        try {
            initialize();
            return (String)reserve.invoke(null);
        } catch (ReflectiveOperationException | LinkageError | RuntimeException error) {
            warn(error);
            return "budget-unavailable";
        }
    }

    static boolean reservationHeld() {
        try {
            initialize();
            return Boolean.TRUE.equals(held.invoke(null));
        } catch (ReflectiveOperationException | LinkageError | RuntimeException error) {
            warn(error);
            return false;
        }
    }

    static void started() {
        try {
            initialize();
            started.invoke(null);
        } catch (ReflectiveOperationException | LinkageError | RuntimeException error) {
            warn(error);
        }
    }

    static void release(String outcome) {
        try {
            initialize();
            release.invoke(null,outcome);
        } catch (ReflectiveOperationException | LinkageError | RuntimeException error) {
            warn(error);
        }
    }

    static String status() {
        try {
            initialize();
            return (String)status.invoke(null);
        } catch (ReflectiveOperationException | LinkageError | RuntimeException error) {
            warn(error);
            return "unavailable";
        }
    }

    private static synchronized void initialize() throws ReflectiveOperationException {
        if(initialized)return;
        Class<?> api=Class.forName("maki.lodcpu.ResourceBudgetApi",true,LodResourceBudgetBridge.class.getClassLoader());
        reserve=api.getMethod("reserveBackfill");
        held=api.getMethod("backfillReservationHeld");
        started=api.getMethod("backfillStarted");
        release=api.getMethod("releaseBackfill",String.class);
        status=api.getMethod("status");
        initialized=true;
    }

    private static void warn(Throwable error) {
        if(!warned) {
            warned=true;
            LOG.error("Shared follow/backfill resource budget is unavailable; new backfills will remain fail-closed",error);
        }
    }
}
