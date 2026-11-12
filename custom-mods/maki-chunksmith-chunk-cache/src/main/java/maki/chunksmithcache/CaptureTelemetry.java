package maki.chunksmithcache;

import java.util.concurrent.CompletableFuture;

/** Serializes logical termination against physical starts; late terminals still report. */
final class CaptureTelemetry {
    interface Events {
        void started(String stage);
        void finished(String stage,long nanos,boolean success);
        void failed();
    }
    private final CompletableFuture<Boolean> result;
    private final Events events;
    CaptureTelemetry(CompletableFuture<Boolean> result,Events events){
        this.result=result;this.events=events;
        result.whenComplete((ok,error)->terminal(Boolean.TRUE.equals(ok)&&error==null));
    }
    synchronized boolean start(String stage){
        if(result.isDone())return false;
        events.started(stage);return true;
    }
    void finish(String stage,long nanos,boolean success){events.finished(stage,nanos,success);}
    private synchronized void terminal(boolean success){if(!success)events.failed();}
}
