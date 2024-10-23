import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IReferanceDownloadTask } from '../referance-download-task.model';
import { ReferanceDownloadTaskService } from '../service/referance-download-task.service';

const referanceDownloadTaskResolve = (route: ActivatedRouteSnapshot): Observable<null | IReferanceDownloadTask> => {
  const id = route.params.id;
  if (id) {
    return inject(ReferanceDownloadTaskService)
      .find(id)
      .pipe(
        mergeMap((referanceDownloadTask: HttpResponse<IReferanceDownloadTask>) => {
          if (referanceDownloadTask.body) {
            return of(referanceDownloadTask.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default referanceDownloadTaskResolve;
