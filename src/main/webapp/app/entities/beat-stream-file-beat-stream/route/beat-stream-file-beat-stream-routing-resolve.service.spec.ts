import { TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { ActivatedRoute, ActivatedRouteSnapshot, Router, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';

import { IBeatStreamFileBeatStream } from '../beat-stream-file-beat-stream.model';
import { BeatStreamFileBeatStreamService } from '../service/beat-stream-file-beat-stream.service';

import beatStreamFileResolve from './beat-stream-file-beat-stream-routing-resolve.service';

describe('BeatStreamFileBeatStream routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let service: BeatStreamFileBeatStreamService;
  let resultBeatStreamFileBeatStream: IBeatStreamFileBeatStream | null | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    service = TestBed.inject(BeatStreamFileBeatStreamService);
    resultBeatStreamFileBeatStream = undefined;
  });

  describe('resolve', () => {
    it('should return IBeatStreamFileBeatStream returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      TestBed.runInInjectionContext(() => {
        beatStreamFileResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultBeatStreamFileBeatStream = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith('ABC');
      expect(resultBeatStreamFileBeatStream).toEqual({ id: 'ABC' });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      TestBed.runInInjectionContext(() => {
        beatStreamFileResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultBeatStreamFileBeatStream = result;
          },
        });
      });

      // THEN
      expect(service.find).not.toHaveBeenCalled();
      expect(resultBeatStreamFileBeatStream).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<IBeatStreamFileBeatStream>({ body: null })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      TestBed.runInInjectionContext(() => {
        beatStreamFileResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultBeatStreamFileBeatStream = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith('ABC');
      expect(resultBeatStreamFileBeatStream).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
