package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BinaryNode;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.api.attachment.BinaryAttachmentSerializer;

public final class CM implements CK {
   private final BinaryAttachmentSerializer<?> a;

   public CM(BinaryAttachmentSerializer<?> var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      super();
      this.a = var1;
   }

   @Override
   public final JsonNode a(Object var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();
      Intrinsics.checkNotNull(this.a);
      ((BinaryAttachmentSerializer<Object>)this.a).serialize(var1, new DataOutputStream(var2));
      return new BinaryNode(var2.toByteArray());
   }

   @Override
   public final Object a(JsonNode var1, Class<?> var2) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      byte[] var3 = ((BinaryNode)var1).binaryValue();
      ByteArrayInputStream var4 = new ByteArrayInputStream(var3);
      return this.a.deserialize(new DataInputStream(var4));
   }
}
