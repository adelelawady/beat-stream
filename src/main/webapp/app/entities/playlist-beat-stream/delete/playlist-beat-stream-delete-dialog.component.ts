import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPlaylistBeatStream } from '../playlist-beat-stream.model';
import { PlaylistBeatStreamService } from '../service/playlist-beat-stream.service';

@Component({
  standalone: true,
  templateUrl: './playlist-beat-stream-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PlaylistBeatStreamDeleteDialogComponent {
  playlist?: IPlaylistBeatStream;

  protected playlistService = inject(PlaylistBeatStreamService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.playlistService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
