package com.konsol.beatstream.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TaskNodeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TaskNode getTaskNodeSample1() {
        return new TaskNode()
            .id("id1")
            .taskName("taskName1")
            .taskLog("taskLog1")
            .downloadFilesize("downloadFilesize1")
            .downloadSpeed("downloadSpeed1")
            .downloadEta("downloadEta1");
    }

    public static TaskNode getTaskNodeSample2() {
        return new TaskNode()
            .id("id2")
            .taskName("taskName2")
            .taskLog("taskLog2")
            .downloadFilesize("downloadFilesize2")
            .downloadSpeed("downloadSpeed2")
            .downloadEta("downloadEta2");
    }

    public static TaskNode getTaskNodeRandomSampleGenerator() {
        return new TaskNode()
            .id(UUID.randomUUID().toString())
            .taskName(UUID.randomUUID().toString())
            .taskLog(UUID.randomUUID().toString())
            .downloadFilesize(UUID.randomUUID().toString())
            .downloadSpeed(UUID.randomUUID().toString())
            .downloadEta(UUID.randomUUID().toString());
    }
}
