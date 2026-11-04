package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class im implements Iterator<Node> {
   private final NodeList a;
   private int b = 0;

   public im(Node var1) {
      if (var1 == null) {
         throw new NullPointerException("Node must not be null.");
      }

      this.a = var1.getChildNodes();
   }

   public im(NodeList var1) {
      if (var1 == null) {
         throw new NullPointerException("NodeList must not be null.");
      }

      this.a = var1;
   }

   @Override
   public final boolean hasNext() {
      return this.a != null && this.b < this.a.getLength();
   }

   private Node a() {
      if (this.a != null && this.b < this.a.getLength()) {
         return this.a.item(this.b++);
      } else {
         throw new NoSuchElementException("underlying nodeList has no more elements");
      }
   }

   @Override
   public final void remove() {
      throw new UnsupportedOperationException("remove() method not supported for a NodeListIterator.");
   }

   // $VF: synthetic method
   @Override
   public final Object next() {
      im var1 = this;
      if (this.a != null && var1.b < var1.a.getLength()) {
         return var1.a.item(var1.b++);
      } else {
         throw new NoSuchElementException("underlying nodeList has no more elements");
      }
   }
}
