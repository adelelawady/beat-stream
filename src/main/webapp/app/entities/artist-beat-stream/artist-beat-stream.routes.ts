import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import ArtistBeatStreamResolve from './route/artist-beat-stream-routing-resolve.service';

const artistRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/artist-beat-stream.component').then(m => m.ArtistBeatStreamComponent),
    data: {},
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/artist-beat-stream-detail.component').then(m => m.ArtistBeatStreamDetailComponent),
    resolve: {
      artist: ArtistBeatStreamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/artist-beat-stream-update.component').then(m => m.ArtistBeatStreamUpdateComponent),
    resolve: {
      artist: ArtistBeatStreamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/artist-beat-stream-update.component').then(m => m.ArtistBeatStreamUpdateComponent),
    resolve: {
      artist: ArtistBeatStreamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default artistRoute;
