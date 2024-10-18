import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPlaylistBeatStream, NewPlaylistBeatStream } from '../playlist-beat-stream.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPlaylistBeatStream for edit and NewPlaylistBeatStreamFormGroupInput for create.
 */
type PlaylistBeatStreamFormGroupInput = IPlaylistBeatStream | PartialWithRequiredKeyOf<NewPlaylistBeatStream>;

type PlaylistBeatStreamFormDefaults = Pick<NewPlaylistBeatStream, 'id' | 'tracks'>;

type PlaylistBeatStreamFormGroupContent = {
  id: FormControl<IPlaylistBeatStream['id'] | NewPlaylistBeatStream['id']>;
  title: FormControl<IPlaylistBeatStream['title']>;
  description: FormControl<IPlaylistBeatStream['description']>;
  tracks: FormControl<IPlaylistBeatStream['tracks']>;
};

export type PlaylistBeatStreamFormGroup = FormGroup<PlaylistBeatStreamFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PlaylistBeatStreamFormService {
  createPlaylistBeatStreamFormGroup(playlist: PlaylistBeatStreamFormGroupInput = { id: null }): PlaylistBeatStreamFormGroup {
    const playlistRawValue = {
      ...this.getFormDefaults(),
      ...playlist,
    };
    return new FormGroup<PlaylistBeatStreamFormGroupContent>({
      id: new FormControl(
        { value: playlistRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      title: new FormControl(playlistRawValue.title, {
        validators: [Validators.required],
      }),
      description: new FormControl(playlistRawValue.description),
      tracks: new FormControl(playlistRawValue.tracks ?? []),
    });
  }

  getPlaylistBeatStream(form: PlaylistBeatStreamFormGroup): IPlaylistBeatStream | NewPlaylistBeatStream {
    return form.getRawValue() as IPlaylistBeatStream | NewPlaylistBeatStream;
  }

  resetForm(form: PlaylistBeatStreamFormGroup, playlist: PlaylistBeatStreamFormGroupInput): void {
    const playlistRawValue = { ...this.getFormDefaults(), ...playlist };
    form.reset(
      {
        ...playlistRawValue,
        id: { value: playlistRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PlaylistBeatStreamFormDefaults {
    return {
      id: null,
      tracks: [],
    };
  }
}
