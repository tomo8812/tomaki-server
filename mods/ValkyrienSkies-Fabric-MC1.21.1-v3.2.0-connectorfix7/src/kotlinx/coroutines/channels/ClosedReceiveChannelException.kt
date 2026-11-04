package kotlinx.coroutines.channels

import java.util.NoSuchElementException

public class ClosedReceiveChannelException(message: String?) : NoSuchElementException(message)
