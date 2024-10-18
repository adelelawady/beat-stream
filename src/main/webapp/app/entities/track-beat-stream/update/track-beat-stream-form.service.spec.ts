import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../track-beat-stream.test-samples';

import { TrackBeatStreamFormService } from './track-beat-stream-form.service';

describe('TrackBeatStream Form Service', () => {
  let service: TrackBeatStreamFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TrackBeatStreamFormService);
  });

  describe('Service methods', () => {
    describe('createTrackBeatStreamFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTrackBeatStreamFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            duration: expect.any(Object),
            liked: expect.any(Object),
            audioFileId: expect.any(Object),
            coverImageFileId: expect.any(Object),
            artist: expect.any(Object),
            album: expect.any(Object),
          }),
        );
      });

      it('passing ITrackBeatStream should create a new form with FormGroup', () => {
        const formGroup = service.createTrackBeatStreamFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            duration: expect.any(Object),
            liked: expect.any(Object),
            audioFileId: expect.any(Object),
            coverImageFileId: expect.any(Object),
            artist: expect.any(Object),
            album: expect.any(Object),
          }),
        );
      });
    });

    describe('getTrackBeatStream', () => {
      it('should return NewTrackBeatStream for default TrackBeatStream initial value', () => {
        const formGroup = service.createTrackBeatStreamFormGroup(sampleWithNewData);

        const track = service.getTrackBeatStream(formGroup) as any;

        expect(track).toMatchObject(sampleWithNewData);
      });

      it('should return NewTrackBeatStream for empty TrackBeatStream initial value', () => {
        const formGroup = service.createTrackBeatStreamFormGroup();

        const track = service.getTrackBeatStream(formGroup) as any;

        expect(track).toMatchObject({});
      });

      it('should return ITrackBeatStream', () => {
        const formGroup = service.createTrackBeatStreamFormGroup(sampleWithRequiredData);

        const track = service.getTrackBeatStream(formGroup) as any;

        expect(track).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITrackBeatStream should not enable id FormControl', () => {
        const formGroup = service.createTrackBeatStreamFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTrackBeatStream should disable id FormControl', () => {
        const formGroup = service.createTrackBeatStreamFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
