import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../artist-beat-stream.test-samples';

import { ArtistBeatStreamFormService } from './artist-beat-stream-form.service';

describe('ArtistBeatStream Form Service', () => {
  let service: ArtistBeatStreamFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ArtistBeatStreamFormService);
  });

  describe('Service methods', () => {
    describe('createArtistBeatStreamFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createArtistBeatStreamFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            bio: expect.any(Object),
            coverImageFileId: expect.any(Object),
          }),
        );
      });

      it('passing IArtistBeatStream should create a new form with FormGroup', () => {
        const formGroup = service.createArtistBeatStreamFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            bio: expect.any(Object),
            coverImageFileId: expect.any(Object),
          }),
        );
      });
    });

    describe('getArtistBeatStream', () => {
      it('should return NewArtistBeatStream for default ArtistBeatStream initial value', () => {
        const formGroup = service.createArtistBeatStreamFormGroup(sampleWithNewData);

        const artist = service.getArtistBeatStream(formGroup) as any;

        expect(artist).toMatchObject(sampleWithNewData);
      });

      it('should return NewArtistBeatStream for empty ArtistBeatStream initial value', () => {
        const formGroup = service.createArtistBeatStreamFormGroup();

        const artist = service.getArtistBeatStream(formGroup) as any;

        expect(artist).toMatchObject({});
      });

      it('should return IArtistBeatStream', () => {
        const formGroup = service.createArtistBeatStreamFormGroup(sampleWithRequiredData);

        const artist = service.getArtistBeatStream(formGroup) as any;

        expect(artist).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IArtistBeatStream should not enable id FormControl', () => {
        const formGroup = service.createArtistBeatStreamFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewArtistBeatStream should disable id FormControl', () => {
        const formGroup = service.createArtistBeatStreamFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
