package maki.lodcpu;

import java.util.Properties;

public final class Settings {
    public int min = 1, max = 8, upSamples = 6, downSamples = 2;
    public double interval = 5, lowCpu = 65, highCpu = 80, emergencyCpu = 95;
    public double lowMspt = 40, highMspt = 50, emergencyMspt = 100, highHeap = 75;
    public double cpuCapacity = 0;
    public int onlineMax=2, followRadius=4096, recenterSeconds=30, emptyGraceSeconds=0;
    public boolean followEnabled=true, generateWhileOnline=false;
    public String followPlayer="tomo8812", followUuid="9845d198-4c17-4be1-a109-b96b7e42aef9";
    public int windowSeconds=30,settleSeconds=8,retrySeconds=60,minimumEvents=8,maxStep=8,noProgressSeconds=60;
    public double minimumGainPercent=5,memoryReserveMb=512,gcPressurePercent=20;
    public double backfillIoPressurePercent=25;
    public int backfillLodQueueHigh=64,backfillWriteQueueHigh=32;
    public int backfillExclusiveMaxSeconds=60,backfillExclusiveMaxChunks=16,backfillFollowMinSliceSeconds=30,backfillDrainWaitSeconds=30,backfillSampleMaxAgeSeconds=10;

    public static Settings parse(Properties p) {
        Settings s = new Settings();
        s.min = integer(p,"min-concurrent",1,1,64);
        s.max = integer(p,"max-concurrent",8,s.min,64);
        s.interval = number(p,"sample-seconds",5,2,60);
        s.upSamples = integer(p,"increase-after-samples",6,2,120);
        s.downSamples = integer(p,"decrease-after-samples",2,1,10);
        s.lowCpu = number(p,"cpu-increase-below-percent",65,1,90);
        s.highCpu = number(p,"cpu-decrease-at-percent",80,s.lowCpu+1,99);
        s.emergencyCpu = number(p,"cpu-emergency-at-percent",95,s.highCpu+1,100);
        s.lowMspt = number(p,"mspt-increase-below",40,1,45);
        s.highMspt = number(p,"mspt-decrease-at",50,s.lowMspt+1,80);
        s.emergencyMspt = number(p,"mspt-emergency-at",100,s.highMspt+1,500);
        s.highHeap = number(p,"heap-decrease-at-percent",75,50,90);
        s.cpuCapacity = number(p,"cpu-capacity-cores",0,0,256);
        s.windowSeconds=integer(p,"throughput-window-seconds",30,20,120);
        s.settleSeconds=integer(p,"throughput-settle-seconds",8,4,30);
        s.retrySeconds=integer(p,"throughput-retry-seconds",60,30,600);
        s.minimumEvents=integer(p,"throughput-minimum-events",8,4,1000);
        s.maxStep=integer(p,"throughput-max-step",8,1,16);
        s.noProgressSeconds=integer(p,"no-progress-seconds",60,30,300);
        s.minimumGainPercent=number(p,"throughput-minimum-gain-percent",5,2,25);
        s.memoryReserveMb=number(p,"minimum-system-headroom-mb",512,256,4096);
        s.gcPressurePercent=number(p,"gc-pressure-percent",20,10,50);
        s.backfillIoPressurePercent=number(p,"backfill-io-pressure-percent",25,1,100);
        s.backfillLodQueueHigh=integer(p,"backfill-lod-queue-high",64,1,100000);
        s.backfillWriteQueueHigh=integer(p,"backfill-write-queue-high",32,1,100000);
        s.backfillExclusiveMaxSeconds=integer(p,"backfill-exclusive-max-seconds",60,5,600);
        s.backfillExclusiveMaxChunks=integer(p,"backfill-exclusive-max-chunks",16,1,1024);
        s.backfillFollowMinSliceSeconds=integer(p,"backfill-follow-min-slice-seconds",30,5,600);
        s.backfillDrainWaitSeconds=integer(p,"backfill-drain-wait-seconds",30,5,300);
        s.backfillSampleMaxAgeSeconds=integer(p,"backfill-sample-max-age-seconds",10,4,60);
        s.onlineMax=integer(p,"online-max-concurrent",2,1,2);
        s.followRadius=integer(p,"follow-radius-blocks",4096,128,8192);
        s.recenterSeconds=integer(p,"follow-recenter-seconds",30,10,600);
        s.emptyGraceSeconds=integer(p,"follow-empty-grace-seconds",0,0,3600);
        String enabled=p.getProperty("follow-enabled","true");
        if(!enabled.equals("true")&&!enabled.equals("false"))throw new IllegalArgumentException("follow-enabled");
        s.followEnabled=Boolean.parseBoolean(enabled);
        String online=p.getProperty("generate-while-online","false");
        if(!online.equals("true")&&!online.equals("false"))throw new IllegalArgumentException("generate-while-online");
        s.generateWhileOnline=Boolean.parseBoolean(online);
        s.followPlayer=p.getProperty("follow-player","tomo8812");
        if(!s.followPlayer.matches("[A-Za-z0-9_]{1,16}"))throw new IllegalArgumentException("follow-player");
        s.followUuid=java.util.UUID.fromString(p.getProperty("follow-player-uuid",s.followUuid)).toString();
        return s;
    }
    private static double number(Properties p,String k,double d,double lo,double hi) {
        double v=Double.parseDouble(p.getProperty(k,Double.toString(d)));
        if (!Double.isFinite(v) || v<lo || v>hi) throw new IllegalArgumentException(k);
        return v;
    }
    private static int integer(Properties p,String k,int d,int lo,int hi) {
        int v=Integer.parseInt(p.getProperty(k,Integer.toString(d)));
        if(v<lo||v>hi) throw new IllegalArgumentException(k);
        return v;
    }
}
