import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import ReferanceDownloadTaskResolve from './route/referance-download-task-routing-resolve.service';

const referanceDownloadTaskRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/referance-download-task.component').then(m => m.ReferanceDownloadTaskComponent),
    data: {},
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/referance-download-task-detail.component').then(m => m.ReferanceDownloadTaskDetailComponent),
    resolve: {
      referanceDownloadTask: ReferanceDownloadTaskResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/referance-download-task-update.component').then(m => m.ReferanceDownloadTaskUpdateComponent),
    resolve: {
      referanceDownloadTask: ReferanceDownloadTaskResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/referance-download-task-update.component').then(m => m.ReferanceDownloadTaskUpdateComponent),
    resolve: {
      referanceDownloadTask: ReferanceDownloadTaskResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default referanceDownloadTaskRoute;
