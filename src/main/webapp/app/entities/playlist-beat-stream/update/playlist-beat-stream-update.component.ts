import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ITrackBeatStream } from 'app/entities/track-beat-stream/track-beat-stream.model';
import { TrackBeatStreamService } from 'app/entities/track-beat-stream/service/track-beat-stream.service';
import { IPlaylistBeatStream } from '../playlist-beat-stream.model';
import { PlaylistBeatStreamService } from '../service/playlist-beat-stream.service';
import { PlaylistBeatStreamFormGroup, PlaylistBeatStreamFormService } from './playlist-beat-stream-form.service';

@Component({
  standalone: true,
  selector: 'jhi-playlist-beat-stream-update',
  templateUrl: './playlist-beat-stream-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PlaylistBeatStreamUpdateComponent implements OnInit {
  isSaving = false;
  playlist: IPlaylistBeatStream | null = null;

  tracksSharedCollection: ITrackBeatStream[] = [];

  protected playlistService = inject(PlaylistBeatStreamService);
  protected playlistFormService = inject(PlaylistBeatStreamFormService);
  protected trackService = inject(TrackBeatStreamService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PlaylistBeatStreamFormGroup = this.playlistFormService.createPlaylistBeatStreamFormGroup();

  compareTrackBeatStream = (o1: ITrackBeatStream | null, o2: ITrackBeatStream | null): boolean =>
    this.trackService.compareTrackBeatStream(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ playlist }) => {
      this.playlist = playlist;
      if (playlist) {
        this.updateForm(playlist);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const playlist = this.playlistFormService.getPlaylistBeatStream(this.editForm);
    if (playlist.id !== null) {
      this.subscribeToSaveResponse(this.playlistService.update(playlist));
    } else {
      this.subscribeToSaveResponse(this.playlistService.create(playlist));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPlaylistBeatStream>>): void {
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

  protected updateForm(playlist: IPlaylistBeatStream): void {
    this.playlist = playlist;
    this.playlistFormService.resetForm(this.editForm, playlist);

    this.tracksSharedCollection = this.trackService.addTrackBeatStreamToCollectionIfMissing<ITrackBeatStream>(
      this.tracksSharedCollection,
      ...(playlist.tracks ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.trackService
      .query()
      .pipe(map((res: HttpResponse<ITrackBeatStream[]>) => res.body ?? []))
      .pipe(
        map((tracks: ITrackBeatStream[]) =>
          this.trackService.addTrackBeatStreamToCollectionIfMissing<ITrackBeatStream>(tracks, ...(this.playlist?.tracks ?? [])),
        ),
      )
      .subscribe((tracks: ITrackBeatStream[]) => (this.tracksSharedCollection = tracks));
  }
}
