package reloc.net.fabricmc.classtweaker.api;

import java.util.Map;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import reloc.net.fabricmc.classtweaker.utils.EntryTriple;

@NonExtendable
public interface AccessWidener {
   AccessWidener.Access getClassAccess();

   AccessWidener.Access getMethodAccess(EntryTriple var1);

   AccessWidener.Access getFieldAccess(EntryTriple var1);

   AccessWidener.Access getCanonicalConstructorAccess();

   Map<EntryTriple, AccessWidener.Access> getAllMethodAccesses();

   Map<EntryTriple, AccessWidener.Access> getAllFieldAccesses();

   @NonExtendable
   interface Access {
      boolean isAccessible();

      boolean isExtendable();

      boolean isMutable();

      default boolean isChanged() {
         return this.isAccessible() || this.isExtendable() || this.isMutable();
      }

      int apply(int var1, String var2, int var3);
   }
}
