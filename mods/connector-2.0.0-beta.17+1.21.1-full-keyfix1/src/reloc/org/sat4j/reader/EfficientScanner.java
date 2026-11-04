package reloc.org.sat4j.reader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;

public class EfficientScanner implements Serializable {
   private static final long serialVersionUID = 1L;
   private static final int TAILLE_BUF = 16384;
   private final transient BufferedInputStream in;
   private static final char EOF = '\uffff';
   private final char commentChar;

   public EfficientScanner(InputStream input, char commentChar) {
      this.in = new BufferedInputStream(input, 16384);
      this.commentChar = commentChar;
   }

   public EfficientScanner(InputStream input) {
      this(input, 'c');
   }

   public void close() throws IOException {
      this.in.close();
   }

   public void skipComments() throws IOException {
      while (true) {
         char currentChar = this.currentChar();
         if (currentChar == this.commentChar) {
            this.skipRestOfLine();
            if (currentChar != '\uffff') {
               continue;
            }
         }

         return;
      }
   }

   public int nextInt() throws IOException, ParseFormatException {
      int val = 0;
      boolean neg = false;
      char currentChar = this.skipSpaces();
      if (currentChar == '-') {
         neg = true;
         currentChar = (char)this.in.read();
      } else if (currentChar == '+') {
         currentChar = (char)this.in.read();
      } else {
         if (currentChar < '0' || currentChar > '9') {
            throw new ParseFormatException("Unknown character " + currentChar);
         }

         val = currentChar - '0';
         currentChar = (char)this.in.read();
      }

      while (currentChar >= '0' && currentChar <= '9') {
         val = val * 10 + currentChar - 48;
         currentChar = (char)this.in.read();
      }

      if (currentChar == '\r') {
         this.in.read();
      }

      return neg ? -val : val;
   }

   public BigInteger nextBigInteger() throws IOException, ParseFormatException {
      StringBuilder stb = new StringBuilder();
      char currentChar = this.skipSpaces();
      if (currentChar == '-') {
         stb.append(currentChar);
         currentChar = (char)this.in.read();
      } else if (currentChar == '+') {
         currentChar = (char)this.in.read();
      } else {
         if (currentChar < '0' || currentChar > '9') {
            throw new ParseFormatException("Unknown character " + currentChar);
         }

         stb.append(currentChar);
         currentChar = (char)this.in.read();
      }

      while (currentChar >= '0' && currentChar <= '9') {
         stb.append(currentChar);
         currentChar = (char)this.in.read();
      }

      return new BigInteger(stb.toString());
   }

   public String next() throws IOException, ParseFormatException {
      StringBuilder stb = new StringBuilder();

      for (char currentChar = this.skipSpaces(); currentChar != ' ' && currentChar != '\n'; currentChar = (char)this.in.read()) {
         stb.append(currentChar);
      }

      return stb.toString();
   }

   public char skipSpaces() throws IOException {
      char car;
      do {
         car = (char)this.in.read();
      } while (car == ' ' || car == '\n');

      return car;
   }

   public String nextLine() throws IOException {
      StringBuilder stb = new StringBuilder();

      char car;
      do {
         car = (char)this.in.read();
         stb.append(car);
      } while (car != '\n' && car != '\uffff');

      return stb.toString();
   }

   public void skipRestOfLine() throws IOException {
      char car;
      do {
         car = (char)this.in.read();
      } while (car != '\n' && car != '\uffff');
   }

   public boolean eof() throws IOException {
      return this.currentChar() == '\uffff';
   }

   public char currentChar() throws IOException {
      this.in.mark(10);
      char car = (char)this.in.read();
      this.in.reset();
      return car;
   }
}
