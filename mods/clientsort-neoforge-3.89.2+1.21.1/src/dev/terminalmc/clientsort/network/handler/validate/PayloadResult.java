package dev.terminalmc.clientsort.network.handler.validate;

import dev.terminalmc.clientsort.util.Localization;

public enum PayloadResult {
   INCONSISTENT_STATE(4, Localization.translationKey("payloadResult", "failure.inconsistentState")),
   INVALID_DATA(3, Localization.translationKey("payloadResult", "failure.invalidData")),
   UNSUPPORTED_OP(2, Localization.translationKey("payloadResult", "failure.unsupportedOp")),
   FAILURE(1, Localization.translationKey("payloadResult", "failure")),
   SUCCESS(0, Localization.translationKey("payloadResult", "success")),
   UNKNOWN(-1, Localization.translationKey("payloadResult", "unknown"));

   public final int code;
   public final String translationKey;

   PayloadResult(int code, String translationKey) {
      this.code = code;
      this.translationKey = translationKey;
   }

   public boolean isSuccess() {
      return this.code == SUCCESS.code;
   }

   public boolean isUnknown() {
      return this.code == UNKNOWN.code;
   }

   public static PayloadResult get(int code) {
      for (PayloadResult err : values()) {
         if (code == err.code) {
            return err;
         }
      }

      return UNKNOWN;
   }

   // $VF: synthetic method
   private static PayloadResult[] $values() {
      return new PayloadResult[]{INCONSISTENT_STATE, INVALID_DATA, UNSUPPORTED_OP, FAILURE, SUCCESS, UNKNOWN};
   }
}
