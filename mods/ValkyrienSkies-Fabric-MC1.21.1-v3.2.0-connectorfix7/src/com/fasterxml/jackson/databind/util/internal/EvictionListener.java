package com.fasterxml.jackson.databind.util.internal;

interface EvictionListener<K, V> {
   void onEviction(K var1, V var2);
}
