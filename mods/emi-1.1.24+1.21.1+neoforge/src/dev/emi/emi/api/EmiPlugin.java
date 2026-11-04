package dev.emi.emi.api;

public interface EmiPlugin {
   default void initialize(EmiInitRegistry registry) {
   }

   void register(EmiRegistry var1);
}
