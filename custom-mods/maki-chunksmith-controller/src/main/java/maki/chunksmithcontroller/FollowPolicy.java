package maki.chunksmithcontroller;
public final class FollowPolicy {
    public static int center(double coordinate){
        if(!Double.isFinite(coordinate)||Math.abs(coordinate)>29_990_000)throw new IllegalArgumentException("Invalid world coordinate");
        return Math.floorDiv((int)Math.floor(coordinate),16)*16+8;
    }
    public static boolean moved(int x,int z,int oldX,int oldZ){return Math.floorDiv(x,16)!=Math.floorDiv(oldX,16)||Math.floorDiv(z,16)!=Math.floorDiv(oldZ,16);}
    public static boolean due(long now,long last,int seconds){return last==0||now-last>=seconds*1000L;}
}
