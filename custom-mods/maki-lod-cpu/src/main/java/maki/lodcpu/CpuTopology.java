package maki.lodcpu;

import java.util.BitSet;

/** Linux CPU identifiers are not a processor count and need not be contiguous. */
final class CpuTopology {
    private CpuTopology() {}
    static int count(String text){
        BitSet cpus=new BitSet();
        for(String part:text.trim().split(",",-1)){
            String[] span=part.trim().split("-",-1);
            if(span.length<1||span.length>2)throw new IllegalArgumentException("CPU list");
            int first=Integer.parseInt(span[0]);
            int last=span.length==1?first:Integer.parseInt(span[1]);
            if(first<0||last<first||last>65535)throw new IllegalArgumentException("CPU range");
            cpus.set(first,last+1);
        }
        if(cpus.isEmpty())throw new IllegalArgumentException("Empty CPU list");
        return cpus.cardinality();
    }
    static double restrict(double current,String cpus){
        try{return Math.min(current,count(cpus));}
        catch(IllegalArgumentException ignored){return current;}
    }
}
