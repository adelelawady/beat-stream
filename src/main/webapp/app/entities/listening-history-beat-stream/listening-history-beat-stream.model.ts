import { ITrackBeatStream } from 'app/entities/track-beat-stream/track-beat-stream.model';

export interface IListeningHistoryBeatStream {
  id: string;
  timestamp?: string | null;
  track?: Pick<ITrackBeatStream, 'id'> | null;
}

export type NewListeningHistoryBeatStream = Omit<IListeningHistoryBeatStream, 'id'> & { id: null };
