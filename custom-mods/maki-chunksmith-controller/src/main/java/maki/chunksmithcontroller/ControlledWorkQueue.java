package maki.chunksmithcontroller;

import java.util.ArrayDeque;
import maki.chunksmithfixes.GenerationWorkEvent;

/** Opaque generation jobs: admission and completion measurements only. */
final class ControlledWorkQueue {
    private final ArrayDeque<GenerationWorkEvent> waiting=new ArrayDeque<>();
    private volatile int running;
    private long epoch;
    synchronized void submit(GenerationWorkEvent event){waiting.add(event);}
    int running(){return running;}
    synchronized void tick(int capacity,boolean resourcesAvailable){
        if(!resourcesAvailable)return;
        int slots=Math.max(0,capacity-running);
        for(int i=0;i<slots&&!waiting.isEmpty();i++){
            GenerationWorkEvent work=waiting.remove();running++;long runEpoch=epoch;
            try{work.start().whenComplete((unused,error)->finished(runEpoch));}
            catch(RuntimeException error){finished(runEpoch);}
        }
    }
    private synchronized void finished(long runEpoch){
        if(runEpoch!=epoch)return;
        running--;
    }
    synchronized void clear(){epoch++;waiting.clear();running=0;}
    synchronized int waiting(){return waiting.size();}
}
