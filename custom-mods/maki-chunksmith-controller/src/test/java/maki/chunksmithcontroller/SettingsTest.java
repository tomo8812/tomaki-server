package maki.chunksmithcontroller;

import java.util.Properties;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SettingsTest {
    @Test void acceptsExpandedFollowRadiusBounds() {
        var million = new Properties();
        million.setProperty("follow-radius-blocks", "1000000");
        assertEquals(1_000_000, Settings.parse(million).followRadius);

        var maxAllowed = new Properties();
        maxAllowed.setProperty("follow-radius-blocks", "10000000");
        assertEquals(10_000_000, Settings.parse(maxAllowed).followRadius);
    }

    @Test void rejectsOutOfRangeFollowRadius() {
        var tooSmall = new Properties();
        tooSmall.setProperty("follow-radius-blocks", "127");
        assertThrows(IllegalArgumentException.class, () -> Settings.parse(tooSmall));

        var tooLarge = new Properties();
        tooLarge.setProperty("follow-radius-blocks", "10000001");
        assertThrows(IllegalArgumentException.class, () -> Settings.parse(tooLarge));
    }
}
