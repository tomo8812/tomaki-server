package org.sinytra.adapter.env.ctx;

public interface RefmapHolder {
   String remap(String var1, String var2);

   void copyEntries(String var1, String var2);
}
