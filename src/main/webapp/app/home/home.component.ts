import { Component, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
// prettier-ignore
@Component({
  standalone: true,
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  imports: [SharedModule, RouterModule],
})
export default class HomeComponent implements OnInit, OnDestroy {
  showModal = false;  // Track whether the modal is visible
  title = '';    
  account = signal<Account | null>(null);
  isCollapsed = false;
  private readonly destroy$ = new Subject<void>();

  private accountService = inject(AccountService);
  private router = inject(Router);


  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => this.account.set(account));
  }
  login(): void {
    this.router.navigate(['/login']);
  }
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
   // Method to open the modal
   openModal(): void {
    this.showModal = true;
  }

  // Method to close the modal
  closeModal(): void {
    this.showModal = false;
  }

  // Method to handle submission
  submitTitle(): void {
    //console.log('Title entered:', this.title);
    this.closeModal(); // Close the modal after submission
    // You can add additional logic here (e.g., save the title)
  }
}
