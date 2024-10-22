/* eslint-disable prettier/prettier */

import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AudioService {
  audioSourceSubject = new BehaviorSubject<any>(null);
  audioSource$ = this.audioSourceSubject.asObservable();

  setAudioSource(source: any): void {
    this.audioSourceSubject.next(source);
  }
}
