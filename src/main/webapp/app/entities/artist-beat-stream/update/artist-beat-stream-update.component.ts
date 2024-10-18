import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IArtistBeatStream } from '../artist-beat-stream.model';
import { ArtistBeatStreamService } from '../service/artist-beat-stream.service';
import { ArtistBeatStreamFormGroup, ArtistBeatStreamFormService } from './artist-beat-stream-form.service';

@Component({
  standalone: true,
  selector: 'jhi-artist-beat-stream-update',
  templateUrl: './artist-beat-stream-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ArtistBeatStreamUpdateComponent implements OnInit {
  isSaving = false;
  artist: IArtistBeatStream | null = null;

  protected artistService = inject(ArtistBeatStreamService);
  protected artistFormService = inject(ArtistBeatStreamFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ArtistBeatStreamFormGroup = this.artistFormService.createArtistBeatStreamFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ artist }) => {
      this.artist = artist;
      if (artist) {
        this.updateForm(artist);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const artist = this.artistFormService.getArtistBeatStream(this.editForm);
    if (artist.id !== null) {
      this.subscribeToSaveResponse(this.artistService.update(artist));
    } else {
      this.subscribeToSaveResponse(this.artistService.create(artist));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IArtistBeatStream>>): void {
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

  protected updateForm(artist: IArtistBeatStream): void {
    this.artist = artist;
    this.artistFormService.resetForm(this.editForm, artist);
  }
}
