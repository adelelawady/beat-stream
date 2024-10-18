import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ITrackBeatStream } from 'app/entities/track-beat-stream/track-beat-stream.model';
import { TrackBeatStreamService } from 'app/entities/track-beat-stream/service/track-beat-stream.service';
import { ListeningHistoryBeatStreamService } from '../service/listening-history-beat-stream.service';
import { IListeningHistoryBeatStream } from '../listening-history-beat-stream.model';
import { ListeningHistoryBeatStreamFormService } from './listening-history-beat-stream-form.service';

import { ListeningHistoryBeatStreamUpdateComponent } from './listening-history-beat-stream-update.component';

describe('ListeningHistoryBeatStream Management Update Component', () => {
  let comp: ListeningHistoryBeatStreamUpdateComponent;
  let fixture: ComponentFixture<ListeningHistoryBeatStreamUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let listeningHistoryFormService: ListeningHistoryBeatStreamFormService;
  let listeningHistoryService: ListeningHistoryBeatStreamService;
  let trackService: TrackBeatStreamService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ListeningHistoryBeatStreamUpdateComponent],
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
      .overrideTemplate(ListeningHistoryBeatStreamUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ListeningHistoryBeatStreamUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    listeningHistoryFormService = TestBed.inject(ListeningHistoryBeatStreamFormService);
    listeningHistoryService = TestBed.inject(ListeningHistoryBeatStreamService);
    trackService = TestBed.inject(TrackBeatStreamService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call TrackBeatStream query and add missing value', () => {
      const listeningHistory: IListeningHistoryBeatStream = { id: 'CBA' };
      const track: ITrackBeatStream = { id: 'c7298a34-845d-4b9e-8210-205d6e117c99' };
      listeningHistory.track = track;

      const trackCollection: ITrackBeatStream[] = [{ id: '0f5914ba-1523-4df3-8a67-97bf620180fb' }];
      jest.spyOn(trackService, 'query').mockReturnValue(of(new HttpResponse({ body: trackCollection })));
      const additionalTrackBeatStreams = [track];
      const expectedCollection: ITrackBeatStream[] = [...additionalTrackBeatStreams, ...trackCollection];
      jest.spyOn(trackService, 'addTrackBeatStreamToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ listeningHistory });
      comp.ngOnInit();

      expect(trackService.query).toHaveBeenCalled();
      expect(trackService.addTrackBeatStreamToCollectionIfMissing).toHaveBeenCalledWith(
        trackCollection,
        ...additionalTrackBeatStreams.map(expect.objectContaining),
      );
      expect(comp.tracksSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const listeningHistory: IListeningHistoryBeatStream = { id: 'CBA' };
      const track: ITrackBeatStream = { id: '3535eb12-27a0-4aa2-a37b-47eefa0c81b4' };
      listeningHistory.track = track;

      activatedRoute.data = of({ listeningHistory });
      comp.ngOnInit();

      expect(comp.tracksSharedCollection).toContain(track);
      expect(comp.listeningHistory).toEqual(listeningHistory);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IListeningHistoryBeatStream>>();
      const listeningHistory = { id: 'ABC' };
      jest.spyOn(listeningHistoryFormService, 'getListeningHistoryBeatStream').mockReturnValue(listeningHistory);
      jest.spyOn(listeningHistoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ listeningHistory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: listeningHistory }));
      saveSubject.complete();

      // THEN
      expect(listeningHistoryFormService.getListeningHistoryBeatStream).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(listeningHistoryService.update).toHaveBeenCalledWith(expect.objectContaining(listeningHistory));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IListeningHistoryBeatStream>>();
      const listeningHistory = { id: 'ABC' };
      jest.spyOn(listeningHistoryFormService, 'getListeningHistoryBeatStream').mockReturnValue({ id: null });
      jest.spyOn(listeningHistoryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ listeningHistory: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: listeningHistory }));
      saveSubject.complete();

      // THEN
      expect(listeningHistoryFormService.getListeningHistoryBeatStream).toHaveBeenCalled();
      expect(listeningHistoryService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IListeningHistoryBeatStream>>();
      const listeningHistory = { id: 'ABC' };
      jest.spyOn(listeningHistoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ listeningHistory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(listeningHistoryService.update).toHaveBeenCalled();
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
