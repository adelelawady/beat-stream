import { IArtistBeatStream, NewArtistBeatStream } from './artist-beat-stream.model';

export const sampleWithRequiredData: IArtistBeatStream = {
  id: 'ec62850a-5410-455b-b101-eeb03f6f2e00',
  name: 'favorite',
};

export const sampleWithPartialData: IArtistBeatStream = {
  id: '9fd5892e-4c13-4d12-be5a-038627951143',
  name: 'grade hopeful',
};

export const sampleWithFullData: IArtistBeatStream = {
  id: 'c2f58b30-9ead-42ee-8366-5c1dc2cc321d',
  name: 'ascertain circle',
  bio: 'grass eternity afore',
  coverImageFileId: 'or',
};

export const sampleWithNewData: NewArtistBeatStream = {
  name: 'grouchy bowed why',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
