package com.kishku7.chunksmith.command;

import com.kishku7.chunksmith.platform.Sender;
import java.util.List;

public interface ChunksmithCommand {
   void execute(Sender var1, CommandArguments var2);

   List<String> suggestions(CommandArguments var1);
}
