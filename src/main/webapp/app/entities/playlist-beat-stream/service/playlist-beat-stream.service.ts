import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPlaylistBeatStream, NewPlaylistBeatStream } from '../playlist-beat-stream.model';

export type PartialUpdatePlaylistBeatStream = Partial<IPlaylistBeatStream> & Pick<IPlaylistBeatStream, 'id'>;

export type EntityResponseType = HttpResponse<IPlaylistBeatStream>;
export type EntityArrayResponseType = HttpResponse<IPlaylistBeatStream[]>;

@Injectable({ providedIn: 'root' })
export class PlaylistBeatStreamService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/playlists');

  protected resourceAPIUrl = this.applicationConfigService.getEndpointFor('api/playlist');

  create(playlist: NewPlaylistBeatStream): Observable<EntityResponseType> {
    return this.http.post<IPlaylistBeatStream>(this.resourceUrl, playlist, { observe: 'response' });
  }

  createPlayList(playlist: any): Observable<HttpResponse<any>> {
    return this.http.post(this.resourceAPIUrl, playlist, { observe: 'response' });
  }

  getAllPlaylists(): Observable<HttpResponse<any>> {
    return this.http.get(this.resourceAPIUrl, { observe: 'response' });
  }

  getPlaylist(id: string): Observable<HttpResponse<any>> {
    return this.http.get(`${this.resourceAPIUrl}/${id}`, { observe: 'response' });
  }

  deletePlaylist(id: string): Observable<HttpResponse<any>> {
    return this.http.delete(`${this.resourceAPIUrl}/${id}`, { observe: 'response' });
  }

  update(playlist: IPlaylistBeatStream): Observable<EntityResponseType> {
    return this.http.put<IPlaylistBeatStream>(`${this.resourceUrl}/${this.getPlaylistBeatStreamIdentifier(playlist)}`, playlist, {
      observe: 'response',
    });
  }

  partialUpdate(playlist: PartialUpdatePlaylistBeatStream): Observable<EntityResponseType> {
    return this.http.patch<IPlaylistBeatStream>(`${this.resourceUrl}/${this.getPlaylistBeatStreamIdentifier(playlist)}`, playlist, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IPlaylistBeatStream>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPlaylistBeatStream[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPlaylistBeatStreamIdentifier(playlist: Pick<IPlaylistBeatStream, 'id'>): string {
    return playlist.id;
  }

  comparePlaylistBeatStream(o1: Pick<IPlaylistBeatStream, 'id'> | null, o2: Pick<IPlaylistBeatStream, 'id'> | null): boolean {
    return o1 && o2 ? this.getPlaylistBeatStreamIdentifier(o1) === this.getPlaylistBeatStreamIdentifier(o2) : o1 === o2;
  }

  addPlaylistBeatStreamToCollectionIfMissing<Type extends Pick<IPlaylistBeatStream, 'id'>>(
    playlistCollection: Type[],
    ...playlistsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const playlists: Type[] = playlistsToCheck.filter(isPresent);
    if (playlists.length > 0) {
      const playlistCollectionIdentifiers = playlistCollection.map(playlistItem => this.getPlaylistBeatStreamIdentifier(playlistItem));
      const playlistsToAdd = playlists.filter(playlistItem => {
        const playlistIdentifier = this.getPlaylistBeatStreamIdentifier(playlistItem);
        if (playlistCollectionIdentifiers.includes(playlistIdentifier)) {
          return false;
        }
        playlistCollectionIdentifiers.push(playlistIdentifier);
        return true;
      });
      return [...playlistsToAdd, ...playlistCollection];
    }
    return playlistCollection;
  }
}
