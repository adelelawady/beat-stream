import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IArtistBeatStream } from '../artist-beat-stream.model';

@Component({
  standalone: true,
  selector: 'jhi-artist-beat-stream-detail',
  templateUrl: './artist-beat-stream-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class ArtistBeatStreamDetailComponent {
  artist = input<IArtistBeatStream | null>(null);

  previousState(): void {
    window.history.back();
  }
}
