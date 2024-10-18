import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IListeningHistoryBeatStream, NewListeningHistoryBeatStream } from '../listening-history-beat-stream.model';

export type PartialUpdateListeningHistoryBeatStream = Partial<IListeningHistoryBeatStream> & Pick<IListeningHistoryBeatStream, 'id'>;

export type EntityResponseType = HttpResponse<IListeningHistoryBeatStream>;
export type EntityArrayResponseType = HttpResponse<IListeningHistoryBeatStream[]>;

@Injectable({ providedIn: 'root' })
export class ListeningHistoryBeatStreamService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/listening-histories');

  create(listeningHistory: NewListeningHistoryBeatStream): Observable<EntityResponseType> {
    return this.http.post<IListeningHistoryBeatStream>(this.resourceUrl, listeningHistory, { observe: 'response' });
  }

  update(listeningHistory: IListeningHistoryBeatStream): Observable<EntityResponseType> {
    return this.http.put<IListeningHistoryBeatStream>(
      `${this.resourceUrl}/${this.getListeningHistoryBeatStreamIdentifier(listeningHistory)}`,
      listeningHistory,
      { observe: 'response' },
    );
  }

  partialUpdate(listeningHistory: PartialUpdateListeningHistoryBeatStream): Observable<EntityResponseType> {
    return this.http.patch<IListeningHistoryBeatStream>(
      `${this.resourceUrl}/${this.getListeningHistoryBeatStreamIdentifier(listeningHistory)}`,
      listeningHistory,
      { observe: 'response' },
    );
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IListeningHistoryBeatStream>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IListeningHistoryBeatStream[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getListeningHistoryBeatStreamIdentifier(listeningHistory: Pick<IListeningHistoryBeatStream, 'id'>): string {
    return listeningHistory.id;
  }

  compareListeningHistoryBeatStream(
    o1: Pick<IListeningHistoryBeatStream, 'id'> | null,
    o2: Pick<IListeningHistoryBeatStream, 'id'> | null,
  ): boolean {
    return o1 && o2 ? this.getListeningHistoryBeatStreamIdentifier(o1) === this.getListeningHistoryBeatStreamIdentifier(o2) : o1 === o2;
  }

  addListeningHistoryBeatStreamToCollectionIfMissing<Type extends Pick<IListeningHistoryBeatStream, 'id'>>(
    listeningHistoryCollection: Type[],
    ...listeningHistoriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const listeningHistories: Type[] = listeningHistoriesToCheck.filter(isPresent);
    if (listeningHistories.length > 0) {
      const listeningHistoryCollectionIdentifiers = listeningHistoryCollection.map(listeningHistoryItem =>
        this.getListeningHistoryBeatStreamIdentifier(listeningHistoryItem),
      );
      const listeningHistoriesToAdd = listeningHistories.filter(listeningHistoryItem => {
        const listeningHistoryIdentifier = this.getListeningHistoryBeatStreamIdentifier(listeningHistoryItem);
        if (listeningHistoryCollectionIdentifiers.includes(listeningHistoryIdentifier)) {
          return false;
        }
        listeningHistoryCollectionIdentifiers.push(listeningHistoryIdentifier);
        return true;
      });
      return [...listeningHistoriesToAdd, ...listeningHistoryCollection];
    }
    return listeningHistoryCollection;
  }
}
