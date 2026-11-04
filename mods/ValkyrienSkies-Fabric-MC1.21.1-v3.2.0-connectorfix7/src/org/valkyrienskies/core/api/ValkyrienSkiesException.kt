package org.valkyrienskies.core.api

public open class ValkyrienSkiesException : Exception {
   public constructor(message: String, cause: Throwable) : super(message, cause)
   public constructor(message: String) : super(message)}
