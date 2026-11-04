package org.valkyrienskies.core.impl.shadow

import com.fasterxml.jackson.databind.ObjectMapper
import javax.inject.Named
import org.valkyrienskies.core.impl.util.serialization.VSJacksonUtil

@dW
public class GK {
   @dX
   @JvmStatic
   public fun a(): ObjectMapper {
      return VSJacksonUtil.INSTANCE.defaultMapper
   }

   @dX
   @Named("dto")
   @JvmStatic
   public fun b(): ObjectMapper {
      return VSJacksonUtil.INSTANCE.dtoMapper
   }

   @dX
   @Named("config")
   @JvmStatic
   public fun c(): ObjectMapper {
      return VSJacksonUtil.INSTANCE.configMapper
   }

   @dX
   @Named("delta")
   @JvmStatic
   public fun d(): ObjectMapper {
      return VSJacksonUtil.INSTANCE.deltaMapper
   }

   @dX
   @Named("packet")
   @JvmStatic
   public fun e(): ObjectMapper {
      return VSJacksonUtil.INSTANCE.packetMapper
   }
}
