import { IArtistBeatStream } from 'app/entities/artist-beat-stream/artist-beat-stream.model';
import { IGenreBeatStream } from 'app/entities/genre-beat-stream/genre-beat-stream.model';

export interface IAlbumBeatStream {
  id: string;
  title?: string | null;
  releaseDate?: string | null;
  coverImageFileId?: string | null;
  artist?: Pick<IArtistBeatStream, 'id'> | null;
  genres?: Pick<IGenreBeatStream, 'id'>[] | null;
}

export type NewAlbumBeatStream = Omit<IAlbumBeatStream, 'id'> & { id: null };
