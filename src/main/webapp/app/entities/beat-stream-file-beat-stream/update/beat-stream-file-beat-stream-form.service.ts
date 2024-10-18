import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IBeatStreamFileBeatStream, NewBeatStreamFileBeatStream } from '../beat-stream-file-beat-stream.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBeatStreamFileBeatStream for edit and NewBeatStreamFileBeatStreamFormGroupInput for create.
 */
type BeatStreamFileBeatStreamFormGroupInput = IBeatStreamFileBeatStream | PartialWithRequiredKeyOf<NewBeatStreamFileBeatStream>;

type BeatStreamFileBeatStreamFormDefaults = Pick<NewBeatStreamFileBeatStream, 'id'>;

type BeatStreamFileBeatStreamFormGroupContent = {
  id: FormControl<IBeatStreamFileBeatStream['id'] | NewBeatStreamFileBeatStream['id']>;
  name: FormControl<IBeatStreamFileBeatStream['name']>;
  size: FormControl<IBeatStreamFileBeatStream['size']>;
  bucket: FormControl<IBeatStreamFileBeatStream['bucket']>;
  type: FormControl<IBeatStreamFileBeatStream['type']>;
};

export type BeatStreamFileBeatStreamFormGroup = FormGroup<BeatStreamFileBeatStreamFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BeatStreamFileBeatStreamFormService {
  createBeatStreamFileBeatStreamFormGroup(
    beatStreamFile: BeatStreamFileBeatStreamFormGroupInput = { id: null },
  ): BeatStreamFileBeatStreamFormGroup {
    const beatStreamFileRawValue = {
      ...this.getFormDefaults(),
      ...beatStreamFile,
    };
    return new FormGroup<BeatStreamFileBeatStreamFormGroupContent>({
      id: new FormControl(
        { value: beatStreamFileRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(beatStreamFileRawValue.name, {
        validators: [Validators.required],
      }),
      size: new FormControl(beatStreamFileRawValue.size),
      bucket: new FormControl(beatStreamFileRawValue.bucket),
      type: new FormControl(beatStreamFileRawValue.type),
    });
  }

  getBeatStreamFileBeatStream(form: BeatStreamFileBeatStreamFormGroup): IBeatStreamFileBeatStream | NewBeatStreamFileBeatStream {
    return form.getRawValue() as IBeatStreamFileBeatStream | NewBeatStreamFileBeatStream;
  }

  resetForm(form: BeatStreamFileBeatStreamFormGroup, beatStreamFile: BeatStreamFileBeatStreamFormGroupInput): void {
    const beatStreamFileRawValue = { ...this.getFormDefaults(), ...beatStreamFile };
    form.reset(
      {
        ...beatStreamFileRawValue,
        id: { value: beatStreamFileRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): BeatStreamFileBeatStreamFormDefaults {
    return {
      id: null,
    };
  }
}
