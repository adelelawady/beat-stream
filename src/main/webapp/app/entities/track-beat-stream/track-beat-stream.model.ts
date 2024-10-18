import { IArtistBeatStream } from 'app/entities/artist-beat-stream/artist-beat-stream.model';
import { IAlbumBeatStream } from 'app/entities/album-beat-stream/album-beat-stream.model';

export interface ITrackBeatStream {
  id: string;
  title?: string | null;
  duration?: number | null;
  liked?: boolean | null;
  audioFileId?: string | null;
  coverImageFileId?: string | null;
  artist?: Pick<IArtistBeatStream, 'id'> | null;
  album?: Pick<IAlbumBeatStream, 'id'> | null;
}

export type NewTrackBeatStream = Omit<ITrackBeatStream, 'id'> & { id: null };
