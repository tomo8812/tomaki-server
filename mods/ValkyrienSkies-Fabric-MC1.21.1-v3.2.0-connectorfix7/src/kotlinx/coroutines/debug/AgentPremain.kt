package kotlinx.coroutines.debug

import android.annotation.SuppressLint
import java.lang.instrument.ClassFileTransformer
import java.lang.instrument.Instrumentation
import java.security.ProtectionDomain
import kotlinx.coroutines.debug.internal.AgentInstallationType
import kotlinx.coroutines.debug.internal.DebugProbesImpl
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
import sun.misc.Signal

@SuppressLint(["all"])
@IgnoreJRERequirement
internal object AgentPremain {
   private final val enableCreationStackTraces: Boolean

   @JvmStatic
   public fun premain(args: String?, instrumentation: Instrumentation) {
      AgentInstallationType.INSTANCE.isInstalledStatically = true
      instrumentation.addTransformer(AgentPremain.DebugProbesTransformer.INSTANCE)
      DebugProbesImpl.INSTANCE.enableCreationStackTraces = enableCreationStackTraces
      DebugProbesImpl.INSTANCE.install$kotlinx_coroutines_core()
      INSTANCE.installSignalHandler()
   }

   private fun installSignalHandler() {
      try {
         Signal.handle(Signal("TRAP"), { it: Signal ->
            if (DebugProbesImpl.INSTANCE.isInstalled) {
               DebugProbesImpl.INSTANCE.dumpCoroutines(System.out)
            } else {
               System.out.println((Object)"Cannot perform coroutines dump, debug probes are disabled")
            }
         })
      } catch (var2: java.lang.Throwable) {
      }
   }

   @JvmStatic
   fun {
      val var0: AgentPremain = INSTANCE

      var `$this$enableCreationStackTraces_u24lambda_u240`: Any
      try {
         val var10000: java.lang.String = System.getProperty("kotlinx.coroutines.debug.enable.creation.stack.trace")
         `$this$enableCreationStackTraces_u24lambda_u240` = Result.constructor_impl/* $VF was: constructor-impl */(
            if (var10000 != null) java.lang.Boolean.parseBoolean(var10000) else null
         )
      } catch (var3: java.lang.Throwable) {
         `$this$enableCreationStackTraces_u24lambda_u240` = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var3))
      }

      val var5: java.lang.Boolean = (
         if (Result.isFailure_impl/* $VF was: isFailure-impl */(`$this$enableCreationStackTraces_u24lambda_u240`))
            null
            else
            `$this$enableCreationStackTraces_u24lambda_u240`
      ) as java.lang.Boolean
      enableCreationStackTraces = var5 ?: DebugProbesImpl.INSTANCE.enableCreationStackTraces
   }

   internal object DebugProbesTransformer : ClassFileTransformer {
      public override fun transform(
         loader: ClassLoader?,
         className: String,
         classBeingRedefined: Class<*>?,
         protectionDomain: ProtectionDomain,
         classfileBuffer: ByteArray?
      ): ByteArray? {
         if (loader != null && className == "kotlin/coroutines/jvm/internal/DebugProbesKt") {
            AgentInstallationType.INSTANCE.isInstalledStatically = true
            return ByteStreamsKt.readBytes(loader.getResourceAsStream("DebugProbesKt.bin"))
         } else {
            return null
         }
      }
   }
}
