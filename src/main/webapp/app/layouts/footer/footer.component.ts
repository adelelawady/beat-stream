/* eslint-disable prettier/prettier */

import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';
import { PlyrMainComponent } from 'app/plyr/plyr.component';
import { TranslateDirective } from 'app/shared/language';

@Component({
  standalone: true,
  selector: 'jhi-footer',
  templateUrl: './footer.component.html',
  imports: [TranslateDirective, PlyrMainComponent],
})
export default class FooterComponent {}
