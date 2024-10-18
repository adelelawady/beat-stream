import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPlaylistBeatStream } from '../playlist-beat-stream.model';
import { PlaylistBeatStreamService } from '../service/playlist-beat-stream.service';

const playlistResolve = (route: ActivatedRouteSnapshot): Observable<null | IPlaylistBeatStream> => {
  const id = route.params.id;
  if (id) {
    return inject(PlaylistBeatStreamService)
      .find(id)
      .pipe(
        mergeMap((playlist: HttpResponse<IPlaylistBeatStream>) => {
          if (playlist.body) {
            return of(playlist.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default playlistResolve;
