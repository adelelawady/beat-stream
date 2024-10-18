import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBeatStreamFileBeatStream } from '../beat-stream-file-beat-stream.model';
import { BeatStreamFileBeatStreamService } from '../service/beat-stream-file-beat-stream.service';

const beatStreamFileResolve = (route: ActivatedRouteSnapshot): Observable<null | IBeatStreamFileBeatStream> => {
  const id = route.params.id;
  if (id) {
    return inject(BeatStreamFileBeatStreamService)
      .find(id)
      .pipe(
        mergeMap((beatStreamFile: HttpResponse<IBeatStreamFileBeatStream>) => {
          if (beatStreamFile.body) {
            return of(beatStreamFile.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default beatStreamFileResolve;
