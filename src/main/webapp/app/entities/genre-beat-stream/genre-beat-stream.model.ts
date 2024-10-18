import { IArtistBeatStream } from 'app/entities/artist-beat-stream/artist-beat-stream.model';

export interface IGenreBeatStream {
  id: string;
  name?: string | null;
  artists?: Pick<IArtistBeatStream, 'id'>[] | null;
}

export type NewGenreBeatStream = Omit<IGenreBeatStream, 'id'> & { id: null };
