package com.github.imifou.jsonschema.module.addon;

@Deprecated
public enum TypeFormat {
   DATE("date"),
   TIME("time"),
   DATE_TIME("date-time"),
   REGEX("regex"),
   EMAIL("email"),
   IDN_EMAIL("idn-email"),
   HOSTNAME("hostname"),
   IDN_HOSTNAME("idn-hostname"),
   IPV4("ipv4"),
   IPV6("ipv6"),
   JSON_POINTER("json-pointer"),
   RELATIVE_JSON_POINTER("relative-json-pointer"),
   URI("uri"),
   URI_REFERENCE("uri-reference"),
   URI_TEMPLATE("uri-template"),
   IRI("iri"),
   IRI_REFERENCE("iri-reference"),
   NONE("none");

   private final String value;

   TypeFormat(String value) {
      this.value = value;
   }

   @Override
   public String toString() {
      return this.value;
   }

   // $VF: synthetic method
   private static TypeFormat[] $values() {
      return new TypeFormat[]{
         DATE,
         TIME,
         DATE_TIME,
         REGEX,
         EMAIL,
         IDN_EMAIL,
         HOSTNAME,
         IDN_HOSTNAME,
         IPV4,
         IPV6,
         JSON_POINTER,
         RELATIVE_JSON_POINTER,
         URI,
         URI_REFERENCE,
         URI_TEMPLATE,
         IRI,
         IRI_REFERENCE,
         NONE
      };
   }
}
