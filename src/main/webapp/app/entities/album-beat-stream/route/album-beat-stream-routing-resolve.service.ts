import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAlbumBeatStream } from '../album-beat-stream.model';
import { AlbumBeatStreamService } from '../service/album-beat-stream.service';

const albumResolve = (route: ActivatedRouteSnapshot): Observable<null | IAlbumBeatStream> => {
  const id = route.params.id;
  if (id) {
    return inject(AlbumBeatStreamService)
      .find(id)
      .pipe(
        mergeMap((album: HttpResponse<IAlbumBeatStream>) => {
          if (album.body) {
            return of(album.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default albumResolve;
