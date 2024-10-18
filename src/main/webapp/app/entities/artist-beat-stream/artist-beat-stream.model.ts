export interface IArtistBeatStream {
  id: string;
  name?: string | null;
  bio?: string | null;
  coverImageFileId?: string | null;
}

export type NewArtistBeatStream = Omit<IArtistBeatStream, 'id'> & { id: null };
