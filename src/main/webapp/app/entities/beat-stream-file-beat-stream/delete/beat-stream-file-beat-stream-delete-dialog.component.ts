import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IBeatStreamFileBeatStream } from '../beat-stream-file-beat-stream.model';
import { BeatStreamFileBeatStreamService } from '../service/beat-stream-file-beat-stream.service';

@Component({
  standalone: true,
  templateUrl: './beat-stream-file-beat-stream-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class BeatStreamFileBeatStreamDeleteDialogComponent {
  beatStreamFile?: IBeatStreamFileBeatStream;

  protected beatStreamFileService = inject(BeatStreamFileBeatStreamService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.beatStreamFileService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
