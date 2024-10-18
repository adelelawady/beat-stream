export interface IBeatStreamFileBeatStream {
  id: string;
  name?: string | null;
  size?: number | null;
  bucket?: string | null;
  type?: string | null;
}

export type NewBeatStreamFileBeatStream = Omit<IBeatStreamFileBeatStream, 'id'> & { id: null };
