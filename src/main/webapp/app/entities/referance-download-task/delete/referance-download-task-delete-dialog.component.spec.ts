jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, fakeAsync, inject, tick } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ReferanceDownloadTaskService } from '../service/referance-download-task.service';

import { ReferanceDownloadTaskDeleteDialogComponent } from './referance-download-task-delete-dialog.component';

describe('ReferanceDownloadTask Management Delete Component', () => {
  let comp: ReferanceDownloadTaskDeleteDialogComponent;
  let fixture: ComponentFixture<ReferanceDownloadTaskDeleteDialogComponent>;
  let service: ReferanceDownloadTaskService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReferanceDownloadTaskDeleteDialogComponent],
      providers: [provideHttpClient(), NgbActiveModal],
    })
      .overrideTemplate(ReferanceDownloadTaskDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ReferanceDownloadTaskDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ReferanceDownloadTaskService);
    mockActiveModal = TestBed.inject(NgbActiveModal);
  });

  describe('confirmDelete', () => {
    it('Should call delete service on confirmDelete', inject(
      [],
      fakeAsync(() => {
        // GIVEN
        jest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({ body: {} })));

        // WHEN
        comp.confirmDelete('ABC');
        tick();

        // THEN
        expect(service.delete).toHaveBeenCalledWith('ABC');
        expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
      }),
    ));

    it('Should not call delete service on clear', () => {
      // GIVEN
      jest.spyOn(service, 'delete');

      // WHEN
      comp.cancel();

      // THEN
      expect(service.delete).not.toHaveBeenCalled();
      expect(mockActiveModal.close).not.toHaveBeenCalled();
      expect(mockActiveModal.dismiss).toHaveBeenCalled();
    });
  });
});
