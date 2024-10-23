import { TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { ActivatedRoute, ActivatedRouteSnapshot, Router, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';

import { IReferanceDownloadTask } from '../referance-download-task.model';
import { ReferanceDownloadTaskService } from '../service/referance-download-task.service';

import referanceDownloadTaskResolve from './referance-download-task-routing-resolve.service';

describe('ReferanceDownloadTask routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let service: ReferanceDownloadTaskService;
  let resultReferanceDownloadTask: IReferanceDownloadTask | null | undefined;

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
    service = TestBed.inject(ReferanceDownloadTaskService);
    resultReferanceDownloadTask = undefined;
  });

  describe('resolve', () => {
    it('should return IReferanceDownloadTask returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      TestBed.runInInjectionContext(() => {
        referanceDownloadTaskResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultReferanceDownloadTask = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith('ABC');
      expect(resultReferanceDownloadTask).toEqual({ id: 'ABC' });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      TestBed.runInInjectionContext(() => {
        referanceDownloadTaskResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultReferanceDownloadTask = result;
          },
        });
      });

      // THEN
      expect(service.find).not.toHaveBeenCalled();
      expect(resultReferanceDownloadTask).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<IReferanceDownloadTask>({ body: null })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      TestBed.runInInjectionContext(() => {
        referanceDownloadTaskResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultReferanceDownloadTask = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith('ABC');
      expect(resultReferanceDownloadTask).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
