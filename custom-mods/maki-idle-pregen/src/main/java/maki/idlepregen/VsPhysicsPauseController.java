package maki.idlepregen;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.reflect.Method;

final class VsPhysicsPauseController {
    private static final Logger LOG = System.getLogger("MakiIdlePregen");

    private static Object ownedPipeline;
    private static Method getRunningMethod;
    private static Method setRunningMethod;
    private static boolean decisionMade;

    private VsPhysicsPauseController() {
    }

    static void beginSession() {
        if (ownedPipeline != null) {
            restoreOwnedPause();
        }
        resetSession();
    }

    static void tick(Object server) {
        if (decisionMade) {
            observeOwnedPause();
            return;
        }

        try {
            Method getPipeline = server.getClass().getMethod("getVsPipeline");
            Object pipeline = getPipeline.invoke(server);
            if (pipeline == null) {
                // VS creates its pipeline after the MinecraftServer instance exists.
                // Retry until the pipeline becomes available, then make one ownership
                // decision for this idle session.
                return;
            }

            getRunningMethod = pipeline.getClass().getMethod("getArePhysicsRunning");
            setRunningMethod = pipeline.getClass().getMethod("setArePhysicsRunning", boolean.class);
            decisionMade = true;

            if (!readRunning(pipeline)) {
                LOG.log(Level.INFO,
                        "Valkyrien Skies physics was already paused; idle pregen will not take ownership");
                return;
            }

            writeRunning(pipeline, false);
            if (readRunning(pipeline)) {
                LOG.log(Level.WARNING,
                        "Valkyrien Skies physics did not enter the paused state; continuing without ownership");
                return;
            }

            ownedPipeline = pipeline;
            LOG.log(Level.INFO, "Paused Valkyrien Skies physics for idle pregen");
        } catch (NoSuchMethodException ex) {
            decisionMade = true;
            LOG.log(Level.INFO,
                    "Valkyrien Skies physics pause API is unavailable; continuing without VS physics ownership");
        } catch (ReflectiveOperationException | RuntimeException ex) {
            decisionMade = true;
            LOG.log(Level.WARNING,
                    "Failed to pause Valkyrien Skies physics; continuing without VS physics ownership", ex);
        }
    }

    static boolean ownsPause() {
        return ownedPipeline != null;
    }

    static void leaveSession() {
        restoreOwnedPause();
        resetSession();
    }

    private static void observeOwnedPause() {
        if (ownedPipeline == null) {
            return;
        }

        try {
            if (readRunning(ownedPipeline)) {
                // Match the vanilla freeze ownership rule: if somebody else resumes
                // physics while idle mode is active, relinquish ownership and do not
                // immediately pause it again during the same idle session.
                ownedPipeline = null;
                LOG.log(Level.INFO,
                        "Valkyrien Skies physics was resumed externally; idle pregen ownership released");
            }
        } catch (ReflectiveOperationException | RuntimeException ex) {
            ownedPipeline = null;
            LOG.log(Level.WARNING,
                    "Could not observe Valkyrien Skies physics ownership; ownership released", ex);
        }
    }

    private static void restoreOwnedPause() {
        if (ownedPipeline == null) {
            return;
        }

        Object pipeline = ownedPipeline;
        ownedPipeline = null;
        try {
            if (!readRunning(pipeline)) {
                writeRunning(pipeline, true);
                LOG.log(Level.INFO, "Resumed Valkyrien Skies physics owned by idle pregen");
            }
        } catch (ReflectiveOperationException | RuntimeException ex) {
            LOG.log(Level.WARNING,
                    "Failed to resume Valkyrien Skies physics owned by idle pregen", ex);
        }
    }

    private static boolean readRunning(Object pipeline) throws ReflectiveOperationException {
        return (Boolean)getRunningMethod.invoke(pipeline);
    }

    private static void writeRunning(Object pipeline, boolean running) throws ReflectiveOperationException {
        setRunningMethod.invoke(pipeline, running);
    }

    private static void resetSession() {
        ownedPipeline = null;
        getRunningMethod = null;
        setRunningMethod = null;
        decisionMade = false;
    }
}
