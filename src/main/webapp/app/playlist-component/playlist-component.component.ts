/* eslint-disable prettier/prettier */
/* eslint-disable */

import { AfterViewInit, Component, ElementRef, inject, Input, OnChanges, SimpleChanges, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { PlaylistBeatStreamService } from 'app/entities/playlist-beat-stream/service/playlist-beat-stream.service';
import { HttpClient } from '@angular/common/http';
import { TrackBeatStreamService } from 'app/entities/track-beat-stream/service/track-beat-stream.service';
import { CommonModule } from '@angular/common';
import { PlyrComponent, PlyrModule } from 'ngx-plyr';
import FooterComponent from 'app/layouts/footer/footer.component';
import { AudioService } from 'app/shared/service/audio.service';
import { NgxFileDropModule } from 'ngx-file-drop';
import { NgxFileDropEntry, FileSystemFileEntry, FileSystemDirectoryEntry } from 'ngx-file-drop';
import { timer } from 'rxjs';

@Component({
  selector: 'jhi-playlist-component',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, NgxFileDropModule],
  templateUrl: './playlist-component.component.html',
  styleUrl: './playlist-component.component.scss',
})
export class PlaylistComponentComponent implements OnChanges {
  showModal = false; // Track whether the modal is visible
  @ViewChild(FooterComponent) PlayerComponent!: FooterComponent;
  @Input() playlist: any;
  trackBeatStreamService = inject(TrackBeatStreamService);
  uploadForm!: FormGroup;
  resourceAPIUrl = this.trackBeatStreamService.applicationConfigService.getEndpointFor('api/audio');

  files: any[] = [];
  fileUploadProgress: { [key: string]: number } = {}; // To track progress for each file
  uploadFinished: boolean = false;
  isUploading: boolean = false;
  uploadInProgress: boolean = false;

  playlistBeatStreamService = inject(PlaylistBeatStreamService);

  // @ts-ignore
  public files: NgxFileDropEntry[] = [];

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

  ngOnChanges(changes: SimpleChanges): void {
    const currentValue = changes['playlist'].currentValue;
    if (currentValue[0]) {
      this.loadPlaylist(currentValue[0].id);
    }
  }

  play(track: any): void {
    this.audioService.setAudioSource(track);

    // this.audioService.setAudioSource("http://localhost:4200/api/track/play/67157ca05b57112d41f7e8d0");
  }

  playAnyTrack(): void {
    if (this.playlist.tracks.length > 0) {
      const randomIndex = Math.floor(Math.random() * this.playlist.tracks.length);
      const randomTrack = this.playlist.tracks[randomIndex];
      this.play(randomTrack);
    }
    // this.audioService.setAudioSource("http://localhost:4200/api/track/play/67157ca05b57112d41f7e8d0");
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

  removeFile(file: NgxFileDropEntry) {
    const index = this.files.findIndex(f => f.relativePath === file.relativePath);
    if (index !== -1) {
      this.files[index].relativePath = '---[UPLOADED]--- ' + this.files[index].relativePath;
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

  onCancel() {
    this.showModal = false;
  }

  public dropped(files: NgxFileDropEntry[]) {
    this.files = files;
    for (const droppedFile of files) {
      // Is it a file?
      if (droppedFile.fileEntry.isFile) {
        const fileEntry = droppedFile.fileEntry as FileSystemFileEntry;
        fileEntry.file((file: File) => {
          // Here you can access the real file
          console.log(droppedFile.relativePath, file);
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
        console.log(droppedFile.relativePath, fileEntry);
      }
    }
  }

  public fileOver(event: any) {
    console.log(event);
  }

  public fileLeave(event: any) {
    console.log(event);
  }

  onFilesDropped(files: NgxFileDropEntry[]) {
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

  onUpload() {
    this.isUploading = true;
    this.uploadFinished = false;
    this.uploadInProgress = true;

    // @ts-ignore
    const uploadPromises = this.files.map(file => this.uploadFile(file, null));

    Promise.all(uploadPromises).then(() => {
      this.uploadFinished = true;
      this.uploadInProgress = false;
      this.isUploading = false;
    });
  }

  formatDuration(seconds: number): string {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;

    const formattedSeconds = remainingSeconds < 10 ? `0${remainingSeconds}` : remainingSeconds;

    return `${minutes}:${formattedSeconds}`;
  }
}
