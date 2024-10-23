import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IReferanceDownloadTask, NewReferanceDownloadTask } from '../referance-download-task.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IReferanceDownloadTask for edit and NewReferanceDownloadTaskFormGroupInput for create.
 */
type ReferanceDownloadTaskFormGroupInput = IReferanceDownloadTask | PartialWithRequiredKeyOf<NewReferanceDownloadTask>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IReferanceDownloadTask | NewReferanceDownloadTask> = Omit<T, 'referanceScheduleDate'> & {
  referanceScheduleDate?: string | null;
};

type ReferanceDownloadTaskFormRawValue = FormValueOf<IReferanceDownloadTask>;

type NewReferanceDownloadTaskFormRawValue = FormValueOf<NewReferanceDownloadTask>;

type ReferanceDownloadTaskFormDefaults = Pick<NewReferanceDownloadTask, 'id' | 'referanceScheduleDate'>;

type ReferanceDownloadTaskFormGroupContent = {
  id: FormControl<ReferanceDownloadTaskFormRawValue['id'] | NewReferanceDownloadTask['id']>;
  referanceId: FormControl<ReferanceDownloadTaskFormRawValue['referanceId']>;
  referanceType: FormControl<ReferanceDownloadTaskFormRawValue['referanceType']>;
  referanceTrackId: FormControl<ReferanceDownloadTaskFormRawValue['referanceTrackId']>;
  referanceStatus: FormControl<ReferanceDownloadTaskFormRawValue['referanceStatus']>;
  referanceScheduleDate: FormControl<ReferanceDownloadTaskFormRawValue['referanceScheduleDate']>;
  referanceLog: FormControl<ReferanceDownloadTaskFormRawValue['referanceLog']>;
};

export type ReferanceDownloadTaskFormGroup = FormGroup<ReferanceDownloadTaskFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ReferanceDownloadTaskFormService {
  createReferanceDownloadTaskFormGroup(
    referanceDownloadTask: ReferanceDownloadTaskFormGroupInput = { id: null },
  ): ReferanceDownloadTaskFormGroup {
    const referanceDownloadTaskRawValue = this.convertReferanceDownloadTaskToReferanceDownloadTaskRawValue({
      ...this.getFormDefaults(),
      ...referanceDownloadTask,
    });
    return new FormGroup<ReferanceDownloadTaskFormGroupContent>({
      id: new FormControl(
        { value: referanceDownloadTaskRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      referanceId: new FormControl(referanceDownloadTaskRawValue.referanceId),
      referanceType: new FormControl(referanceDownloadTaskRawValue.referanceType),
      referanceTrackId: new FormControl(referanceDownloadTaskRawValue.referanceTrackId),
      referanceStatus: new FormControl(referanceDownloadTaskRawValue.referanceStatus),
      referanceScheduleDate: new FormControl(referanceDownloadTaskRawValue.referanceScheduleDate),
      referanceLog: new FormControl(referanceDownloadTaskRawValue.referanceLog),
    });
  }

  getReferanceDownloadTask(form: ReferanceDownloadTaskFormGroup): IReferanceDownloadTask | NewReferanceDownloadTask {
    return this.convertReferanceDownloadTaskRawValueToReferanceDownloadTask(
      form.getRawValue() as ReferanceDownloadTaskFormRawValue | NewReferanceDownloadTaskFormRawValue,
    );
  }

  resetForm(form: ReferanceDownloadTaskFormGroup, referanceDownloadTask: ReferanceDownloadTaskFormGroupInput): void {
    const referanceDownloadTaskRawValue = this.convertReferanceDownloadTaskToReferanceDownloadTaskRawValue({
      ...this.getFormDefaults(),
      ...referanceDownloadTask,
    });
    form.reset(
      {
        ...referanceDownloadTaskRawValue,
        id: { value: referanceDownloadTaskRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ReferanceDownloadTaskFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      referanceScheduleDate: currentTime,
    };
  }

  private convertReferanceDownloadTaskRawValueToReferanceDownloadTask(
    rawReferanceDownloadTask: ReferanceDownloadTaskFormRawValue | NewReferanceDownloadTaskFormRawValue,
  ): IReferanceDownloadTask | NewReferanceDownloadTask {
    return {
      ...rawReferanceDownloadTask,
      referanceScheduleDate: dayjs(rawReferanceDownloadTask.referanceScheduleDate, DATE_TIME_FORMAT),
    };
  }

  private convertReferanceDownloadTaskToReferanceDownloadTaskRawValue(
    referanceDownloadTask: IReferanceDownloadTask | (Partial<NewReferanceDownloadTask> & ReferanceDownloadTaskFormDefaults),
  ): ReferanceDownloadTaskFormRawValue | PartialWithRequiredKeyOf<NewReferanceDownloadTaskFormRawValue> {
    return {
      ...referanceDownloadTask,
      referanceScheduleDate: referanceDownloadTask.referanceScheduleDate
        ? referanceDownloadTask.referanceScheduleDate.format(DATE_TIME_FORMAT)
        : undefined,
    };
  }
}
