import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { BeatStreamFileBeatStreamService } from '../service/beat-stream-file-beat-stream.service';
import { IBeatStreamFileBeatStream } from '../beat-stream-file-beat-stream.model';
import { BeatStreamFileBeatStreamFormService } from './beat-stream-file-beat-stream-form.service';

import { BeatStreamFileBeatStreamUpdateComponent } from './beat-stream-file-beat-stream-update.component';

describe('BeatStreamFileBeatStream Management Update Component', () => {
  let comp: BeatStreamFileBeatStreamUpdateComponent;
  let fixture: ComponentFixture<BeatStreamFileBeatStreamUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let beatStreamFileFormService: BeatStreamFileBeatStreamFormService;
  let beatStreamFileService: BeatStreamFileBeatStreamService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [BeatStreamFileBeatStreamUpdateComponent],
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
      .overrideTemplate(BeatStreamFileBeatStreamUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BeatStreamFileBeatStreamUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    beatStreamFileFormService = TestBed.inject(BeatStreamFileBeatStreamFormService);
    beatStreamFileService = TestBed.inject(BeatStreamFileBeatStreamService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const beatStreamFile: IBeatStreamFileBeatStream = { id: 'CBA' };

      activatedRoute.data = of({ beatStreamFile });
      comp.ngOnInit();

      expect(comp.beatStreamFile).toEqual(beatStreamFile);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBeatStreamFileBeatStream>>();
      const beatStreamFile = { id: 'ABC' };
      jest.spyOn(beatStreamFileFormService, 'getBeatStreamFileBeatStream').mockReturnValue(beatStreamFile);
      jest.spyOn(beatStreamFileService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ beatStreamFile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: beatStreamFile }));
      saveSubject.complete();

      // THEN
      expect(beatStreamFileFormService.getBeatStreamFileBeatStream).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(beatStreamFileService.update).toHaveBeenCalledWith(expect.objectContaining(beatStreamFile));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBeatStreamFileBeatStream>>();
      const beatStreamFile = { id: 'ABC' };
      jest.spyOn(beatStreamFileFormService, 'getBeatStreamFileBeatStream').mockReturnValue({ id: null });
      jest.spyOn(beatStreamFileService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ beatStreamFile: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: beatStreamFile }));
      saveSubject.complete();

      // THEN
      expect(beatStreamFileFormService.getBeatStreamFileBeatStream).toHaveBeenCalled();
      expect(beatStreamFileService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBeatStreamFileBeatStream>>();
      const beatStreamFile = { id: 'ABC' };
      jest.spyOn(beatStreamFileService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ beatStreamFile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(beatStreamFileService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
