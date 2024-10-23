package com.konsol.beatstream.domain;

import java.util.UUID;

public class ReferanceDownloadTaskTestSamples {

    public static ReferanceDownloadTask getReferanceDownloadTaskSample1() {
        return new ReferanceDownloadTask()
            .id("id1")
            .referanceId("referanceId1")
            .referanceType("referanceType1")
            .referanceTrackId("referanceTrackId1")
            .referanceStatus("referanceStatus1")
            .referanceLog("referanceLog1");
    }

    public static ReferanceDownloadTask getReferanceDownloadTaskSample2() {
        return new ReferanceDownloadTask()
            .id("id2")
            .referanceId("referanceId2")
            .referanceType("referanceType2")
            .referanceTrackId("referanceTrackId2")
            .referanceStatus("referanceStatus2")
            .referanceLog("referanceLog2");
    }

    public static ReferanceDownloadTask getReferanceDownloadTaskRandomSampleGenerator() {
        return new ReferanceDownloadTask()
            .id(UUID.randomUUID().toString())
            .referanceId(UUID.randomUUID().toString())
            .referanceType(UUID.randomUUID().toString())
            .referanceTrackId(UUID.randomUUID().toString())
            .referanceStatus(UUID.randomUUID().toString())
            .referanceLog(UUID.randomUUID().toString());
    }
}
