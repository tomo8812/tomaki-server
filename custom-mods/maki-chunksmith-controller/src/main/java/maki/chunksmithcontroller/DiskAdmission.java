package maki.chunksmithcontroller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

/** Admission only; Minecraft continues saving already submitted world data. */
final class DiskAdmission {
    private volatile boolean blocked;
    private volatile long usable=-1;
    private long sampled;
    void sample(Path world,Settings settings,long now){
        if(sampled!=0&&now-sampled<5000)return;sampled=now;
        try{update(Files.getFileStore(world).getUsableSpace(),settings);}
        catch(IOException error){blocked=true;usable=-1;}
    }
    void update(long available,Settings settings){
        usable=available;
        double gib=available/(double)(1L<<30);
        if(gib<settings.diskPauseGiB)blocked=true;
        else if(gib>=settings.diskResumeGiB)blocked=false;
    }
    boolean blocked(){return blocked;}
    long usableBytes(){return usable;}
    void reset(){sampled=0;usable=-1;blocked=false;}
}
