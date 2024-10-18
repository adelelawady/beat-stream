import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IGenreBeatStream } from '../genre-beat-stream.model';
import { GenreBeatStreamService } from '../service/genre-beat-stream.service';

const genreResolve = (route: ActivatedRouteSnapshot): Observable<null | IGenreBeatStream> => {
  const id = route.params.id;
  if (id) {
    return inject(GenreBeatStreamService)
      .find(id)
      .pipe(
        mergeMap((genre: HttpResponse<IGenreBeatStream>) => {
          if (genre.body) {
            return of(genre.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default genreResolve;
