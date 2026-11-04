package org.valkyrienskies.mod.common

import java.util.Comparator
import kotlin.jvm.internal.SourceDebugExtension

// $VF: Class flags could not be determined
@SourceDebugExtension(["SMAP\nComparisons.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Comparisons.kt\nkotlin/comparisons/ComparisonsKt__ComparisonsKt$compareByDescending$1\n+ 2 BlockStateInfoProvider.kt\norg/valkyrienskies/mod/common/BlockStateInfo\n*L\n1#1,121:1\n63#2:122\n*E\n"])
internal class `BlockStateInfo$init$$inlined$sortedByDescending$1`<T> : Comparator {
   override final fun compare(a: T, b: T): Int {
      ComparisonsKt.compareValues((b as BlockStateInfoProvider).priority, (a as BlockStateInfoProvider).priority)
   }
}
