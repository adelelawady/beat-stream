import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import ListeningHistoryBeatStreamResolve from './route/listening-history-beat-stream-routing-resolve.service';

const listeningHistoryRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/listening-history-beat-stream.component').then(m => m.ListeningHistoryBeatStreamComponent),
    data: {},
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () =>
      import('./detail/listening-history-beat-stream-detail.component').then(m => m.ListeningHistoryBeatStreamDetailComponent),
    resolve: {
      listeningHistory: ListeningHistoryBeatStreamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () =>
      import('./update/listening-history-beat-stream-update.component').then(m => m.ListeningHistoryBeatStreamUpdateComponent),
    resolve: {
      listeningHistory: ListeningHistoryBeatStreamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () =>
      import('./update/listening-history-beat-stream-update.component').then(m => m.ListeningHistoryBeatStreamUpdateComponent),
    resolve: {
      listeningHistory: ListeningHistoryBeatStreamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default listeningHistoryRoute;
