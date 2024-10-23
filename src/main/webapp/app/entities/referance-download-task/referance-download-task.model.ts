import dayjs from 'dayjs/esm';

export interface IReferanceDownloadTask {
  id: string;
  referanceId?: string | null;
  referanceType?: string | null;
  referanceTrackId?: string | null;
  referanceStatus?: string | null;
  referanceScheduleDate?: dayjs.Dayjs | null;
  referanceLog?: string | null;
}

export type NewReferanceDownloadTask = Omit<IReferanceDownloadTask, 'id'> & { id: null };
