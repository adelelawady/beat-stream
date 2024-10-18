import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IArtistBeatStream } from 'app/entities/artist-beat-stream/artist-beat-stream.model';
import { ArtistBeatStreamService } from 'app/entities/artist-beat-stream/service/artist-beat-stream.service';
import { IGenreBeatStream } from '../genre-beat-stream.model';
import { GenreBeatStreamService } from '../service/genre-beat-stream.service';
import { GenreBeatStreamFormGroup, GenreBeatStreamFormService } from './genre-beat-stream-form.service';

@Component({
  standalone: true,
  selector: 'jhi-genre-beat-stream-update',
  templateUrl: './genre-beat-stream-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class GenreBeatStreamUpdateComponent implements OnInit {
  isSaving = false;
  genre: IGenreBeatStream | null = null;

  artistsSharedCollection: IArtistBeatStream[] = [];

  protected genreService = inject(GenreBeatStreamService);
  protected genreFormService = inject(GenreBeatStreamFormService);
  protected artistService = inject(ArtistBeatStreamService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: GenreBeatStreamFormGroup = this.genreFormService.createGenreBeatStreamFormGroup();

  compareArtistBeatStream = (o1: IArtistBeatStream | null, o2: IArtistBeatStream | null): boolean =>
    this.artistService.compareArtistBeatStream(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ genre }) => {
      this.genre = genre;
      if (genre) {
        this.updateForm(genre);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const genre = this.genreFormService.getGenreBeatStream(this.editForm);
    if (genre.id !== null) {
      this.subscribeToSaveResponse(this.genreService.update(genre));
    } else {
      this.subscribeToSaveResponse(this.genreService.create(genre));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGenreBeatStream>>): void {
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

  protected updateForm(genre: IGenreBeatStream): void {
    this.genre = genre;
    this.genreFormService.resetForm(this.editForm, genre);

    this.artistsSharedCollection = this.artistService.addArtistBeatStreamToCollectionIfMissing<IArtistBeatStream>(
      this.artistsSharedCollection,
      ...(genre.artists ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.artistService
      .query()
      .pipe(map((res: HttpResponse<IArtistBeatStream[]>) => res.body ?? []))
      .pipe(
        map((artists: IArtistBeatStream[]) =>
          this.artistService.addArtistBeatStreamToCollectionIfMissing<IArtistBeatStream>(artists, ...(this.genre?.artists ?? [])),
        ),
      )
      .subscribe((artists: IArtistBeatStream[]) => (this.artistsSharedCollection = artists));
  }
}
