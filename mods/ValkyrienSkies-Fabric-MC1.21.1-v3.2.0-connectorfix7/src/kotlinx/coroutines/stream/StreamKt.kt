package kotlinx.coroutines.stream

import java.util.stream.Stream
import kotlinx.coroutines.flow.Flow

public fun <T> Stream<Any>.consumeAsFlow(): Flow<Any> {
   return StreamFlow(`$this$consumeAsFlow`)
}
