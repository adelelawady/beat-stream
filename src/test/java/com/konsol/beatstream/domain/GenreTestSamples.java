package com.konsol.beatstream.domain;

import java.util.UUID;

public class GenreTestSamples {

    public static Genre getGenreSample1() {
        return new Genre().id("id1").name("name1");
    }

    public static Genre getGenreSample2() {
        return new Genre().id("id2").name("name2");
    }

    public static Genre getGenreRandomSampleGenerator() {
        return new Genre().id(UUID.randomUUID().toString()).name(UUID.randomUUID().toString());
    }
}
