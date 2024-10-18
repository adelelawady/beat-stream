import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../album-beat-stream.test-samples';

import { AlbumBeatStreamFormService } from './album-beat-stream-form.service';

describe('AlbumBeatStream Form Service', () => {
  let service: AlbumBeatStreamFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AlbumBeatStreamFormService);
  });

  describe('Service methods', () => {
    describe('createAlbumBeatStreamFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createAlbumBeatStreamFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            releaseDate: expect.any(Object),
            coverImageFileId: expect.any(Object),
            artist: expect.any(Object),
            genres: expect.any(Object),
          }),
        );
      });

      it('passing IAlbumBeatStream should create a new form with FormGroup', () => {
        const formGroup = service.createAlbumBeatStreamFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            releaseDate: expect.any(Object),
            coverImageFileId: expect.any(Object),
            artist: expect.any(Object),
            genres: expect.any(Object),
          }),
        );
      });
    });

    describe('getAlbumBeatStream', () => {
      it('should return NewAlbumBeatStream for default AlbumBeatStream initial value', () => {
        const formGroup = service.createAlbumBeatStreamFormGroup(sampleWithNewData);

        const album = service.getAlbumBeatStream(formGroup) as any;

        expect(album).toMatchObject(sampleWithNewData);
      });

      it('should return NewAlbumBeatStream for empty AlbumBeatStream initial value', () => {
        const formGroup = service.createAlbumBeatStreamFormGroup();

        const album = service.getAlbumBeatStream(formGroup) as any;

        expect(album).toMatchObject({});
      });

      it('should return IAlbumBeatStream', () => {
        const formGroup = service.createAlbumBeatStreamFormGroup(sampleWithRequiredData);

        const album = service.getAlbumBeatStream(formGroup) as any;

        expect(album).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IAlbumBeatStream should not enable id FormControl', () => {
        const formGroup = service.createAlbumBeatStreamFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewAlbumBeatStream should disable id FormControl', () => {
        const formGroup = service.createAlbumBeatStreamFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
