import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IReferanceDownloadTask } from '../referance-download-task.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../referance-download-task.test-samples';

import { ReferanceDownloadTaskService, RestReferanceDownloadTask } from './referance-download-task.service';

const requireRestSample: RestReferanceDownloadTask = {
  ...sampleWithRequiredData,
  referanceScheduleDate: sampleWithRequiredData.referanceScheduleDate?.toJSON(),
};

describe('ReferanceDownloadTask Service', () => {
  let service: ReferanceDownloadTaskService;
  let httpMock: HttpTestingController;
  let expectedResult: IReferanceDownloadTask | IReferanceDownloadTask[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ReferanceDownloadTaskService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a ReferanceDownloadTask', () => {
      const referanceDownloadTask = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(referanceDownloadTask).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ReferanceDownloadTask', () => {
      const referanceDownloadTask = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(referanceDownloadTask).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ReferanceDownloadTask', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ReferanceDownloadTask', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ReferanceDownloadTask', () => {
      const expected = true;

      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addReferanceDownloadTaskToCollectionIfMissing', () => {
      it('should add a ReferanceDownloadTask to an empty array', () => {
        const referanceDownloadTask: IReferanceDownloadTask = sampleWithRequiredData;
        expectedResult = service.addReferanceDownloadTaskToCollectionIfMissing([], referanceDownloadTask);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(referanceDownloadTask);
      });

      it('should not add a ReferanceDownloadTask to an array that contains it', () => {
        const referanceDownloadTask: IReferanceDownloadTask = sampleWithRequiredData;
        const referanceDownloadTaskCollection: IReferanceDownloadTask[] = [
          {
            ...referanceDownloadTask,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addReferanceDownloadTaskToCollectionIfMissing(referanceDownloadTaskCollection, referanceDownloadTask);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ReferanceDownloadTask to an array that doesn't contain it", () => {
        const referanceDownloadTask: IReferanceDownloadTask = sampleWithRequiredData;
        const referanceDownloadTaskCollection: IReferanceDownloadTask[] = [sampleWithPartialData];
        expectedResult = service.addReferanceDownloadTaskToCollectionIfMissing(referanceDownloadTaskCollection, referanceDownloadTask);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(referanceDownloadTask);
      });

      it('should add only unique ReferanceDownloadTask to an array', () => {
        const referanceDownloadTaskArray: IReferanceDownloadTask[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const referanceDownloadTaskCollection: IReferanceDownloadTask[] = [sampleWithRequiredData];
        expectedResult = service.addReferanceDownloadTaskToCollectionIfMissing(
          referanceDownloadTaskCollection,
          ...referanceDownloadTaskArray,
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const referanceDownloadTask: IReferanceDownloadTask = sampleWithRequiredData;
        const referanceDownloadTask2: IReferanceDownloadTask = sampleWithPartialData;
        expectedResult = service.addReferanceDownloadTaskToCollectionIfMissing([], referanceDownloadTask, referanceDownloadTask2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(referanceDownloadTask);
        expect(expectedResult).toContain(referanceDownloadTask2);
      });

      it('should accept null and undefined values', () => {
        const referanceDownloadTask: IReferanceDownloadTask = sampleWithRequiredData;
        expectedResult = service.addReferanceDownloadTaskToCollectionIfMissing([], null, referanceDownloadTask, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(referanceDownloadTask);
      });

      it('should return initial array if no ReferanceDownloadTask is added', () => {
        const referanceDownloadTaskCollection: IReferanceDownloadTask[] = [sampleWithRequiredData];
        expectedResult = service.addReferanceDownloadTaskToCollectionIfMissing(referanceDownloadTaskCollection, undefined, null);
        expect(expectedResult).toEqual(referanceDownloadTaskCollection);
      });
    });

    describe('compareReferanceDownloadTask', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareReferanceDownloadTask(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = null;

        const compareResult1 = service.compareReferanceDownloadTask(entity1, entity2);
        const compareResult2 = service.compareReferanceDownloadTask(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'CBA' };

        const compareResult1 = service.compareReferanceDownloadTask(entity1, entity2);
        const compareResult2 = service.compareReferanceDownloadTask(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'ABC' };

        const compareResult1 = service.compareReferanceDownloadTask(entity1, entity2);
        const compareResult2 = service.compareReferanceDownloadTask(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
