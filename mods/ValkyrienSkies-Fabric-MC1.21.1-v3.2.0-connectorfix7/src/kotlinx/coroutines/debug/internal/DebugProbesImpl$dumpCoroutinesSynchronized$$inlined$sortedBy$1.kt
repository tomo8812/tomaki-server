package kotlinx.coroutines.debug.internal

{ a: Any, b: Any ->
   return ComparisonsKt.compareValues((a as DebugProbesImpl.CoroutineOwner).info.sequenceNumber, (b as DebugProbesImpl.CoroutineOwner).info.sequenceNumber)
}