import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../listening-history-beat-stream.test-samples';

import { ListeningHistoryBeatStreamFormService } from './listening-history-beat-stream-form.service';

describe('ListeningHistoryBeatStream Form Service', () => {
  let service: ListeningHistoryBeatStreamFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ListeningHistoryBeatStreamFormService);
  });

  describe('Service methods', () => {
    describe('createListeningHistoryBeatStreamFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createListeningHistoryBeatStreamFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            timestamp: expect.any(Object),
            track: expect.any(Object),
          }),
        );
      });

      it('passing IListeningHistoryBeatStream should create a new form with FormGroup', () => {
        const formGroup = service.createListeningHistoryBeatStreamFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            timestamp: expect.any(Object),
            track: expect.any(Object),
          }),
        );
      });
    });

    describe('getListeningHistoryBeatStream', () => {
      it('should return NewListeningHistoryBeatStream for default ListeningHistoryBeatStream initial value', () => {
        const formGroup = service.createListeningHistoryBeatStreamFormGroup(sampleWithNewData);

        const listeningHistory = service.getListeningHistoryBeatStream(formGroup) as any;

        expect(listeningHistory).toMatchObject(sampleWithNewData);
      });

      it('should return NewListeningHistoryBeatStream for empty ListeningHistoryBeatStream initial value', () => {
        const formGroup = service.createListeningHistoryBeatStreamFormGroup();

        const listeningHistory = service.getListeningHistoryBeatStream(formGroup) as any;

        expect(listeningHistory).toMatchObject({});
      });

      it('should return IListeningHistoryBeatStream', () => {
        const formGroup = service.createListeningHistoryBeatStreamFormGroup(sampleWithRequiredData);

        const listeningHistory = service.getListeningHistoryBeatStream(formGroup) as any;

        expect(listeningHistory).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IListeningHistoryBeatStream should not enable id FormControl', () => {
        const formGroup = service.createListeningHistoryBeatStreamFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewListeningHistoryBeatStream should disable id FormControl', () => {
        const formGroup = service.createListeningHistoryBeatStreamFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
