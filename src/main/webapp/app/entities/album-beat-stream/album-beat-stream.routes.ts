import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import AlbumBeatStreamResolve from './route/album-beat-stream-routing-resolve.service';

const albumRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/album-beat-stream.component').then(m => m.AlbumBeatStreamComponent),
    data: {},
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/album-beat-stream-detail.component').then(m => m.AlbumBeatStreamDetailComponent),
    resolve: {
      album: AlbumBeatStreamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/album-beat-stream-update.component').then(m => m.AlbumBeatStreamUpdateComponent),
    resolve: {
      album: AlbumBeatStreamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/album-beat-stream-update.component').then(m => m.AlbumBeatStreamUpdateComponent),
    resolve: {
      album: AlbumBeatStreamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default albumRoute;
