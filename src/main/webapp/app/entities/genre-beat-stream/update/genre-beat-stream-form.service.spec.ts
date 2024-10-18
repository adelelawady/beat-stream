import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../genre-beat-stream.test-samples';

import { GenreBeatStreamFormService } from './genre-beat-stream-form.service';

describe('GenreBeatStream Form Service', () => {
  let service: GenreBeatStreamFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GenreBeatStreamFormService);
  });

  describe('Service methods', () => {
    describe('createGenreBeatStreamFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createGenreBeatStreamFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            artists: expect.any(Object),
          }),
        );
      });

      it('passing IGenreBeatStream should create a new form with FormGroup', () => {
        const formGroup = service.createGenreBeatStreamFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            artists: expect.any(Object),
          }),
        );
      });
    });

    describe('getGenreBeatStream', () => {
      it('should return NewGenreBeatStream for default GenreBeatStream initial value', () => {
        const formGroup = service.createGenreBeatStreamFormGroup(sampleWithNewData);

        const genre = service.getGenreBeatStream(formGroup) as any;

        expect(genre).toMatchObject(sampleWithNewData);
      });

      it('should return NewGenreBeatStream for empty GenreBeatStream initial value', () => {
        const formGroup = service.createGenreBeatStreamFormGroup();

        const genre = service.getGenreBeatStream(formGroup) as any;

        expect(genre).toMatchObject({});
      });

      it('should return IGenreBeatStream', () => {
        const formGroup = service.createGenreBeatStreamFormGroup(sampleWithRequiredData);

        const genre = service.getGenreBeatStream(formGroup) as any;

        expect(genre).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IGenreBeatStream should not enable id FormControl', () => {
        const formGroup = service.createGenreBeatStreamFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewGenreBeatStream should disable id FormControl', () => {
        const formGroup = service.createGenreBeatStreamFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
