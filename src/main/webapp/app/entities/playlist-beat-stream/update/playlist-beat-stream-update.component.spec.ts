import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ITrackBeatStream } from 'app/entities/track-beat-stream/track-beat-stream.model';
import { TrackBeatStreamService } from 'app/entities/track-beat-stream/service/track-beat-stream.service';
import { PlaylistBeatStreamService } from '../service/playlist-beat-stream.service';
import { IPlaylistBeatStream } from '../playlist-beat-stream.model';
import { PlaylistBeatStreamFormService } from './playlist-beat-stream-form.service';

import { PlaylistBeatStreamUpdateComponent } from './playlist-beat-stream-update.component';

describe('PlaylistBeatStream Management Update Component', () => {
  let comp: PlaylistBeatStreamUpdateComponent;
  let fixture: ComponentFixture<PlaylistBeatStreamUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let playlistFormService: PlaylistBeatStreamFormService;
  let playlistService: PlaylistBeatStreamService;
  let trackService: TrackBeatStreamService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [PlaylistBeatStreamUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(PlaylistBeatStreamUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PlaylistBeatStreamUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    playlistFormService = TestBed.inject(PlaylistBeatStreamFormService);
    playlistService = TestBed.inject(PlaylistBeatStreamService);
    trackService = TestBed.inject(TrackBeatStreamService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call TrackBeatStream query and add missing value', () => {
      const playlist: IPlaylistBeatStream = { id: 'CBA' };
      const tracks: ITrackBeatStream[] = [{ id: 'ff6ac779-af4f-4932-aee3-9bd09d3fc42d' }];
      playlist.tracks = tracks;

      const trackCollection: ITrackBeatStream[] = [{ id: '9c257921-8b1b-4b93-a014-ec5ddfe74d2b' }];
      jest.spyOn(trackService, 'query').mockReturnValue(of(new HttpResponse({ body: trackCollection })));
      const additionalTrackBeatStreams = [...tracks];
      const expectedCollection: ITrackBeatStream[] = [...additionalTrackBeatStreams, ...trackCollection];
      jest.spyOn(trackService, 'addTrackBeatStreamToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ playlist });
      comp.ngOnInit();

      expect(trackService.query).toHaveBeenCalled();
      expect(trackService.addTrackBeatStreamToCollectionIfMissing).toHaveBeenCalledWith(
        trackCollection,
        ...additionalTrackBeatStreams.map(expect.objectContaining),
      );
      expect(comp.tracksSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const playlist: IPlaylistBeatStream = { id: 'CBA' };
      const track: ITrackBeatStream = { id: '3389d556-5448-49f0-a702-056714df89a8' };
      playlist.tracks = [track];

      activatedRoute.data = of({ playlist });
      comp.ngOnInit();

      expect(comp.tracksSharedCollection).toContain(track);
      expect(comp.playlist).toEqual(playlist);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlaylistBeatStream>>();
      const playlist = { id: 'ABC' };
      jest.spyOn(playlistFormService, 'getPlaylistBeatStream').mockReturnValue(playlist);
      jest.spyOn(playlistService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ playlist });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: playlist }));
      saveSubject.complete();

      // THEN
      expect(playlistFormService.getPlaylistBeatStream).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(playlistService.update).toHaveBeenCalledWith(expect.objectContaining(playlist));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlaylistBeatStream>>();
      const playlist = { id: 'ABC' };
      jest.spyOn(playlistFormService, 'getPlaylistBeatStream').mockReturnValue({ id: null });
      jest.spyOn(playlistService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ playlist: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: playlist }));
      saveSubject.complete();

      // THEN
      expect(playlistFormService.getPlaylistBeatStream).toHaveBeenCalled();
      expect(playlistService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlaylistBeatStream>>();
      const playlist = { id: 'ABC' };
      jest.spyOn(playlistService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ playlist });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(playlistService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTrackBeatStream', () => {
      it('Should forward to trackService', () => {
        const entity = { id: 'ABC' };
        const entity2 = { id: 'CBA' };
        jest.spyOn(trackService, 'compareTrackBeatStream');
        comp.compareTrackBeatStream(entity, entity2);
        expect(trackService.compareTrackBeatStream).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
