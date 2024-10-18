import { TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { ActivatedRoute, ActivatedRouteSnapshot, Router, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';

import { IListeningHistoryBeatStream } from '../listening-history-beat-stream.model';
import { ListeningHistoryBeatStreamService } from '../service/listening-history-beat-stream.service';

import listeningHistoryResolve from './listening-history-beat-stream-routing-resolve.service';

describe('ListeningHistoryBeatStream routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let service: ListeningHistoryBeatStreamService;
  let resultListeningHistoryBeatStream: IListeningHistoryBeatStream | null | undefined;

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
    service = TestBed.inject(ListeningHistoryBeatStreamService);
    resultListeningHistoryBeatStream = undefined;
  });

  describe('resolve', () => {
    it('should return IListeningHistoryBeatStream returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      TestBed.runInInjectionContext(() => {
        listeningHistoryResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultListeningHistoryBeatStream = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith('ABC');
      expect(resultListeningHistoryBeatStream).toEqual({ id: 'ABC' });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      TestBed.runInInjectionContext(() => {
        listeningHistoryResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultListeningHistoryBeatStream = result;
          },
        });
      });

      // THEN
      expect(service.find).not.toHaveBeenCalled();
      expect(resultListeningHistoryBeatStream).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<IListeningHistoryBeatStream>({ body: null })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      TestBed.runInInjectionContext(() => {
        listeningHistoryResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultListeningHistoryBeatStream = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith('ABC');
      expect(resultListeningHistoryBeatStream).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
