import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IReferanceDownloadTask } from '../referance-download-task.model';

@Component({
  standalone: true,
  selector: 'jhi-referance-download-task-detail',
  templateUrl: './referance-download-task-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class ReferanceDownloadTaskDetailComponent {
  referanceDownloadTask = input<IReferanceDownloadTask | null>(null);

  previousState(): void {
    window.history.back();
  }
}
