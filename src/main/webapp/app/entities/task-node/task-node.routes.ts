import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import TaskNodeResolve from './route/task-node-routing-resolve.service';

const taskNodeRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/task-node.component').then(m => m.TaskNodeComponent),
    data: {},
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/task-node-detail.component').then(m => m.TaskNodeDetailComponent),
    resolve: {
      taskNode: TaskNodeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/task-node-update.component').then(m => m.TaskNodeUpdateComponent),
    resolve: {
      taskNode: TaskNodeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/task-node-update.component').then(m => m.TaskNodeUpdateComponent),
    resolve: {
      taskNode: TaskNodeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default taskNodeRoute;
