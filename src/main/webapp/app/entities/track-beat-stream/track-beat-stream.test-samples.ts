import { ITrackBeatStream, NewTrackBeatStream } from './track-beat-stream.model';

export const sampleWithRequiredData: ITrackBeatStream = {
  id: '57cd0483-fa94-4b0a-854d-919fbeef71c1',
  title: 'descendant ack humidity',
};

export const sampleWithPartialData: ITrackBeatStream = {
  id: '452884ee-318f-4cc3-8ca7-47b7dc63d366',
  title: 'when supposing where',
  duration: 6525,
  liked: false,
  audioFileId: 'triumphantly since',
  coverImageFileId: 'hollow',
};

export const sampleWithFullData: ITrackBeatStream = {
  id: 'c60741ba-556f-47b9-978f-30c3a570a698',
  title: 'lazily',
  duration: 23199,
  liked: false,
  audioFileId: 'circulate potentially putrefy',
  coverImageFileId: 'youthfully',
};

export const sampleWithNewData: NewTrackBeatStream = {
  title: 'sans',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
