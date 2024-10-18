import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import PlaylistBeatStreamResolve from './route/playlist-beat-stream-routing-resolve.service';

const playlistRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/playlist-beat-stream.component').then(m => m.PlaylistBeatStreamComponent),
    data: {},
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/playlist-beat-stream-detail.component').then(m => m.PlaylistBeatStreamDetailComponent),
    resolve: {
      playlist: PlaylistBeatStreamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/playlist-beat-stream-update.component').then(m => m.PlaylistBeatStreamUpdateComponent),
    resolve: {
      playlist: PlaylistBeatStreamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/playlist-beat-stream-update.component').then(m => m.PlaylistBeatStreamUpdateComponent),
    resolve: {
      playlist: PlaylistBeatStreamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default playlistRoute;
