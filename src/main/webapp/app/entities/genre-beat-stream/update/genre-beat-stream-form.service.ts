import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IGenreBeatStream, NewGenreBeatStream } from '../genre-beat-stream.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IGenreBeatStream for edit and NewGenreBeatStreamFormGroupInput for create.
 */
type GenreBeatStreamFormGroupInput = IGenreBeatStream | PartialWithRequiredKeyOf<NewGenreBeatStream>;

type GenreBeatStreamFormDefaults = Pick<NewGenreBeatStream, 'id' | 'artists'>;

type GenreBeatStreamFormGroupContent = {
  id: FormControl<IGenreBeatStream['id'] | NewGenreBeatStream['id']>;
  name: FormControl<IGenreBeatStream['name']>;
  artists: FormControl<IGenreBeatStream['artists']>;
};

export type GenreBeatStreamFormGroup = FormGroup<GenreBeatStreamFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class GenreBeatStreamFormService {
  createGenreBeatStreamFormGroup(genre: GenreBeatStreamFormGroupInput = { id: null }): GenreBeatStreamFormGroup {
    const genreRawValue = {
      ...this.getFormDefaults(),
      ...genre,
    };
    return new FormGroup<GenreBeatStreamFormGroupContent>({
      id: new FormControl(
        { value: genreRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(genreRawValue.name, {
        validators: [Validators.required],
      }),
      artists: new FormControl(genreRawValue.artists ?? []),
    });
  }

  getGenreBeatStream(form: GenreBeatStreamFormGroup): IGenreBeatStream | NewGenreBeatStream {
    return form.getRawValue() as IGenreBeatStream | NewGenreBeatStream;
  }

  resetForm(form: GenreBeatStreamFormGroup, genre: GenreBeatStreamFormGroupInput): void {
    const genreRawValue = { ...this.getFormDefaults(), ...genre };
    form.reset(
      {
        ...genreRawValue,
        id: { value: genreRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): GenreBeatStreamFormDefaults {
    return {
      id: null,
      artists: [],
    };
  }
}
