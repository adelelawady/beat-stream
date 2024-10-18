import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IListeningHistoryBeatStream, NewListeningHistoryBeatStream } from '../listening-history-beat-stream.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IListeningHistoryBeatStream for edit and NewListeningHistoryBeatStreamFormGroupInput for create.
 */
type ListeningHistoryBeatStreamFormGroupInput = IListeningHistoryBeatStream | PartialWithRequiredKeyOf<NewListeningHistoryBeatStream>;

type ListeningHistoryBeatStreamFormDefaults = Pick<NewListeningHistoryBeatStream, 'id'>;

type ListeningHistoryBeatStreamFormGroupContent = {
  id: FormControl<IListeningHistoryBeatStream['id'] | NewListeningHistoryBeatStream['id']>;
  timestamp: FormControl<IListeningHistoryBeatStream['timestamp']>;
  track: FormControl<IListeningHistoryBeatStream['track']>;
};

export type ListeningHistoryBeatStreamFormGroup = FormGroup<ListeningHistoryBeatStreamFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ListeningHistoryBeatStreamFormService {
  createListeningHistoryBeatStreamFormGroup(
    listeningHistory: ListeningHistoryBeatStreamFormGroupInput = { id: null },
  ): ListeningHistoryBeatStreamFormGroup {
    const listeningHistoryRawValue = {
      ...this.getFormDefaults(),
      ...listeningHistory,
    };
    return new FormGroup<ListeningHistoryBeatStreamFormGroupContent>({
      id: new FormControl(
        { value: listeningHistoryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      timestamp: new FormControl(listeningHistoryRawValue.timestamp),
      track: new FormControl(listeningHistoryRawValue.track),
    });
  }

  getListeningHistoryBeatStream(form: ListeningHistoryBeatStreamFormGroup): IListeningHistoryBeatStream | NewListeningHistoryBeatStream {
    return form.getRawValue() as IListeningHistoryBeatStream | NewListeningHistoryBeatStream;
  }

  resetForm(form: ListeningHistoryBeatStreamFormGroup, listeningHistory: ListeningHistoryBeatStreamFormGroupInput): void {
    const listeningHistoryRawValue = { ...this.getFormDefaults(), ...listeningHistory };
    form.reset(
      {
        ...listeningHistoryRawValue,
        id: { value: listeningHistoryRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ListeningHistoryBeatStreamFormDefaults {
    return {
      id: null,
    };
  }
}
