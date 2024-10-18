import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IArtistBeatStream, NewArtistBeatStream } from '../artist-beat-stream.model';

export type PartialUpdateArtistBeatStream = Partial<IArtistBeatStream> & Pick<IArtistBeatStream, 'id'>;

export type EntityResponseType = HttpResponse<IArtistBeatStream>;
export type EntityArrayResponseType = HttpResponse<IArtistBeatStream[]>;

@Injectable({ providedIn: 'root' })
export class ArtistBeatStreamService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/artists');

  create(artist: NewArtistBeatStream): Observable<EntityResponseType> {
    return this.http.post<IArtistBeatStream>(this.resourceUrl, artist, { observe: 'response' });
  }

  update(artist: IArtistBeatStream): Observable<EntityResponseType> {
    return this.http.put<IArtistBeatStream>(`${this.resourceUrl}/${this.getArtistBeatStreamIdentifier(artist)}`, artist, {
      observe: 'response',
    });
  }

  partialUpdate(artist: PartialUpdateArtistBeatStream): Observable<EntityResponseType> {
    return this.http.patch<IArtistBeatStream>(`${this.resourceUrl}/${this.getArtistBeatStreamIdentifier(artist)}`, artist, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IArtistBeatStream>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IArtistBeatStream[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getArtistBeatStreamIdentifier(artist: Pick<IArtistBeatStream, 'id'>): string {
    return artist.id;
  }

  compareArtistBeatStream(o1: Pick<IArtistBeatStream, 'id'> | null, o2: Pick<IArtistBeatStream, 'id'> | null): boolean {
    return o1 && o2 ? this.getArtistBeatStreamIdentifier(o1) === this.getArtistBeatStreamIdentifier(o2) : o1 === o2;
  }

  addArtistBeatStreamToCollectionIfMissing<Type extends Pick<IArtistBeatStream, 'id'>>(
    artistCollection: Type[],
    ...artistsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const artists: Type[] = artistsToCheck.filter(isPresent);
    if (artists.length > 0) {
      const artistCollectionIdentifiers = artistCollection.map(artistItem => this.getArtistBeatStreamIdentifier(artistItem));
      const artistsToAdd = artists.filter(artistItem => {
        const artistIdentifier = this.getArtistBeatStreamIdentifier(artistItem);
        if (artistCollectionIdentifiers.includes(artistIdentifier)) {
          return false;
        }
        artistCollectionIdentifiers.push(artistIdentifier);
        return true;
      });
      return [...artistsToAdd, ...artistCollection];
    }
    return artistCollection;
  }
}
