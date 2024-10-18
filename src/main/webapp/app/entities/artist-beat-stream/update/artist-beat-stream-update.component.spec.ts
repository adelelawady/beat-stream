import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ArtistBeatStreamService } from '../service/artist-beat-stream.service';
import { IArtistBeatStream } from '../artist-beat-stream.model';
import { ArtistBeatStreamFormService } from './artist-beat-stream-form.service';

import { ArtistBeatStreamUpdateComponent } from './artist-beat-stream-update.component';

describe('ArtistBeatStream Management Update Component', () => {
  let comp: ArtistBeatStreamUpdateComponent;
  let fixture: ComponentFixture<ArtistBeatStreamUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let artistFormService: ArtistBeatStreamFormService;
  let artistService: ArtistBeatStreamService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ArtistBeatStreamUpdateComponent],
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
      .overrideTemplate(ArtistBeatStreamUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ArtistBeatStreamUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    artistFormService = TestBed.inject(ArtistBeatStreamFormService);
    artistService = TestBed.inject(ArtistBeatStreamService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const artist: IArtistBeatStream = { id: 'CBA' };

      activatedRoute.data = of({ artist });
      comp.ngOnInit();

      expect(comp.artist).toEqual(artist);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IArtistBeatStream>>();
      const artist = { id: 'ABC' };
      jest.spyOn(artistFormService, 'getArtistBeatStream').mockReturnValue(artist);
      jest.spyOn(artistService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ artist });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: artist }));
      saveSubject.complete();

      // THEN
      expect(artistFormService.getArtistBeatStream).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(artistService.update).toHaveBeenCalledWith(expect.objectContaining(artist));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IArtistBeatStream>>();
      const artist = { id: 'ABC' };
      jest.spyOn(artistFormService, 'getArtistBeatStream').mockReturnValue({ id: null });
      jest.spyOn(artistService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ artist: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: artist }));
      saveSubject.complete();

      // THEN
      expect(artistFormService.getArtistBeatStream).toHaveBeenCalled();
      expect(artistService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IArtistBeatStream>>();
      const artist = { id: 'ABC' };
      jest.spyOn(artistService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ artist });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(artistService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
