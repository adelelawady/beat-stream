import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IAlbumBeatStream } from '../album-beat-stream.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../album-beat-stream.test-samples';

import { AlbumBeatStreamService } from './album-beat-stream.service';

const requireRestSample: IAlbumBeatStream = {
  ...sampleWithRequiredData,
};

describe('AlbumBeatStream Service', () => {
  let service: AlbumBeatStreamService;
  let httpMock: HttpTestingController;
  let expectedResult: IAlbumBeatStream | IAlbumBeatStream[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(AlbumBeatStreamService);
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

    it('should create a AlbumBeatStream', () => {
      const album = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(album).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a AlbumBeatStream', () => {
      const album = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(album).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a AlbumBeatStream', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of AlbumBeatStream', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a AlbumBeatStream', () => {
      const expected = true;

      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addAlbumBeatStreamToCollectionIfMissing', () => {
      it('should add a AlbumBeatStream to an empty array', () => {
        const album: IAlbumBeatStream = sampleWithRequiredData;
        expectedResult = service.addAlbumBeatStreamToCollectionIfMissing([], album);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(album);
      });

      it('should not add a AlbumBeatStream to an array that contains it', () => {
        const album: IAlbumBeatStream = sampleWithRequiredData;
        const albumCollection: IAlbumBeatStream[] = [
          {
            ...album,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addAlbumBeatStreamToCollectionIfMissing(albumCollection, album);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a AlbumBeatStream to an array that doesn't contain it", () => {
        const album: IAlbumBeatStream = sampleWithRequiredData;
        const albumCollection: IAlbumBeatStream[] = [sampleWithPartialData];
        expectedResult = service.addAlbumBeatStreamToCollectionIfMissing(albumCollection, album);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(album);
      });

      it('should add only unique AlbumBeatStream to an array', () => {
        const albumArray: IAlbumBeatStream[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const albumCollection: IAlbumBeatStream[] = [sampleWithRequiredData];
        expectedResult = service.addAlbumBeatStreamToCollectionIfMissing(albumCollection, ...albumArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const album: IAlbumBeatStream = sampleWithRequiredData;
        const album2: IAlbumBeatStream = sampleWithPartialData;
        expectedResult = service.addAlbumBeatStreamToCollectionIfMissing([], album, album2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(album);
        expect(expectedResult).toContain(album2);
      });

      it('should accept null and undefined values', () => {
        const album: IAlbumBeatStream = sampleWithRequiredData;
        expectedResult = service.addAlbumBeatStreamToCollectionIfMissing([], null, album, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(album);
      });

      it('should return initial array if no AlbumBeatStream is added', () => {
        const albumCollection: IAlbumBeatStream[] = [sampleWithRequiredData];
        expectedResult = service.addAlbumBeatStreamToCollectionIfMissing(albumCollection, undefined, null);
        expect(expectedResult).toEqual(albumCollection);
      });
    });

    describe('compareAlbumBeatStream', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareAlbumBeatStream(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = null;

        const compareResult1 = service.compareAlbumBeatStream(entity1, entity2);
        const compareResult2 = service.compareAlbumBeatStream(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'CBA' };

        const compareResult1 = service.compareAlbumBeatStream(entity1, entity2);
        const compareResult2 = service.compareAlbumBeatStream(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'ABC' };

        const compareResult1 = service.compareAlbumBeatStream(entity1, entity2);
        const compareResult2 = service.compareAlbumBeatStream(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
