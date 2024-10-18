package com.konsol.beatstream.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class BeatStreamFileTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static BeatStreamFile getBeatStreamFileSample1() {
        return new BeatStreamFile().id("id1").name("name1").size(1L).bucket("bucket1").type("type1");
    }

    public static BeatStreamFile getBeatStreamFileSample2() {
        return new BeatStreamFile().id("id2").name("name2").size(2L).bucket("bucket2").type("type2");
    }

    public static BeatStreamFile getBeatStreamFileRandomSampleGenerator() {
        return new BeatStreamFile()
            .id(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .size(longCount.incrementAndGet())
            .bucket(UUID.randomUUID().toString())
            .type(UUID.randomUUID().toString());
    }
}
