package maki.chunksmithcontroller;

import com.kishku7.chunksmith.GenerationTask;

/** Chunksmith-side generation observation and admission; owns no FULL or LOD validity policy. */
public final class PregenControlHooks {
    private PregenControlHooks(){}
    public static java.util.concurrent.CompletableFuture<Void> prepareDispatch(com.kishku7.chunksmith.platform.World world,int x,int z,GenerationTask owner){
        return PendingDispatch.prepare(()->PlayerCenter.admissionOpen(owner)&&!owner.isStopping(),
            ()->maki.chunksmithcache.CacheManager.prepareRequest(world,x,z,owner),
            java.util.concurrent.CompletableFuture.delayedExecutor(1,java.util.concurrent.TimeUnit.SECONDS))
            .whenComplete((ignored,error)->{
                if(error!=null&&PlayerCenter.admissionOpen(owner)&&!owner.isStopping())preparationFailed(world.getName(),x,z);
            });
    }
    public static void preparationFailed(String dim,int x,int z){PregenFlowController.preparationFailed(dim,x,z);}
    public static void preparationSucceeded(String dim,int x,int z){PregenFlowController.preparationSucceeded(dim,x,z);}
    public static boolean reserveCandidate(String dim,int x,int z,GenerationTask task){
        return PregenFlowController.reserveCandidate(dim,x,z,task);
    }
    public static void classify(String dim,int x,int z,GenerationTask task,boolean alreadyGenerated){
        PregenFlowController.classify(dim,x,z,task,alreadyGenerated);
    }
    public static boolean beginActual(String dim,int x,int z,GenerationTask task){
        return PregenFlowController.beginClassified(dim,x,z,task);
    }
    public static void candidateFailed(String dim,int x,int z,GenerationTask task){
        PregenFlowController.candidateFailed(dim,x,z,task);
    }
    public static void requestFailed(String dim,int x,int z,GenerationTask task){
        PregenFlowController.requestFailed(dim,x,z,task);
    }
    public static void requestSucceeded(String dim,int x,int z,GenerationTask task){PregenFlowController.requestSucceeded(dim,x,z,task);}
    public static void nativeCoordinateFinished(String dim,int x,int z,GenerationTask task){
        PregenFlowController.nativeCoordinateFinished(dim,x,z,task);
    }
    public static void requestInvoked(String dim,int x,int z,GenerationTask owner){PregenFlowController.requestInvoked(dim,x,z,owner);}
    /** Native Chunksmith writer notification, independent from FULL-cache policy. */
    public static void lodWriteStarted(String token){
        PregenFlowController.physicalStart(token,PregenFlowController.Stage.LOD);
    }
    public static void lodWriteFinished(String token,long serviceNanos,boolean success){
        PregenFlowController.physicalTerminal(token,PregenFlowController.Stage.LOD,serviceNanos,success);
    }
    /** Valid when a native LOD completion is independently proven for this attempt. */
    public static void lodCommitted(String token){
        PregenFlowController.lodCommitted(token);
    }

}
