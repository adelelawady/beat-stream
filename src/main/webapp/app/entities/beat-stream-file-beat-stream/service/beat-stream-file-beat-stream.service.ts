import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IBeatStreamFileBeatStream, NewBeatStreamFileBeatStream } from '../beat-stream-file-beat-stream.model';

export type PartialUpdateBeatStreamFileBeatStream = Partial<IBeatStreamFileBeatStream> & Pick<IBeatStreamFileBeatStream, 'id'>;

export type EntityResponseType = HttpResponse<IBeatStreamFileBeatStream>;
export type EntityArrayResponseType = HttpResponse<IBeatStreamFileBeatStream[]>;

@Injectable({ providedIn: 'root' })
export class BeatStreamFileBeatStreamService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/beat-stream-files');

  create(beatStreamFile: NewBeatStreamFileBeatStream): Observable<EntityResponseType> {
    return this.http.post<IBeatStreamFileBeatStream>(this.resourceUrl, beatStreamFile, { observe: 'response' });
  }

  update(beatStreamFile: IBeatStreamFileBeatStream): Observable<EntityResponseType> {
    return this.http.put<IBeatStreamFileBeatStream>(
      `${this.resourceUrl}/${this.getBeatStreamFileBeatStreamIdentifier(beatStreamFile)}`,
      beatStreamFile,
      { observe: 'response' },
    );
  }

  partialUpdate(beatStreamFile: PartialUpdateBeatStreamFileBeatStream): Observable<EntityResponseType> {
    return this.http.patch<IBeatStreamFileBeatStream>(
      `${this.resourceUrl}/${this.getBeatStreamFileBeatStreamIdentifier(beatStreamFile)}`,
      beatStreamFile,
      { observe: 'response' },
    );
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IBeatStreamFileBeatStream>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBeatStreamFileBeatStream[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getBeatStreamFileBeatStreamIdentifier(beatStreamFile: Pick<IBeatStreamFileBeatStream, 'id'>): string {
    return beatStreamFile.id;
  }

  compareBeatStreamFileBeatStream(
    o1: Pick<IBeatStreamFileBeatStream, 'id'> | null,
    o2: Pick<IBeatStreamFileBeatStream, 'id'> | null,
  ): boolean {
    return o1 && o2 ? this.getBeatStreamFileBeatStreamIdentifier(o1) === this.getBeatStreamFileBeatStreamIdentifier(o2) : o1 === o2;
  }

  addBeatStreamFileBeatStreamToCollectionIfMissing<Type extends Pick<IBeatStreamFileBeatStream, 'id'>>(
    beatStreamFileCollection: Type[],
    ...beatStreamFilesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const beatStreamFiles: Type[] = beatStreamFilesToCheck.filter(isPresent);
    if (beatStreamFiles.length > 0) {
      const beatStreamFileCollectionIdentifiers = beatStreamFileCollection.map(beatStreamFileItem =>
        this.getBeatStreamFileBeatStreamIdentifier(beatStreamFileItem),
      );
      const beatStreamFilesToAdd = beatStreamFiles.filter(beatStreamFileItem => {
        const beatStreamFileIdentifier = this.getBeatStreamFileBeatStreamIdentifier(beatStreamFileItem);
        if (beatStreamFileCollectionIdentifiers.includes(beatStreamFileIdentifier)) {
          return false;
        }
        beatStreamFileCollectionIdentifiers.push(beatStreamFileIdentifier);
        return true;
      });
      return [...beatStreamFilesToAdd, ...beatStreamFileCollection];
    }
    return beatStreamFileCollection;
  }
}
