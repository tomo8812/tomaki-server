package kotlinx.coroutines.internal

@JvmInline
internal value class SegmentOrClosed<S extends Segment<S>> {
   private final val value: Any?

   public final val isClosed: Boolean
      public final get() {
         return arg0 === ConcurrentLinkedListKt.access$getCLOSED$p()
      }


   public final val segment: Any
      public final get() {
         if (arg0 === ConcurrentLinkedListKt.access$getCLOSED$p()) {
            throw IllegalStateException("Does not contain segment".toString())
         } else {
            return (S)arg0
         }
      }


   @JvmStatic
   public open fun toString(): String {
      return "SegmentOrClosed(value=$arg0)"
   }

   override fun toString(): java.lang.String {
      toString_impl/* $VF was: toString-impl */(this.value)
   }

   @JvmStatic
   public open fun hashCode(): Int {
      return if (arg0 == null) 0 else arg0.hashCode()
   }

   override fun hashCode(): Int {
      hashCode_impl/* $VF was: hashCode-impl */(this.value)
   }

   @JvmStatic
   public open operator fun equals(other: Any?): Boolean {
      return other is SegmentOrClosed && arg0 == (other as SegmentOrClosed).unbox_impl/* $VF was: unbox-impl */()
   }

   override fun equals(other: Any): Boolean {
      equals_impl/* $VF was: equals-impl */(this.value, other)
   }

   @JvmSynthetic
   fun SegmentOrClosed(value: Any) {
      this.value = value
   }

   @JvmStatic
   fun <S extends Segment<S>> `constructor-impl`(value: Any?) {
      value
   }

   @JvmStatic
   @JvmSynthetic
   fun `box-impl`(v: Any): SegmentOrClosed {
      SegmentOrClosed(v)
   }

   @JvmSynthetic
   fun `unbox-impl`() {
      this.value
   }

   @JvmStatic
   fun `equals-impl0`(p1: Any, p2: Any): Boolean {
      p1 == p2
   }
}
