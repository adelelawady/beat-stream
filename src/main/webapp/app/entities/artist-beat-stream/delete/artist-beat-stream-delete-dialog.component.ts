import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IArtistBeatStream } from '../artist-beat-stream.model';
import { ArtistBeatStreamService } from '../service/artist-beat-stream.service';

@Component({
  standalone: true,
  templateUrl: './artist-beat-stream-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ArtistBeatStreamDeleteDialogComponent {
  artist?: IArtistBeatStream;

  protected artistService = inject(ArtistBeatStreamService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.artistService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
