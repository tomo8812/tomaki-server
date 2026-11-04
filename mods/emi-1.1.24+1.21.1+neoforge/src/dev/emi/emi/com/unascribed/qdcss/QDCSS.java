package dev.emi.emi.com.unascribed.qdcss;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QDCSS {
   private final String prelude;
   private final Map<String, List<QDCSS.BlameString>> data;
   private Consumer<String> yapLog;
   private static final Pattern JUNK_PATTERN = Pattern.compile("^(\\s*(/\\*.*?\\*/)?\\s*)*$", 32);
   private static final Pattern RULESET_PATTERN = Pattern.compile("[#.]?(\\w+?)\\s*\\{(.*?)\\}", 32);
   private static final Pattern RULE_PATTERN = Pattern.compile("(\\S+?)\\s*:\\s*(\\\".*?\\\"|'.*?'|.+?)\\s*(;|$)");
   private static final Splitter SLASH_SPLITTER = Splitter.on('/');

   private QDCSS(String prelude, Map<String, List<QDCSS.BlameString>> data) {
      this.prelude = prelude;
      this.data = data;
   }

   public void setYapLog(Consumer<String> yapLog) {
      this.yapLog = yapLog;
   }

   public boolean containsKey(String key) {
      return this.data.containsKey(key) && !this.data.get(key).isEmpty();
   }

   public void put(String key, String value) {
      List<QDCSS.BlameString> li = new ArrayList<>();
      li.add(new QDCSS.BlameString(value));
      this.data.put(key, li);
   }

   public void put(String key, String... values) {
      List<QDCSS.BlameString> li = new ArrayList<>();

      for (String v : values) {
         li.add(new QDCSS.BlameString(v));
      }

      this.data.put(key, li);
   }

   public void put(String key, Iterable<String> values) {
      List<QDCSS.BlameString> li = new ArrayList<>();

      for (String v : values) {
         li.add(new QDCSS.BlameString(v));
      }

      this.data.put(key, li);
   }

   public List<String> getAll(String key) {
      return this.unwrap(this.data.get(key));
   }

   private List<QDCSS.BlameString> getAllBlamed(String key) {
      return this.data.containsKey(key) ? this.data.get(key) : Collections.emptyList();
   }

   public String getBlame(String key) {
      return this.getBlamed(key).map(QDCSS.BlameString::blame).orElse("<unknown>");
   }

   public String getBlame(String key, int index) {
      return this.containsKey(key) ? this.getAllBlamed(key).get(index).blame() : "<unknown>";
   }

   private List<String> unwrap(List<QDCSS.BlameString> list) {
      return list == null ? Collections.emptyList() : new AbstractList<String>() {
         public String get(int index) {
            return QDCSS.this.unwrap(list.get(index));
         }

         @Override
         public int size() {
            return list.size();
         }

         // $VF: synthetic method
         // $VF: bridge method
         @Override
         public Object get(int param1) {
            return this.get(var1);
         }
      };
   }

   private String unwrap(QDCSS.BlameString bs) {
      return bs == null ? null : bs.value;
   }

   public Optional<String> get(String key) {
      return Optional.ofNullable(this.getLast(this.getAll(key)));
   }

   private Optional<QDCSS.BlameString> getBlamed(String key) {
      return Optional.ofNullable(this.getLast(this.getAllBlamed(key)));
   }

   public Optional<Integer> getInt(String key) throws QDCSS.BadValueException {
      return this.getParsed(key, Integer::parseInt, () -> "a whole number");
   }

   public Optional<Double> getDouble(String key) throws QDCSS.BadValueException {
      return this.getParsed(key, Double::parseDouble, () -> "a number");
   }

   public Optional<Boolean> getBoolean(String key) throws QDCSS.BadValueException {
      return this.getParsed(key, this::strictParseBoolean, () -> "true/on or false/off");
   }

   private boolean strictParseBoolean(String s) {
      switch (s.toLowerCase(Locale.ROOT)) {
         case "on":
         case "true":
            return true;
         case "off":
         case "false":
            return false;
         default:
            throw new IllegalArgumentException();
      }
   }

   public <E extends Enum<E>> Optional<E> getEnum(String key, Class<E> clazz) throws QDCSS.BadValueException {
      return this.getParsed(key, s -> Enum.valueOf(clazz, s.toUpperCase(Locale.ROOT)), () -> {
         StringBuilder sb = new StringBuilder("one of ");
         boolean first = true;

         for (E e : (Enum[])clazz.getEnumConstants()) {
            if (first) {
               first = false;
            } else {
               sb.append(", ");
            }

            sb.append(e.name().toLowerCase(Locale.ROOT));
         }

         return sb.toString();
      });
   }

   private <T> Optional<T> getParsed(String key, Function<String, ? extends T> parser, Supplier<String> error) throws QDCSS.BadValueException {
      Optional<String> s = this.get(key);
      if (!s.isPresent()) {
         return Optional.empty();
      }

      try {
         return Optional.of((T)parser.apply(s.get()));
      } catch (IllegalArgumentException e) {
         String msg = key + " must be " + error.get() + " (got " + s.get() + ") near " + this.getBlame(key);
         if (this.yapLog != null) {
            this.yapLog.accept(msg);
            return Optional.empty();
         } else {
            throw new QDCSS.BadValueException(msg, e);
         }
      }
   }

   private <T> T getLast(List<T> list) {
      return list != null && !list.isEmpty() ? list.get(list.size() - 1) : null;
   }

   public Set<String> keySet() {
      return this.data.keySet();
   }

   public Set<Entry<String, List<String>>> entrySet() {
      return new AbstractSet<Entry<String, List<String>>>() {
         @Override
         public Iterator<Entry<String, List<String>>> iterator() {
            final Iterator<Entry<String, List<QDCSS.BlameString>>> delegate = QDCSS.this.data.entrySet().iterator();
            return new Iterator<Entry<String, List<String>>>() {
               @Override
               public boolean hasNext() {
                  return delegate.hasNext();
               }

               public Entry<String, List<String>> next() {
                  Entry<String, List<QDCSS.BlameString>> den = delegate.next();
                  return new SimpleImmutableEntry<>(den.getKey(), QDCSS.this.unwrap(den.getValue()));
               }

               // $VF: synthetic method
               // $VF: bridge method
               @Override
               public Object next() {
                  return this.next();
               }
            };
         }

         @Override
         public int size() {
            return this.size();
         }
      };
   }

   public int size() {
      return this.data.size();
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("; Loaded from ");
      sb.append(this.prelude);
      sb.append("\r\n");

      for (Entry<String, List<String>> en : this.entrySet()) {
         for (String v : en.getValue()) {
            sb.append(en.getKey());
            sb.append("=");
            sb.append(v);
            sb.append("\r\n");
         }
      }

      return sb.toString();
   }

   public QDCSS merge(QDCSS that) {
      Map<String, List<QDCSS.BlameString>> newData = new LinkedHashMap<>(Math.max(this.size(), that.size()));
      newData.putAll(this.data);

      for (Entry<String, List<QDCSS.BlameString>> en : that.data.entrySet()) {
         if (newData.containsKey(en.getKey())) {
            List<QDCSS.BlameString> merged = new ArrayList<>(newData.get(en.getKey()).size() + en.getValue().size());
            merged.addAll(newData.get(en.getKey()));
            merged.addAll(en.getValue());
            newData.put(en.getKey(), Collections.unmodifiableList(merged));
         } else {
            newData.put(en.getKey(), en.getValue());
         }
      }

      return new QDCSS(this.prelude + ", merged with " + that.prelude, Collections.unmodifiableMap(newData));
   }

   public Map<String, String> flatten() {
      return new AbstractMap<String, String>() {
         public String get(Object key) {
            return QDCSS.this.get((String)key).orElse(null);
         }

         @Override
         public boolean containsKey(Object key) {
            return QDCSS.this.containsKey((String)key);
         }

         @Override
         public Set<String> keySet() {
            return QDCSS.this.keySet();
         }

         @Override
         public int size() {
            return QDCSS.this.size();
         }

         @Override
         public Set<Entry<String, String>> entrySet() {
            return new AbstractSet<Entry<String, String>>() {
               @Override
               public Iterator<Entry<String, String>> iterator() {
                  final Iterator<Entry<String, List<String>>> delegate = QDCSS.this.entrySet().iterator();
                  return new Iterator<Entry<String, String>>() {
                     @Override
                     public boolean hasNext() {
                        return delegate.hasNext();
                     }

                     public Entry<String, String> next() {
                        Entry<String, List<String>> den = delegate.next();
                        return new SimpleImmutableEntry<>(den.getKey(), QDCSS.this.getLast(den.getValue()));
                     }

                     // $VF: synthetic method
                     // $VF: bridge method
                     @Override
                     public Object next() {
                        return this.next();
                     }
                  };
               }

               @Override
               public int size() {
                  return this.size();
               }
            };
         }

         // $VF: synthetic method
         // $VF: bridge method
         @Override
         public Object get(Object param1) {
            return this.get(var1);
         }
      };
   }

   public static QDCSS load(String fileName, String s) throws QDCSS.SyntaxErrorException {
      Map<String, List<QDCSS.BlameString>> data = new LinkedHashMap<>();
      Matcher ruleset = RULESET_PATTERN.matcher(s);

      int lastEnd;
      for (lastEnd = 0; ruleset.find(); lastEnd = ruleset.end()) {
         String skipped = s.substring(lastEnd, ruleset.start());
         if (!JUNK_PATTERN.matcher(skipped).matches()) {
            throw new QDCSS.SyntaxErrorException("Expected a ruleset near line " + getLine(s, ruleset.start()) + " in " + fileName);
         }

         String selector = ruleset.group(1);
         String rules = ruleset.group(2);
         Matcher rule = RULE_PATTERN.matcher(rules);

         int lastRulesEnd;
         for (lastRulesEnd = 0; rule.find(); lastRulesEnd = rule.end()) {
            String skippedRule = rules.substring(lastRulesEnd, rule.start());
            if (!JUNK_PATTERN.matcher(skippedRule).matches()) {
               throw new QDCSS.SyntaxErrorException("Expected a rule near line " + getLine(s, ruleset.start(2) + rule.start()) + " in " + fileName);
            }

            String property = rule.group(1);
            String value = rule.group(2);
            String key = selector + "." + property;
            if (!data.containsKey(key)) {
               data.put(key, Lists.newArrayList());
            }

            data.get(key).add(new QDCSS.BlameString(value, fileName, getLine(s, ruleset.start(2) + rule.start())));
         }

         String skippedRule = rules.substring(lastRulesEnd);
         if (!JUNK_PATTERN.matcher(skippedRule).matches()) {
            throw new QDCSS.SyntaxErrorException("Expected a rule near line " + getLine(s, ruleset.start(2) + lastRulesEnd) + " in " + fileName);
         }
      }

      String skipped = s.substring(lastEnd);
      if (!JUNK_PATTERN.matcher(skipped).matches()) {
         throw new QDCSS.SyntaxErrorException("Expected a ruleset or EOF near line " + getLine(s, lastEnd) + " in " + fileName);
      } else {
         return new QDCSS(fileName, data);
      }
   }

   private static int getLine(String s, int start) {
      int line = 1;

      for (int i = 0; i < start; i++) {
         if (s.charAt(i) == '\n') {
            line++;
         }
      }

      return line;
   }

   public static QDCSS load(File f) throws IOException {
      try (InputStream in = new FileInputStream(f)) {
         return load(f.getName(), in);
      }
   }

   public static QDCSS load(Path p) throws IOException {
      try (InputStream in = Files.newInputStream(p)) {
         return load(p.getFileName().toString(), in);
      }
   }

   public static QDCSS load(URL u) throws IOException {
      try (InputStream in = u.openStream()) {
         return load((String)Iterables.getLast(SLASH_SPLITTER.split(u.getPath())), in);
      }
   }

   public static QDCSS load(String fileName, InputStream in) throws IOException {
      return load(fileName, new InputStreamReader(in, StandardCharsets.UTF_8));
   }

   public static QDCSS load(String fileName, Reader r) throws IOException {
      return load(fileName, CharStreams.toString(r));
   }

   public static class BadValueException extends QDCSS.QDCSSException {
      public BadValueException() {
      }

      public BadValueException(String message, Throwable cause) {
         super(message, cause);
      }

      public BadValueException(String s) {
         super(s);
      }

      public BadValueException(Throwable cause) {
         super(cause);
      }
   }

   private static class BlameString {
      public final String value;
      public final String file;
      public final int line;

      private BlameString(String value) {
         this(value, null, -1);
      }

      private BlameString(String value, String file, int line) {
         this.value = value;
         this.file = file;
         this.line = line;
      }

      public String blame() {
         return this.line == -1 ? "<unknown>" : "line " + this.line + " in " + this.file;
      }
   }

   public static class QDCSSException extends IllegalArgumentException {
      public QDCSSException() {
      }

      public QDCSSException(String message, Throwable cause) {
         super(message, cause);
      }

      public QDCSSException(String s) {
         super(s);
      }

      public QDCSSException(Throwable cause) {
         super(cause);
      }
   }

   public static class SyntaxErrorException extends QDCSS.QDCSSException {
      public SyntaxErrorException() {
      }

      public SyntaxErrorException(String message, Throwable cause) {
         super(message, cause);
      }

      public SyntaxErrorException(String s) {
         super(s);
      }

      public SyntaxErrorException(Throwable cause) {
         super(cause);
      }
   }
}
