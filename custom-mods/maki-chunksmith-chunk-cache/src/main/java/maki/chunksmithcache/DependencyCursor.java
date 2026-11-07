package maki.chunksmithcache;

/** Progress survives cooperative scan yields, including a yield after every position. */
final class DependencyCursor {
    private final int minZ,maxX,maxZ;
    private int x,z;
    private boolean done;
    DependencyCursor(int centerX,int centerZ,int radius) {
        if (radius<0) throw new IllegalArgumentException("negative dependency radius");
        x=centerX-radius; minZ=centerZ-radius; z=minZ;
        maxX=centerX+radius; maxZ=centerZ+radius;
    }
    boolean hasNext() { return !done; }
    int x() { return x; }
    int z() { return z; }
    void advance() {
        if (done) throw new IllegalStateException("dependency scan exhausted");
        if (z==maxZ) {
            z=minZ;
            if (x==maxX) done=true; else x++;
        } else z++;
    }
}
