import { ITrackBeatStream } from 'app/entities/track-beat-stream/track-beat-stream.model';

export interface IPlaylistBeatStream {
  id: string;
  title?: string | null;
  description?: string | null;
  tracks?: Pick<ITrackBeatStream, 'id'>[] | null;
}

export type NewPlaylistBeatStream = Omit<IPlaylistBeatStream, 'id'> & { id: null };
