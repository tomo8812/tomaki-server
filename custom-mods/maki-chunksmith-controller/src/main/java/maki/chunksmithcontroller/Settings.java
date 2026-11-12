package maki.chunksmithcontroller;

import java.util.Properties;

public final class Settings {
    public long maxStorageBytes=15L<<30;
    public int min = 1, max = 8;
    public double interval = 5;
    public double highMspt = 50, emergencyMspt = 100, highHeap = 75;
    public double cpuCapacity = 0;
    public int followRadius=4096, recenterSeconds=30, emptyGraceSeconds=0;
    public boolean followEnabled=true;
    public String followPlayer="tomo8812", followUuid="9845d198-4c17-4be1-a109-b96b7e42aef9";
    public int windowSeconds=30,settleSeconds=8,retrySeconds=60,minimumEvents=8,noProgressSeconds=60;
    public double minimumGainPercent=5,memoryReserveMb=512,gcPressurePercent=20;

    public static Settings parse(Properties p) {
        Settings s = new Settings();
        s.maxStorageBytes=Long.parseLong(p.getProperty("generation-max-storage-bytes",Long.toString(s.maxStorageBytes)));
        if(s.maxStorageBytes<=0)throw new IllegalArgumentException("generation-max-storage-bytes");
        s.min = integer(p,"min-concurrent",1,1,64);
        s.max = integer(p,"max-concurrent",8,s.min,64);
        s.interval = number(p,"sample-seconds",5,2,60);
        s.highMspt = number(p,"mspt-decrease-at",50,1,80);
        s.emergencyMspt = number(p,"mspt-emergency-at",100,s.highMspt+1,500);
        s.highHeap = number(p,"heap-decrease-at-percent",75,50,90);
        s.cpuCapacity = number(p,"cpu-capacity-cores",0,0,256);
        s.windowSeconds=integer(p,"throughput-window-seconds",30,20,120);
        s.settleSeconds=integer(p,"throughput-settle-seconds",8,4,30);
        s.retrySeconds=integer(p,"throughput-retry-seconds",60,30,600);
        s.minimumEvents=integer(p,"throughput-minimum-events",8,4,1000);
        s.noProgressSeconds=integer(p,"no-progress-seconds",60,30,300);
        s.minimumGainPercent=number(p,"throughput-minimum-gain-percent",5,2,25);
        s.memoryReserveMb=number(p,"minimum-system-headroom-mb",512,256,4096);
        s.gcPressurePercent=number(p,"gc-pressure-percent",20,10,50);
        s.followRadius=integer(p,"follow-radius-blocks",4096,128,8192);
        s.recenterSeconds=integer(p,"follow-recenter-seconds",30,10,600);
        s.emptyGraceSeconds=integer(p,"follow-empty-grace-seconds",0,0,3600);
        String enabled=p.getProperty("follow-enabled","true");
        if(!enabled.equals("true")&&!enabled.equals("false"))throw new IllegalArgumentException("follow-enabled");
        s.followEnabled=Boolean.parseBoolean(enabled);
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
