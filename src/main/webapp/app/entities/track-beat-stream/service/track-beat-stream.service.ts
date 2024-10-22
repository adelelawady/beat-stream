/* eslint-disable prettier/prettier */

import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITrackBeatStream, NewTrackBeatStream } from '../track-beat-stream.model';

export type PartialUpdateTrackBeatStream = Partial<ITrackBeatStream> & Pick<ITrackBeatStream, 'id'>;

export type EntityResponseType = HttpResponse<ITrackBeatStream>;
export type EntityArrayResponseType = HttpResponse<ITrackBeatStream[]>;

@Injectable({ providedIn: 'root' })
export class TrackBeatStreamService {
  public applicationConfigService = inject(ApplicationConfigService);
  protected http = inject(HttpClient);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tracks');

  protected resourceAPIUrl = this.applicationConfigService.getEndpointFor('api/audio');

  create(track: NewTrackBeatStream): Observable<EntityResponseType> {
    return this.http.post<ITrackBeatStream>(this.resourceUrl, track, { observe: 'response' });
  }

  update(track: ITrackBeatStream): Observable<EntityResponseType> {
    return this.http.put<ITrackBeatStream>(`${this.resourceUrl}/${this.getTrackBeatStreamIdentifier(track)}`, track, {
      observe: 'response',
    });
  }

  uploadSongFile(trackUploadData: any): Observable<HttpResponse<object>> {
    return this.http.post(`${this.resourceAPIUrl}/upload`, trackUploadData, {
      observe: 'response',
    });
  }

  partialUpdate(track: PartialUpdateTrackBeatStream): Observable<EntityResponseType> {
    return this.http.patch<ITrackBeatStream>(`${this.resourceUrl}/${this.getTrackBeatStreamIdentifier(track)}`, track, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<ITrackBeatStream>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITrackBeatStream[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTrackBeatStreamIdentifier(track: Pick<ITrackBeatStream, 'id'>): string {
    return track.id;
  }

  compareTrackBeatStream(o1: Pick<ITrackBeatStream, 'id'> | null, o2: Pick<ITrackBeatStream, 'id'> | null): boolean {
    return o1 && o2 ? this.getTrackBeatStreamIdentifier(o1) === this.getTrackBeatStreamIdentifier(o2) : o1 === o2;
  }

  addTrackBeatStreamToCollectionIfMissing<Type extends Pick<ITrackBeatStream, 'id'>>(
    trackCollection: Type[],
    ...tracksToCheck: (Type | null | undefined)[]
  ): Type[] {
    const tracks: Type[] = tracksToCheck.filter(isPresent);
    if (tracks.length > 0) {
      const trackCollectionIdentifiers = trackCollection.map(trackItem => this.getTrackBeatStreamIdentifier(trackItem));
      const tracksToAdd = tracks.filter(trackItem => {
        const trackIdentifier = this.getTrackBeatStreamIdentifier(trackItem);
        if (trackCollectionIdentifiers.includes(trackIdentifier)) {
          return false;
        }
        trackCollectionIdentifiers.push(trackIdentifier);
        return true;
      });
      return [...tracksToAdd, ...trackCollection];
    }
    return trackCollection;
  }
}
