import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IPlaylistBeatStream } from '../playlist-beat-stream.model';

@Component({
  standalone: true,
  selector: 'jhi-playlist-beat-stream-detail',
  templateUrl: './playlist-beat-stream-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class PlaylistBeatStreamDetailComponent {
  playlist = input<IPlaylistBeatStream | null>(null);

  previousState(): void {
    window.history.back();
  }
}
