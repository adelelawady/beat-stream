import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IAlbumBeatStream, NewAlbumBeatStream } from '../album-beat-stream.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAlbumBeatStream for edit and NewAlbumBeatStreamFormGroupInput for create.
 */
type AlbumBeatStreamFormGroupInput = IAlbumBeatStream | PartialWithRequiredKeyOf<NewAlbumBeatStream>;

type AlbumBeatStreamFormDefaults = Pick<NewAlbumBeatStream, 'id' | 'genres'>;

type AlbumBeatStreamFormGroupContent = {
  id: FormControl<IAlbumBeatStream['id'] | NewAlbumBeatStream['id']>;
  title: FormControl<IAlbumBeatStream['title']>;
  releaseDate: FormControl<IAlbumBeatStream['releaseDate']>;
  coverImageFileId: FormControl<IAlbumBeatStream['coverImageFileId']>;
  artist: FormControl<IAlbumBeatStream['artist']>;
  genres: FormControl<IAlbumBeatStream['genres']>;
};

export type AlbumBeatStreamFormGroup = FormGroup<AlbumBeatStreamFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AlbumBeatStreamFormService {
  createAlbumBeatStreamFormGroup(album: AlbumBeatStreamFormGroupInput = { id: null }): AlbumBeatStreamFormGroup {
    const albumRawValue = {
      ...this.getFormDefaults(),
      ...album,
    };
    return new FormGroup<AlbumBeatStreamFormGroupContent>({
      id: new FormControl(
        { value: albumRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      title: new FormControl(albumRawValue.title, {
        validators: [Validators.required],
      }),
      releaseDate: new FormControl(albumRawValue.releaseDate),
      coverImageFileId: new FormControl(albumRawValue.coverImageFileId),
      artist: new FormControl(albumRawValue.artist),
      genres: new FormControl(albumRawValue.genres ?? []),
    });
  }

  getAlbumBeatStream(form: AlbumBeatStreamFormGroup): IAlbumBeatStream | NewAlbumBeatStream {
    return form.getRawValue() as IAlbumBeatStream | NewAlbumBeatStream;
  }

  resetForm(form: AlbumBeatStreamFormGroup, album: AlbumBeatStreamFormGroupInput): void {
    const albumRawValue = { ...this.getFormDefaults(), ...album };
    form.reset(
      {
        ...albumRawValue,
        id: { value: albumRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): AlbumBeatStreamFormDefaults {
    return {
      id: null,
      genres: [],
    };
  }
}
