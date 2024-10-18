import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { ITrackBeatStream } from '../track-beat-stream.model';

@Component({
  standalone: true,
  selector: 'jhi-track-beat-stream-detail',
  templateUrl: './track-beat-stream-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class TrackBeatStreamDetailComponent {
  track = input<ITrackBeatStream | null>(null);

  previousState(): void {
    window.history.back();
  }
}
