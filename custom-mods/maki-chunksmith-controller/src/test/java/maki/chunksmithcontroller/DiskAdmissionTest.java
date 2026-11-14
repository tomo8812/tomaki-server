package maki.chunksmithcontroller;

import java.util.Properties;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DiskAdmissionTest {
    @Test void pausesOnlyBelowReserveAndResumesAtHigherWatermark(){
        var settings=new Settings();var disk=new DiskAdmission();
        disk.update(5L<<30,settings);assertFalse(disk.blocked());
        disk.update((5L<<30)-1,settings);assertTrue(disk.blocked());
        disk.update(7L<<30,settings);assertTrue(disk.blocked());
        disk.update(8L<<30,settings);assertFalse(disk.blocked());
    }
    @Test void customizedWatermarksAndInvalidSettings(){
        var properties=new Properties();properties.setProperty("disk-pause-free-gib","10");properties.setProperty("disk-resume-free-gib","12");
        var settings=Settings.parse(properties);var disk=new DiskAdmission();
        disk.update(9L<<30,settings);assertTrue(disk.blocked());
        disk.update(11L<<30,settings);assertTrue(disk.blocked());
        disk.update(12L<<30,settings);assertFalse(disk.blocked());
        properties.setProperty("disk-resume-free-gib","10");assertThrows(IllegalArgumentException.class,()->Settings.parse(properties));
    }
}
