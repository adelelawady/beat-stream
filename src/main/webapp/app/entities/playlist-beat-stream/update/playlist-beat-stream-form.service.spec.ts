import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../playlist-beat-stream.test-samples';

import { PlaylistBeatStreamFormService } from './playlist-beat-stream-form.service';

describe('PlaylistBeatStream Form Service', () => {
  let service: PlaylistBeatStreamFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PlaylistBeatStreamFormService);
  });

  describe('Service methods', () => {
    describe('createPlaylistBeatStreamFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPlaylistBeatStreamFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            description: expect.any(Object),
            tracks: expect.any(Object),
          }),
        );
      });

      it('passing IPlaylistBeatStream should create a new form with FormGroup', () => {
        const formGroup = service.createPlaylistBeatStreamFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            description: expect.any(Object),
            tracks: expect.any(Object),
          }),
        );
      });
    });

    describe('getPlaylistBeatStream', () => {
      it('should return NewPlaylistBeatStream for default PlaylistBeatStream initial value', () => {
        const formGroup = service.createPlaylistBeatStreamFormGroup(sampleWithNewData);

        const playlist = service.getPlaylistBeatStream(formGroup) as any;

        expect(playlist).toMatchObject(sampleWithNewData);
      });

      it('should return NewPlaylistBeatStream for empty PlaylistBeatStream initial value', () => {
        const formGroup = service.createPlaylistBeatStreamFormGroup();

        const playlist = service.getPlaylistBeatStream(formGroup) as any;

        expect(playlist).toMatchObject({});
      });

      it('should return IPlaylistBeatStream', () => {
        const formGroup = service.createPlaylistBeatStreamFormGroup(sampleWithRequiredData);

        const playlist = service.getPlaylistBeatStream(formGroup) as any;

        expect(playlist).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPlaylistBeatStream should not enable id FormControl', () => {
        const formGroup = service.createPlaylistBeatStreamFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPlaylistBeatStream should disable id FormControl', () => {
        const formGroup = service.createPlaylistBeatStreamFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
