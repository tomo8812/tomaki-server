package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.members.HierarchicType;
import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.classmate.members.ResolvedMethod;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class bb implements aI {
   private static final Logger a = LoggerFactory.getLogger(bb.class);
   private final aK b;
   private final aU c;
   private final Map<aX, ObjectNode> d = new LinkedHashMap<>();
   private final Map<aX, List<ObjectNode>> e = new HashMap<>();
   private final Map<aX, List<ObjectNode>> f = new HashMap<>();

   public bb(aK var1, aU var2) {
      this.b = var1;
      this.c = var2;
   }

   @Override
   public final aK a() {
      return this.b;
   }

   @Override
   public final aU b() {
      return this.c;
   }

   public final aX c(ResolvedType var1) {
      this.a(var1, null, false);
      return new aX(var1, null);
   }

   private bb a(ResolvedType var1, ObjectNode var2, av var3) {
      this.d.put(new aX(var1, var3), var2);
      return this;
   }

   private boolean a(ResolvedType var1, av var2) {
      return this.d.containsKey(new aX(var1, var2));
   }

   public final ObjectNode a(aX var1) {
      return this.d.get(var1);
   }

   public final Set<aX> c() {
      return Collections.unmodifiableSet(this.d.keySet());
   }

   public final bb a(ResolvedType var1, ObjectNode var2, av var3, boolean var4) {
      Map var6 = var4 ? this.f : this.e;
      aX var5 = new aX(var1, var3);
      var6.computeIfAbsent(var5, var0 -> new ArrayList<>()).add(var2);
      return this;
   }

   public final List<ObjectNode> b(aX var1) {
      return Collections.unmodifiableList(this.e.getOrDefault(var1, Collections.emptyList()));
   }

   public final List<ObjectNode> c(aX var1) {
      return Collections.unmodifiableList(this.f.getOrDefault(var1, Collections.emptyList()));
   }

   @Override
   public final ObjectNode a(ResolvedType var1) {
      return this.a(var1, (bk)null);
   }

   @Override
   public final ObjectNode b(ResolvedType var1) {
      return this.a(var1, (bv)null);
   }

   @Override
   public final ObjectNode a(ResolvedType var1, bk var2) {
      ObjectNode var3 = this.b.o();
      aV var4 = this.c.b(var1);
      this.a(var4, var3, false, true, var2);
      return var3;
   }

   @Override
   public final ObjectNode a(ay var1, ax<ay> var2) {
      return this.a(var1, false, true, var2);
   }

   @Override
   public final JsonNode a(aC var1, ax<aC> var2) {
      return this.a(var1, false, true, var2);
   }

   @Override
   public final ObjectNode a(ResolvedType var1, bv var2) {
      ObjectNode var3 = this.b.o();
      aV var4 = this.c.b(var1);
      this.a(var4, var3, false, false, var2);
      return var3;
   }

   @Override
   public final ObjectNode b(ay var1, ax<ay> var2) {
      return this.a(var1, false, false, var2);
   }

   @Override
   public final JsonNode b(aC var1, ax<aC> var2) {
      return this.a(var1, false, false, var2);
   }

   private void a(ResolvedType var1, ObjectNode var2, boolean var3) {
      aV var4 = this.c.b(var1);
      this.a(var4, var2, false, false, null);
   }

   private void a(aV var1, ObjectNode var2, boolean var3, boolean var4, av var5) {
      ResolvedType var6 = var1.d;
      if (!var4 && this.a(var6, var5)) {
         a.debug("adding reference to existing definition of {}", var6);
         this.a(var6, var2, var5, var3);
      } else {
         boolean var7;
         at var8;
         ObjectNode var11;
         if ((var8 = this.b.a(var6, this, var5)) == null || !var8.b() && !var4) {
            var7 = aU.c(var6);
            if (!var4 && (!var7 || var2 == null || var8 != null)) {
               var11 = this.b.o();
               this.a(var6, var11, var5);
               if (var2 != null) {
                  this.a(var6, var2, var5, var3);
               }
            } else {
               var11 = var2;
            }

            boolean var10000;
            if (var8 != null) {
               a.debug("applying configured custom definition for {}", var6);
               var11.setAll(var8.a);
               var10000 = var8.c();
            } else if (var7) {
               a.debug("generating array definition for {}", var6);
               this.a(var1, var11, var3);
               var10000 = true;
            } else {
               a.debug("generating definition for {}", var6);
               var10000 = !this.a(var6, var11);
            }

            var7 = var10000;
         } else {
            var7 = var8.c();
            if (var2 == null) {
               a.debug("storing configured custom inline type for {} as definition (since it is the main schema \"#\")", var6);
               var11 = var8.a;
               this.a(var6, var11, var5);
            } else {
               a.debug("directly applying configured custom inline type for {}", var6);
               var2.setAll(var8.a);
               var11 = var2;
            }

            if (var3) {
               this.a(var11);
            }
         }

         if (var7) {
            Set var13 = this.b(var11);
            (var2 = aW.a(var1, this, var13)).setAll(var11);
            var11.setAll(var2);
         }

         this.b.r().forEach(var3x -> var3x.a(var11, var1, this));
      }
   }

   private boolean a(ResolvedType var1, ObjectNode var2) {
      List var3;
      if ((var3 = this.b.a(var1, this)).isEmpty()) {
         this.b(var1, var2);
         return false;
      } else {
         aP var4 = var3.size() == 1 ? aP.TAG_ALLOF : aP.TAG_ANYOF;
         ArrayNode var5 = var2.withArray(this.a(var4));
         var3.forEach(var2x -> this.a(var2x, var5.addObject(), false));
         return true;
      }
   }

   private Set<String> b(ObjectNode var1) {
      JsonNode var2;
      Set var3;
      if ((var2 = var1.get(this.a(aP.TAG_TYPE))) == null) {
         var3 = Collections.emptySet();
      } else if (var2.isTextual()) {
         var3 = Collections.singleton(var2.textValue());
      } else {
         var3 = StreamSupport.stream(var2.spliterator(), false).map(JsonNode::textValue).collect(Collectors.toSet());
      }

      return var3;
   }

   private void a(aV var1, ObjectNode var2, boolean var3) {
      if (var3) {
         ArrayNode var5 = this.b.p().add(this.a(aP.TAG_TYPE_ARRAY)).add(this.a(aP.TAG_TYPE_NULL));
         var2.set(this.a(aP.TAG_TYPE), var5);
      } else {
         var2.put(this.a(aP.TAG_TYPE), this.a(aP.TAG_TYPE_ARRAY));
      }

      aB var6;
      if (var1 instanceof aB && !(var6 = (aB)var1).c) {
         aB var8 = ((aB)var1).e();
         JsonNode var4;
         if (var1 instanceof ay) {
            var4 = this.a((ay)var8);
         } else {
            if (!(var1 instanceof aC)) {
               throw new IllegalStateException("Unsupported member type: " + var1.getClass().getName());
            }

            var4 = this.a((aC)var8);
         }

         var2.set(this.a(aP.TAG_ITEMS), var4);
      } else {
         ObjectNode var7 = this.b.o();
         var2.set(this.a(aP.TAG_ITEMS), var7);
         this.a(var1.w(), var7, false);
      }
   }

   private void b(ResolvedType var1, ObjectNode var2) {
      var2.put(this.a(aP.TAG_TYPE), this.a(aP.TAG_TYPE_OBJECT));
      LinkedHashMap var3 = new LinkedHashMap();
      HashSet var4 = new HashSet();
      this.a(var1, var3, var4);
      if (!var3.isEmpty()) {
         ObjectNode var8 = this.b.o();
         List var9;
         Iterator var5 = (var9 = var3.values().stream().sorted(this.b::a).collect(Collectors.toList())).iterator();

         while (var5.hasNext()) {
            aB var6;
            JsonNode var7;
            if ((var6 = (aB)var5.next()) instanceof ay) {
               var7 = this.a((ay)var6);
            } else {
               if (!(var6 instanceof aC)) {
                  throw new IllegalStateException("Unsupported member scope of type " + var6.getClass());
               }

               var7 = this.a((aC)var6);
            }

            var8.set(var6.s(), var7);
         }

         var2.set(this.a(aP.TAG_PROPERTIES), var8);
         if (!var4.isEmpty()) {
            ArrayNode var10 = this.b.p();
            var9.stream().map(aB::s).filter(var4::contains).forEach(var10::add);
            var2.set(this.a(aP.TAG_REQUIRED), var10);
         }
      }
   }

   private void a(ResolvedType var1, Map<String, aB<?, ?>> var2, Set<String> var3) {
      a.debug("collecting non-static fields and methods from {}", var1);
      ResolvedTypeWithMembers var4 = this.c.a(var1);
      this.a(var4, ResolvedTypeWithMembers::getMemberFields, var2, var3);
      this.b(var4, ResolvedTypeWithMembers::getMemberMethods, var2, var3);
      boolean var5 = this.b.i();
      boolean var6 = this.b.j();
      if (var5 || var6) {
         Iterator var7 = var4.allTypesAndOverrides().iterator();

         while (var7.hasNext()) {
            ResolvedType var8 = ((HierarchicType)var7.next()).getType();
            a.debug("collecting static fields and methods from {}", var8);
            if (var5 && !var8.getStaticFields().isEmpty() || var6 && !var8.getStaticMethods().isEmpty()) {
               ResolvedTypeWithMembers var9;
               if (var8 == var1) {
                  var9 = var4;
               } else {
                  var9 = this.c.a(var8);
               }

               if (var5) {
                  this.a(var9, ResolvedTypeWithMembers::getStaticFields, var2, var3);
               }

               if (var6) {
                  this.b(var9, ResolvedTypeWithMembers::getStaticMethods, var2, var3);
               }
            }
         }
      }
   }

   private void a(ResolvedTypeWithMembers var1, Function<ResolvedTypeWithMembers, ResolvedField[]> var2, Map<String, aB<?, ?>> var3, Set<String> var4) {
      Stream.<ResolvedField>of((ResolvedField[])var2.apply(var1)).map(var2x -> this.c.a(var2x, var1)).filter(var1x -> !this.b.e(var1x)).forEach(var3x -> {
         Set var4x = var4;
         Map var9 = var3;
         ay var8 = var3x;
         bb var7 = this;
         ay var5;
         String var6;
         if (var8.c) {
            var5 = var8;
            var6 = var8.s();
         } else {
            String var10;
            var6 = (var5 = (var10 = var7.b.h(var8)) == null ? var8 : var8.a(var10)).s();
            if (var7.b.b(var8)) {
               var4x.add(var6);
            }

            if (var9.containsKey(var6)) {
               a.debug("ignoring overridden {}.{}", var5.a.getDeclaringType(), var5.k());
               return;
            }
         }

         var9.put(var6, var5);
      });
   }

   private void b(ResolvedTypeWithMembers var1, Function<ResolvedTypeWithMembers, ResolvedMethod[]> var2, Map<String, aB<?, ?>> var3, Set<String> var4) {
      Stream.<ResolvedMethod>of((ResolvedMethod[])var2.apply(var1)).map(var2x -> this.c.a(var2x, var1)).filter(var1x -> !this.b.e(var1x)).forEach(var3x -> {
         Set var4x = var4;
         Map var9 = var3;
         aC var8 = var3x;
         bb var7 = this;
         aC var5;
         String var6;
         if (var8.c) {
            var5 = var8;
            var6 = var8.s();
         } else {
            String var10;
            var6 = (var5 = (var10 = var7.b.h(var8)) == null ? var8 : var8.a(var10)).s();
            if (var7.b.b(var8)) {
               var4x.add(var6);
            }

            if (var9.containsKey(var6)) {
               a.debug("ignoring overridden {}.{}", var5.a.getDeclaringType(), var5.k());
               return;
            }
         }

         var9.put(var6, var5);
      });
   }

   private void a(ay var1, Map<String, aB<?, ?>> var2, Set<String> var3) {
      ay var4;
      String var5;
      if (var1.c) {
         var4 = var1;
         var5 = var1.s();
      } else {
         String var6;
         var5 = (var4 = (var6 = this.b.h(var1)) == null ? var1 : var1.a(var6)).s();
         if (this.b.b(var1)) {
            var3.add(var5);
         }

         if (var2.containsKey(var5)) {
            a.debug("ignoring overridden {}.{}", var4.a.getDeclaringType(), var4.k());
            return;
         }
      }

      var2.put(var5, var4);
   }

   private ObjectNode a(ay var1) {
      List var2;
      if ((var2 = this.b.g(var1)) == null) {
         var2 = this.b.a(var1.d, this);
      }

      if (var2 != null && !var2.isEmpty()) {
         var2 = var2.stream().map(var1::a).collect(Collectors.toList());
      } else {
         var2 = Collections.singletonList(var1);
      }

      boolean var5 = !((Field)var1.a.getRawMember()).isEnumConstant() && (!var1.c || this.b.m()) && this.b.a(var1);
      if (var2.size() == 1) {
         return this.a((ay)var2.get(0), var5, false, null);
      }

      ObjectNode var3;
      ArrayNode var4 = (var3 = this.b.o()).withArray(this.a(aP.TAG_ANYOF));
      if (var5) {
         var4.addObject().put(this.a(aP.TAG_TYPE), this.a(aP.TAG_TYPE_NULL));
      }

      var2.forEach(var2x -> var4.add(this.a(var2x, false, false, null)));
      return var3;
   }

   private ObjectNode a(ay var1, boolean var2, boolean var3, ax<ay> var4) {
      ObjectNode var5 = this.b.o();
      ObjectNode var6 = aW.a(var1, this);
      this.a(var1, var5, var2, var3, var6, var4);
      return var5;
   }

   private void a(aC var1, Map<String, aB<?, ?>> var2, Set<String> var3) {
      aC var4;
      String var5;
      if (var1.c) {
         var4 = var1;
         var5 = var1.s();
      } else {
         String var6;
         var5 = (var4 = (var6 = this.b.h(var1)) == null ? var1 : var1.a(var6)).s();
         if (this.b.b(var1)) {
            var3.add(var5);
         }

         if (var2.containsKey(var5)) {
            a.debug("ignoring overridden {}.{}", var4.a.getDeclaringType(), var4.k());
            return;
         }
      }

      var2.put(var5, var4);
   }

   private JsonNode a(aC var1) {
      List var2;
      if ((var2 = this.b.g(var1)) == null && !var1.b()) {
         var2 = this.b.a(var1.d, this);
      }

      if (var2 != null && !var2.isEmpty()) {
         var2 = var2.stream().map(var1::a).collect(Collectors.toList());
      } else {
         var2 = Collections.singletonList(var1);
      }

      boolean var5 = var1.b() || (!var1.c || this.b.m()) && this.b.a(var1);
      if (var2.size() == 1) {
         return this.a((aC)var2.get(0), var5, false, null);
      }

      ObjectNode var3;
      ArrayNode var4 = (var3 = this.b.o()).withArray(this.a(aP.TAG_ANYOF));
      if (var5) {
         var4.add(this.b.o().put(this.a(aP.TAG_TYPE), this.a(aP.TAG_TYPE_NULL)));
      }

      var2.forEach(var2x -> var4.add(this.a(var2x, false, false, null)));
      return var3;
   }

   private JsonNode a(aC var1, boolean var2, boolean var3, ax<aC> var4) {
      if (var1.b()) {
         return BooleanNode.FALSE;
      }

      ObjectNode var5 = this.b.o();
      ObjectNode var6 = aW.a(var1, this);
      this.a(var1, var5, var2, var3, var6, var4);
      return var5;
   }

   private <M extends aB<?, ?>> void a(M var1, ObjectNode var2, boolean var3, boolean var4, ObjectNode var5, ax<M> var6) {
      at var9;
      if ((var9 = this.b.a((M)var1, this, var6)) != null && var9.b()) {
         var2.setAll(var9.a);
         if (var9.c()) {
            aW.a(var2, var5);
            Set var11 = this.b(var2);
            ObjectNode var8 = aW.a(var1, this, var11);
            aW.a(var2, var8);
         }

         if (var3) {
            this.a(var2);
            return;
         }
      } else {
         ObjectNode var10;
         if ((var9 == null || var9.c()) && var5 != null && var5.size() != 0) {
            if (var9 == null && var1.v()) {
               var10 = var2;
               aW.a(var2, var5);
            } else {
               var10 = this.b.o();
               var2.set(this.a(aP.TAG_ALLOF), this.b.p().add(var10).add(var5));
            }
         } else {
            var10 = var2;
         }

         try {
            this.a(var1, var10, var3, var4, null);
            return;
         } catch (UnsupportedOperationException var7) {
            a.warn("Skipping type definition due to error", var7);
         }
      }
   }

   @Override
   public final ObjectNode a(ObjectNode var1) {
      String var2 = this.a(aP.TAG_TYPE_NULL);
      if (!var1.has(this.a(aP.TAG_REF))
         && !var1.has(this.a(aP.TAG_ALLOF))
         && !var1.has(this.a(aP.TAG_ANYOF))
         && !var1.has(this.a(aP.TAG_ONEOF))
         && !var1.has(this.a(aP.TAG_CONST))
         && !var1.has(this.a(aP.TAG_ENUM))) {
         JsonNode var8;
         if ((var8 = var1.get(this.a(aP.TAG_TYPE))) instanceof ArrayNode) {
            var8 = var8;
            boolean var4 = false;

            for (JsonNode var6 : var8) {
               var4 = var4 || var2.equals(var6.textValue());
            }

            if (!var4) {
               var1.replace(this.a(aP.TAG_TYPE), this.b.p().addAll(var8).add(var2));
            }
         } else if (var8 instanceof TextNode && !var2.equals(var8.textValue())) {
            var1.replace(this.a(aP.TAG_TYPE), this.b.p().add(var8).add(var2));
         }
      } else {
         ObjectNode var3 = this.b.o().put(this.a(aP.TAG_TYPE), var2);
         ArrayNode var7 = this.b.p().add(var3).add(this.b.o().setAll(var1));
         var1.removeAll();
         var1.set(this.a(aP.TAG_ANYOF), var7);
      }

      return var1;
   }

   @Override
   public final String a(aP var1) {
      return this.b.a(var1);
   }
}
