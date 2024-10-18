import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ITrackBeatStream } from '../track-beat-stream.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../track-beat-stream.test-samples';

import { TrackBeatStreamService } from './track-beat-stream.service';

const requireRestSample: ITrackBeatStream = {
  ...sampleWithRequiredData,
};

describe('TrackBeatStream Service', () => {
  let service: TrackBeatStreamService;
  let httpMock: HttpTestingController;
  let expectedResult: ITrackBeatStream | ITrackBeatStream[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TrackBeatStreamService);
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

    it('should create a TrackBeatStream', () => {
      const track = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(track).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TrackBeatStream', () => {
      const track = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(track).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TrackBeatStream', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TrackBeatStream', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TrackBeatStream', () => {
      const expected = true;

      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTrackBeatStreamToCollectionIfMissing', () => {
      it('should add a TrackBeatStream to an empty array', () => {
        const track: ITrackBeatStream = sampleWithRequiredData;
        expectedResult = service.addTrackBeatStreamToCollectionIfMissing([], track);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(track);
      });

      it('should not add a TrackBeatStream to an array that contains it', () => {
        const track: ITrackBeatStream = sampleWithRequiredData;
        const trackCollection: ITrackBeatStream[] = [
          {
            ...track,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTrackBeatStreamToCollectionIfMissing(trackCollection, track);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TrackBeatStream to an array that doesn't contain it", () => {
        const track: ITrackBeatStream = sampleWithRequiredData;
        const trackCollection: ITrackBeatStream[] = [sampleWithPartialData];
        expectedResult = service.addTrackBeatStreamToCollectionIfMissing(trackCollection, track);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(track);
      });

      it('should add only unique TrackBeatStream to an array', () => {
        const trackArray: ITrackBeatStream[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const trackCollection: ITrackBeatStream[] = [sampleWithRequiredData];
        expectedResult = service.addTrackBeatStreamToCollectionIfMissing(trackCollection, ...trackArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const track: ITrackBeatStream = sampleWithRequiredData;
        const track2: ITrackBeatStream = sampleWithPartialData;
        expectedResult = service.addTrackBeatStreamToCollectionIfMissing([], track, track2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(track);
        expect(expectedResult).toContain(track2);
      });

      it('should accept null and undefined values', () => {
        const track: ITrackBeatStream = sampleWithRequiredData;
        expectedResult = service.addTrackBeatStreamToCollectionIfMissing([], null, track, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(track);
      });

      it('should return initial array if no TrackBeatStream is added', () => {
        const trackCollection: ITrackBeatStream[] = [sampleWithRequiredData];
        expectedResult = service.addTrackBeatStreamToCollectionIfMissing(trackCollection, undefined, null);
        expect(expectedResult).toEqual(trackCollection);
      });
    });

    describe('compareTrackBeatStream', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTrackBeatStream(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = null;

        const compareResult1 = service.compareTrackBeatStream(entity1, entity2);
        const compareResult2 = service.compareTrackBeatStream(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'CBA' };

        const compareResult1 = service.compareTrackBeatStream(entity1, entity2);
        const compareResult2 = service.compareTrackBeatStream(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'ABC' };

        const compareResult1 = service.compareTrackBeatStream(entity1, entity2);
        const compareResult2 = service.compareTrackBeatStream(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
