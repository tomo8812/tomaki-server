package maki.lodcpu;

import com.kishku7.chunksmith.GenerationTask;

/** Chunksmith-side generation observation and admission; owns no FULL or LOD validity policy. */
public final class PregenControlHooks {
    private PregenControlHooks(){}
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
    public static void nativeCoordinateFinished(String dim,int x,int z,GenerationTask task){
        PregenFlowController.nativeCoordinateFinished(dim,x,z,task);
    }
    public static void defer(String dim,int x,int z,GenerationTask task){
        PregenFlowController.defer(dim,x,z,task);
    }
    public static boolean consumeRejected(String dim,int x,int z,GenerationTask task){
        return PregenFlowController.consumeRejected(dim,x,z,task);
    }
}
