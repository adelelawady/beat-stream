/* eslint-disable prettier/prettier */
import { CommonModule } from '@angular/common';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AudioService } from 'app/shared/service/audio.service';

@Component({
  selector: 'jhi-plyr',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './plyr.component.html',
  styleUrl: './plyr.component.scss',
})
export class PlyrMainComponent implements OnInit {
  @ViewChild('audioPlayer') audioPlayer!: ElementRef<HTMLAudioElement>;
  isPlaying = false;
  trackProgress = 0;

  currentTrack = {
    title: 'Song Title',
    artist: 'Artist Name',
    url: 'https://path/to/audio.mp3',
    artwork: 'https://as2.ftcdn.net/v2/jpg/02/98/27/61/1000_F_298276112_Xa5xbk8UNgzNozC0n3itmPyQO2alNZnl.jpg',
    duration: 0,
  };

  constructor(private audioService: AudioService) {}

  // Play/Pause logic
  togglePlayPause(): void {
    const audio: HTMLAudioElement = this.audioPlayer.nativeElement;
    if (this.isPlaying) {
      audio.pause();
    } else {
      audio.play();
    }
    this.isPlaying = !this.isPlaying;
  }

  // Update progress bar while track is playing
  onTimeUpdate(event: any): void {
    const audio: HTMLAudioElement = event.target as HTMLAudioElement;
    this.trackProgress = (audio.currentTime / audio.duration) * 100;
  }

  // Seek track position
  seekTrack(event: any): void {
    const audio: HTMLAudioElement = this.audioPlayer.nativeElement;
    const seekValue = event.target.value;
    audio.currentTime = (seekValue / 100) * audio.duration;
  }

  ngOnInit(): void {
    // Initialize subscription to audio source
    this.audioService.audioSource$.subscribe(track => {
      if (track !== null) {
        const audio: HTMLAudioElement = this.audioPlayer.nativeElement;
        this.currentTrack.url = `http://localhost:4200/api/track/play/${track.id}`;
        this.currentTrack.duration = track.duration;
        this.currentTrack.artist = this.formatDuration(track.duration);

        this.currentTrack.title = track.title;
        audio.load();
        audio.play();
      }
    });
  }

  formatDuration(seconds: number): string {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;

    const formattedSeconds = remainingSeconds < 10 ? `0${remainingSeconds}` : remainingSeconds;

    return `${minutes}:${formattedSeconds}`;
  }
}