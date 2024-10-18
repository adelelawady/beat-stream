import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IGenreBeatStream } from '../genre-beat-stream.model';

@Component({
  standalone: true,
  selector: 'jhi-genre-beat-stream-detail',
  templateUrl: './genre-beat-stream-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class GenreBeatStreamDetailComponent {
  genre = input<IGenreBeatStream | null>(null);

  previousState(): void {
    window.history.back();
  }
}
