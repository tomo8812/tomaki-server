package org.sinytra.adapter.patch.processor;

import org.sinytra.adapter.env.util.OrderedRegistry;
import org.sinytra.adapter.patch.processor.extract.ExtractMixinProcessor;

public class Processors extends OrderedRegistry<Processor> {
   public Processors() {
      this.registerDefaultProcessors();
   }

   private void registerDefaultProcessors() {
      this.add(new DisableMixinProcessor());
      this.add(new MixinTypeProcessor());
      this.add(new PropertyProcessor());
      this.add(new TargetMethodProcessor());
      this.add(new ParametersProcessor());
      this.add(new ParametersPostProcessor());
      this.add(new ReturnTypeProcessor());
      this.add(new StaticAccessProcessor());
      this.add(new ExtractMixinProcessor());
   }
}
