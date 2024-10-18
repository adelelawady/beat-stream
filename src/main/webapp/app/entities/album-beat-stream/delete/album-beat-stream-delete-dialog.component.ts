import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IAlbumBeatStream } from '../album-beat-stream.model';
import { AlbumBeatStreamService } from '../service/album-beat-stream.service';

@Component({
  standalone: true,
  templateUrl: './album-beat-stream-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class AlbumBeatStreamDeleteDialogComponent {
  album?: IAlbumBeatStream;

  protected albumService = inject(AlbumBeatStreamService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.albumService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
