import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class TaskNodeDownloadService {
  TaskListSubject = new BehaviorSubject<any>(null);
  TaskList$ = this.TaskListSubject.asObservable();

  setTaskList(source: any): void {
    this.TaskListSubject.next(source);
  }
}
