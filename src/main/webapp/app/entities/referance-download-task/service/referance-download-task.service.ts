import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IReferanceDownloadTask, NewReferanceDownloadTask } from '../referance-download-task.model';

export type PartialUpdateReferanceDownloadTask = Partial<IReferanceDownloadTask> & Pick<IReferanceDownloadTask, 'id'>;

type RestOf<T extends IReferanceDownloadTask | NewReferanceDownloadTask> = Omit<T, 'referanceScheduleDate'> & {
  referanceScheduleDate?: string | null;
};

export type RestReferanceDownloadTask = RestOf<IReferanceDownloadTask>;

export type NewRestReferanceDownloadTask = RestOf<NewReferanceDownloadTask>;

export type PartialUpdateRestReferanceDownloadTask = RestOf<PartialUpdateReferanceDownloadTask>;

export type EntityResponseType = HttpResponse<IReferanceDownloadTask>;
export type EntityArrayResponseType = HttpResponse<IReferanceDownloadTask[]>;

@Injectable({ providedIn: 'root' })
export class ReferanceDownloadTaskService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/referance-download-tasks');

  create(referanceDownloadTask: NewReferanceDownloadTask): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(referanceDownloadTask);
    return this.http
      .post<RestReferanceDownloadTask>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(referanceDownloadTask: IReferanceDownloadTask): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(referanceDownloadTask);
    return this.http
      .put<RestReferanceDownloadTask>(`${this.resourceUrl}/${this.getReferanceDownloadTaskIdentifier(referanceDownloadTask)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(referanceDownloadTask: PartialUpdateReferanceDownloadTask): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(referanceDownloadTask);
    return this.http
      .patch<RestReferanceDownloadTask>(`${this.resourceUrl}/${this.getReferanceDownloadTaskIdentifier(referanceDownloadTask)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http
      .get<RestReferanceDownloadTask>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestReferanceDownloadTask[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getReferanceDownloadTaskIdentifier(referanceDownloadTask: Pick<IReferanceDownloadTask, 'id'>): string {
    return referanceDownloadTask.id;
  }

  compareReferanceDownloadTask(o1: Pick<IReferanceDownloadTask, 'id'> | null, o2: Pick<IReferanceDownloadTask, 'id'> | null): boolean {
    return o1 && o2 ? this.getReferanceDownloadTaskIdentifier(o1) === this.getReferanceDownloadTaskIdentifier(o2) : o1 === o2;
  }

  addReferanceDownloadTaskToCollectionIfMissing<Type extends Pick<IReferanceDownloadTask, 'id'>>(
    referanceDownloadTaskCollection: Type[],
    ...referanceDownloadTasksToCheck: (Type | null | undefined)[]
  ): Type[] {
    const referanceDownloadTasks: Type[] = referanceDownloadTasksToCheck.filter(isPresent);
    if (referanceDownloadTasks.length > 0) {
      const referanceDownloadTaskCollectionIdentifiers = referanceDownloadTaskCollection.map(referanceDownloadTaskItem =>
        this.getReferanceDownloadTaskIdentifier(referanceDownloadTaskItem),
      );
      const referanceDownloadTasksToAdd = referanceDownloadTasks.filter(referanceDownloadTaskItem => {
        const referanceDownloadTaskIdentifier = this.getReferanceDownloadTaskIdentifier(referanceDownloadTaskItem);
        if (referanceDownloadTaskCollectionIdentifiers.includes(referanceDownloadTaskIdentifier)) {
          return false;
        }
        referanceDownloadTaskCollectionIdentifiers.push(referanceDownloadTaskIdentifier);
        return true;
      });
      return [...referanceDownloadTasksToAdd, ...referanceDownloadTaskCollection];
    }
    return referanceDownloadTaskCollection;
  }

  protected convertDateFromClient<T extends IReferanceDownloadTask | NewReferanceDownloadTask | PartialUpdateReferanceDownloadTask>(
    referanceDownloadTask: T,
  ): RestOf<T> {
    return {
      ...referanceDownloadTask,
      referanceScheduleDate: referanceDownloadTask.referanceScheduleDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restReferanceDownloadTask: RestReferanceDownloadTask): IReferanceDownloadTask {
    return {
      ...restReferanceDownloadTask,
      referanceScheduleDate: restReferanceDownloadTask.referanceScheduleDate
        ? dayjs(restReferanceDownloadTask.referanceScheduleDate)
        : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestReferanceDownloadTask>): HttpResponse<IReferanceDownloadTask> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestReferanceDownloadTask[]>): HttpResponse<IReferanceDownloadTask[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
