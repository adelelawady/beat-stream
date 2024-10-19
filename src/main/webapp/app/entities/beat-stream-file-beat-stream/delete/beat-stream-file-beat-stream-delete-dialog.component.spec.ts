jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, fakeAsync, inject, tick } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { BeatStreamFileBeatStreamService } from '../service/beat-stream-file-beat-stream.service';

import { BeatStreamFileBeatStreamDeleteDialogComponent } from './beat-stream-file-beat-stream-delete-dialog.component';

describe('BeatStreamFileBeatStream Management Delete Component', () => {
  let comp: BeatStreamFileBeatStreamDeleteDialogComponent;
  let fixture: ComponentFixture<BeatStreamFileBeatStreamDeleteDialogComponent>;
  let service: BeatStreamFileBeatStreamService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [BeatStreamFileBeatStreamDeleteDialogComponent],
      providers: [provideHttpClient(), NgbActiveModal],
    })
      .overrideTemplate(BeatStreamFileBeatStreamDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(BeatStreamFileBeatStreamDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(BeatStreamFileBeatStreamService);
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