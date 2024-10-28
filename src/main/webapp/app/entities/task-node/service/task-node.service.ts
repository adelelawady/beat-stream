import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITaskNode, NewTaskNode } from '../task-node.model';

export type PartialUpdateTaskNode = Partial<ITaskNode> & Pick<ITaskNode, 'id'>;

type RestOf<T extends ITaskNode | NewTaskNode> = Omit<T, 'scheduledStartTime'> & {
  scheduledStartTime?: string | null;
};

export type RestTaskNode = RestOf<ITaskNode>;

export type NewRestTaskNode = RestOf<NewTaskNode>;

export type PartialUpdateRestTaskNode = RestOf<PartialUpdateTaskNode>;

export type EntityResponseType = HttpResponse<ITaskNode>;
export type EntityArrayResponseType = HttpResponse<ITaskNode[]>;

@Injectable({ providedIn: 'root' })
export class TaskNodeService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/task-nodes');

  create(taskNode: NewTaskNode): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(taskNode);
    return this.http
      .post<RestTaskNode>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(taskNode: ITaskNode): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(taskNode);
    return this.http
      .put<RestTaskNode>(`${this.resourceUrl}/${this.getTaskNodeIdentifier(taskNode)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(taskNode: PartialUpdateTaskNode): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(taskNode);
    return this.http
      .patch<RestTaskNode>(`${this.resourceUrl}/${this.getTaskNodeIdentifier(taskNode)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http
      .get<RestTaskNode>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTaskNode[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTaskNodeIdentifier(taskNode: Pick<ITaskNode, 'id'>): string {
    return taskNode.id;
  }

  compareTaskNode(o1: Pick<ITaskNode, 'id'> | null, o2: Pick<ITaskNode, 'id'> | null): boolean {
    return o1 && o2 ? this.getTaskNodeIdentifier(o1) === this.getTaskNodeIdentifier(o2) : o1 === o2;
  }

  addTaskNodeToCollectionIfMissing<Type extends Pick<ITaskNode, 'id'>>(
    taskNodeCollection: Type[],
    ...taskNodesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const taskNodes: Type[] = taskNodesToCheck.filter(isPresent);
    if (taskNodes.length > 0) {
      const taskNodeCollectionIdentifiers = taskNodeCollection.map(taskNodeItem => this.getTaskNodeIdentifier(taskNodeItem));
      const taskNodesToAdd = taskNodes.filter(taskNodeItem => {
        const taskNodeIdentifier = this.getTaskNodeIdentifier(taskNodeItem);
        if (taskNodeCollectionIdentifiers.includes(taskNodeIdentifier)) {
          return false;
        }
        taskNodeCollectionIdentifiers.push(taskNodeIdentifier);
        return true;
      });
      return [...taskNodesToAdd, ...taskNodeCollection];
    }
    return taskNodeCollection;
  }

  protected convertDateFromClient<T extends ITaskNode | NewTaskNode | PartialUpdateTaskNode>(taskNode: T): RestOf<T> {
    return {
      ...taskNode,
      scheduledStartTime: taskNode.scheduledStartTime?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restTaskNode: RestTaskNode): ITaskNode {
    return {
      ...restTaskNode,
      scheduledStartTime: restTaskNode.scheduledStartTime ? dayjs(restTaskNode.scheduledStartTime) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTaskNode>): HttpResponse<ITaskNode> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestTaskNode[]>): HttpResponse<ITaskNode[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
