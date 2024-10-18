import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IArtistBeatStream, NewArtistBeatStream } from '../artist-beat-stream.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IArtistBeatStream for edit and NewArtistBeatStreamFormGroupInput for create.
 */
type ArtistBeatStreamFormGroupInput = IArtistBeatStream | PartialWithRequiredKeyOf<NewArtistBeatStream>;

type ArtistBeatStreamFormDefaults = Pick<NewArtistBeatStream, 'id'>;

type ArtistBeatStreamFormGroupContent = {
  id: FormControl<IArtistBeatStream['id'] | NewArtistBeatStream['id']>;
  name: FormControl<IArtistBeatStream['name']>;
  bio: FormControl<IArtistBeatStream['bio']>;
  coverImageFileId: FormControl<IArtistBeatStream['coverImageFileId']>;
};

export type ArtistBeatStreamFormGroup = FormGroup<ArtistBeatStreamFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ArtistBeatStreamFormService {
  createArtistBeatStreamFormGroup(artist: ArtistBeatStreamFormGroupInput = { id: null }): ArtistBeatStreamFormGroup {
    const artistRawValue = {
      ...this.getFormDefaults(),
      ...artist,
    };
    return new FormGroup<ArtistBeatStreamFormGroupContent>({
      id: new FormControl(
        { value: artistRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(artistRawValue.name, {
        validators: [Validators.required],
      }),
      bio: new FormControl(artistRawValue.bio),
      coverImageFileId: new FormControl(artistRawValue.coverImageFileId),
    });
  }

  getArtistBeatStream(form: ArtistBeatStreamFormGroup): IArtistBeatStream | NewArtistBeatStream {
    return form.getRawValue() as IArtistBeatStream | NewArtistBeatStream;
  }

  resetForm(form: ArtistBeatStreamFormGroup, artist: ArtistBeatStreamFormGroupInput): void {
    const artistRawValue = { ...this.getFormDefaults(), ...artist };
    form.reset(
      {
        ...artistRawValue,
        id: { value: artistRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ArtistBeatStreamFormDefaults {
    return {
      id: null,
    };
  }
}
