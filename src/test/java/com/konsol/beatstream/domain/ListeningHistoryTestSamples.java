package com.konsol.beatstream.domain;

import java.util.UUID;

public class ListeningHistoryTestSamples {

    public static ListeningHistory getListeningHistorySample1() {
        return new ListeningHistory().id("id1").timestamp("timestamp1");
    }

    public static ListeningHistory getListeningHistorySample2() {
        return new ListeningHistory().id("id2").timestamp("timestamp2");
    }

    public static ListeningHistory getListeningHistoryRandomSampleGenerator() {
        return new ListeningHistory().id(UUID.randomUUID().toString()).timestamp(UUID.randomUUID().toString());
    }
}
