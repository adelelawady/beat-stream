import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ReferanceDownloadTaskService } from '../service/referance-download-task.service';
import { IReferanceDownloadTask } from '../referance-download-task.model';
import { ReferanceDownloadTaskFormService } from './referance-download-task-form.service';

import { ReferanceDownloadTaskUpdateComponent } from './referance-download-task-update.component';

describe('ReferanceDownloadTask Management Update Component', () => {
  let comp: ReferanceDownloadTaskUpdateComponent;
  let fixture: ComponentFixture<ReferanceDownloadTaskUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let referanceDownloadTaskFormService: ReferanceDownloadTaskFormService;
  let referanceDownloadTaskService: ReferanceDownloadTaskService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReferanceDownloadTaskUpdateComponent],
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
      .overrideTemplate(ReferanceDownloadTaskUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ReferanceDownloadTaskUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    referanceDownloadTaskFormService = TestBed.inject(ReferanceDownloadTaskFormService);
    referanceDownloadTaskService = TestBed.inject(ReferanceDownloadTaskService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const referanceDownloadTask: IReferanceDownloadTask = { id: 'CBA' };

      activatedRoute.data = of({ referanceDownloadTask });
      comp.ngOnInit();

      expect(comp.referanceDownloadTask).toEqual(referanceDownloadTask);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReferanceDownloadTask>>();
      const referanceDownloadTask = { id: 'ABC' };
      jest.spyOn(referanceDownloadTaskFormService, 'getReferanceDownloadTask').mockReturnValue(referanceDownloadTask);
      jest.spyOn(referanceDownloadTaskService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ referanceDownloadTask });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: referanceDownloadTask }));
      saveSubject.complete();

      // THEN
      expect(referanceDownloadTaskFormService.getReferanceDownloadTask).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(referanceDownloadTaskService.update).toHaveBeenCalledWith(expect.objectContaining(referanceDownloadTask));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReferanceDownloadTask>>();
      const referanceDownloadTask = { id: 'ABC' };
      jest.spyOn(referanceDownloadTaskFormService, 'getReferanceDownloadTask').mockReturnValue({ id: null });
      jest.spyOn(referanceDownloadTaskService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ referanceDownloadTask: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: referanceDownloadTask }));
      saveSubject.complete();

      // THEN
      expect(referanceDownloadTaskFormService.getReferanceDownloadTask).toHaveBeenCalled();
      expect(referanceDownloadTaskService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReferanceDownloadTask>>();
      const referanceDownloadTask = { id: 'ABC' };
      jest.spyOn(referanceDownloadTaskService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ referanceDownloadTask });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(referanceDownloadTaskService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
