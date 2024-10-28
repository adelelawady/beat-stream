import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ITaskNode } from '../task-node.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../task-node.test-samples';

import { RestTaskNode, TaskNodeService } from './task-node.service';

const requireRestSample: RestTaskNode = {
  ...sampleWithRequiredData,
  scheduledStartTime: sampleWithRequiredData.scheduledStartTime?.toJSON(),
};

describe('TaskNode Service', () => {
  let service: TaskNodeService;
  let httpMock: HttpTestingController;
  let expectedResult: ITaskNode | ITaskNode[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TaskNodeService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a TaskNode', () => {
      const taskNode = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(taskNode).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TaskNode', () => {
      const taskNode = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(taskNode).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TaskNode', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TaskNode', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TaskNode', () => {
      const expected = true;

      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTaskNodeToCollectionIfMissing', () => {
      it('should add a TaskNode to an empty array', () => {
        const taskNode: ITaskNode = sampleWithRequiredData;
        expectedResult = service.addTaskNodeToCollectionIfMissing([], taskNode);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(taskNode);
      });

      it('should not add a TaskNode to an array that contains it', () => {
        const taskNode: ITaskNode = sampleWithRequiredData;
        const taskNodeCollection: ITaskNode[] = [
          {
            ...taskNode,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTaskNodeToCollectionIfMissing(taskNodeCollection, taskNode);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TaskNode to an array that doesn't contain it", () => {
        const taskNode: ITaskNode = sampleWithRequiredData;
        const taskNodeCollection: ITaskNode[] = [sampleWithPartialData];
        expectedResult = service.addTaskNodeToCollectionIfMissing(taskNodeCollection, taskNode);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(taskNode);
      });

      it('should add only unique TaskNode to an array', () => {
        const taskNodeArray: ITaskNode[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const taskNodeCollection: ITaskNode[] = [sampleWithRequiredData];
        expectedResult = service.addTaskNodeToCollectionIfMissing(taskNodeCollection, ...taskNodeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const taskNode: ITaskNode = sampleWithRequiredData;
        const taskNode2: ITaskNode = sampleWithPartialData;
        expectedResult = service.addTaskNodeToCollectionIfMissing([], taskNode, taskNode2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(taskNode);
        expect(expectedResult).toContain(taskNode2);
      });

      it('should accept null and undefined values', () => {
        const taskNode: ITaskNode = sampleWithRequiredData;
        expectedResult = service.addTaskNodeToCollectionIfMissing([], null, taskNode, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(taskNode);
      });

      it('should return initial array if no TaskNode is added', () => {
        const taskNodeCollection: ITaskNode[] = [sampleWithRequiredData];
        expectedResult = service.addTaskNodeToCollectionIfMissing(taskNodeCollection, undefined, null);
        expect(expectedResult).toEqual(taskNodeCollection);
      });
    });

    describe('compareTaskNode', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTaskNode(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = null;

        const compareResult1 = service.compareTaskNode(entity1, entity2);
        const compareResult2 = service.compareTaskNode(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'CBA' };

        const compareResult1 = service.compareTaskNode(entity1, entity2);
        const compareResult2 = service.compareTaskNode(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 'ABC' };
        const entity2 = { id: 'ABC' };

        const compareResult1 = service.compareTaskNode(entity1, entity2);
        const compareResult2 = service.compareTaskNode(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
