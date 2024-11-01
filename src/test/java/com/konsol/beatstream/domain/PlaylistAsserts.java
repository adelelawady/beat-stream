package com.konsol.beatstream.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class PlaylistAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPlaylistAllPropertiesEquals(Playlist expected, Playlist actual) {
        assertPlaylistAutoGeneratedPropertiesEquals(expected, actual);
        assertPlaylistAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPlaylistAllUpdatablePropertiesEquals(Playlist expected, Playlist actual) {
        assertPlaylistUpdatableFieldsEquals(expected, actual);
        assertPlaylistUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPlaylistAutoGeneratedPropertiesEquals(Playlist expected, Playlist actual) {
        assertThat(expected)
            .as("Verify Playlist auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPlaylistUpdatableFieldsEquals(Playlist expected, Playlist actual) {
        assertThat(expected)
            .as("Verify Playlist relevant properties")
            .satisfies(e -> assertThat(e.getTitle()).as("check title").isEqualTo(actual.getTitle()))
            .satisfies(e -> assertThat(e.getDescription()).as("check description").isEqualTo(actual.getDescription()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertPlaylistUpdatableRelationshipsEquals(Playlist expected, Playlist actual) {
        assertThat(expected)
            .as("Verify Playlist relationships")
            .satisfies(e -> assertThat(e.getTracks()).as("check tracks").isEqualTo(actual.getTracks()));
    }
}
