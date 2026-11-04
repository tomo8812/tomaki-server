package org.sinytra.adapter.types;

import org.objectweb.asm.Type;

public interface TypeAdapterProvider {
   TypeAdapter provide(Type var1, Type var2);
}
