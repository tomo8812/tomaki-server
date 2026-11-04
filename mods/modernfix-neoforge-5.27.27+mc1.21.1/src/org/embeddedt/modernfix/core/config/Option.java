package org.embeddedt.modernfix.core.config;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class Option<T> {
   private final String name;
   private final OptionType<T> type;
   private Set<String> modDefined = null;
   private T value;
   private boolean userDefined;
   private Option<?> parent = null;

   public Option(String name, OptionType<T> type, T value, boolean userDefined) {
      this.name = name;
      this.type = type;
      this.value = value;
      this.userDefined = userDefined;
   }

   public OptionType<T> getType() {
      return this.type;
   }

   public T getValue() {
      return this.value;
   }

   public void setValue(T value, boolean userDefined) {
      if (!Objects.equals(this.value, value)) {
         this.value = value;
         this.userDefined = userDefined;
      }
   }

   public void addModOverride(T value, String modId) {
      if (!Objects.equals(this.value, value)) {
         this.value = value;
         if (this.modDefined == null) {
            this.modDefined = new LinkedHashSet<>();
         }

         this.modDefined.add(modId);
      }
   }

   public String getSerializedValue() {
      return this.type.serialize(this.value);
   }

   public void setFromString(String value, boolean userDefined) {
      this.setValue(this.type.parse(value), userDefined);
   }

   public <U> Option<U> asType(Class<U> type) {
      if (this.type.type() != type) {
         throw new IllegalStateException("Option '" + this.name + "' is not an option of type " + type.getName());
      } else {
         return (Option<U>)this;
      }
   }

   public Option<Boolean> asBoolean() {
      return this.asType(Boolean.class);
   }

   public void setParent(Option<?> option) {
      this.parent = option;
   }

   public Option<?> getParent() {
      return this.parent;
   }

   public int getDepth() {
      return this.parent == null ? 0 : this.parent.getDepth() + 1;
   }

   public boolean isEffectivelyDisabledByParent() {
      return this.parent != null
         && this.parent.type == OptionType.BOOLEAN
         && (!this.parent.asBoolean().getValue() || this.parent.isEffectivelyDisabledByParent());
   }

   public boolean isOverridden() {
      return this.isUserDefined() || this.isModDefined();
   }

   public boolean isUserDefined() {
      return this.userDefined;
   }

   public boolean isModDefined() {
      return this.modDefined != null;
   }

   public String getName() {
      return this.name;
   }

   public String getSelfName() {
      return this.parent == null ? this.name : this.name.substring(this.parent.getName().length() + 1);
   }

   public Collection<String> getDefiningMods() {
      return this.modDefined != null ? Collections.unmodifiableCollection(this.modDefined) : Collections.emptyList();
   }
}
