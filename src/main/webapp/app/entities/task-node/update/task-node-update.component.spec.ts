import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { TaskNodeService } from '../service/task-node.service';
import { ITaskNode } from '../task-node.model';
import { TaskNodeFormService } from './task-node-form.service';

import { TaskNodeUpdateComponent } from './task-node-update.component';

describe('TaskNode Management Update Component', () => {
  let comp: TaskNodeUpdateComponent;
  let fixture: ComponentFixture<TaskNodeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let taskNodeFormService: TaskNodeFormService;
  let taskNodeService: TaskNodeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TaskNodeUpdateComponent],
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
      .overrideTemplate(TaskNodeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TaskNodeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    taskNodeFormService = TestBed.inject(TaskNodeFormService);
    taskNodeService = TestBed.inject(TaskNodeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call TaskNode query and add missing value', () => {
      const taskNode: ITaskNode = { id: 'CBA' };
      const parentTask: ITaskNode = { id: '623eb78e-d815-4a4c-b4fd-0de7453d4139' };
      taskNode.parentTask = parentTask;

      const taskNodeCollection: ITaskNode[] = [{ id: '950cea9b-e318-4899-8e18-8b19579b69b4' }];
      jest.spyOn(taskNodeService, 'query').mockReturnValue(of(new HttpResponse({ body: taskNodeCollection })));
      const additionalTaskNodes = [parentTask];
      const expectedCollection: ITaskNode[] = [...additionalTaskNodes, ...taskNodeCollection];
      jest.spyOn(taskNodeService, 'addTaskNodeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ taskNode });
      comp.ngOnInit();

      expect(taskNodeService.query).toHaveBeenCalled();
      expect(taskNodeService.addTaskNodeToCollectionIfMissing).toHaveBeenCalledWith(
        taskNodeCollection,
        ...additionalTaskNodes.map(expect.objectContaining),
      );
      expect(comp.taskNodesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const taskNode: ITaskNode = { id: 'CBA' };
      const parentTask: ITaskNode = { id: '451c651f-c8c8-4117-8d6b-7893c3e7c9ba' };
      taskNode.parentTask = parentTask;

      activatedRoute.data = of({ taskNode });
      comp.ngOnInit();

      expect(comp.taskNodesSharedCollection).toContain(parentTask);
      expect(comp.taskNode).toEqual(taskNode);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITaskNode>>();
      const taskNode = { id: 'ABC' };
      jest.spyOn(taskNodeFormService, 'getTaskNode').mockReturnValue(taskNode);
      jest.spyOn(taskNodeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ taskNode });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: taskNode }));
      saveSubject.complete();

      // THEN
      expect(taskNodeFormService.getTaskNode).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(taskNodeService.update).toHaveBeenCalledWith(expect.objectContaining(taskNode));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITaskNode>>();
      const taskNode = { id: 'ABC' };
      jest.spyOn(taskNodeFormService, 'getTaskNode').mockReturnValue({ id: null });
      jest.spyOn(taskNodeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ taskNode: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: taskNode }));
      saveSubject.complete();

      // THEN
      expect(taskNodeFormService.getTaskNode).toHaveBeenCalled();
      expect(taskNodeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITaskNode>>();
      const taskNode = { id: 'ABC' };
      jest.spyOn(taskNodeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ taskNode });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(taskNodeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTaskNode', () => {
      it('Should forward to taskNodeService', () => {
        const entity = { id: 'ABC' };
        const entity2 = { id: 'CBA' };
        jest.spyOn(taskNodeService, 'compareTaskNode');
        comp.compareTaskNode(entity, entity2);
        expect(taskNodeService.compareTaskNode).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
