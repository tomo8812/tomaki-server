package maki.idlepregen;

public final class VsPhysicsPauseControllerHarness {
    private VsPhysicsPauseControllerHarness() {
    }

    public static void main(String[] args) {
        asyncOwnedPauseUsesZeroDeltaPulse();
        preexistingAsyncPauseIsPreserved();
        synchronizedPhysicsUsesZeroDeltaWithoutPauseOwnership();
        latePipelineIsPausedWhenItAppears();
        externalResumeReleasesAsyncOwnership();
        synchronizedModeTransitionRestoresOwnedAsyncPause();
        System.out.println("VsPhysicsPauseControllerHarness: PASS");
    }

    private static void asyncOwnedPauseUsesZeroDeltaPulse() {
        FakePipeline pipeline = new FakePipeline(true, false);
        FakeServer server = new FakeServer(pipeline);

        VsPhysicsPauseController.beginSession();
        VsPhysicsPauseController.tick(server);
        check(!pipeline.running, "idle pregen must pause a running async VS pipeline");
        check(VsPhysicsPauseController.ownsAsyncPause(), "idle pregen must own the async pause it created");

        VsPhysicsPauseController.afterGameFrame(pipeline);
        check(pipeline.running, "game-frame handoff must wake one async state-sync pass");

        double adjusted = VsPhysicsPauseController.adjustPhysicsDelta(pipeline, 0.016D);
        check(adjusted == 0.0D, "idle state-sync pass must use a zero physics delta");
        check(VsPhysicsPauseController.ownsAsyncPause(), "expected pulse must retain ownership");

        VsPhysicsPauseController.completePhysicsStep(pipeline);
        check(!pipeline.running, "async worker must be paused again after the zero-delta pass");

        VsPhysicsPauseController.leaveSession();
        check(pipeline.running, "owned async VS pause must be resumed on exit");
        check(pipeline.setCalls == 4,
                "owned async pulse should perform pause, wake, re-pause, and final resume");
    }

    private static void preexistingAsyncPauseIsPreserved() {
        FakePipeline pipeline = new FakePipeline(false, false);
        FakeServer server = new FakeServer(pipeline);

        VsPhysicsPauseController.beginSession();
        VsPhysicsPauseController.tick(server);
        check(!VsPhysicsPauseController.ownsAsyncPause(), "preexisting async VS pause must not be claimed");
        check(VsPhysicsPauseController.adjustPhysicsDelta(pipeline, 0.016D) == 0.0D,
                "idle physics time must remain frozen even without pause ownership");

        VsPhysicsPauseController.afterGameFrame(pipeline);
        VsPhysicsPauseController.leaveSession();
        check(!pipeline.running, "preexisting async VS pause must remain paused on exit");
        check(pipeline.setCalls == 0, "preexisting async VS pause must not be mutated");
    }

    private static void synchronizedPhysicsUsesZeroDeltaWithoutPauseOwnership() {
        FakePipeline pipeline = new FakePipeline(true, true);
        FakeServer server = new FakeServer(pipeline);

        VsPhysicsPauseController.beginSession();
        VsPhysicsPauseController.tick(server);
        check(pipeline.running, "synchronized VS physics must not use the ineffective async pause flag");
        check(!VsPhysicsPauseController.ownsAsyncPause(), "synchronized physics must not claim async pause ownership");

        check(VsPhysicsPauseController.adjustPhysicsDelta(pipeline, 0.05D) == 0.0D,
                "synchronized VS physics must still receive zero delta while idle");
        VsPhysicsPauseController.afterGameFrame(pipeline);
        VsPhysicsPauseController.completePhysicsStep(pipeline);

        VsPhysicsPauseController.leaveSession();
        check(pipeline.running, "synchronized pipeline running state must remain unchanged");
        check(pipeline.setCalls == 0, "synchronized pipeline pause flag must not be mutated");
    }

    private static void latePipelineIsPausedWhenItAppears() {
        FakeServer server = new FakeServer(null);

        VsPhysicsPauseController.beginSession();
        VsPhysicsPauseController.tick(server);
        check(!VsPhysicsPauseController.ownsAsyncPause(), "missing VS pipeline must not create ownership");

        FakePipeline pipeline = new FakePipeline(true, false);
        server.pipeline = pipeline;
        VsPhysicsPauseController.tick(server);
        check(!pipeline.running, "VS pipeline created during idle must still be paused");
        check(VsPhysicsPauseController.ownsAsyncPause(), "late async VS pipeline pause must be owned");

        VsPhysicsPauseController.leaveSession();
        check(pipeline.running, "late pipeline must resume on exit");
    }

    private static void externalResumeReleasesAsyncOwnership() {
        FakePipeline pipeline = new FakePipeline(true, false);
        FakeServer server = new FakeServer(pipeline);

        VsPhysicsPauseController.beginSession();
        VsPhysicsPauseController.tick(server);
        check(VsPhysicsPauseController.ownsAsyncPause(), "setup must own the async VS pause");

        pipeline.externalSetRunning(true);
        VsPhysicsPauseController.tick(server);
        check(!VsPhysicsPauseController.ownsAsyncPause(), "external resume must release async ownership");
        check(pipeline.running, "external resume must be respected");
        check(VsPhysicsPauseController.adjustPhysicsDelta(pipeline, 0.016D) == 0.0D,
                "idle zero-delta guard must remain active after ownership release");

        VsPhysicsPauseController.tick(server);
        check(pipeline.running, "idle pregen must not re-pause after external resume in the same session");

        VsPhysicsPauseController.leaveSession();
        check(pipeline.running, "exit must not undo an external resume");
        check(pipeline.setCalls == 1, "external resume path must not add another owned write");
    }

    private static void synchronizedModeTransitionRestoresOwnedAsyncPause() {
        FakePipeline pipeline = new FakePipeline(true, false);
        FakeServer server = new FakeServer(pipeline);

        VsPhysicsPauseController.beginSession();
        VsPhysicsPauseController.tick(server);
        check(!pipeline.running && VsPhysicsPauseController.ownsAsyncPause(),
                "setup must own an async pause");

        pipeline.c = true;
        VsPhysicsPauseController.tick(server);
        check(pipeline.running, "switching to synchronized mode must restore the async pause flag");
        check(!VsPhysicsPauseController.ownsAsyncPause(),
                "synchronized mode must not retain async pause ownership");
        check(VsPhysicsPauseController.adjustPhysicsDelta(pipeline, 0.05D) == 0.0D,
                "mode transition must keep physics time frozen");

        pipeline.c = false;
        VsPhysicsPauseController.tick(server);
        check(!pipeline.running && VsPhysicsPauseController.ownsAsyncPause(),
                "returning to async mode may reacquire a pause that this session owns");

        VsPhysicsPauseController.leaveSession();
        check(pipeline.running, "transitioned async pause must resume on exit");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public static final class FakeServer {
        private FakePipeline pipeline;

        FakeServer(FakePipeline pipeline) {
            this.pipeline = pipeline;
        }

        public FakePipeline getVsPipeline() {
            return pipeline;
        }
    }

    public static final class FakePipeline {
        public volatile boolean c;
        private boolean running;
        private int setCalls;

        FakePipeline(boolean running, boolean synchronizedPhysics) {
            this.running = running;
            this.c = synchronizedPhysics;
        }

        public boolean getArePhysicsRunning() {
            return running;
        }

        public void setArePhysicsRunning(boolean running) {
            setCalls++;
            this.running = running;
        }

        void externalSetRunning(boolean running) {
            this.running = running;
        }
    }
}
