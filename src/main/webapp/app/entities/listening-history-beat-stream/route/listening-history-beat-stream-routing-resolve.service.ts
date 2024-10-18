import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IListeningHistoryBeatStream } from '../listening-history-beat-stream.model';
import { ListeningHistoryBeatStreamService } from '../service/listening-history-beat-stream.service';

const listeningHistoryResolve = (route: ActivatedRouteSnapshot): Observable<null | IListeningHistoryBeatStream> => {
  const id = route.params.id;
  if (id) {
    return inject(ListeningHistoryBeatStreamService)
      .find(id)
      .pipe(
        mergeMap((listeningHistory: HttpResponse<IListeningHistoryBeatStream>) => {
          if (listeningHistory.body) {
            return of(listeningHistory.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default listeningHistoryResolve;
