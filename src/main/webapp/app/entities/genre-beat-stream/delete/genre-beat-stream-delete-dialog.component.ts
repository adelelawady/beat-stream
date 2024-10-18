import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IGenreBeatStream } from '../genre-beat-stream.model';
import { GenreBeatStreamService } from '../service/genre-beat-stream.service';

@Component({
  standalone: true,
  templateUrl: './genre-beat-stream-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class GenreBeatStreamDeleteDialogComponent {
  genre?: IGenreBeatStream;

  protected genreService = inject(GenreBeatStreamService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.genreService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
