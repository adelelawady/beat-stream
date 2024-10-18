import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IArtistBeatStream } from '../artist-beat-stream.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../artist-beat-stream.test-samples';

import { ArtistBeatStreamService } from './artist-beat-stream.service';

const requireRestSample: IArtistBeatStream = {
  ...sampleWithRequiredData,
};

describe('ArtistBeatStream Service', () => {
  let service: ArtistBeatStreamService;
  let httpMock: HttpTestingController;
  let expectedResult: IArtistBeatStream | IArtistBeatStream[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ArtistBeatStreamService);
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

    it('should create a ArtistBeatStream', () => {
      const artist = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(artist).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ArtistBeatStream', () => {
      const artist = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(artist).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ArtistBeatStream', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ArtistBeatStream', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ArtistBeatStream', () => {
      const expected = true;

      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addArtistBeatStreamToCollectionIfMissing', () => {
      it('should add a ArtistBeatStream to an empty array', () => {
        const artist: IArtistBeatStream = sampleWithRequiredData;
        expectedResult = service.addArtistBeatStreamToCollectionIfMissing([], artist);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(artist);
      });

      it('should not add a ArtistBeatStream to an array that contains it', () => {
        const artist: IArtistBeatStream = sampleWithRequiredData;
        const artistCollection: IArtistBeatStream[] = [
          {
            ...artist,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addArtistBeatStreamToCollectionIfMissing(artistCollection, artist);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ArtistBeatStream to an array that doesn't contain it", () => {
        const artist: IArtistBeatStream = sampleWithRequiredData;
        const artistCollection: IArtistBeatStream[] = [sampleWithPartialData];
        expectedResult = service.addArtistBeatStreamToCollectionIfMissing(artistCollection, artist);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(artist);
      });

      it('should add only unique ArtistBeatStream to an array', () => {
        const artistArray: IArtistBeatStream[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const artistCollection: IArtistBeatStream[] = [sampleWithRequiredData];
        expectedResult = service.addArtistBeatStreamToCollectionIfMissing(artistCollection, ...artistArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const artist: IArtistBeatStream = sampleWithRequiredData;
        const artist2: IArtistBeatStream = sampleWithPartialData;
        expectedResult = service.addArtistBeatStreamToCollectionIfMissing([], artist, artist2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(artist);
        expect(expectedResult).toContain(artist2);
      });

      it('should accept null and undefined values', () => {
        const artist: IArtistBeatStream = sampleWithRequiredData;
        expectedResult = service.addArtistBeatStreamToCollectionIfMissing([], null, artist, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(artist);
      });

      it('should return initial array if no ArtistBeatStream is added', () => {
        const artistCollection: IArtistBeatStream[] = [sampleWithRequiredData];
        expectedResult = service.addArtistBeatStreamToCollectionIfMissing(artistCollection, undefined, null);
        expect(expectedResult).toEqual(artistCollection);
      });
    });

    describe('compareArtistBeatStream', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareArtistBeatStream(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = null;

        const compareResult1 = service.compareArtistBeatStream(entity1, entity2);
        const compareResult2 = service.compareArtistBeatStream(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'CBA' };

        const compareResult1 = service.compareArtistBeatStream(entity1, entity2);
        const compareResult2 = service.compareArtistBeatStream(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'ABC' };

        const compareResult1 = service.compareArtistBeatStream(entity1, entity2);
        const compareResult2 = service.compareArtistBeatStream(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
