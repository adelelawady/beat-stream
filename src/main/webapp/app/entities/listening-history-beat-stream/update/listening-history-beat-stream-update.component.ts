import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ITrackBeatStream } from 'app/entities/track-beat-stream/track-beat-stream.model';
import { TrackBeatStreamService } from 'app/entities/track-beat-stream/service/track-beat-stream.service';
import { IListeningHistoryBeatStream } from '../listening-history-beat-stream.model';
import { ListeningHistoryBeatStreamService } from '../service/listening-history-beat-stream.service';
import { ListeningHistoryBeatStreamFormGroup, ListeningHistoryBeatStreamFormService } from './listening-history-beat-stream-form.service';

@Component({
  standalone: true,
  selector: 'jhi-listening-history-beat-stream-update',
  templateUrl: './listening-history-beat-stream-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ListeningHistoryBeatStreamUpdateComponent implements OnInit {
  isSaving = false;
  listeningHistory: IListeningHistoryBeatStream | null = null;

  tracksSharedCollection: ITrackBeatStream[] = [];

  protected listeningHistoryService = inject(ListeningHistoryBeatStreamService);
  protected listeningHistoryFormService = inject(ListeningHistoryBeatStreamFormService);
  protected trackService = inject(TrackBeatStreamService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ListeningHistoryBeatStreamFormGroup = this.listeningHistoryFormService.createListeningHistoryBeatStreamFormGroup();

  compareTrackBeatStream = (o1: ITrackBeatStream | null, o2: ITrackBeatStream | null): boolean =>
    this.trackService.compareTrackBeatStream(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ listeningHistory }) => {
      this.listeningHistory = listeningHistory;
      if (listeningHistory) {
        this.updateForm(listeningHistory);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const listeningHistory = this.listeningHistoryFormService.getListeningHistoryBeatStream(this.editForm);
    if (listeningHistory.id !== null) {
      this.subscribeToSaveResponse(this.listeningHistoryService.update(listeningHistory));
    } else {
      this.subscribeToSaveResponse(this.listeningHistoryService.create(listeningHistory));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IListeningHistoryBeatStream>>): void {
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

  protected updateForm(listeningHistory: IListeningHistoryBeatStream): void {
    this.listeningHistory = listeningHistory;
    this.listeningHistoryFormService.resetForm(this.editForm, listeningHistory);

    this.tracksSharedCollection = this.trackService.addTrackBeatStreamToCollectionIfMissing<ITrackBeatStream>(
      this.tracksSharedCollection,
      listeningHistory.track,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.trackService
      .query()
      .pipe(map((res: HttpResponse<ITrackBeatStream[]>) => res.body ?? []))
      .pipe(
        map((tracks: ITrackBeatStream[]) =>
          this.trackService.addTrackBeatStreamToCollectionIfMissing<ITrackBeatStream>(tracks, this.listeningHistory?.track),
        ),
      )
      .subscribe((tracks: ITrackBeatStream[]) => (this.tracksSharedCollection = tracks));
  }
}
