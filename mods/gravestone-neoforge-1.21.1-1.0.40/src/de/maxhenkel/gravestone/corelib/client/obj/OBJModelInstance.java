package de.maxhenkel.gravestone.corelib.client.obj;

public class OBJModelInstance<T> {
   private OBJModel model;
   private OBJModelOptions<T> options;

   public OBJModelInstance(OBJModel model, OBJModelOptions<T> options) {
      this.model = model;
      this.options = options;
   }

   public OBJModel getModel() {
      return this.model;
   }

   public OBJModelOptions<T> getOptions() {
      return this.options;
   }
}
