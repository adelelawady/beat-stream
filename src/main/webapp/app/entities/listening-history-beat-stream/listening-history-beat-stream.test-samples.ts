import { IListeningHistoryBeatStream, NewListeningHistoryBeatStream } from './listening-history-beat-stream.model';

export const sampleWithRequiredData: IListeningHistoryBeatStream = {
  id: '4c03a82c-c7cf-4ddc-8451-b8ad08792458',
};

export const sampleWithPartialData: IListeningHistoryBeatStream = {
  id: 'b125eeb7-7353-4c90-9d19-499f1ce05a35',
  timestamp: 'royal',
};

export const sampleWithFullData: IListeningHistoryBeatStream = {
  id: 'a6427d74-56d8-483b-ad7d-2273d098d93c',
  timestamp: 'miserable because',
};

export const sampleWithNewData: NewListeningHistoryBeatStream = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
