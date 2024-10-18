import { IGenreBeatStream, NewGenreBeatStream } from './genre-beat-stream.model';

export const sampleWithRequiredData: IGenreBeatStream = {
  id: 'e014834e-c619-416a-913a-ccdee0d42863',
  name: 'peppery siege import',
};

export const sampleWithPartialData: IGenreBeatStream = {
  id: 'e9e7e7c7-a7a9-4bed-ad6c-844e688b78ae',
  name: 'next bus inconsequential',
};

export const sampleWithFullData: IGenreBeatStream = {
  id: '88bb199c-556f-4a89-9188-c4d42d3ea431',
  name: 'after brr where',
};

export const sampleWithNewData: NewGenreBeatStream = {
  name: 'lest condense',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
