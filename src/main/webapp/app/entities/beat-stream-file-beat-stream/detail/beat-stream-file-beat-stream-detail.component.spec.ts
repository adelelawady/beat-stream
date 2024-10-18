import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { BeatStreamFileBeatStreamDetailComponent } from './beat-stream-file-beat-stream-detail.component';

describe('BeatStreamFileBeatStream Management Detail Component', () => {
  let comp: BeatStreamFileBeatStreamDetailComponent;
  let fixture: ComponentFixture<BeatStreamFileBeatStreamDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BeatStreamFileBeatStreamDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () =>
                import('./beat-stream-file-beat-stream-detail.component').then(m => m.BeatStreamFileBeatStreamDetailComponent),
              resolve: { beatStreamFile: () => of({ id: 'ABC' }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(BeatStreamFileBeatStreamDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BeatStreamFileBeatStreamDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load beatStreamFile on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', BeatStreamFileBeatStreamDetailComponent);

      // THEN
      expect(instance.beatStreamFile()).toEqual(expect.objectContaining({ id: 'ABC' }));
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
