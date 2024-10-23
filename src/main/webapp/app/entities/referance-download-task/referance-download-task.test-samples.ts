import dayjs from 'dayjs/esm';

import { IReferanceDownloadTask, NewReferanceDownloadTask } from './referance-download-task.model';

export const sampleWithRequiredData: IReferanceDownloadTask = {
  id: '140319e1-91d4-4f68-a778-67e2fbe86daa',
};

export const sampleWithPartialData: IReferanceDownloadTask = {
  id: 'b3e7817c-d1ca-46af-a74e-493b808553a1',
  referanceId: 'airbus pish',
  referanceType: 'inscribe newsprint which',
};

export const sampleWithFullData: IReferanceDownloadTask = {
  id: '6b75e47a-5107-470f-98d6-1312b3328b2f',
  referanceId: 'outset uh-huh despite',
  referanceType: 'ethyl',
  referanceTrackId: 'defiantly internationalize cow',
  referanceStatus: 'overcooked whoever',
  referanceScheduleDate: dayjs('2024-10-22T17:19'),
  referanceLog: 'atop oxidise',
};

export const sampleWithNewData: NewReferanceDownloadTask = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
