import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITaskNode, NewTaskNode } from '../task-node.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITaskNode for edit and NewTaskNodeFormGroupInput for create.
 */
type TaskNodeFormGroupInput = ITaskNode | PartialWithRequiredKeyOf<NewTaskNode>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITaskNode | NewTaskNode> = Omit<T, 'scheduledStartTime'> & {
  scheduledStartTime?: string | null;
};

type TaskNodeFormRawValue = FormValueOf<ITaskNode>;

type NewTaskNodeFormRawValue = FormValueOf<NewTaskNode>;

type TaskNodeFormDefaults = Pick<NewTaskNode, 'id' | 'scheduledStartTime'>;

type TaskNodeFormGroupContent = {
  id: FormControl<TaskNodeFormRawValue['id'] | NewTaskNode['id']>;
  referenceType: FormControl<TaskNodeFormRawValue['referenceType']>;
  referenceId: FormControl<TaskNodeFormRawValue['referenceId']>;
  taskName: FormControl<TaskNodeFormRawValue['taskName']>;
  taskLog: FormControl<TaskNodeFormRawValue['taskLog']>;
  trackId: FormControl<TaskNodeFormRawValue['trackId']>;
  scheduledStartTime: FormControl<TaskNodeFormRawValue['scheduledStartTime']>;
  startDelayMinutes: FormControl<TaskNodeFormRawValue['startDelayMinutes']>;
  startDelayHours: FormControl<TaskNodeFormRawValue['startDelayHours']>;
  elapsedHours: FormControl<TaskNodeFormRawValue['elapsedHours']>;
  elapsedMinutes: FormControl<TaskNodeFormRawValue['elapsedMinutes']>;
  progressPercentage: FormControl<TaskNodeFormRawValue['progressPercentage']>;
  downloadFilesize: FormControl<TaskNodeFormRawValue['downloadFilesize']>;
  downloadSpeed: FormControl<TaskNodeFormRawValue['downloadSpeed']>;
  downloadEta: FormControl<TaskNodeFormRawValue['downloadEta']>;
  nodeIndex: FormControl<TaskNodeFormRawValue['nodeIndex']>;
  status: FormControl<TaskNodeFormRawValue['status']>;
  type: FormControl<TaskNodeFormRawValue['type']>;
  failCount: FormControl<TaskNodeFormRawValue['failCount']>;
  retryCount: FormControl<TaskNodeFormRawValue['retryCount']>;
  maxRetryCount: FormControl<TaskNodeFormRawValue['maxRetryCount']>;
  parentTask: FormControl<TaskNodeFormRawValue['parentTask']>;
};

export type TaskNodeFormGroup = FormGroup<TaskNodeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TaskNodeFormService {
  createTaskNodeFormGroup(taskNode: TaskNodeFormGroupInput = { id: null }): TaskNodeFormGroup {
    const taskNodeRawValue = this.convertTaskNodeToTaskNodeRawValue({
      ...this.getFormDefaults(),
      ...taskNode,
    });
    return new FormGroup<TaskNodeFormGroupContent>({
      id: new FormControl(
        { value: taskNodeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      referenceType: new FormControl(taskNodeRawValue.referenceType, {
        validators: [Validators.required],
      }),
      referenceId: new FormControl(taskNodeRawValue.referenceId, {
        validators: [Validators.required],
      }),
      taskName: new FormControl(taskNodeRawValue.taskName, {
        validators: [Validators.required],
      }),
      taskLog: new FormControl(taskNodeRawValue.taskLog),
      trackId: new FormControl(taskNodeRawValue.trackId),
      scheduledStartTime: new FormControl(taskNodeRawValue.scheduledStartTime),
      startDelayMinutes: new FormControl(taskNodeRawValue.startDelayMinutes),
      startDelayHours: new FormControl(taskNodeRawValue.startDelayHours),
      elapsedHours: new FormControl(taskNodeRawValue.elapsedHours),
      elapsedMinutes: new FormControl(taskNodeRawValue.elapsedMinutes),
      progressPercentage: new FormControl(taskNodeRawValue.progressPercentage),
      downloadFilesize: new FormControl(taskNodeRawValue.downloadFilesize),
      downloadSpeed: new FormControl(taskNodeRawValue.downloadSpeed),
      downloadEta: new FormControl(taskNodeRawValue.downloadEta),
      nodeIndex: new FormControl(taskNodeRawValue.nodeIndex),
      status: new FormControl(taskNodeRawValue.status, {
        validators: [Validators.required],
      }),
      type: new FormControl(taskNodeRawValue.type, {
        validators: [Validators.required],
      }),
      failCount: new FormControl(taskNodeRawValue.failCount),
      retryCount: new FormControl(taskNodeRawValue.retryCount),
      maxRetryCount: new FormControl(taskNodeRawValue.maxRetryCount),
      parentTask: new FormControl(taskNodeRawValue.parentTask),
    });
  }

  getTaskNode(form: TaskNodeFormGroup): ITaskNode | NewTaskNode {
    return this.convertTaskNodeRawValueToTaskNode(form.getRawValue() as TaskNodeFormRawValue | NewTaskNodeFormRawValue);
  }

  resetForm(form: TaskNodeFormGroup, taskNode: TaskNodeFormGroupInput): void {
    const taskNodeRawValue = this.convertTaskNodeToTaskNodeRawValue({ ...this.getFormDefaults(), ...taskNode });
    form.reset(
      {
        ...taskNodeRawValue,
        id: { value: taskNodeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TaskNodeFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      scheduledStartTime: currentTime,
    };
  }

  private convertTaskNodeRawValueToTaskNode(rawTaskNode: TaskNodeFormRawValue | NewTaskNodeFormRawValue): ITaskNode | NewTaskNode {
    return {
      ...rawTaskNode,
      scheduledStartTime: dayjs(rawTaskNode.scheduledStartTime, DATE_TIME_FORMAT),
    };
  }

  private convertTaskNodeToTaskNodeRawValue(
    taskNode: ITaskNode | (Partial<NewTaskNode> & TaskNodeFormDefaults),
  ): TaskNodeFormRawValue | PartialWithRequiredKeyOf<NewTaskNodeFormRawValue> {
    return {
      ...taskNode,
      scheduledStartTime: taskNode.scheduledStartTime ? taskNode.scheduledStartTime.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
