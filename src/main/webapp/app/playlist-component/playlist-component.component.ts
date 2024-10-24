/* eslint-disable prettier/prettier */

import { Component, inject, Input, OnChanges, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { PlaylistBeatStreamService } from 'app/entities/playlist-beat-stream/service/playlist-beat-stream.service';
import { HttpClient } from '@angular/common/http';
import { TrackBeatStreamService } from 'app/entities/track-beat-stream/service/track-beat-stream.service';
import { CommonModule } from '@angular/common';
import FooterComponent from 'app/layouts/footer/footer.component';
import { AudioService } from 'app/shared/service/audio.service';
import { FileSystemDirectoryEntry, FileSystemFileEntry, NgxFileDropEntry, NgxFileDropModule } from 'ngx-file-drop';
import { timer } from 'rxjs';

@Component({
  selector: 'jhi-playlist-component',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    CommonModule,
    NgxFileDropModule,
    FormsModule, // Add FormsModule here
  ],
  templateUrl: './playlist-component.component.html',
  styleUrl: './playlist-component.component.scss',
})
export class PlaylistComponentComponent implements OnChanges, OnInit {
  showModal = false; // Track whether the modal is visible
  showYoutubeModal = false;
  showYoutubeDownloadModal = false;
  showConfirmation = false;
  enableStatus = true;
  selectedTrack: any;
  statusList: string[] = [
    'Initializing download...',
    'Connecting to server...',
    'Downloading file...',
    'Download in progress...',
    'Halfway there...',
    'Almost done...',
    'Finalizing download...',
    'Verifying file integrity...',
    'Extracting files...',
    'Saving data...',
    'Cleaning up...',
    'Setting up...',
    'Updating configurations...',
    'Preparing for launch...',
    'Launching application...',
    'Download completed successfully!',
    'Download failed, retrying...',
    'Retrying download...',
    'Error encountered, please check your connection.',
    'Download paused, resuming...',
    'Download canceled.',
    'Checking for updates...',
    'Update found, downloading...',
    'Re-downloading...',
    'Please wait while we finalize your download...',
  ];
  currentStatus = '';
  currentColor = '';
  index = 0;
  intervalId: any;
  youtubeVideoUrl = '';
  @ViewChild(FooterComponent) PlayerComponent!: FooterComponent;
  @Input() playlist: any;
  trackBeatStreamService = inject(TrackBeatStreamService);
  uploadForm!: FormGroup;
  resourceAPIUrl = this.trackBeatStreamService.applicationConfigService.getEndpointFor('api/audio');

  files: any[] = [];

  fileUploadProgress: Record<string, number> = {};
  uploadFinished = false;
  isUploading = false;
  uploadInProgress = false;

  playlistBeatStreamService = inject(PlaylistBeatStreamService);

  constructor(
    private formBuilder: FormBuilder,
    private http: HttpClient,
    private audioService: AudioService,
  ) {
    this.uploadForm = this.formBuilder.group({
      title: [''],
      audioFile: [null],
    });
  }

  ngOnInit(): void {
    this.audioService.audioSource$.subscribe(track => {
      if (track !== null && track === 'PlayAny') {
        this.playAnyTrack();
      }
    });
  }
  ngOnChanges(changes: SimpleChanges): void {
    const currentValue = changes['playlist'].currentValue;
    if (currentValue[0]) {
      this.loadPlaylist(currentValue[0].id);
    }
  }

  getTotalPlayCount(playlist: any): any {
    if (playlist?.tracks?.length > 0) {
      return playlist.tracks.reduce((sum: number, track: { playCount: number }) => sum + track.playCount, 0);
    } else {
      return 0;
    }
  }
  play(track: any): void {
    // track.playCount++;
    this.audioService.setAudioSource(track);
    this.selectedTrack = track;
  }

  playAnyTrack(): void {
    if (this.playlist.tracks.length > 0) {
      const randomIndex = Math.floor(Math.random() * this.playlist.tracks.length);
      const randomTrack = this.playlist.tracks[randomIndex];
      this.play(randomTrack);
    }
  }

  onFileSelect(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.uploadForm.patchValue({
        audioFile: file,
      });
    }
  }

  // Method to open the modal
  openUploadAudioModal(): void {
    this.showModal = true;
  }

  openYoutubeModal(): void {
    this.showYoutubeModal = true;
  }

  closeYoutubeModal(): void {
    this.showYoutubeModal = false;
  }

  // Method to close the modal
  closeUploadAudioModal(): void {
    this.showModal = false;
  }

  loadPlaylist(id: string): void {
    this.playlistBeatStreamService.getPlaylist(id).subscribe(newPlaylist => {
      this.playlist = newPlaylist.body;
    });
  }

  deletePlaylist(id: string): void {
    this.playlistBeatStreamService.deletePlaylist(id).subscribe(newPlaylist => {
      this.showConfirmation = false;
      window.location.reload();
    });
  }

  uploadFile(file: File, droppedFile: NgxFileDropEntry): void {
    const formData = new FormData();

    formData.append('title', file.name);

    formData.append('audioFile', file);
    formData.append('playlistId', this.playlist.id);

    this.trackBeatStreamService.uploadSongFile(formData).subscribe(track => {
      this.loadPlaylist(this.playlist.id);
      this.removeFile(droppedFile);
    });
  }

  removeFile(file: NgxFileDropEntry): void {
    const index = this.files.findIndex(f => f.relativePath === file.relativePath);
    if (index !== -1) {
      this.files[index].relativePath = '---[UPLOADED]--- ' + String(this.files[index].relativePath);
      this.startTimer(0.07, index);
    }
  }

  startTimer(minutes: number, index: number): void {
    const milliseconds = minutes * 60 * 1000; // Convert minutes to milliseconds
    timer(milliseconds).subscribe(() => {
      this.executeAction(index);
    });
  }

  executeAction(index: number): void {
    // Place your action or function logic here
    this.files.splice(index, 1);
  }

  onCancel(): void {
    this.showModal = false;
  }

  public dropped(files: NgxFileDropEntry[]): void {
    this.files = files;
    for (const droppedFile of files) {
      // Is it a file?
      if (droppedFile.fileEntry.isFile) {
        const fileEntry = droppedFile.fileEntry as FileSystemFileEntry;
        fileEntry.file((file: File) => {
          this.uploadFile(file, droppedFile);
          /**
           // You could upload it like this:
           const formData = new FormData()
           formData.append('logo', file, relativePath)

           // Headers
           const headers = new HttpHeaders({
           'security-token': 'mytoken'
           })

           this.http.post('https://mybackend.com/api/upload/sanitize-and-save-logo', formData, { headers: headers, responseType: 'blob' })
           .subscribe(data => {
           // Sanitized logo returned from backend
           })
           **/
        });
      } else {
        // It was a directory (empty directories are added, otherwise only files)
        const fileEntry = droppedFile.fileEntry as FileSystemDirectoryEntry;
      }
    }
  }

  onFilesDropped(files: NgxFileDropEntry[]): void {
    this.files = []; // Reset files
    for (const droppedFile of files) {
      if (droppedFile.fileEntry.isFile) {
        const fileEntry = droppedFile.fileEntry as FileSystemFileEntry;
        fileEntry.file((file: File) => {
          this.files.push(file);
          this.fileUploadProgress[file.name] = 0; // Initialize progress
        });
      }
    }
  }

  formatDuration(seconds: number): string {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;

    const formattedSeconds = remainingSeconds < 10 ? `0${remainingSeconds}` : remainingSeconds;

    return `${minutes}:${formattedSeconds}`;
  }

  extractYouTubeVideoId(url: string): string | null {
    const regExp = /(?:youtube\.com\/(?:[^/\n\s]+\/\S+\/|(?:v|e(?:mbed)?)\/|\S*?[?&]v=)|youtu\.be\/)([a-zA-Z0-9_-]{11})/;
    const match = url.match(regExp);
    return match ? match[1] : null;
  }

  submitYouTubeVideoUrl(): void {
    if (!this.youtubeVideoUrl) {
      return;
    }
    const videoType = this.isYouTubeOrSoundCloud(this.youtubeVideoUrl);
    let videoId;
    if (videoType === 'YouTube') {
      videoId = this.extractYouTubeVideoId(this.youtubeVideoUrl);
    }
    if (videoType === 'SoundCloud') {
      videoId = encodeURIComponent(this.youtubeVideoUrl);
    }

    if (videoId) {
      // encodeURIComponent(url);

      this.showYoutubeDownloadModal = true;
      this.startSimulation();
      this.trackBeatStreamService.downloadYoutubeVideo(videoId, videoType, this.playlist.id).subscribe((videoRes: any) => {
        this.enableStatus = false;
        this.currentStatus = videoRes.body.status;
        this.loadPlaylist(this.playlist.id);
        this.intervalId = setInterval(() => {
          this.showYoutubeDownloadModal = false;
          this.index = 0;
          this.currentStatus = '';
        }, 6000);
      });
      this.closeYoutubeModal();
      //
    }
  }
  isYouTubeOrSoundCloud(url: string): string {
    const youtubeRegex = /^(https?:\/\/)?(www\.)?(youtube\.com|youtu\.?be)\/.+$/;
    const soundcloudRegex = /^(https?:\/\/)?(www\.)?soundcloud\.com\/.+$/;

    if (youtubeRegex.test(url)) {
      return 'YouTube';
    } else if (soundcloudRegex.test(url)) {
      return 'SoundCloud';
    } else {
      return 'Invalid URL';
    }
  }

  startSimulation(): void {
    this.currentStatus = this.statusList[this.index];
    this.currentColor = this.getColor(this.index);
    this.enableStatus = true;
    this.intervalId = setInterval(() => {
      if (!this.enableStatus) {
        return;
      }
      this.index = (this.index + 1) % this.statusList.length;
      this.currentStatus = this.statusList[this.index];
      this.currentColor = this.getColor(this.index);

      // Stop simulation when the last status is shown
      if (this.index === this.statusList.length - 1) {
        clearInterval(this.intervalId);
      }
    }, 3000); // Change text every second
  }

  getColor(index: number): string {
    const colors = ['#3498db', '#e67e22', '#2ecc71', '#9b59b6', '#f1c40f'];
    return colors[index];
  }

  openDeleteConfirmation(): void {
    this.showConfirmation = true;
  }

  // Confirm delete action
  confirmDelete(): void {
    // Perform the delete action here
    this.deletePlaylist(this.playlist.id);
  }

  // Close the confirmation popup
  closeDeleteConfirmation(): void {
    this.showConfirmation = false;
  }
}
