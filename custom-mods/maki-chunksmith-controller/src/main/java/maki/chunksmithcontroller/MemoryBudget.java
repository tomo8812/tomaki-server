package maki.chunksmithcontroller;

import java.nio.file.*;
import java.util.*;

/** Read-only Linux diagnostics; unknown is -1, never fabricated as zero free memory. */
public record MemoryBudget(double headroomMb,double rssMb,long oomKills,double memoryPsi,double ioPsi,long throttledMicros) {
    public static MemoryBudget read(){return read(Path.of("/proc"),Path.of("/sys/fs/cgroup"));}
    static MemoryBudget read(Path proc,Path group){
        double free=-1,rss=-1;
        try{free=kbField(Files.readString(proc.resolve("meminfo")),"MemAvailable:")/1024;}catch(Exception ignored){}
        try{rss=kbField(Files.readString(proc.resolve("self/status")),"VmRSS:")/1024;}catch(Exception ignored){}
        Path currentGroup=group;
        // On a directly hosted server the process can live in a systemd child cgroup.
        // A container cgroup namespace normally exposes its current group at the root.
        try{
            String path=Files.readAllLines(proc.resolve("self/cgroup")).stream().filter(s->s.startsWith("0::")).findFirst().orElseThrow().substring(3);
            Path candidate=group.resolve(path.replaceFirst("^/+","")).normalize();
            if(candidate.startsWith(group)&&Files.exists(candidate.resolve("memory.current")))currentGroup=candidate;
        }catch(Exception ignored){}
        long oom=-1,throttled=-1;
        // Apply every visible ancestor memory limit, not only the leaf's memory.max.
        Path node=currentGroup;
        while(node!=null&&node.startsWith(group)){
            try{
                String max=Files.readString(node.resolve("memory.max")).trim();
                if(!max.equals("max")){
                    long limit=Long.parseLong(max),used=Long.parseLong(Files.readString(node.resolve("memory.current")).trim());
                    double remaining=Math.max(0,limit-used)/1048576.0;
                    free=free<0?remaining:Math.min(free,remaining);
                }
            }catch(Exception ignored){}
            if(node.equals(group))break;node=node.getParent();
        }
        try{oom=plainField(Files.readString(currentGroup.resolve("memory.events")),"oom_kill");}catch(Exception ignored){}
        try{throttled=plainField(Files.readString(currentGroup.resolve("cpu.stat")),"throttled_usec");}catch(Exception ignored){}
        return new MemoryBudget(free,rss,oom,psi(proc.resolve("pressure/memory")),psi(proc.resolve("pressure/io")),throttled);
    }
    static double kbField(String text,String key){
        for(String line:text.split("\\R"))if(line.startsWith(key))return Double.parseDouble(line.substring(key.length()).trim().split("\\s+")[0]);
        throw new IllegalArgumentException(key);
    }
    static long plainField(String text,String key){
        for(String line:text.split("\\R")){String[] p=line.trim().split("\\s+");if(p.length==2&&p[0].equals(key))return Long.parseLong(p[1]);}
        throw new IllegalArgumentException(key);
    }
    private static double psi(Path path){
        try{for(String line:Files.readAllLines(path))if(line.startsWith("some "))for(String part:line.split(" "))if(part.startsWith("avg10="))return Double.parseDouble(part.substring(6));}catch(Exception ignored){}
        return -1;
    }
}
