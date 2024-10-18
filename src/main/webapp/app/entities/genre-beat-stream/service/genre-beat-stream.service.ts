import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IGenreBeatStream, NewGenreBeatStream } from '../genre-beat-stream.model';

export type PartialUpdateGenreBeatStream = Partial<IGenreBeatStream> & Pick<IGenreBeatStream, 'id'>;

export type EntityResponseType = HttpResponse<IGenreBeatStream>;
export type EntityArrayResponseType = HttpResponse<IGenreBeatStream[]>;

@Injectable({ providedIn: 'root' })
export class GenreBeatStreamService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/genres');

  create(genre: NewGenreBeatStream): Observable<EntityResponseType> {
    return this.http.post<IGenreBeatStream>(this.resourceUrl, genre, { observe: 'response' });
  }

  update(genre: IGenreBeatStream): Observable<EntityResponseType> {
    return this.http.put<IGenreBeatStream>(`${this.resourceUrl}/${this.getGenreBeatStreamIdentifier(genre)}`, genre, {
      observe: 'response',
    });
  }

  partialUpdate(genre: PartialUpdateGenreBeatStream): Observable<EntityResponseType> {
    return this.http.patch<IGenreBeatStream>(`${this.resourceUrl}/${this.getGenreBeatStreamIdentifier(genre)}`, genre, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IGenreBeatStream>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IGenreBeatStream[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getGenreBeatStreamIdentifier(genre: Pick<IGenreBeatStream, 'id'>): string {
    return genre.id;
  }

  compareGenreBeatStream(o1: Pick<IGenreBeatStream, 'id'> | null, o2: Pick<IGenreBeatStream, 'id'> | null): boolean {
    return o1 && o2 ? this.getGenreBeatStreamIdentifier(o1) === this.getGenreBeatStreamIdentifier(o2) : o1 === o2;
  }

  addGenreBeatStreamToCollectionIfMissing<Type extends Pick<IGenreBeatStream, 'id'>>(
    genreCollection: Type[],
    ...genresToCheck: (Type | null | undefined)[]
  ): Type[] {
    const genres: Type[] = genresToCheck.filter(isPresent);
    if (genres.length > 0) {
      const genreCollectionIdentifiers = genreCollection.map(genreItem => this.getGenreBeatStreamIdentifier(genreItem));
      const genresToAdd = genres.filter(genreItem => {
        const genreIdentifier = this.getGenreBeatStreamIdentifier(genreItem);
        if (genreCollectionIdentifiers.includes(genreIdentifier)) {
          return false;
        }
        genreCollectionIdentifiers.push(genreIdentifier);
        return true;
      });
      return [...genresToAdd, ...genreCollection];
    }
    return genreCollection;
  }
}
