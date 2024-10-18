import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAlbumBeatStream, NewAlbumBeatStream } from '../album-beat-stream.model';

export type PartialUpdateAlbumBeatStream = Partial<IAlbumBeatStream> & Pick<IAlbumBeatStream, 'id'>;

export type EntityResponseType = HttpResponse<IAlbumBeatStream>;
export type EntityArrayResponseType = HttpResponse<IAlbumBeatStream[]>;

@Injectable({ providedIn: 'root' })
export class AlbumBeatStreamService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/albums');

  create(album: NewAlbumBeatStream): Observable<EntityResponseType> {
    return this.http.post<IAlbumBeatStream>(this.resourceUrl, album, { observe: 'response' });
  }

  update(album: IAlbumBeatStream): Observable<EntityResponseType> {
    return this.http.put<IAlbumBeatStream>(`${this.resourceUrl}/${this.getAlbumBeatStreamIdentifier(album)}`, album, {
      observe: 'response',
    });
  }

  partialUpdate(album: PartialUpdateAlbumBeatStream): Observable<EntityResponseType> {
    return this.http.patch<IAlbumBeatStream>(`${this.resourceUrl}/${this.getAlbumBeatStreamIdentifier(album)}`, album, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IAlbumBeatStream>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAlbumBeatStream[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getAlbumBeatStreamIdentifier(album: Pick<IAlbumBeatStream, 'id'>): string {
    return album.id;
  }

  compareAlbumBeatStream(o1: Pick<IAlbumBeatStream, 'id'> | null, o2: Pick<IAlbumBeatStream, 'id'> | null): boolean {
    return o1 && o2 ? this.getAlbumBeatStreamIdentifier(o1) === this.getAlbumBeatStreamIdentifier(o2) : o1 === o2;
  }

  addAlbumBeatStreamToCollectionIfMissing<Type extends Pick<IAlbumBeatStream, 'id'>>(
    albumCollection: Type[],
    ...albumsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const albums: Type[] = albumsToCheck.filter(isPresent);
    if (albums.length > 0) {
      const albumCollectionIdentifiers = albumCollection.map(albumItem => this.getAlbumBeatStreamIdentifier(albumItem));
      const albumsToAdd = albums.filter(albumItem => {
        const albumIdentifier = this.getAlbumBeatStreamIdentifier(albumItem);
        if (albumCollectionIdentifiers.includes(albumIdentifier)) {
          return false;
        }
        albumCollectionIdentifiers.push(albumIdentifier);
        return true;
      });
      return [...albumsToAdd, ...albumCollection];
    }
    return albumCollection;
  }
}
