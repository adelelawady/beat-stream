import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: '78afdb4f-489e-41a4-a2ad-a56fe607f242',
};

export const sampleWithPartialData: IAuthority = {
  name: 'efabeaa5-4052-44ec-911a-f0a707ade224',
};

export const sampleWithFullData: IAuthority = {
  name: '71df8c81-5c32-4f7b-ae06-3a48116bb850',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
