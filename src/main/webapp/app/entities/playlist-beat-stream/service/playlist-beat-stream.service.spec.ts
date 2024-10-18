import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IPlaylistBeatStream } from '../playlist-beat-stream.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../playlist-beat-stream.test-samples';

import { PlaylistBeatStreamService } from './playlist-beat-stream.service';

const requireRestSample: IPlaylistBeatStream = {
  ...sampleWithRequiredData,
};

describe('PlaylistBeatStream Service', () => {
  let service: PlaylistBeatStreamService;
  let httpMock: HttpTestingController;
  let expectedResult: IPlaylistBeatStream | IPlaylistBeatStream[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PlaylistBeatStreamService);
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

    it('should create a PlaylistBeatStream', () => {
      const playlist = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(playlist).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PlaylistBeatStream', () => {
      const playlist = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(playlist).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PlaylistBeatStream', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PlaylistBeatStream', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PlaylistBeatStream', () => {
      const expected = true;

      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addPlaylistBeatStreamToCollectionIfMissing', () => {
      it('should add a PlaylistBeatStream to an empty array', () => {
        const playlist: IPlaylistBeatStream = sampleWithRequiredData;
        expectedResult = service.addPlaylistBeatStreamToCollectionIfMissing([], playlist);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(playlist);
      });

      it('should not add a PlaylistBeatStream to an array that contains it', () => {
        const playlist: IPlaylistBeatStream = sampleWithRequiredData;
        const playlistCollection: IPlaylistBeatStream[] = [
          {
            ...playlist,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPlaylistBeatStreamToCollectionIfMissing(playlistCollection, playlist);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PlaylistBeatStream to an array that doesn't contain it", () => {
        const playlist: IPlaylistBeatStream = sampleWithRequiredData;
        const playlistCollection: IPlaylistBeatStream[] = [sampleWithPartialData];
        expectedResult = service.addPlaylistBeatStreamToCollectionIfMissing(playlistCollection, playlist);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(playlist);
      });

      it('should add only unique PlaylistBeatStream to an array', () => {
        const playlistArray: IPlaylistBeatStream[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const playlistCollection: IPlaylistBeatStream[] = [sampleWithRequiredData];
        expectedResult = service.addPlaylistBeatStreamToCollectionIfMissing(playlistCollection, ...playlistArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const playlist: IPlaylistBeatStream = sampleWithRequiredData;
        const playlist2: IPlaylistBeatStream = sampleWithPartialData;
        expectedResult = service.addPlaylistBeatStreamToCollectionIfMissing([], playlist, playlist2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(playlist);
        expect(expectedResult).toContain(playlist2);
      });

      it('should accept null and undefined values', () => {
        const playlist: IPlaylistBeatStream = sampleWithRequiredData;
        expectedResult = service.addPlaylistBeatStreamToCollectionIfMissing([], null, playlist, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(playlist);
      });

      it('should return initial array if no PlaylistBeatStream is added', () => {
        const playlistCollection: IPlaylistBeatStream[] = [sampleWithRequiredData];
        expectedResult = service.addPlaylistBeatStreamToCollectionIfMissing(playlistCollection, undefined, null);
        expect(expectedResult).toEqual(playlistCollection);
      });
    });

    describe('comparePlaylistBeatStream', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePlaylistBeatStream(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = null;

        const compareResult1 = service.comparePlaylistBeatStream(entity1, entity2);
        const compareResult2 = service.comparePlaylistBeatStream(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'CBA' };

        const compareResult1 = service.comparePlaylistBeatStream(entity1, entity2);
        const compareResult2 = service.comparePlaylistBeatStream(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'ABC' };

        const compareResult1 = service.comparePlaylistBeatStream(entity1, entity2);
        const compareResult2 = service.comparePlaylistBeatStream(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
