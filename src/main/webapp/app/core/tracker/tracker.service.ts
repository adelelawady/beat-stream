/* eslint-disable  prettier/prettier */
import { Injectable, inject } from '@angular/core';
import { Location } from '@angular/common';
import { Event, NavigationEnd, Router } from '@angular/router';
import { Observer, Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';

import SockJS from 'sockjs-client';
import { RxStomp } from '@stomp/rx-stomp';

import { AuthServerProvider } from 'app/core/auth/auth-jwt.service';
import { AccountService } from '../auth/account.service';
import { Account } from '../auth/account.model';
import { TrackerActivity } from './tracker-activity.model';
import { ToastService } from 'app/toast/toast.service';
import { TaskNodeDownloadService } from 'app/shared/service/TaskNodeDownload.service';

const DESTINATION_TRACKER = '/topic/tracker';

const DESTINATION_ACTIVITY = '/topic/activity';

@Injectable({ providedIn: 'root' })
export class TrackerService {
  DESTINATION_DOWNLOAD_TRACKER = '/topic/tasks';
  private rxStomp?: RxStomp;
  private routerSubscription: Subscription | null = null;

  private router = inject(Router);
  private accountService = inject(AccountService);
  private authServerProvider = inject(AuthServerProvider);
  private location = inject(Location);
  private toastService = inject(ToastService);
  private taskListSerivce = inject(TaskNodeDownloadService);
  setup(): void {
    this.rxStomp = new RxStomp();
    this.rxStomp.configure({
      // eslint-disable-next-line
      //debug: (msg: string): void => console.log(new Date(), msg), // correct
    });

    this.accountService.getAuthenticationState().subscribe({
      next: (account: Account | null) => {
        if (account) {
          this.connect();
        } else {
          this.disconnect();
        }
      },
    });
    this.rxStomp.connected$.subscribe(() => {
      this.sendActivity();

      this.stomp
        .watch(this.DESTINATION_DOWNLOAD_TRACKER)
        // eslint-disable-next-line @typescript-eslint/no-unsafe-return
        .subscribe(observer => {
          if (observer.body.toString().includes('[Message]')) {
            if (observer.body.toString().includes('[completed]')) {
              this.taskListSerivce.setTaskList('reload');
            }
            this.toastService.addToast({ message: observer.body.replace('[Message]', ''), type: 'success' });
          } else {
            this.taskListSerivce.setTaskList(observer.body);
          }
        });

      this.routerSubscription = this.router.events
        .pipe(filter((event: Event) => event instanceof NavigationEnd))
        .subscribe(() => this.sendActivity());
    });
  }

  get stomp(): RxStomp {
    if (!this.rxStomp) {
      throw new Error('Stomp connection not initialized');
    }
    return this.rxStomp;
  }

  public subscribe(observer: Partial<Observer<TrackerActivity>>): Subscription {
    return (
      this.stomp
        .watch(DESTINATION_TRACKER)
        // eslint-disable-next-line @typescript-eslint/no-unsafe-return
        .pipe(map(imessage => JSON.parse(imessage.body)))
        .subscribe(observer)
    );
  }

  sendActivity(): void {
    this.stomp.publish({
      destination: DESTINATION_ACTIVITY,
      body: JSON.stringify({ page: this.router.routerState.snapshot.url }),
    });
  }

  private connect(): void {
    this.updateCredentials();
    return this.stomp.activate();
  }

  private disconnect(): Promise<void> {
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
      this.routerSubscription = null;
    }
    return this.stomp.deactivate();
  }

  private buildUrl(): string {
    // building absolute path so that websocket doesn't fail when deploying with a context path
    let url = '/websocket/tracker';
    url = this.location.prepareExternalUrl(url);
    const authToken = this.authServerProvider.getToken();
    if (authToken) {
      return `${url}?access_token=${authToken}`;
    }
    return url;
  }

  private updateCredentials(): void {
    this.stomp.configure({
      webSocketFactory: () => SockJS(this.buildUrl()),
    });
  }
}
