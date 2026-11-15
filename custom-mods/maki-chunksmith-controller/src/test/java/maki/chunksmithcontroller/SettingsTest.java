package maki.chunksmithcontroller;

import java.util.Properties;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SettingsTest {
    @Test void acceptsExpandedFollowRadiusBounds() {
        var configValue = new Properties();
        configValue.setProperty("follow-radius-blocks", "8192");
        assertEquals(8192, Settings.parse(configValue).followRadius);

        var maxAllowed = new Properties();
        maxAllowed.setProperty("follow-radius-blocks", "16384");
        assertEquals(16384, Settings.parse(maxAllowed).followRadius);
    }

    @Test void rejectsOutOfRangeFollowRadius() {
        var tooSmall = new Properties();
        tooSmall.setProperty("follow-radius-blocks", "127");
        assertThrows(IllegalArgumentException.class, () -> Settings.parse(tooSmall));

        var tooLarge = new Properties();
        tooLarge.setProperty("follow-radius-blocks", "16385");
        assertThrows(IllegalArgumentException.class, () -> Settings.parse(tooLarge));
    }
}
