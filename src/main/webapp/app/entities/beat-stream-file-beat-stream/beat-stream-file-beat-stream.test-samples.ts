import { IBeatStreamFileBeatStream, NewBeatStreamFileBeatStream } from './beat-stream-file-beat-stream.model';

export const sampleWithRequiredData: IBeatStreamFileBeatStream = {
  id: '70b34ff2-1375-4628-8b21-b9281a79b5ba',
  name: 'conceal zowie boohoo',
};

export const sampleWithPartialData: IBeatStreamFileBeatStream = {
  id: '08a26bc3-a5e9-4b9c-b136-9489c8c12749',
  name: 'bashfully',
  bucket: 'than',
};

export const sampleWithFullData: IBeatStreamFileBeatStream = {
  id: 'e6c7a995-b302-47a6-8363-5575a242c673',
  name: 'stay',
  size: 1864,
  bucket: 'vivaciously twin',
  type: 'brr into',
};

export const sampleWithNewData: NewBeatStreamFileBeatStream = {
  name: 'aside',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
