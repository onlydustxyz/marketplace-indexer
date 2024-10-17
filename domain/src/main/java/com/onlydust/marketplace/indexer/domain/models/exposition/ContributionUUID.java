package com.onlydust.marketplace.indexer.domain.models.exposition;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import onlydust.com.marketplace.kernel.model.UuidWrapper;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@NoArgsConstructor(staticName = "random")
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class ContributionUUID extends UuidWrapper {

    public static ContributionUUID of(@NonNull final UUID uuid) {
        return ContributionUUID.builder().uuid(uuid).build();
    }

    public static ContributionUUID of(@NonNull final Long id) {
        return of(id.toString());
    }

    public static ContributionUUID of(@NonNull final String id) {
        final byte[] bytes = concatenate(toByteArray(new UUID(0L, 0L)), id.getBytes(StandardCharsets.UTF_8));
        return of(UUID.nameUUIDFromBytes(bytes));
    }

    private static byte[] toByteArray(UUID uuid) {
        final ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    private static byte[] concatenate(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;

        final byte[] c = (byte[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }
}
