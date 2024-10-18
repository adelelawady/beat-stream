import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IBeatStreamFileBeatStream } from '../beat-stream-file-beat-stream.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../beat-stream-file-beat-stream.test-samples';

import { BeatStreamFileBeatStreamService } from './beat-stream-file-beat-stream.service';

const requireRestSample: IBeatStreamFileBeatStream = {
  ...sampleWithRequiredData,
};

describe('BeatStreamFileBeatStream Service', () => {
  let service: BeatStreamFileBeatStreamService;
  let httpMock: HttpTestingController;
  let expectedResult: IBeatStreamFileBeatStream | IBeatStreamFileBeatStream[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(BeatStreamFileBeatStreamService);
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

    it('should create a BeatStreamFileBeatStream', () => {
      const beatStreamFile = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(beatStreamFile).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a BeatStreamFileBeatStream', () => {
      const beatStreamFile = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(beatStreamFile).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a BeatStreamFileBeatStream', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of BeatStreamFileBeatStream', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a BeatStreamFileBeatStream', () => {
      const expected = true;

      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addBeatStreamFileBeatStreamToCollectionIfMissing', () => {
      it('should add a BeatStreamFileBeatStream to an empty array', () => {
        const beatStreamFile: IBeatStreamFileBeatStream = sampleWithRequiredData;
        expectedResult = service.addBeatStreamFileBeatStreamToCollectionIfMissing([], beatStreamFile);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(beatStreamFile);
      });

      it('should not add a BeatStreamFileBeatStream to an array that contains it', () => {
        const beatStreamFile: IBeatStreamFileBeatStream = sampleWithRequiredData;
        const beatStreamFileCollection: IBeatStreamFileBeatStream[] = [
          {
            ...beatStreamFile,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addBeatStreamFileBeatStreamToCollectionIfMissing(beatStreamFileCollection, beatStreamFile);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a BeatStreamFileBeatStream to an array that doesn't contain it", () => {
        const beatStreamFile: IBeatStreamFileBeatStream = sampleWithRequiredData;
        const beatStreamFileCollection: IBeatStreamFileBeatStream[] = [sampleWithPartialData];
        expectedResult = service.addBeatStreamFileBeatStreamToCollectionIfMissing(beatStreamFileCollection, beatStreamFile);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(beatStreamFile);
      });

      it('should add only unique BeatStreamFileBeatStream to an array', () => {
        const beatStreamFileArray: IBeatStreamFileBeatStream[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const beatStreamFileCollection: IBeatStreamFileBeatStream[] = [sampleWithRequiredData];
        expectedResult = service.addBeatStreamFileBeatStreamToCollectionIfMissing(beatStreamFileCollection, ...beatStreamFileArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const beatStreamFile: IBeatStreamFileBeatStream = sampleWithRequiredData;
        const beatStreamFile2: IBeatStreamFileBeatStream = sampleWithPartialData;
        expectedResult = service.addBeatStreamFileBeatStreamToCollectionIfMissing([], beatStreamFile, beatStreamFile2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(beatStreamFile);
        expect(expectedResult).toContain(beatStreamFile2);
      });

      it('should accept null and undefined values', () => {
        const beatStreamFile: IBeatStreamFileBeatStream = sampleWithRequiredData;
        expectedResult = service.addBeatStreamFileBeatStreamToCollectionIfMissing([], null, beatStreamFile, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(beatStreamFile);
      });

      it('should return initial array if no BeatStreamFileBeatStream is added', () => {
        const beatStreamFileCollection: IBeatStreamFileBeatStream[] = [sampleWithRequiredData];
        expectedResult = service.addBeatStreamFileBeatStreamToCollectionIfMissing(beatStreamFileCollection, undefined, null);
        expect(expectedResult).toEqual(beatStreamFileCollection);
      });
    });

    describe('compareBeatStreamFileBeatStream', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareBeatStreamFileBeatStream(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = null;

        const compareResult1 = service.compareBeatStreamFileBeatStream(entity1, entity2);
        const compareResult2 = service.compareBeatStreamFileBeatStream(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'CBA' };

        const compareResult1 = service.compareBeatStreamFileBeatStream(entity1, entity2);
        const compareResult2 = service.compareBeatStreamFileBeatStream(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'ABC' };

        const compareResult1 = service.compareBeatStreamFileBeatStream(entity1, entity2);
        const compareResult2 = service.compareBeatStreamFileBeatStream(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
