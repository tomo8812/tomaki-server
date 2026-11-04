package kotlinx.coroutines.internal

internal final var value: Int
   internal final inline get() {
      return `$this$value`.get()
   }

   internal final inline set(value) {
      `$this$value`.set(value)
   }

