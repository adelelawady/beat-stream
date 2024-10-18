import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IArtistBeatStream } from 'app/entities/artist-beat-stream/artist-beat-stream.model';
import { ArtistBeatStreamService } from 'app/entities/artist-beat-stream/service/artist-beat-stream.service';
import { IGenreBeatStream } from 'app/entities/genre-beat-stream/genre-beat-stream.model';
import { GenreBeatStreamService } from 'app/entities/genre-beat-stream/service/genre-beat-stream.service';
import { AlbumBeatStreamService } from '../service/album-beat-stream.service';
import { IAlbumBeatStream } from '../album-beat-stream.model';
import { AlbumBeatStreamFormGroup, AlbumBeatStreamFormService } from './album-beat-stream-form.service';

@Component({
  standalone: true,
  selector: 'jhi-album-beat-stream-update',
  templateUrl: './album-beat-stream-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class AlbumBeatStreamUpdateComponent implements OnInit {
  isSaving = false;
  album: IAlbumBeatStream | null = null;

  artistsSharedCollection: IArtistBeatStream[] = [];
  genresSharedCollection: IGenreBeatStream[] = [];

  protected albumService = inject(AlbumBeatStreamService);
  protected albumFormService = inject(AlbumBeatStreamFormService);
  protected artistService = inject(ArtistBeatStreamService);
  protected genreService = inject(GenreBeatStreamService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AlbumBeatStreamFormGroup = this.albumFormService.createAlbumBeatStreamFormGroup();

  compareArtistBeatStream = (o1: IArtistBeatStream | null, o2: IArtistBeatStream | null): boolean =>
    this.artistService.compareArtistBeatStream(o1, o2);

  compareGenreBeatStream = (o1: IGenreBeatStream | null, o2: IGenreBeatStream | null): boolean =>
    this.genreService.compareGenreBeatStream(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ album }) => {
      this.album = album;
      if (album) {
        this.updateForm(album);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const album = this.albumFormService.getAlbumBeatStream(this.editForm);
    if (album.id !== null) {
      this.subscribeToSaveResponse(this.albumService.update(album));
    } else {
      this.subscribeToSaveResponse(this.albumService.create(album));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAlbumBeatStream>>): void {
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

  protected updateForm(album: IAlbumBeatStream): void {
    this.album = album;
    this.albumFormService.resetForm(this.editForm, album);

    this.artistsSharedCollection = this.artistService.addArtistBeatStreamToCollectionIfMissing<IArtistBeatStream>(
      this.artistsSharedCollection,
      album.artist,
    );
    this.genresSharedCollection = this.genreService.addGenreBeatStreamToCollectionIfMissing<IGenreBeatStream>(
      this.genresSharedCollection,
      ...(album.genres ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.artistService
      .query()
      .pipe(map((res: HttpResponse<IArtistBeatStream[]>) => res.body ?? []))
      .pipe(
        map((artists: IArtistBeatStream[]) =>
          this.artistService.addArtistBeatStreamToCollectionIfMissing<IArtistBeatStream>(artists, this.album?.artist),
        ),
      )
      .subscribe((artists: IArtistBeatStream[]) => (this.artistsSharedCollection = artists));

    this.genreService
      .query()
      .pipe(map((res: HttpResponse<IGenreBeatStream[]>) => res.body ?? []))
      .pipe(
        map((genres: IGenreBeatStream[]) =>
          this.genreService.addGenreBeatStreamToCollectionIfMissing<IGenreBeatStream>(genres, ...(this.album?.genres ?? [])),
        ),
      )
      .subscribe((genres: IGenreBeatStream[]) => (this.genresSharedCollection = genres));
  }
}
