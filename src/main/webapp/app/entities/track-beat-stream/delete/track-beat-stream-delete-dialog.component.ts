import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ITrackBeatStream } from '../track-beat-stream.model';
import { TrackBeatStreamService } from '../service/track-beat-stream.service';

@Component({
  standalone: true,
  templateUrl: './track-beat-stream-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TrackBeatStreamDeleteDialogComponent {
  track?: ITrackBeatStream;

  protected trackService = inject(TrackBeatStreamService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.trackService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
