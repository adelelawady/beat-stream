package com.konsol.beatstream.domain;

import java.util.UUID;

public class AlbumTestSamples {

    public static Album getAlbumSample1() {
        return new Album().id("id1").title("title1").releaseDate("releaseDate1").coverImageFileId("coverImageFileId1");
    }

    public static Album getAlbumSample2() {
        return new Album().id("id2").title("title2").releaseDate("releaseDate2").coverImageFileId("coverImageFileId2");
    }

    public static Album getAlbumRandomSampleGenerator() {
        return new Album()
            .id(UUID.randomUUID().toString())
            .title(UUID.randomUUID().toString())
            .releaseDate(UUID.randomUUID().toString())
            .coverImageFileId(UUID.randomUUID().toString());
    }
}
