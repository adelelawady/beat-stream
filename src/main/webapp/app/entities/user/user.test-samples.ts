import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: '6fe20ddb-27fc-4a45-909e-c4261d53c201',
  login: 'T@.IIx',
};

export const sampleWithPartialData: IUser = {
  id: '33f6588e-27da-4f65-8ef0-657230fe8df2',
  login: '0nMbf',
};

export const sampleWithFullData: IUser = {
  id: '71b9a204-57b7-465a-8e96-c519e1afb4be',
  login: 'KOQrq',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
