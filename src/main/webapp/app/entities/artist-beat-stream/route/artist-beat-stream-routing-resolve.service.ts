import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IArtistBeatStream } from '../artist-beat-stream.model';
import { ArtistBeatStreamService } from '../service/artist-beat-stream.service';

const artistResolve = (route: ActivatedRouteSnapshot): Observable<null | IArtistBeatStream> => {
  const id = route.params.id;
  if (id) {
    return inject(ArtistBeatStreamService)
      .find(id)
      .pipe(
        mergeMap((artist: HttpResponse<IArtistBeatStream>) => {
          if (artist.body) {
            return of(artist.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default artistResolve;
