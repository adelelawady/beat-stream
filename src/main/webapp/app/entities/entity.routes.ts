import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'beatStreamApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'album-beat-stream',
    data: { pageTitle: 'beatStreamApp.album.home.title' },
    loadChildren: () => import('./album-beat-stream/album-beat-stream.routes'),
  },
  {
    path: 'artist-beat-stream',
    data: { pageTitle: 'beatStreamApp.artist.home.title' },
    loadChildren: () => import('./artist-beat-stream/artist-beat-stream.routes'),
  },
  {
    path: 'beat-stream-file-beat-stream',
    data: { pageTitle: 'beatStreamApp.beatStreamFile.home.title' },
    loadChildren: () => import('./beat-stream-file-beat-stream/beat-stream-file-beat-stream.routes'),
  },
  {
    path: 'genre-beat-stream',
    data: { pageTitle: 'beatStreamApp.genre.home.title' },
    loadChildren: () => import('./genre-beat-stream/genre-beat-stream.routes'),
  },
  {
    path: 'listening-history-beat-stream',
    data: { pageTitle: 'beatStreamApp.listeningHistory.home.title' },
    loadChildren: () => import('./listening-history-beat-stream/listening-history-beat-stream.routes'),
  },
  {
    path: 'playlist-beat-stream',
    data: { pageTitle: 'beatStreamApp.playlist.home.title' },
    loadChildren: () => import('./playlist-beat-stream/playlist-beat-stream.routes'),
  },
  {
    path: 'track-beat-stream',
    data: { pageTitle: 'beatStreamApp.track.home.title' },
    loadChildren: () => import('./track-beat-stream/track-beat-stream.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
