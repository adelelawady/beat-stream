import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITaskNode } from '../task-node.model';
import { TaskNodeService } from '../service/task-node.service';

const taskNodeResolve = (route: ActivatedRouteSnapshot): Observable<null | ITaskNode> => {
  const id = route.params.id;
  if (id) {
    return inject(TaskNodeService)
      .find(id)
      .pipe(
        mergeMap((taskNode: HttpResponse<ITaskNode>) => {
          if (taskNode.body) {
            return of(taskNode.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default taskNodeResolve;
