package com.konsol.beatstream.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class AlbumAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAlbumAllPropertiesEquals(Album expected, Album actual) {
        assertAlbumAutoGeneratedPropertiesEquals(expected, actual);
        assertAlbumAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAlbumAllUpdatablePropertiesEquals(Album expected, Album actual) {
        assertAlbumUpdatableFieldsEquals(expected, actual);
        assertAlbumUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAlbumAutoGeneratedPropertiesEquals(Album expected, Album actual) {
        assertThat(expected)
            .as("Verify Album auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAlbumUpdatableFieldsEquals(Album expected, Album actual) {
        assertThat(expected)
            .as("Verify Album relevant properties")
            .satisfies(e -> assertThat(e.getTitle()).as("check title").isEqualTo(actual.getTitle()))
            .satisfies(e -> assertThat(e.getReleaseDate()).as("check releaseDate").isEqualTo(actual.getReleaseDate()))
            .satisfies(e -> assertThat(e.getCoverImageFileId()).as("check coverImageFileId").isEqualTo(actual.getCoverImageFileId()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAlbumUpdatableRelationshipsEquals(Album expected, Album actual) {
        assertThat(expected)
            .as("Verify Album relationships")
            .satisfies(e -> assertThat(e.getArtist()).as("check artist").isEqualTo(actual.getArtist()))
            .satisfies(e -> assertThat(e.getGenres()).as("check genres").isEqualTo(actual.getGenres()));
    }
}