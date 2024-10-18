import { IPlaylistBeatStream, NewPlaylistBeatStream } from './playlist-beat-stream.model';

export const sampleWithRequiredData: IPlaylistBeatStream = {
  id: '03a02831-cb16-4fb1-b31f-867941015d4e',
  title: 'perfectly after',
};

export const sampleWithPartialData: IPlaylistBeatStream = {
  id: '5001f9d2-9c79-4ddf-9949-38b157a7f876',
  title: 'irritably twin',
  description: 'abnormally',
};

export const sampleWithFullData: IPlaylistBeatStream = {
  id: 'a2b3854f-37d8-4e12-a6fa-84a1d31a1d0b',
  title: 'circumnavigate',
  description: 'anenst zowie',
};

export const sampleWithNewData: NewPlaylistBeatStream = {
  title: 'off fooey',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
