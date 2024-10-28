import dayjs from 'dayjs/esm';
import { ReferenceType } from 'app/entities/enumerations/reference-type.model';
import { DownloadStatus } from 'app/entities/enumerations/download-status.model';
import { DownloadType } from 'app/entities/enumerations/download-type.model';

export interface ITaskNode {
  id: string;
  referenceType?: keyof typeof ReferenceType | null;
  referenceId?: number | null;
  taskName?: string | null;
  taskLog?: string | null;
  trackId?: number | null;
  scheduledStartTime?: dayjs.Dayjs | null;
  startDelayMinutes?: number | null;
  startDelayHours?: number | null;
  elapsedHours?: number | null;
  elapsedMinutes?: number | null;
  progressPercentage?: number | null;
  downloadFilesize?: string | null;
  downloadSpeed?: string | null;
  downloadEta?: string | null;
  nodeIndex?: number | null;
  status?: keyof typeof DownloadStatus | null;
  type?: keyof typeof DownloadType | null;
  failCount?: number | null;
  retryCount?: number | null;
  maxRetryCount?: number | null;
  parentTask?: Pick<ITaskNode, 'id'> | null;
}

export type NewTaskNode = Omit<ITaskNode, 'id'> & { id: null };
