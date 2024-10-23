import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IReferanceDownloadTask } from '../referance-download-task.model';
import { ReferanceDownloadTaskService } from '../service/referance-download-task.service';
import { ReferanceDownloadTaskFormGroup, ReferanceDownloadTaskFormService } from './referance-download-task-form.service';

@Component({
  standalone: true,
  selector: 'jhi-referance-download-task-update',
  templateUrl: './referance-download-task-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ReferanceDownloadTaskUpdateComponent implements OnInit {
  isSaving = false;
  referanceDownloadTask: IReferanceDownloadTask | null = null;

  protected referanceDownloadTaskService = inject(ReferanceDownloadTaskService);
  protected referanceDownloadTaskFormService = inject(ReferanceDownloadTaskFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ReferanceDownloadTaskFormGroup = this.referanceDownloadTaskFormService.createReferanceDownloadTaskFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ referanceDownloadTask }) => {
      this.referanceDownloadTask = referanceDownloadTask;
      if (referanceDownloadTask) {
        this.updateForm(referanceDownloadTask);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const referanceDownloadTask = this.referanceDownloadTaskFormService.getReferanceDownloadTask(this.editForm);
    if (referanceDownloadTask.id !== null) {
      this.subscribeToSaveResponse(this.referanceDownloadTaskService.update(referanceDownloadTask));
    } else {
      this.subscribeToSaveResponse(this.referanceDownloadTaskService.create(referanceDownloadTask));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IReferanceDownloadTask>>): void {
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

  protected updateForm(referanceDownloadTask: IReferanceDownloadTask): void {
    this.referanceDownloadTask = referanceDownloadTask;
    this.referanceDownloadTaskFormService.resetForm(this.editForm, referanceDownloadTask);
  }
}
