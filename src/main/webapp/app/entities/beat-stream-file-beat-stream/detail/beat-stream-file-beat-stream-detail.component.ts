import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IBeatStreamFileBeatStream } from '../beat-stream-file-beat-stream.model';

@Component({
  standalone: true,
  selector: 'jhi-beat-stream-file-beat-stream-detail',
  templateUrl: './beat-stream-file-beat-stream-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class BeatStreamFileBeatStreamDetailComponent {
  beatStreamFile = input<IBeatStreamFileBeatStream | null>(null);

  previousState(): void {
    window.history.back();
  }
}
