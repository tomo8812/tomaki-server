package net.fabricmc.loader.impl.util.version;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.version.VersionComparisonOperator;
import net.fabricmc.loader.api.metadata.version.VersionInterval;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;

public final class VersionPredicateParser {
   private static final VersionComparisonOperator[] OPERATORS = VersionComparisonOperator.values();
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !VersionPredicateParser.class.desiredAssertionStatus();

   public static VersionPredicate parse(String predicate) throws VersionParsingException {
      List<VersionPredicateParser.SingleVersionPredicate> predicateList = new ArrayList<>();

      for (String s : predicate.split(" ")) {
         s = s.trim();
         if (!s.isEmpty() && !s.equals("*")) {
            VersionComparisonOperator operator = VersionComparisonOperator.EQUAL;

            for (VersionComparisonOperator op : OPERATORS) {
               if (s.startsWith(op.getSerialized())) {
                  operator = op;
                  s = s.substring(op.getSerialized().length());
                  break;
               }
            }

            Version version = VersionParser.parse(s, true);
            if (version instanceof SemanticVersion semVer) {
               if (semVer.hasWildcard()) {
                  if (operator != VersionComparisonOperator.EQUAL) {
                     throw new VersionParsingException(
                        "Invalid predicate: " + predicate + ", version ranges with wildcards (.X) require using the equality operator or no operator at all!"
                     );
                  }

                  assert !semVer.getPrereleaseKey().isPresent();
                  int compCount = semVer.getVersionComponentCount();
                  assert compCount == 2 || compCount == 3;
                  operator = compCount == 2 ? VersionComparisonOperator.SAME_TO_NEXT_MAJOR : VersionComparisonOperator.SAME_TO_NEXT_MINOR;
                  int[] newComponents = new int[semVer.getVersionComponentCount() - 1];

                  for (int i = 0; i < semVer.getVersionComponentCount() - 1; i++) {
                     newComponents[i] = semVer.getVersionComponent(i);
                  }

                  version = new SemanticVersionImpl(newComponents, "", semVer.getBuildKey().orElse(null));
               }
            } else {
               if (!operator.isMinInclusive() && !operator.isMaxInclusive()) {
                  throw new VersionParsingException(
                     "Invalid predicate: " + predicate + ", version ranges need to be semantic version compatible to use operators that exclude the bound!"
                  );
               }

               operator = VersionComparisonOperator.EQUAL;
            }

            predicateList.add(new VersionPredicateParser.SingleVersionPredicate(operator, version));
         }
      }

      if (predicateList.isEmpty()) {
         return VersionPredicateParser.AnyVersionPredicate.INSTANCE;
      } else {
         return predicateList.size() == 1 ? predicateList.get(0) : new VersionPredicateParser.MultiVersionPredicate(predicateList);
      }
   }

   public static Set<VersionPredicate> parse(Collection<String> predicates) throws VersionParsingException {
      Set<VersionPredicate> ret = new HashSet<>(predicates.size());

      for (String version : predicates) {
         ret.add(parse(version));
      }

      return ret;
   }

   public static VersionPredicate getAny() {
      return VersionPredicateParser.AnyVersionPredicate.INSTANCE;
   }

   static class AnyVersionPredicate implements VersionPredicate {
      static final VersionPredicate INSTANCE = new VersionPredicateParser.AnyVersionPredicate();

      private AnyVersionPredicate() {
      }

      public boolean test(Version t) {
         return true;
      }

      public List<? extends VersionPredicate.PredicateTerm> getTerms() {
         return Collections.emptyList();
      }

      @Override
      public VersionInterval getInterval() {
         return VersionIntervalImpl.INFINITE;
      }

      @Override
      public String toString() {
         return "*";
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Collection getTerms() {
         return this.getTerms();
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public boolean test(Object param1) {
         return this.test((Version)var1);
      }
   }

   static class MultiVersionPredicate implements VersionPredicate {
      private final List<VersionPredicateParser.SingleVersionPredicate> predicates;

      MultiVersionPredicate(List<VersionPredicateParser.SingleVersionPredicate> predicates) {
         this.predicates = predicates;
      }

      public boolean test(Version version) {
         Objects.requireNonNull(version, "null version");

         for (VersionPredicateParser.SingleVersionPredicate predicate : this.predicates) {
            if (!predicate.test(version)) {
               return false;
            }
         }

         return true;
      }

      public List<? extends VersionPredicate.PredicateTerm> getTerms() {
         return this.predicates;
      }

      @Override
      public VersionInterval getInterval() {
         if (this.predicates.isEmpty()) {
            return VersionPredicateParser.AnyVersionPredicate.INSTANCE.getInterval();
         }

         VersionInterval ret = this.predicates.get(0).getInterval();

         for (int i = 1; i < this.predicates.size(); i++) {
            ret = VersionIntervalImpl.and(ret, this.predicates.get(i).getInterval());
         }

         return ret;
      }

      @Override
      public boolean equals(Object obj) {
         return obj instanceof VersionPredicateParser.MultiVersionPredicate o ? this.predicates.equals(o.predicates) : false;
      }

      @Override
      public int hashCode() {
         return this.predicates.hashCode();
      }

      @Override
      public String toString() {
         StringBuilder ret = new StringBuilder();

         for (VersionPredicateParser.SingleVersionPredicate predicate : this.predicates) {
            if (ret.length() > 0) {
               ret.append(' ');
            }

            ret.append(predicate.toString());
         }

         return ret.toString();
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Collection getTerms() {
         return this.getTerms();
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public boolean test(Object param1) {
         return this.test((Version)var1);
      }
   }

   static class SingleVersionPredicate implements VersionPredicate, VersionPredicate.PredicateTerm {
      private final VersionComparisonOperator operator;
      private final Version refVersion;

      SingleVersionPredicate(VersionComparisonOperator operator, Version refVersion) {
         this.operator = operator;
         this.refVersion = refVersion;
      }

      public boolean test(Version version) {
         Objects.requireNonNull(version, "null version");
         return this.operator.test(version, this.refVersion);
      }

      public List<VersionPredicate.PredicateTerm> getTerms() {
         return Collections.singletonList(this);
      }

      @Override
      public VersionInterval getInterval() {
         return this.refVersion instanceof SemanticVersion version
            ? new VersionIntervalImpl(
               this.operator.minVersion(version), this.operator.isMinInclusive(), this.operator.maxVersion(version), this.operator.isMaxInclusive()
            )
            : new VersionIntervalImpl(this.refVersion, true, this.refVersion, true);
      }

      @Override
      public VersionComparisonOperator getOperator() {
         return this.operator;
      }

      @Override
      public Version getReferenceVersion() {
         return this.refVersion;
      }

      @Override
      public boolean equals(Object obj) {
         return !(obj instanceof VersionPredicateParser.SingleVersionPredicate o) ? false : this.operator == o.operator && this.refVersion.equals(o.refVersion);
      }

      @Override
      public int hashCode() {
         return this.operator.ordinal() * 31 + this.refVersion.hashCode();
      }

      @Override
      public String toString() {
         return this.operator.getSerialized().concat(this.refVersion.toString());
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Collection getTerms() {
         return this.getTerms();
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public boolean test(Object param1) {
         return this.test((Version)var1);
      }
   }
}
