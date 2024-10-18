import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IListeningHistoryBeatStream } from '../listening-history-beat-stream.model';
import { ListeningHistoryBeatStreamService } from '../service/listening-history-beat-stream.service';

@Component({
  standalone: true,
  templateUrl: './listening-history-beat-stream-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ListeningHistoryBeatStreamDeleteDialogComponent {
  listeningHistory?: IListeningHistoryBeatStream;

  protected listeningHistoryService = inject(ListeningHistoryBeatStreamService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.listeningHistoryService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
