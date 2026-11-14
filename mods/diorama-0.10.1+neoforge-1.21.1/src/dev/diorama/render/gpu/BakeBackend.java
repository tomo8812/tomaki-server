package dev.diorama.render.gpu;

import org.jspecify.annotations.Nullable;

interface BakeBackend {
   boolean draw(GpuTileBaker.Job var1);

   @Nullable String device();

   default void poll() {
   }
}
