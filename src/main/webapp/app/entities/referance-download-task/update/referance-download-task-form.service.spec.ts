import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../referance-download-task.test-samples';

import { ReferanceDownloadTaskFormService } from './referance-download-task-form.service';

describe('ReferanceDownloadTask Form Service', () => {
  let service: ReferanceDownloadTaskFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ReferanceDownloadTaskFormService);
  });

  describe('Service methods', () => {
    describe('createReferanceDownloadTaskFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createReferanceDownloadTaskFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            referanceId: expect.any(Object),
            referanceType: expect.any(Object),
            referanceTrackId: expect.any(Object),
            referanceStatus: expect.any(Object),
            referanceScheduleDate: expect.any(Object),
            referanceLog: expect.any(Object),
          }),
        );
      });

      it('passing IReferanceDownloadTask should create a new form with FormGroup', () => {
        const formGroup = service.createReferanceDownloadTaskFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            referanceId: expect.any(Object),
            referanceType: expect.any(Object),
            referanceTrackId: expect.any(Object),
            referanceStatus: expect.any(Object),
            referanceScheduleDate: expect.any(Object),
            referanceLog: expect.any(Object),
          }),
        );
      });
    });

    describe('getReferanceDownloadTask', () => {
      it('should return NewReferanceDownloadTask for default ReferanceDownloadTask initial value', () => {
        const formGroup = service.createReferanceDownloadTaskFormGroup(sampleWithNewData);

        const referanceDownloadTask = service.getReferanceDownloadTask(formGroup) as any;

        expect(referanceDownloadTask).toMatchObject(sampleWithNewData);
      });

      it('should return NewReferanceDownloadTask for empty ReferanceDownloadTask initial value', () => {
        const formGroup = service.createReferanceDownloadTaskFormGroup();

        const referanceDownloadTask = service.getReferanceDownloadTask(formGroup) as any;

        expect(referanceDownloadTask).toMatchObject({});
      });

      it('should return IReferanceDownloadTask', () => {
        const formGroup = service.createReferanceDownloadTaskFormGroup(sampleWithRequiredData);

        const referanceDownloadTask = service.getReferanceDownloadTask(formGroup) as any;

        expect(referanceDownloadTask).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IReferanceDownloadTask should not enable id FormControl', () => {
        const formGroup = service.createReferanceDownloadTaskFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewReferanceDownloadTask should disable id FormControl', () => {
        const formGroup = service.createReferanceDownloadTaskFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
