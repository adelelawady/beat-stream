/* eslint-disable prettier/prettier */

import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface Toast {
  message: string;
  type: 'success' | 'error' | 'info';
}

@Injectable({
  providedIn: 'root',
})
export class ToastService {
  private toasts: Toast[] = [];
  private toastSubject = new BehaviorSubject<Toast[]>(this.toasts);

  getToasts(): Observable<Toast[]> {
    return this.toastSubject.asObservable();
  }

  addToast(toast: Toast): void {
    const exists = this.toasts.some(existingToast => existingToast.message === toast.message);
    if (!exists) {
      this.toasts.push(toast);
      this.toastSubject.next(this.toasts);
      setTimeout((): void => this.removeToast(toast), 3000); // Auto-remove after 3 seconds
    }
  }

  removeToast(toast: Toast): void {
    this.toasts = this.toasts.filter(t => t !== toast);
    this.toastSubject.next(this.toasts);
  }
}
