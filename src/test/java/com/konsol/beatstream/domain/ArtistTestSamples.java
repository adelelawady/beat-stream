package com.konsol.beatstream.domain;

import java.util.UUID;

public class ArtistTestSamples {

    public static Artist getArtistSample1() {
        return new Artist().id("id1").name("name1").bio("bio1").coverImageFileId("coverImageFileId1");
    }

    public static Artist getArtistSample2() {
        return new Artist().id("id2").name("name2").bio("bio2").coverImageFileId("coverImageFileId2");
    }

    public static Artist getArtistRandomSampleGenerator() {
        return new Artist()
            .id(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .bio(UUID.randomUUID().toString())
            .coverImageFileId(UUID.randomUUID().toString());
    }
}
