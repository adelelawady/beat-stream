import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITrackBeatStream } from '../track-beat-stream.model';
import { TrackBeatStreamService } from '../service/track-beat-stream.service';

const trackResolve = (route: ActivatedRouteSnapshot): Observable<null | ITrackBeatStream> => {
  const id = route.params.id;
  if (id) {
    return inject(TrackBeatStreamService)
      .find(id)
      .pipe(
        mergeMap((track: HttpResponse<ITrackBeatStream>) => {
          if (track.body) {
            return of(track.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default trackResolve;
