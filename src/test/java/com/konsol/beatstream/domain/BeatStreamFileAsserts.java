package com.konsol.beatstream.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class BeatStreamFileAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertBeatStreamFileAllPropertiesEquals(BeatStreamFile expected, BeatStreamFile actual) {
        assertBeatStreamFileAutoGeneratedPropertiesEquals(expected, actual);
        assertBeatStreamFileAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertBeatStreamFileAllUpdatablePropertiesEquals(BeatStreamFile expected, BeatStreamFile actual) {
        assertBeatStreamFileUpdatableFieldsEquals(expected, actual);
        assertBeatStreamFileUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertBeatStreamFileAutoGeneratedPropertiesEquals(BeatStreamFile expected, BeatStreamFile actual) {
        assertThat(expected)
            .as("Verify BeatStreamFile auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertBeatStreamFileUpdatableFieldsEquals(BeatStreamFile expected, BeatStreamFile actual) {
        assertThat(expected)
            .as("Verify BeatStreamFile relevant properties")
            .satisfies(e -> assertThat(e.getName()).as("check name").isEqualTo(actual.getName()))
            .satisfies(e -> assertThat(e.getSize()).as("check size").isEqualTo(actual.getSize()))
            .satisfies(e -> assertThat(e.getBucket()).as("check bucket").isEqualTo(actual.getBucket()))
            .satisfies(e -> assertThat(e.getType()).as("check type").isEqualTo(actual.getType()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertBeatStreamFileUpdatableRelationshipsEquals(BeatStreamFile expected, BeatStreamFile actual) {
        // empty method
    }
}
