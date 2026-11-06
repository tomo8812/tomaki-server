package maki.idlepregen;

public final class VsPhysicsPauseControllerHarness {
    private VsPhysicsPauseControllerHarness() {
    }

    public static void main(String[] args) {
        ownedPauseIsRestored();
        preexistingPauseIsPreserved();
        latePipelineIsPausedWhenItAppears();
        externalResumeReleasesOwnership();
        System.out.println("VsPhysicsPauseControllerHarness: PASS");
    }

    private static void ownedPauseIsRestored() {
        FakePipeline pipeline = new FakePipeline(true);
        FakeServer server = new FakeServer(pipeline);

        VsPhysicsPauseController.beginSession();
        VsPhysicsPauseController.tick(server);
        check(!pipeline.running, "idle pregen must pause a running VS pipeline");
        check(VsPhysicsPauseController.ownsPause(), "idle pregen must own the pause it created");

        VsPhysicsPauseController.leaveSession();
        check(pipeline.running, "owned VS pause must be resumed on exit");
        check(pipeline.setCalls == 2, "owned pause should perform exactly one pause and one resume");
    }

    private static void preexistingPauseIsPreserved() {
        FakePipeline pipeline = new FakePipeline(false);
        FakeServer server = new FakeServer(pipeline);

        VsPhysicsPauseController.beginSession();
        VsPhysicsPauseController.tick(server);
        check(!VsPhysicsPauseController.ownsPause(), "preexisting VS pause must not be claimed");

        VsPhysicsPauseController.leaveSession();
        check(!pipeline.running, "preexisting VS pause must remain paused on exit");
        check(pipeline.setCalls == 0, "preexisting VS pause must not be mutated");
    }

    private static void latePipelineIsPausedWhenItAppears() {
        FakeServer server = new FakeServer(null);

        VsPhysicsPauseController.beginSession();
        VsPhysicsPauseController.tick(server);
        check(!VsPhysicsPauseController.ownsPause(), "missing VS pipeline must not create ownership");

        FakePipeline pipeline = new FakePipeline(true);
        server.pipeline = pipeline;
        VsPhysicsPauseController.tick(server);
        check(!pipeline.running, "VS pipeline created during idle must still be paused");
        check(VsPhysicsPauseController.ownsPause(), "late VS pipeline pause must be owned");

        VsPhysicsPauseController.leaveSession();
        check(pipeline.running, "late pipeline must resume on exit");
    }

    private static void externalResumeReleasesOwnership() {
        FakePipeline pipeline = new FakePipeline(true);
        FakeServer server = new FakeServer(pipeline);

        VsPhysicsPauseController.beginSession();
        VsPhysicsPauseController.tick(server);
        check(VsPhysicsPauseController.ownsPause(), "setup must own the VS pause");

        pipeline.externalSetRunning(true);
        VsPhysicsPauseController.tick(server);
        check(!VsPhysicsPauseController.ownsPause(), "external resume must release ownership");
        check(pipeline.running, "external resume must be respected");

        VsPhysicsPauseController.tick(server);
        check(pipeline.running, "idle pregen must not re-pause after external resume in the same session");

        VsPhysicsPauseController.leaveSession();
        check(pipeline.running, "exit must not undo an external resume");
        check(pipeline.setCalls == 1, "external resume path must not add another owned write");
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
        private boolean running;
        private int setCalls;

        FakePipeline(boolean running) {
            this.running = running;
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
