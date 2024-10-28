/* eslint-disable prettier/prettier */
import { Component, OnInit } from '@angular/core';
import { Toast, ToastService } from '../toast.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'jhi-app-toast',
  standalone: true,
  imports: [CommonModule],

  templateUrl: './toaster.component.html',
  styleUrls: ['./toaster.component.scss'],
})
export class ToasterComponent implements OnInit {
  toasts: Toast[] = [];

  constructor(private toastService: ToastService) {}

  ngOnInit(): void {
    this.toastService.getToasts().subscribe(toasts => {
      this.toasts = toasts;
    });
  }

  remove(toast: Toast): void {
    this.toastService.removeToast(toast);
  }
}
