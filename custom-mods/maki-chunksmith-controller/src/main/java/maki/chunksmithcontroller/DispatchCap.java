package maki.chunksmithcontroller;

public final class DispatchCap {
    private DispatchCap(){}
    public static int apply(int current,int cap,int maximum,boolean nativeThrottle,boolean gated,
        long queue,long queueMaximum){
        int target=Math.max(1,Math.min(cap,maximum));
        if(nativeThrottle||gated||queueMaximum>0&&queue>queueMaximum)return Math.min(current,target);
        return target;
    }
}
