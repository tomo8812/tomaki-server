package org.valkyrienskies.core.internal.hooks

public enum class VsiPlayState {
   SERVERSIDE,
   CLIENT_SINGLEPLAYER,
   CLIENT_TITLESCREEN,
   CLIENT_MULTIPLAYER;

   @JvmStatic
   @JvmSynthetic
   fun `$values`(): Array<VsiPlayState> {
      arrayOf(SERVERSIDE, CLIENT_SINGLEPLAYER, CLIENT_TITLESCREEN, CLIENT_MULTIPLAYER)
   }
}
