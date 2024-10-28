import { Component, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
// eslint-disable
import SharedModule from 'app/shared/shared.module';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { TrackBeatStreamService } from 'app/entities/track-beat-stream/service/track-beat-stream.service';
import { PlaylistBeatStreamService } from 'app/entities/playlist-beat-stream/service/playlist-beat-stream.service';
import { PlaylistComponentComponent } from 'app/playlist-component/playlist-component.component';
import { ToastService } from 'app/toast/toast.service';
import { TaskNodeDownloadService } from 'app/shared/service/TaskNodeDownload.service';
// prettier-ignore
@Component({
  standalone: true,
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  imports: [PlaylistComponentComponent, SharedModule, RouterModule],
})
export default class HomeComponent implements OnInit, OnDestroy {
  showModal = false;  // Track whether the modal is visible
  title = '';
  account = signal<Account | null>(null);
  isCollapsed = false;
  playlists:any[]=[];
  selectedPlaylist={};
  taskList:any=[];
  private readonly destroy$ = new Subject<void>();

  private playlistBeatStreamService = inject(PlaylistBeatStreamService)
  private trackService = inject(TrackBeatStreamService);
  private accountService = inject(AccountService);
  private router = inject(Router);
  private ToastService = inject(ToastService)
  private taskListSerivce=inject(TaskNodeDownloadService);

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => {this.account.set(account); this.loadPlaylists();});
    this.taskListSerivce.TaskList$.subscribe(_taskList=>{
      this.taskList=JSON.parse(_taskList);
    });

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

  createPlaylist():void{


    const createPlayListModel={
      "title": this.title,
      "desc": "description",
    };

    this.playlistBeatStreamService.createPlayList(createPlayListModel).subscribe(playlist=>{
     // this.selectedPlaylist=playlist;
      this.loadPlaylists();

    });
  }
  uploadTrack():void{

     // this.trackService.uploadTrack()

      // uploadTrack
  }
  // Method to handle submission
  submitTitle(): void {
    this.createPlaylist();
    this.closeModal(); // Close the modal after submission

  }
  loadPlaylists():void{
    this.playlistBeatStreamService.getAllPlaylists().subscribe(playlists=>{
      this.playlists=playlists.body;
      if (this.playlists.length>0){


          this.selectedPlaylist=this.playlists[0];

      }
    });
  }
}
