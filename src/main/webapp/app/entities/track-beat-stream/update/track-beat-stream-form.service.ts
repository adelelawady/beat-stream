import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITrackBeatStream, NewTrackBeatStream } from '../track-beat-stream.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITrackBeatStream for edit and NewTrackBeatStreamFormGroupInput for create.
 */
type TrackBeatStreamFormGroupInput = ITrackBeatStream | PartialWithRequiredKeyOf<NewTrackBeatStream>;

type TrackBeatStreamFormDefaults = Pick<NewTrackBeatStream, 'id' | 'liked'>;

type TrackBeatStreamFormGroupContent = {
  id: FormControl<ITrackBeatStream['id'] | NewTrackBeatStream['id']>;
  title: FormControl<ITrackBeatStream['title']>;
  duration: FormControl<ITrackBeatStream['duration']>;
  liked: FormControl<ITrackBeatStream['liked']>;
  audioFileId: FormControl<ITrackBeatStream['audioFileId']>;
  coverImageFileId: FormControl<ITrackBeatStream['coverImageFileId']>;
  artist: FormControl<ITrackBeatStream['artist']>;
  album: FormControl<ITrackBeatStream['album']>;
};

export type TrackBeatStreamFormGroup = FormGroup<TrackBeatStreamFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TrackBeatStreamFormService {
  createTrackBeatStreamFormGroup(track: TrackBeatStreamFormGroupInput = { id: null }): TrackBeatStreamFormGroup {
    const trackRawValue = {
      ...this.getFormDefaults(),
      ...track,
    };
    return new FormGroup<TrackBeatStreamFormGroupContent>({
      id: new FormControl(
        { value: trackRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      title: new FormControl(trackRawValue.title, {
        validators: [Validators.required],
      }),
      duration: new FormControl(trackRawValue.duration),
      liked: new FormControl(trackRawValue.liked),
      audioFileId: new FormControl(trackRawValue.audioFileId),
      coverImageFileId: new FormControl(trackRawValue.coverImageFileId),
      artist: new FormControl(trackRawValue.artist),
      album: new FormControl(trackRawValue.album),
    });
  }

  getTrackBeatStream(form: TrackBeatStreamFormGroup): ITrackBeatStream | NewTrackBeatStream {
    return form.getRawValue() as ITrackBeatStream | NewTrackBeatStream;
  }

  resetForm(form: TrackBeatStreamFormGroup, track: TrackBeatStreamFormGroupInput): void {
    const trackRawValue = { ...this.getFormDefaults(), ...track };
    form.reset(
      {
        ...trackRawValue,
        id: { value: trackRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TrackBeatStreamFormDefaults {
    return {
      id: null,
      liked: false,
    };
  }
}
