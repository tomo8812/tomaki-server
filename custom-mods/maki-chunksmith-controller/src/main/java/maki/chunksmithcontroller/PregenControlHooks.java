package maki.chunksmithcontroller;
import com.kishku7.chunksmith.GenerationTask;
public final class PregenControlHooks {
    private PregenControlHooks(){}
    public static boolean reserveCandidate(String d,int x,int z,GenerationTask t){return PregenFlowController.reserveCandidate(d,x,z,t);}
    public static void classify(String d,int x,int z,GenerationTask t,boolean generated){PregenFlowController.classify(d,x,z,t,generated);}
    public static boolean beginActual(String d,int x,int z,GenerationTask t){return PregenFlowController.beginClassified(d,x,z,t);}
    public static void candidateFailed(String d,int x,int z,GenerationTask t){PregenFlowController.candidateFailed(d,x,z,t);}
    public static void nativeCoordinateFinished(String d,int x,int z,GenerationTask t){PregenFlowController.nativeCoordinateFinished(d,x,z,t);}
    public static void taskFinished(GenerationTask task){PregenFlowController.taskFinished(task);}
    public static void lodWriteStarted(String token){PregenFlowController.physicalStart(token,PregenFlowController.Stage.LOD);}
    public static void lodWriteFinished(String token,long nanos,boolean ok){PregenFlowController.physicalTerminal(token,PregenFlowController.Stage.LOD,nanos,ok);}
}
