import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IListeningHistoryBeatStream } from '../listening-history-beat-stream.model';

@Component({
  standalone: true,
  selector: 'jhi-listening-history-beat-stream-detail',
  templateUrl: './listening-history-beat-stream-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class ListeningHistoryBeatStreamDetailComponent {
  listeningHistory = input<IListeningHistoryBeatStream | null>(null);

  previousState(): void {
    window.history.back();
  }
}
