import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../beat-stream-file-beat-stream.test-samples';

import { BeatStreamFileBeatStreamFormService } from './beat-stream-file-beat-stream-form.service';

describe('BeatStreamFileBeatStream Form Service', () => {
  let service: BeatStreamFileBeatStreamFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BeatStreamFileBeatStreamFormService);
  });

  describe('Service methods', () => {
    describe('createBeatStreamFileBeatStreamFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createBeatStreamFileBeatStreamFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            size: expect.any(Object),
            bucket: expect.any(Object),
            type: expect.any(Object),
          }),
        );
      });

      it('passing IBeatStreamFileBeatStream should create a new form with FormGroup', () => {
        const formGroup = service.createBeatStreamFileBeatStreamFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            size: expect.any(Object),
            bucket: expect.any(Object),
            type: expect.any(Object),
          }),
        );
      });
    });

    describe('getBeatStreamFileBeatStream', () => {
      it('should return NewBeatStreamFileBeatStream for default BeatStreamFileBeatStream initial value', () => {
        const formGroup = service.createBeatStreamFileBeatStreamFormGroup(sampleWithNewData);

        const beatStreamFile = service.getBeatStreamFileBeatStream(formGroup) as any;

        expect(beatStreamFile).toMatchObject(sampleWithNewData);
      });

      it('should return NewBeatStreamFileBeatStream for empty BeatStreamFileBeatStream initial value', () => {
        const formGroup = service.createBeatStreamFileBeatStreamFormGroup();

        const beatStreamFile = service.getBeatStreamFileBeatStream(formGroup) as any;

        expect(beatStreamFile).toMatchObject({});
      });

      it('should return IBeatStreamFileBeatStream', () => {
        const formGroup = service.createBeatStreamFileBeatStreamFormGroup(sampleWithRequiredData);

        const beatStreamFile = service.getBeatStreamFileBeatStream(formGroup) as any;

        expect(beatStreamFile).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IBeatStreamFileBeatStream should not enable id FormControl', () => {
        const formGroup = service.createBeatStreamFileBeatStreamFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewBeatStreamFileBeatStream should disable id FormControl', () => {
        const formGroup = service.createBeatStreamFileBeatStreamFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
