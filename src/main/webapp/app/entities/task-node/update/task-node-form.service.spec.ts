import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../task-node.test-samples';

import { TaskNodeFormService } from './task-node-form.service';

describe('TaskNode Form Service', () => {
  let service: TaskNodeFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TaskNodeFormService);
  });

  describe('Service methods', () => {
    describe('createTaskNodeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTaskNodeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            referenceType: expect.any(Object),
            referenceId: expect.any(Object),
            taskName: expect.any(Object),
            taskLog: expect.any(Object),
            trackId: expect.any(Object),
            scheduledStartTime: expect.any(Object),
            startDelayMinutes: expect.any(Object),
            startDelayHours: expect.any(Object),
            elapsedHours: expect.any(Object),
            elapsedMinutes: expect.any(Object),
            progressPercentage: expect.any(Object),
            downloadFilesize: expect.any(Object),
            downloadSpeed: expect.any(Object),
            downloadEta: expect.any(Object),
            nodeIndex: expect.any(Object),
            status: expect.any(Object),
            type: expect.any(Object),
            failCount: expect.any(Object),
            retryCount: expect.any(Object),
            maxRetryCount: expect.any(Object),
            parentTask: expect.any(Object),
          }),
        );
      });

      it('passing ITaskNode should create a new form with FormGroup', () => {
        const formGroup = service.createTaskNodeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            referenceType: expect.any(Object),
            referenceId: expect.any(Object),
            taskName: expect.any(Object),
            taskLog: expect.any(Object),
            trackId: expect.any(Object),
            scheduledStartTime: expect.any(Object),
            startDelayMinutes: expect.any(Object),
            startDelayHours: expect.any(Object),
            elapsedHours: expect.any(Object),
            elapsedMinutes: expect.any(Object),
            progressPercentage: expect.any(Object),
            downloadFilesize: expect.any(Object),
            downloadSpeed: expect.any(Object),
            downloadEta: expect.any(Object),
            nodeIndex: expect.any(Object),
            status: expect.any(Object),
            type: expect.any(Object),
            failCount: expect.any(Object),
            retryCount: expect.any(Object),
            maxRetryCount: expect.any(Object),
            parentTask: expect.any(Object),
          }),
        );
      });
    });

    describe('getTaskNode', () => {
      it('should return NewTaskNode for default TaskNode initial value', () => {
        const formGroup = service.createTaskNodeFormGroup(sampleWithNewData);

        const taskNode = service.getTaskNode(formGroup) as any;

        expect(taskNode).toMatchObject(sampleWithNewData);
      });

      it('should return NewTaskNode for empty TaskNode initial value', () => {
        const formGroup = service.createTaskNodeFormGroup();

        const taskNode = service.getTaskNode(formGroup) as any;

        expect(taskNode).toMatchObject({});
      });

      it('should return ITaskNode', () => {
        const formGroup = service.createTaskNodeFormGroup(sampleWithRequiredData);

        const taskNode = service.getTaskNode(formGroup) as any;

        expect(taskNode).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITaskNode should not enable id FormControl', () => {
        const formGroup = service.createTaskNodeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTaskNode should disable id FormControl', () => {
        const formGroup = service.createTaskNodeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
