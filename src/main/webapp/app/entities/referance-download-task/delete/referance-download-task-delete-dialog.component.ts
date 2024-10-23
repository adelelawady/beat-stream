import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IReferanceDownloadTask } from '../referance-download-task.model';
import { ReferanceDownloadTaskService } from '../service/referance-download-task.service';

@Component({
  standalone: true,
  templateUrl: './referance-download-task-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ReferanceDownloadTaskDeleteDialogComponent {
  referanceDownloadTask?: IReferanceDownloadTask;

  protected referanceDownloadTaskService = inject(ReferanceDownloadTaskService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.referanceDownloadTaskService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
