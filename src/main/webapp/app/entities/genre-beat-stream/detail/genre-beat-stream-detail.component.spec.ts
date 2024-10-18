import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { GenreBeatStreamDetailComponent } from './genre-beat-stream-detail.component';

describe('GenreBeatStream Management Detail Component', () => {
  let comp: GenreBeatStreamDetailComponent;
  let fixture: ComponentFixture<GenreBeatStreamDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GenreBeatStreamDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./genre-beat-stream-detail.component').then(m => m.GenreBeatStreamDetailComponent),
              resolve: { genre: () => of({ id: 'ABC' }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(GenreBeatStreamDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GenreBeatStreamDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load genre on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', GenreBeatStreamDetailComponent);

      // THEN
      expect(instance.genre()).toEqual(expect.objectContaining({ id: 'ABC' }));
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
