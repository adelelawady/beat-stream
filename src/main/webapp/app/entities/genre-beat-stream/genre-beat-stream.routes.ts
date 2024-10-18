import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import GenreBeatStreamResolve from './route/genre-beat-stream-routing-resolve.service';

const genreRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/genre-beat-stream.component').then(m => m.GenreBeatStreamComponent),
    data: {},
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/genre-beat-stream-detail.component').then(m => m.GenreBeatStreamDetailComponent),
    resolve: {
      genre: GenreBeatStreamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/genre-beat-stream-update.component').then(m => m.GenreBeatStreamUpdateComponent),
    resolve: {
      genre: GenreBeatStreamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/genre-beat-stream-update.component').then(m => m.GenreBeatStreamUpdateComponent),
    resolve: {
      genre: GenreBeatStreamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default genreRoute;
