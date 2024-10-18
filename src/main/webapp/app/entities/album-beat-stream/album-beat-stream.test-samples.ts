import { IAlbumBeatStream, NewAlbumBeatStream } from './album-beat-stream.model';

export const sampleWithRequiredData: IAlbumBeatStream = {
  id: '551ba75c-5b65-4a33-bc35-eb4585f141f5',
  title: 'soggy propound bah',
};

export const sampleWithPartialData: IAlbumBeatStream = {
  id: '19433ec0-fc6f-4914-81ad-6ac28f7dc922',
  title: 'dead so interchange',
};

export const sampleWithFullData: IAlbumBeatStream = {
  id: '6c7ba54b-9eb5-4f55-a619-71d762ff9cc7',
  title: 'circa devil',
  releaseDate: 'around',
  coverImageFileId: 'mid authentic turret',
};

export const sampleWithNewData: NewAlbumBeatStream = {
  title: 'trick duh concrete',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
