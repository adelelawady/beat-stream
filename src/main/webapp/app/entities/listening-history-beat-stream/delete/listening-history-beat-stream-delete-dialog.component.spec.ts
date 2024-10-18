jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, fakeAsync, inject, tick } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ListeningHistoryBeatStreamService } from '../service/listening-history-beat-stream.service';

import { ListeningHistoryBeatStreamDeleteDialogComponent } from './listening-history-beat-stream-delete-dialog.component';

describe('ListeningHistoryBeatStream Management Delete Component', () => {
  let comp: ListeningHistoryBeatStreamDeleteDialogComponent;
  let fixture: ComponentFixture<ListeningHistoryBeatStreamDeleteDialogComponent>;
  let service: ListeningHistoryBeatStreamService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ListeningHistoryBeatStreamDeleteDialogComponent],
      providers: [provideHttpClient(), NgbActiveModal],
    })
      .overrideTemplate(ListeningHistoryBeatStreamDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ListeningHistoryBeatStreamDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ListeningHistoryBeatStreamService);
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
