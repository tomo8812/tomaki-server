package maki.chunksmithcache.mixin;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Guard CsLodRegionStore's signed-int append offset before it can wrap. */
@Mixin(targets="com.kishku7.chunksmith.lod.CsLodRegionStore",remap=false)
public abstract class LodRegionOffsetGuardMixin {
    private static final long MAX_RECORD_BYTES=32L<<20;

    @Redirect(method="write",at=@At(value="INVOKE",target="Ljava/io/RandomAccessFile;length()J"),require=1)
    private long maki$guardSignedOffset(RandomAccessFile file)throws IOException {
        long length=file.length();
        if(length>Integer.MAX_VALUE-MAX_RECORD_BYTES)
            throw new IOException("CSLOD region reached the signed 32-bit offset safety limit at "+length+" bytes");
        return length;
    }
}
