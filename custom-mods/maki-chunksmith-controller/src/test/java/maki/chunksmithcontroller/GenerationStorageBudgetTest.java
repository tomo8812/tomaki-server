package maki.chunksmithcontroller;

import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class GenerationStorageBudgetTest {
    @TempDir Path root;
    @Test void fullAndLodAndPendingShareTheAdmissionLimit(){
        long gib=1L<<30;
        assertTrue(GenerationStorageBudget.atLimit(10*gib,0,8*gib,15*gib));
        assertFalse(GenerationStorageBudget.atLimit(10*gib,0,4*gib,15*gib));
        assertTrue(GenerationStorageBudget.atLimit(10*gib,gib,4*gib,15*gib));
        assertTrue(GenerationStorageBudget.atLimit(15*gib,0,0,15*gib));
    }
    @Test void inventoryCountsBytesWithoutChangingNativeStore()throws Exception{
        Path a=root.resolve("overworld/r.0.0.cslod"),b=root.resolve("nether/r.0.0.cslod");
        Files.createDirectories(a.getParent());Files.createDirectories(b.getParent());
        Files.write(a,new byte[100]);Files.write(b,new byte[70]);
        assertEquals(170,GenerationStorageBudget.inventory(root));
        assertEquals(100,Files.size(a));assertEquals(70,Files.size(b));
        assertEquals(0,GenerationStorageBudget.inventory(root.resolve("absent")));
        assertFalse(Files.exists(root.resolve("absent")));
    }
}
