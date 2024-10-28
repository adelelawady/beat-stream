import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ITaskNode } from '../task-node.model';
import { TaskNodeService } from '../service/task-node.service';

@Component({
  standalone: true,
  templateUrl: './task-node-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TaskNodeDeleteDialogComponent {
  taskNode?: ITaskNode;

  protected taskNodeService = inject(TaskNodeService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.taskNodeService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
