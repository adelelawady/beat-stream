import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import TrackBeatStreamResolve from './route/track-beat-stream-routing-resolve.service';

const trackRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/track-beat-stream.component').then(m => m.TrackBeatStreamComponent),
    data: {},
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/track-beat-stream-detail.component').then(m => m.TrackBeatStreamDetailComponent),
    resolve: {
      track: TrackBeatStreamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/track-beat-stream-update.component').then(m => m.TrackBeatStreamUpdateComponent),
    resolve: {
      track: TrackBeatStreamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/track-beat-stream-update.component').then(m => m.TrackBeatStreamUpdateComponent),
    resolve: {
      track: TrackBeatStreamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default trackRoute;
