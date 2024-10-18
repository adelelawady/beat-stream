package com.konsol.beatstream.domain;

import java.util.UUID;

public class PlaylistTestSamples {

    public static Playlist getPlaylistSample1() {
        return new Playlist().id("id1").title("title1").description("description1");
    }

    public static Playlist getPlaylistSample2() {
        return new Playlist().id("id2").title("title2").description("description2");
    }

    public static Playlist getPlaylistRandomSampleGenerator() {
        return new Playlist()
            .id(UUID.randomUUID().toString())
            .title(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
