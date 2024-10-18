package com.konsol.beatstream.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TrackTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Track getTrackSample1() {
        return new Track().id("id1").title("title1").duration(1L).audioFileId("audioFileId1").coverImageFileId("coverImageFileId1");
    }

    public static Track getTrackSample2() {
        return new Track().id("id2").title("title2").duration(2L).audioFileId("audioFileId2").coverImageFileId("coverImageFileId2");
    }

    public static Track getTrackRandomSampleGenerator() {
        return new Track()
            .id(UUID.randomUUID().toString())
            .title(UUID.randomUUID().toString())
            .duration(longCount.incrementAndGet())
            .audioFileId(UUID.randomUUID().toString())
            .coverImageFileId(UUID.randomUUID().toString());
    }
}
