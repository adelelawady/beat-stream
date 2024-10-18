import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IBeatStreamFileBeatStream } from '../beat-stream-file-beat-stream.model';
import { BeatStreamFileBeatStreamService } from '../service/beat-stream-file-beat-stream.service';
import { BeatStreamFileBeatStreamFormGroup, BeatStreamFileBeatStreamFormService } from './beat-stream-file-beat-stream-form.service';

@Component({
  standalone: true,
  selector: 'jhi-beat-stream-file-beat-stream-update',
  templateUrl: './beat-stream-file-beat-stream-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class BeatStreamFileBeatStreamUpdateComponent implements OnInit {
  isSaving = false;
  beatStreamFile: IBeatStreamFileBeatStream | null = null;

  protected beatStreamFileService = inject(BeatStreamFileBeatStreamService);
  protected beatStreamFileFormService = inject(BeatStreamFileBeatStreamFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: BeatStreamFileBeatStreamFormGroup = this.beatStreamFileFormService.createBeatStreamFileBeatStreamFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ beatStreamFile }) => {
      this.beatStreamFile = beatStreamFile;
      if (beatStreamFile) {
        this.updateForm(beatStreamFile);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const beatStreamFile = this.beatStreamFileFormService.getBeatStreamFileBeatStream(this.editForm);
    if (beatStreamFile.id !== null) {
      this.subscribeToSaveResponse(this.beatStreamFileService.update(beatStreamFile));
    } else {
      this.subscribeToSaveResponse(this.beatStreamFileService.create(beatStreamFile));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBeatStreamFileBeatStream>>): void {
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

  protected updateForm(beatStreamFile: IBeatStreamFileBeatStream): void {
    this.beatStreamFile = beatStreamFile;
    this.beatStreamFileFormService.resetForm(this.editForm, beatStreamFile);
  }
}
