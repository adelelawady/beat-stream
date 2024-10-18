import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IArtistBeatStream } from 'app/entities/artist-beat-stream/artist-beat-stream.model';
import { ArtistBeatStreamService } from 'app/entities/artist-beat-stream/service/artist-beat-stream.service';
import { GenreBeatStreamService } from '../service/genre-beat-stream.service';
import { IGenreBeatStream } from '../genre-beat-stream.model';
import { GenreBeatStreamFormService } from './genre-beat-stream-form.service';

import { GenreBeatStreamUpdateComponent } from './genre-beat-stream-update.component';

describe('GenreBeatStream Management Update Component', () => {
  let comp: GenreBeatStreamUpdateComponent;
  let fixture: ComponentFixture<GenreBeatStreamUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let genreFormService: GenreBeatStreamFormService;
  let genreService: GenreBeatStreamService;
  let artistService: ArtistBeatStreamService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [GenreBeatStreamUpdateComponent],
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
      .overrideTemplate(GenreBeatStreamUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GenreBeatStreamUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    genreFormService = TestBed.inject(GenreBeatStreamFormService);
    genreService = TestBed.inject(GenreBeatStreamService);
    artistService = TestBed.inject(ArtistBeatStreamService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call ArtistBeatStream query and add missing value', () => {
      const genre: IGenreBeatStream = { id: 'CBA' };
      const artists: IArtistBeatStream[] = [{ id: '5b78a32c-ecc3-4032-86e3-beb879e5b712' }];
      genre.artists = artists;

      const artistCollection: IArtistBeatStream[] = [{ id: '35d5f998-b9e7-46ee-8546-ab557404ce1f' }];
      jest.spyOn(artistService, 'query').mockReturnValue(of(new HttpResponse({ body: artistCollection })));
      const additionalArtistBeatStreams = [...artists];
      const expectedCollection: IArtistBeatStream[] = [...additionalArtistBeatStreams, ...artistCollection];
      jest.spyOn(artistService, 'addArtistBeatStreamToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ genre });
      comp.ngOnInit();

      expect(artistService.query).toHaveBeenCalled();
      expect(artistService.addArtistBeatStreamToCollectionIfMissing).toHaveBeenCalledWith(
        artistCollection,
        ...additionalArtistBeatStreams.map(expect.objectContaining),
      );
      expect(comp.artistsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const genre: IGenreBeatStream = { id: 'CBA' };
      const artist: IArtistBeatStream = { id: '57f41bf8-ef74-48d7-bb5d-81fab32ca696' };
      genre.artists = [artist];

      activatedRoute.data = of({ genre });
      comp.ngOnInit();

      expect(comp.artistsSharedCollection).toContain(artist);
      expect(comp.genre).toEqual(genre);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGenreBeatStream>>();
      const genre = { id: 'ABC' };
      jest.spyOn(genreFormService, 'getGenreBeatStream').mockReturnValue(genre);
      jest.spyOn(genreService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ genre });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: genre }));
      saveSubject.complete();

      // THEN
      expect(genreFormService.getGenreBeatStream).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(genreService.update).toHaveBeenCalledWith(expect.objectContaining(genre));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGenreBeatStream>>();
      const genre = { id: 'ABC' };
      jest.spyOn(genreFormService, 'getGenreBeatStream').mockReturnValue({ id: null });
      jest.spyOn(genreService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ genre: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: genre }));
      saveSubject.complete();

      // THEN
      expect(genreFormService.getGenreBeatStream).toHaveBeenCalled();
      expect(genreService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGenreBeatStream>>();
      const genre = { id: 'ABC' };
      jest.spyOn(genreService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ genre });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(genreService.update).toHaveBeenCalled();
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
  });
});
