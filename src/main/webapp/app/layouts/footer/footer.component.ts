import { AfterViewInit, Component, ElementRef, inject, OnInit, signal, ViewChild } from '@angular/core';
import { Account } from 'app/core/auth/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { PlyrMainComponent } from 'app/plyr/plyr.component';
import { TranslateDirective } from 'app/shared/language';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { CommonModule } from '@angular/common';

@Component({
  standalone: true,
  selector: 'jhi-footer',
  templateUrl: './footer.component.html',
  imports: [TranslateDirective, PlyrMainComponent, CommonModule],
})
export default class FooterComponent implements OnInit {
  account = signal<Account | null>(null);
  private accountService = inject(AccountService);
  private readonly destroy$ = new Subject<void>();

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => {
        this.account.set(account);
      });
  }
}
