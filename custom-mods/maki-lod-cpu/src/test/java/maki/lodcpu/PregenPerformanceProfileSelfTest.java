package maki.lodcpu;

import java.nio.file.Files;
import java.nio.file.Path;

public final class PregenPerformanceProfileSelfTest {
    public static void main(String[] args)throws Exception{
        Path dir=Files.createTempDirectory("maki-pregen-profile");
        Path file=dir.resolve("profile.properties");
        Settings settings=new Settings();
        settings.min=1;settings.max=8;
        long now=1_000_000L;
        var env=new PregenPerformanceProfile.Environment(
            PregenPerformanceProfile.CONTROLLER_VERSION,"0-1","200000 100000",2.0,8L<<30,
            "true|50|8","1.4.2",12345L,"MIXED");

        require(PregenPerformanceProfile.workClassBand(3,3).equals("UNKNOWN"),"small samples remain unknown");
        require(PregenPerformanceProfile.workClassBand(8,0).equals("NEW_HEAVY"),"new-heavy band");
        require(PregenPerformanceProfile.workClassBand(0,8).equals("REPAIR_HEAVY"),"repair-heavy band");
        require(PregenPerformanceProfile.workClassBand(4,4).equals("MIXED"),"mixed band");

        require(PregenPerformanceProfile.save(file,env,5,now),"profile writes");
        require(PregenPerformanceProfile.load(file,env,settings,now+1000).orElseThrow()==5,"matching environment warm starts");
        settings.max=4;
        require(PregenPerformanceProfile.load(file,env,settings,now+1000).orElseThrow()==4,"warm cap is safely clamped");

        settings.max=8;
        var changedQuota=new PregenPerformanceProfile.Environment(
            env.controllerVersion(),env.cpuAffinity(),"100000 100000",env.cpuCapacity(),env.heapMaxBytes(),
            env.chunksmithConfig(),env.cacheControllerVersion(),env.fastSignature(),env.workClassBand());
        require(PregenPerformanceProfile.load(file,changedQuota,settings,now+1000).isEmpty(),"quota change rejects warm start");
        var changedBand=new PregenPerformanceProfile.Environment(
            env.controllerVersion(),env.cpuAffinity(),env.cpuQuota(),env.cpuCapacity(),env.heapMaxBytes(),
            env.chunksmithConfig(),env.cacheControllerVersion(),env.fastSignature(),"NEW_HEAVY");
        require(PregenPerformanceProfile.load(file,changedBand,settings,now+1000).isEmpty(),"work-class change rejects warm start");
        require(PregenPerformanceProfile.load(file,env,settings,now+PregenPerformanceProfile.MAX_AGE_MILLIS+1).isEmpty(),
            "stale profile is rejected");

        System.out.println("PregenPerformanceProfileSelfTest PASS");
    }

    private static void require(boolean value,String message){
        if(!value)throw new AssertionError(message);
    }
}
