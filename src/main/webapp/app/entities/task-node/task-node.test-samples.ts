import dayjs from 'dayjs/esm';

import { ITaskNode, NewTaskNode } from './task-node.model';

export const sampleWithRequiredData: ITaskNode = {
  id: '4c7c17b6-c890-4987-8206-5b27b5897d57',
  referenceType: 'YOUTUBE',
  referenceId: 459,
  taskName: 'close',
  status: 'COMPLETED',
  type: 'VIDEO',
};

export const sampleWithPartialData: ITaskNode = {
  id: 'f620279f-05df-4a0a-8b21-5c4b0e13b29b',
  referenceType: 'YOUTUBE',
  referenceId: 32133,
  taskName: 'furiously',
  trackId: 7349,
  startDelayHours: 14285.42,
  elapsedHours: 25923.97,
  elapsedMinutes: 6939.04,
  downloadFilesize: 'ascertain',
  downloadSpeed: 'pity',
  downloadEta: 'premium',
  status: 'PENDING',
  type: 'AUDIO',
  maxRetryCount: 10393.97,
};

export const sampleWithFullData: ITaskNode = {
  id: '2441c951-2a45-4aff-a852-2f1b65333786',
  referenceType: 'SPOTIFY',
  referenceId: 4726,
  taskName: 'collaboration without',
  taskLog: 'yarmulke negative',
  trackId: 8555,
  scheduledStartTime: dayjs('2024-10-25T04:33'),
  startDelayMinutes: 31905.15,
  startDelayHours: 11951.04,
  elapsedHours: 11155.2,
  elapsedMinutes: 7752.76,
  progressPercentage: 8456.53,
  downloadFilesize: 'unlike debut',
  downloadSpeed: 'pace',
  downloadEta: 'accredit impractical',
  nodeIndex: 17439.52,
  status: 'IN_PROGRESS',
  type: 'VIDEO_PLAYLIST',
  failCount: 16301.2,
  retryCount: 26871.7,
  maxRetryCount: 2121.39,
};

export const sampleWithNewData: NewTaskNode = {
  referenceType: 'SOUNDCLOUD',
  referenceId: 32392,
  taskName: 'unselfish unless',
  status: 'PAUSED',
  type: 'FILE_PLAYLIST',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
