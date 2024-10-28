import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { ITaskNode } from '../task-node.model';

@Component({
  standalone: true,
  selector: 'jhi-task-node-detail',
  templateUrl: './task-node-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class TaskNodeDetailComponent {
  taskNode = input<ITaskNode | null>(null);

  previousState(): void {
    window.history.back();
  }
}
