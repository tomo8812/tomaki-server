package maki.chunksmithfixes;

/** Changes since the snapshot that actually reached the LOD store. */
final class LodChangeCounter {
    private long changed,saved,lastChanged;
    void changed(long now){changed++;lastChanged=now;}
    boolean due(int threshold){return changed-saved>=threshold;}
    boolean ready(int threshold,long now,long quietNanos){return due(threshold)&&now-lastChanged>=quietNanos;}
    long snapshot(){return changed;}
    void saved(long snapshot){saved=Math.max(saved,snapshot);}
    boolean clean(){return changed==saved;}
    void restore(int threshold,long now){changed=Math.max(changed,saved+threshold);lastChanged=now;}
}
