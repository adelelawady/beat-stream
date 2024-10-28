import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ReferenceType } from 'app/entities/enumerations/reference-type.model';
import { DownloadStatus } from 'app/entities/enumerations/download-status.model';
import { DownloadType } from 'app/entities/enumerations/download-type.model';
import { TaskNodeService } from '../service/task-node.service';
import { ITaskNode } from '../task-node.model';
import { TaskNodeFormGroup, TaskNodeFormService } from './task-node-form.service';

@Component({
  standalone: true,
  selector: 'jhi-task-node-update',
  templateUrl: './task-node-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TaskNodeUpdateComponent implements OnInit {
  isSaving = false;
  taskNode: ITaskNode | null = null;
  referenceTypeValues = Object.keys(ReferenceType);
  downloadStatusValues = Object.keys(DownloadStatus);
  downloadTypeValues = Object.keys(DownloadType);

  taskNodesSharedCollection: ITaskNode[] = [];

  protected taskNodeService = inject(TaskNodeService);
  protected taskNodeFormService = inject(TaskNodeFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TaskNodeFormGroup = this.taskNodeFormService.createTaskNodeFormGroup();

  compareTaskNode = (o1: ITaskNode | null, o2: ITaskNode | null): boolean => this.taskNodeService.compareTaskNode(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ taskNode }) => {
      this.taskNode = taskNode;
      if (taskNode) {
        this.updateForm(taskNode);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const taskNode = this.taskNodeFormService.getTaskNode(this.editForm);
    if (taskNode.id !== null) {
      this.subscribeToSaveResponse(this.taskNodeService.update(taskNode));
    } else {
      this.subscribeToSaveResponse(this.taskNodeService.create(taskNode));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITaskNode>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(taskNode: ITaskNode): void {
    this.taskNode = taskNode;
    this.taskNodeFormService.resetForm(this.editForm, taskNode);

    this.taskNodesSharedCollection = this.taskNodeService.addTaskNodeToCollectionIfMissing<ITaskNode>(
      this.taskNodesSharedCollection,
      taskNode.parentTask,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.taskNodeService
      .query()
      .pipe(map((res: HttpResponse<ITaskNode[]>) => res.body ?? []))
      .pipe(
        map((taskNodes: ITaskNode[]) =>
          this.taskNodeService.addTaskNodeToCollectionIfMissing<ITaskNode>(taskNodes, this.taskNode?.parentTask),
        ),
      )
      .subscribe((taskNodes: ITaskNode[]) => (this.taskNodesSharedCollection = taskNodes));
  }
}
