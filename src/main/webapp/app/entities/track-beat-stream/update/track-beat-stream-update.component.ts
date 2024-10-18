import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IArtistBeatStream } from 'app/entities/artist-beat-stream/artist-beat-stream.model';
import { ArtistBeatStreamService } from 'app/entities/artist-beat-stream/service/artist-beat-stream.service';
import { IAlbumBeatStream } from 'app/entities/album-beat-stream/album-beat-stream.model';
import { AlbumBeatStreamService } from 'app/entities/album-beat-stream/service/album-beat-stream.service';
import { TrackBeatStreamService } from '../service/track-beat-stream.service';
import { ITrackBeatStream } from '../track-beat-stream.model';
import { TrackBeatStreamFormGroup, TrackBeatStreamFormService } from './track-beat-stream-form.service';

@Component({
  standalone: true,
  selector: 'jhi-track-beat-stream-update',
  templateUrl: './track-beat-stream-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TrackBeatStreamUpdateComponent implements OnInit {
  isSaving = false;
  track: ITrackBeatStream | null = null;

  artistsSharedCollection: IArtistBeatStream[] = [];
  albumsSharedCollection: IAlbumBeatStream[] = [];

  protected trackService = inject(TrackBeatStreamService);
  protected trackFormService = inject(TrackBeatStreamFormService);
  protected artistService = inject(ArtistBeatStreamService);
  protected albumService = inject(AlbumBeatStreamService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TrackBeatStreamFormGroup = this.trackFormService.createTrackBeatStreamFormGroup();

  compareArtistBeatStream = (o1: IArtistBeatStream | null, o2: IArtistBeatStream | null): boolean =>
    this.artistService.compareArtistBeatStream(o1, o2);

  compareAlbumBeatStream = (o1: IAlbumBeatStream | null, o2: IAlbumBeatStream | null): boolean =>
    this.albumService.compareAlbumBeatStream(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ track }) => {
      this.track = track;
      if (track) {
        this.updateForm(track);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const track = this.trackFormService.getTrackBeatStream(this.editForm);
    if (track.id !== null) {
      this.subscribeToSaveResponse(this.trackService.update(track));
    } else {
      this.subscribeToSaveResponse(this.trackService.create(track));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITrackBeatStream>>): void {
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

  protected updateForm(track: ITrackBeatStream): void {
    this.track = track;
    this.trackFormService.resetForm(this.editForm, track);

    this.artistsSharedCollection = this.artistService.addArtistBeatStreamToCollectionIfMissing<IArtistBeatStream>(
      this.artistsSharedCollection,
      track.artist,
    );
    this.albumsSharedCollection = this.albumService.addAlbumBeatStreamToCollectionIfMissing<IAlbumBeatStream>(
      this.albumsSharedCollection,
      track.album,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.artistService
      .query()
      .pipe(map((res: HttpResponse<IArtistBeatStream[]>) => res.body ?? []))
      .pipe(
        map((artists: IArtistBeatStream[]) =>
          this.artistService.addArtistBeatStreamToCollectionIfMissing<IArtistBeatStream>(artists, this.track?.artist),
        ),
      )
      .subscribe((artists: IArtistBeatStream[]) => (this.artistsSharedCollection = artists));

    this.albumService
      .query()
      .pipe(map((res: HttpResponse<IAlbumBeatStream[]>) => res.body ?? []))
      .pipe(
        map((albums: IAlbumBeatStream[]) =>
          this.albumService.addAlbumBeatStreamToCollectionIfMissing<IAlbumBeatStream>(albums, this.track?.album),
        ),
      )
      .subscribe((albums: IAlbumBeatStream[]) => (this.albumsSharedCollection = albums));
  }
}
