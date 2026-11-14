package com.kishku7.chunksmith.command;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

public final class CommandArguments {
   private final int size;
   private final Queue<String> args = new LinkedList<>();

   private CommandArguments(List<String> arguments) {
      List<String> unquoted = new ArrayList<>(arguments.size());

      for (String argument : arguments) {
         unquoted.add(unquote(argument));
      }

      this.size = unquoted.size();
      this.args.addAll(unquoted);
   }

   static String unquote(String value) {
      if (value != null && value.length() >= 2) {
         char first = value.charAt(0);
         char last = value.charAt(value.length() - 1);
         return (first != '"' || last != '"') && (first != '\'' || last != '\'') ? value : value.substring(1, value.length() - 1);
      } else {
         return value;
      }
   }

   public static CommandArguments of(List<String> arguments) {
      return new CommandArguments(arguments);
   }

   public static CommandArguments of(String... arguments) {
      return new CommandArguments(List.of(arguments));
   }

   public static CommandArguments empty() {
      return new CommandArguments(List.of());
   }

   public int size() {
      return this.size;
   }

   public Optional<String> next() {
      return Optional.ofNullable(this.args.poll());
   }

   public List<String> remaining() {
      List<String> arguments = new ArrayList<>(this.args);
      this.args.clear();
      return arguments;
   }

   public String joined() {
      return String.join(" ", this.remaining());
   }
}
