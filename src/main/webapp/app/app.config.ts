/* eslint-disable prettier/prettier */

import { ApplicationConfig, LOCALE_ID, importProvidersFrom, inject } from '@angular/core';
import { BrowserModule, Title } from '@angular/platform-browser';
import {
  NavigationError,
  Router,
  RouterFeatures,
  TitleStrategy,
  provideRouter,
  withComponentInputBinding,
  withDebugTracing,
  withNavigationErrorHandler,
} from '@angular/router';
import { ServiceWorkerModule } from '@angular/service-worker';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

import { NgbDateAdapter } from '@ng-bootstrap/ng-bootstrap';

import { DEBUG_INFO_ENABLED } from 'app/app.constants';
import './config/dayjs';
import { TranslationModule } from 'app/shared/language/translation.module';
import FindLanguageFromKeyPipe from 'app/shared/language/find-language-from-key.pipe';
import { httpInterceptorProviders } from './core/interceptor';
import routes from './app.routes';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { NgbDateDayjsAdapter } from './config/datepicker-adapter';
import { AppPageTitleStrategy } from './app-page-title-strategy';

const routerFeatures: RouterFeatures[] = [
  withComponentInputBinding(),
  withNavigationErrorHandler((e: NavigationError) => {
    const router = inject(Router);
    if (e.error.status === 403) {
      router.navigate(['/accessdenied']);
    } else if (e.error.status === 404) {
      router.navigate(['/404']);
    } else if (e.error.status === 401) {
      router.navigate(['/login']);
    } else {
      router.navigate(['/error']);
    }
  }),
];

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes, ...routerFeatures),
    importProvidersFrom(BrowserModule),
    importProvidersFrom(ServiceWorkerModule.register('ngsw-worker.js', { enabled: false })),
    importProvidersFrom(TranslationModule),
    provideHttpClient(withInterceptorsFromDi()),
    Title,
    { provide: LOCALE_ID, useValue: 'en' },
    { provide: NgbDateAdapter, useClass: NgbDateDayjsAdapter },
    FindLanguageFromKeyPipe,
    httpInterceptorProviders,
    { provide: TitleStrategy, useClass: AppPageTitleStrategy },
  ],
};
