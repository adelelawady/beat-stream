import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IArtistBeatStream } from 'app/entities/artist-beat-stream/artist-beat-stream.model';
import { ArtistBeatStreamService } from 'app/entities/artist-beat-stream/service/artist-beat-stream.service';
import { IGenreBeatStream } from 'app/entities/genre-beat-stream/genre-beat-stream.model';
import { GenreBeatStreamService } from 'app/entities/genre-beat-stream/service/genre-beat-stream.service';
import { IAlbumBeatStream } from '../album-beat-stream.model';
import { AlbumBeatStreamService } from '../service/album-beat-stream.service';
import { AlbumBeatStreamFormService } from './album-beat-stream-form.service';

import { AlbumBeatStreamUpdateComponent } from './album-beat-stream-update.component';

describe('AlbumBeatStream Management Update Component', () => {
  let comp: AlbumBeatStreamUpdateComponent;
  let fixture: ComponentFixture<AlbumBeatStreamUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let albumFormService: AlbumBeatStreamFormService;
  let albumService: AlbumBeatStreamService;
  let artistService: ArtistBeatStreamService;
  let genreService: GenreBeatStreamService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AlbumBeatStreamUpdateComponent],
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
      .overrideTemplate(AlbumBeatStreamUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AlbumBeatStreamUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    albumFormService = TestBed.inject(AlbumBeatStreamFormService);
    albumService = TestBed.inject(AlbumBeatStreamService);
    artistService = TestBed.inject(ArtistBeatStreamService);
    genreService = TestBed.inject(GenreBeatStreamService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call ArtistBeatStream query and add missing value', () => {
      const album: IAlbumBeatStream = { id: 'CBA' };
      const artist: IArtistBeatStream = { id: 'e97a3b88-9c4d-4a79-9a51-a84d48afea16' };
      album.artist = artist;

      const artistCollection: IArtistBeatStream[] = [{ id: 'a4f6bbaa-ad61-45ab-9962-ad5cbbf9bbdf' }];
      jest.spyOn(artistService, 'query').mockReturnValue(of(new HttpResponse({ body: artistCollection })));
      const additionalArtistBeatStreams = [artist];
      const expectedCollection: IArtistBeatStream[] = [...additionalArtistBeatStreams, ...artistCollection];
      jest.spyOn(artistService, 'addArtistBeatStreamToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ album });
      comp.ngOnInit();

      expect(artistService.query).toHaveBeenCalled();
      expect(artistService.addArtistBeatStreamToCollectionIfMissing).toHaveBeenCalledWith(
        artistCollection,
        ...additionalArtistBeatStreams.map(expect.objectContaining),
      );
      expect(comp.artistsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call GenreBeatStream query and add missing value', () => {
      const album: IAlbumBeatStream = { id: 'CBA' };
      const genres: IGenreBeatStream[] = [{ id: '571cddb9-fe59-4acf-a7a0-008564bd29f9' }];
      album.genres = genres;

      const genreCollection: IGenreBeatStream[] = [{ id: '159ebc29-53d3-48ad-a589-6509df9b551d' }];
      jest.spyOn(genreService, 'query').mockReturnValue(of(new HttpResponse({ body: genreCollection })));
      const additionalGenreBeatStreams = [...genres];
      const expectedCollection: IGenreBeatStream[] = [...additionalGenreBeatStreams, ...genreCollection];
      jest.spyOn(genreService, 'addGenreBeatStreamToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ album });
      comp.ngOnInit();

      expect(genreService.query).toHaveBeenCalled();
      expect(genreService.addGenreBeatStreamToCollectionIfMissing).toHaveBeenCalledWith(
        genreCollection,
        ...additionalGenreBeatStreams.map(expect.objectContaining),
      );
      expect(comp.genresSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const album: IAlbumBeatStream = { id: 'CBA' };
      const artist: IArtistBeatStream = { id: '5cf2f844-d451-4ff8-a60e-965355d5bcfa' };
      album.artist = artist;
      const genre: IGenreBeatStream = { id: 'bd27b6bc-d05c-4078-93b1-18655adb6342' };
      album.genres = [genre];

      activatedRoute.data = of({ album });
      comp.ngOnInit();

      expect(comp.artistsSharedCollection).toContain(artist);
      expect(comp.genresSharedCollection).toContain(genre);
      expect(comp.album).toEqual(album);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAlbumBeatStream>>();
      const album = { id: 'ABC' };
      jest.spyOn(albumFormService, 'getAlbumBeatStream').mockReturnValue(album);
      jest.spyOn(albumService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ album });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: album }));
      saveSubject.complete();

      // THEN
      expect(albumFormService.getAlbumBeatStream).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(albumService.update).toHaveBeenCalledWith(expect.objectContaining(album));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAlbumBeatStream>>();
      const album = { id: 'ABC' };
      jest.spyOn(albumFormService, 'getAlbumBeatStream').mockReturnValue({ id: null });
      jest.spyOn(albumService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ album: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: album }));
      saveSubject.complete();

      // THEN
      expect(albumFormService.getAlbumBeatStream).toHaveBeenCalled();
      expect(albumService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAlbumBeatStream>>();
      const album = { id: 'ABC' };
      jest.spyOn(albumService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ album });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(albumService.update).toHaveBeenCalled();
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

    describe('compareGenreBeatStream', () => {
      it('Should forward to genreService', () => {
        const entity = { id: 'ABC' };
        const entity2 = { id: 'CBA' };
        jest.spyOn(genreService, 'compareGenreBeatStream');
        comp.compareGenreBeatStream(entity, entity2);
        expect(genreService.compareGenreBeatStream).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
