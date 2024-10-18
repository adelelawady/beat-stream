import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IArtistBeatStream } from 'app/entities/artist-beat-stream/artist-beat-stream.model';
import { ArtistBeatStreamService } from 'app/entities/artist-beat-stream/service/artist-beat-stream.service';
import { IAlbumBeatStream } from 'app/entities/album-beat-stream/album-beat-stream.model';
import { AlbumBeatStreamService } from 'app/entities/album-beat-stream/service/album-beat-stream.service';
import { ITrackBeatStream } from '../track-beat-stream.model';
import { TrackBeatStreamService } from '../service/track-beat-stream.service';
import { TrackBeatStreamFormService } from './track-beat-stream-form.service';

import { TrackBeatStreamUpdateComponent } from './track-beat-stream-update.component';

describe('TrackBeatStream Management Update Component', () => {
  let comp: TrackBeatStreamUpdateComponent;
  let fixture: ComponentFixture<TrackBeatStreamUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let trackFormService: TrackBeatStreamFormService;
  let trackService: TrackBeatStreamService;
  let artistService: ArtistBeatStreamService;
  let albumService: AlbumBeatStreamService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TrackBeatStreamUpdateComponent],
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
      .overrideTemplate(TrackBeatStreamUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TrackBeatStreamUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    trackFormService = TestBed.inject(TrackBeatStreamFormService);
    trackService = TestBed.inject(TrackBeatStreamService);
    artistService = TestBed.inject(ArtistBeatStreamService);
    albumService = TestBed.inject(AlbumBeatStreamService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call ArtistBeatStream query and add missing value', () => {
      const track: ITrackBeatStream = { id: 'CBA' };
      const artist: IArtistBeatStream = { id: 'a1ef8a1f-5984-454c-978d-ef0fc4fec4e4' };
      track.artist = artist;

      const artistCollection: IArtistBeatStream[] = [{ id: '8d54bc20-5306-4a6f-8364-24a00ae148ff' }];
      jest.spyOn(artistService, 'query').mockReturnValue(of(new HttpResponse({ body: artistCollection })));
      const additionalArtistBeatStreams = [artist];
      const expectedCollection: IArtistBeatStream[] = [...additionalArtistBeatStreams, ...artistCollection];
      jest.spyOn(artistService, 'addArtistBeatStreamToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ track });
      comp.ngOnInit();

      expect(artistService.query).toHaveBeenCalled();
      expect(artistService.addArtistBeatStreamToCollectionIfMissing).toHaveBeenCalledWith(
        artistCollection,
        ...additionalArtistBeatStreams.map(expect.objectContaining),
      );
      expect(comp.artistsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call AlbumBeatStream query and add missing value', () => {
      const track: ITrackBeatStream = { id: 'CBA' };
      const album: IAlbumBeatStream = { id: 'db59f9a6-07e6-4fcb-b37d-644d3e26db44' };
      track.album = album;

      const albumCollection: IAlbumBeatStream[] = [{ id: '1bae137c-f7bd-42e1-911c-332ceb5315d3' }];
      jest.spyOn(albumService, 'query').mockReturnValue(of(new HttpResponse({ body: albumCollection })));
      const additionalAlbumBeatStreams = [album];
      const expectedCollection: IAlbumBeatStream[] = [...additionalAlbumBeatStreams, ...albumCollection];
      jest.spyOn(albumService, 'addAlbumBeatStreamToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ track });
      comp.ngOnInit();

      expect(albumService.query).toHaveBeenCalled();
      expect(albumService.addAlbumBeatStreamToCollectionIfMissing).toHaveBeenCalledWith(
        albumCollection,
        ...additionalAlbumBeatStreams.map(expect.objectContaining),
      );
      expect(comp.albumsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const track: ITrackBeatStream = { id: 'CBA' };
      const artist: IArtistBeatStream = { id: 'c6c19b33-d2f9-486a-a94e-81d3508ae340' };
      track.artist = artist;
      const album: IAlbumBeatStream = { id: '5546c489-e5e0-489d-b74b-05de0839b1ec' };
      track.album = album;

      activatedRoute.data = of({ track });
      comp.ngOnInit();

      expect(comp.artistsSharedCollection).toContain(artist);
      expect(comp.albumsSharedCollection).toContain(album);
      expect(comp.track).toEqual(track);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackBeatStream>>();
      const track = { id: 'ABC' };
      jest.spyOn(trackFormService, 'getTrackBeatStream').mockReturnValue(track);
      jest.spyOn(trackService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ track });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: track }));
      saveSubject.complete();

      // THEN
      expect(trackFormService.getTrackBeatStream).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(trackService.update).toHaveBeenCalledWith(expect.objectContaining(track));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackBeatStream>>();
      const track = { id: 'ABC' };
      jest.spyOn(trackFormService, 'getTrackBeatStream').mockReturnValue({ id: null });
      jest.spyOn(trackService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ track: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: track }));
      saveSubject.complete();

      // THEN
      expect(trackFormService.getTrackBeatStream).toHaveBeenCalled();
      expect(trackService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackBeatStream>>();
      const track = { id: 'ABC' };
      jest.spyOn(trackService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ track });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(trackService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareArtistBeatStream', () => {
      it('Should forward to artistService', () => {
        const entity = { id: 'ABC' };
        const entity2 = { id: 'CBA' };
        jest.spyOn(artistService, 'compareArtistBeatStream');
        comp.compareArtistBeatStream(entity, entity2);
        expect(artistService.compareArtistBeatStream).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareAlbumBeatStream', () => {
      it('Should forward to albumService', () => {
        const entity = { id: 'ABC' };
        const entity2 = { id: 'CBA' };
        jest.spyOn(albumService, 'compareAlbumBeatStream');
        comp.compareAlbumBeatStream(entity, entity2);
        expect(albumService.compareAlbumBeatStream).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
