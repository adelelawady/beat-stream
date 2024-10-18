import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IAlbumBeatStream } from '../album-beat-stream.model';

@Component({
  standalone: true,
  selector: 'jhi-album-beat-stream-detail',
  templateUrl: './album-beat-stream-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class AlbumBeatStreamDetailComponent {
  album = input<IAlbumBeatStream | null>(null);

  previousState(): void {
    window.history.back();
  }
}
