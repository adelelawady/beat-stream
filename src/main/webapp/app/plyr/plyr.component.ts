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
    url: 'none',
    artwork: 'http://localhost:8080/content/images/logo-jhipster.png',
    duration: 0,
  };

  constructor(private audioService: AudioService) {}

  // Play/Pause logic
  togglePlayPause(): void {
    const audio: HTMLAudioElement = this.audioPlayer.nativeElement;
    if (this.isPlaying) {
      audio.pause();
      this.isPlaying = false;
    } else {
      if (this.currentTrack.url === 'none') {
        this.audioService.setAudioSource('PlayAny');
      }
      audio.play();
      this.isPlaying = true;
    }
  }

  // Update progress bar while track is playing
  onTimeUpdate(event: any): void {
    const audio: HTMLAudioElement = event.target as HTMLAudioElement;
    this.trackProgress = (audio.currentTime / audio.duration) * 100;
  }

  seekTrack(event: any): void {
    if (!event) {
      return;
    }
    const audio: HTMLAudioElement = this.audioPlayer.nativeElement;
    const seekValue = event.target.value;
    audio.currentTime = (seekValue / 100) * audio.duration;
  }

  ngOnInit(): void {
    // Initialize subscription to audio source
    this.audioService.audioSource$.subscribe(track => {
      if (track !== null && track !== 'PlayAny') {
        const audio: HTMLAudioElement = this.audioPlayer.nativeElement;
        this.currentTrack.url = `http://localhost:8080/api/track/play/${track.id}`;
        this.currentTrack.duration = track.duration;
        this.currentTrack.artist = this.formatDuration(track.duration);
        this.isPlaying = true;
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
