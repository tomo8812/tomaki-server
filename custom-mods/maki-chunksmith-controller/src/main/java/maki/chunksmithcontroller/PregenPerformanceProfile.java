package maki.chunksmithcontroller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.OptionalInt;
import java.util.Properties;

/** Environment-scoped warm-start hint. It never affects cache/fingerprint validity. */
final class PregenPerformanceProfile {
    static final String CONTROLLER_VERSION="e-lite-v7";
    static final long MAX_AGE_MILLIS=24L*60L*60L*1000L;

    record Environment(
        String controllerVersion,String cpuAffinity,String cpuQuota,double cpuCapacity,long heapMaxBytes,
        String chunksmithConfig,String pairFeedVersion,long fastSignature,String workClassBand
    ) {}

    private PregenPerformanceProfile(){}

    static String workClassBand(long newPairs,long repairPairs) {
        long total=Math.max(0,newPairs)+Math.max(0,repairPairs);
        if(total<8)return "UNKNOWN";
        double ratio=(double)Math.max(0,newPairs)/total;
        return ratio>=.70?"NEW_HEAVY":ratio<=.30?"REPAIR_HEAVY":"MIXED";
    }

    static OptionalInt load(Path file,Environment current,Settings settings,long nowMillis) {
        if(file==null||current==null||settings==null||!Files.isRegularFile(file))return OptionalInt.empty();
        Properties p=new Properties();
        try(InputStream in=Files.newInputStream(file)){p.load(in);}
        catch(IOException|RuntimeException invalid){return OptionalInt.empty();}
        try {
            if(!CONTROLLER_VERSION.equals(p.getProperty("controllerVersion")))return OptionalInt.empty();
            long savedAt=Long.parseLong(p.getProperty("savedAt","-1"));
            if(savedAt<=0||nowMillis<savedAt||nowMillis-savedAt>MAX_AGE_MILLIS)return OptionalInt.empty();
            if(!same(p,current))return OptionalInt.empty();
            int stored=Integer.parseInt(p.getProperty("cap","2"));
            return OptionalInt.of(Math.max(settings.min,Math.min(settings.max,stored)));
        } catch(RuntimeException invalid) {
            return OptionalInt.empty();
        }
    }

    static boolean save(Path file,Environment environment,int cap,long nowMillis) {
        if(file==null||environment==null||"UNKNOWN".equals(environment.workClassBand()))return false;
        Properties p=new Properties();
        p.setProperty("controllerVersion",environment.controllerVersion());
        p.setProperty("savedAt",Long.toString(nowMillis));
        p.setProperty("cap",Integer.toString(cap));
        p.setProperty("cpuAffinity",environment.cpuAffinity());
        p.setProperty("cpuQuota",environment.cpuQuota());
        p.setProperty("cpuCapacity",Double.toString(environment.cpuCapacity()));
        p.setProperty("heapMaxBytes",Long.toString(environment.heapMaxBytes()));
        p.setProperty("chunksmithConfig",environment.chunksmithConfig());
        p.setProperty("pairFeedVersion",environment.pairFeedVersion());
        p.setProperty("fastSignature",Long.toString(environment.fastSignature()));
        p.setProperty("workClassBand",environment.workClassBand());
        Path tmp=file.resolveSibling(file.getFileName()+".tmp");
        try {
            Files.createDirectories(file.toAbsolutePath().normalize().getParent());
            try(OutputStream out=Files.newOutputStream(tmp,StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.WRITE)){
                p.store(out,"maki-chunksmith-controller pregen performance hint; independent from generation cache fingerprint");
            }
            try{Files.move(tmp,file,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING);}
            catch(java.nio.file.AtomicMoveNotSupportedException unsupported){
                Files.move(tmp,file,StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch(IOException|RuntimeException failure) {
            try{Files.deleteIfExists(tmp);}catch(IOException ignored){}
            return false;
        }
    }

    private static boolean same(Properties p,Environment e) {
        if(!e.controllerVersion().equals(p.getProperty("controllerVersion","")))return false;
        if(!e.cpuAffinity().equals(p.getProperty("cpuAffinity","")))return false;
        if(!e.cpuQuota().equals(p.getProperty("cpuQuota","")))return false;
        double capacity=Double.parseDouble(p.getProperty("cpuCapacity","NaN"));
        if(!Double.isFinite(capacity)||Math.abs(capacity-e.cpuCapacity())>.05)return false;
        if(e.heapMaxBytes()!=Long.parseLong(p.getProperty("heapMaxBytes","-1")))return false;
        if(!e.chunksmithConfig().equals(p.getProperty("chunksmithConfig","")))return false;
        if(!e.pairFeedVersion().equals(p.getProperty("pairFeedVersion","")))return false;
        if(e.fastSignature()!=Long.parseLong(p.getProperty("fastSignature","0")))return false;
        return e.workClassBand().equals(p.getProperty("workClassBand",""));
    }
}
