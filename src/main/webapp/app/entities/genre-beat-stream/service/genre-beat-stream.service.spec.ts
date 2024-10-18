import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IGenreBeatStream } from '../genre-beat-stream.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../genre-beat-stream.test-samples';

import { GenreBeatStreamService } from './genre-beat-stream.service';

const requireRestSample: IGenreBeatStream = {
  ...sampleWithRequiredData,
};

describe('GenreBeatStream Service', () => {
  let service: GenreBeatStreamService;
  let httpMock: HttpTestingController;
  let expectedResult: IGenreBeatStream | IGenreBeatStream[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(GenreBeatStreamService);
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

    it('should create a GenreBeatStream', () => {
      const genre = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(genre).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a GenreBeatStream', () => {
      const genre = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(genre).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a GenreBeatStream', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of GenreBeatStream', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a GenreBeatStream', () => {
      const expected = true;

      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addGenreBeatStreamToCollectionIfMissing', () => {
      it('should add a GenreBeatStream to an empty array', () => {
        const genre: IGenreBeatStream = sampleWithRequiredData;
        expectedResult = service.addGenreBeatStreamToCollectionIfMissing([], genre);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(genre);
      });

      it('should not add a GenreBeatStream to an array that contains it', () => {
        const genre: IGenreBeatStream = sampleWithRequiredData;
        const genreCollection: IGenreBeatStream[] = [
          {
            ...genre,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addGenreBeatStreamToCollectionIfMissing(genreCollection, genre);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a GenreBeatStream to an array that doesn't contain it", () => {
        const genre: IGenreBeatStream = sampleWithRequiredData;
        const genreCollection: IGenreBeatStream[] = [sampleWithPartialData];
        expectedResult = service.addGenreBeatStreamToCollectionIfMissing(genreCollection, genre);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(genre);
      });

      it('should add only unique GenreBeatStream to an array', () => {
        const genreArray: IGenreBeatStream[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const genreCollection: IGenreBeatStream[] = [sampleWithRequiredData];
        expectedResult = service.addGenreBeatStreamToCollectionIfMissing(genreCollection, ...genreArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const genre: IGenreBeatStream = sampleWithRequiredData;
        const genre2: IGenreBeatStream = sampleWithPartialData;
        expectedResult = service.addGenreBeatStreamToCollectionIfMissing([], genre, genre2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(genre);
        expect(expectedResult).toContain(genre2);
      });

      it('should accept null and undefined values', () => {
        const genre: IGenreBeatStream = sampleWithRequiredData;
        expectedResult = service.addGenreBeatStreamToCollectionIfMissing([], null, genre, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(genre);
      });

      it('should return initial array if no GenreBeatStream is added', () => {
        const genreCollection: IGenreBeatStream[] = [sampleWithRequiredData];
        expectedResult = service.addGenreBeatStreamToCollectionIfMissing(genreCollection, undefined, null);
        expect(expectedResult).toEqual(genreCollection);
      });
    });

    describe('compareGenreBeatStream', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareGenreBeatStream(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = null;

        const compareResult1 = service.compareGenreBeatStream(entity1, entity2);
        const compareResult2 = service.compareGenreBeatStream(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'CBA' };

        const compareResult1 = service.compareGenreBeatStream(entity1, entity2);
        const compareResult2 = service.compareGenreBeatStream(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'ABC' };

        const compareResult1 = service.compareGenreBeatStream(entity1, entity2);
        const compareResult2 = service.compareGenreBeatStream(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
