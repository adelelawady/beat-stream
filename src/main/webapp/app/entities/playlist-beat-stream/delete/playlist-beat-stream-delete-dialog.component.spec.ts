jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, fakeAsync, inject, tick } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { PlaylistBeatStreamService } from '../service/playlist-beat-stream.service';

import { PlaylistBeatStreamDeleteDialogComponent } from './playlist-beat-stream-delete-dialog.component';

describe('PlaylistBeatStream Management Delete Component', () => {
  let comp: PlaylistBeatStreamDeleteDialogComponent;
  let fixture: ComponentFixture<PlaylistBeatStreamDeleteDialogComponent>;
  let service: PlaylistBeatStreamService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [PlaylistBeatStreamDeleteDialogComponent],
      providers: [provideHttpClient(), NgbActiveModal],
    })
      .overrideTemplate(PlaylistBeatStreamDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PlaylistBeatStreamDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(PlaylistBeatStreamService);
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
