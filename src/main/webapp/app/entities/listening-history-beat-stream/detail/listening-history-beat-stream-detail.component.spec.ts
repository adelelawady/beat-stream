import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { ListeningHistoryBeatStreamDetailComponent } from './listening-history-beat-stream-detail.component';

describe('ListeningHistoryBeatStream Management Detail Component', () => {
  let comp: ListeningHistoryBeatStreamDetailComponent;
  let fixture: ComponentFixture<ListeningHistoryBeatStreamDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListeningHistoryBeatStreamDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () =>
                import('./listening-history-beat-stream-detail.component').then(m => m.ListeningHistoryBeatStreamDetailComponent),
              resolve: { listeningHistory: () => of({ id: 'ABC' }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ListeningHistoryBeatStreamDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ListeningHistoryBeatStreamDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load listeningHistory on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ListeningHistoryBeatStreamDetailComponent);

      // THEN
      expect(instance.listeningHistory()).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });

  describe('PreviousState', () => {
    it('Should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
