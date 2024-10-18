import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import BeatStreamFileBeatStreamResolve from './route/beat-stream-file-beat-stream-routing-resolve.service';

const beatStreamFileRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/beat-stream-file-beat-stream.component').then(m => m.BeatStreamFileBeatStreamComponent),
    data: {},
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () =>
      import('./detail/beat-stream-file-beat-stream-detail.component').then(m => m.BeatStreamFileBeatStreamDetailComponent),
    resolve: {
      beatStreamFile: BeatStreamFileBeatStreamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () =>
      import('./update/beat-stream-file-beat-stream-update.component').then(m => m.BeatStreamFileBeatStreamUpdateComponent),
    resolve: {
      beatStreamFile: BeatStreamFileBeatStreamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () =>
      import('./update/beat-stream-file-beat-stream-update.component').then(m => m.BeatStreamFileBeatStreamUpdateComponent),
    resolve: {
      beatStreamFile: BeatStreamFileBeatStreamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default beatStreamFileRoute;
