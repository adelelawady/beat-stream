import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { ReferanceDownloadTaskDetailComponent } from './referance-download-task-detail.component';

describe('ReferanceDownloadTask Management Detail Component', () => {
  let comp: ReferanceDownloadTaskDetailComponent;
  let fixture: ComponentFixture<ReferanceDownloadTaskDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReferanceDownloadTaskDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./referance-download-task-detail.component').then(m => m.ReferanceDownloadTaskDetailComponent),
              resolve: { referanceDownloadTask: () => of({ id: 'ABC' }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ReferanceDownloadTaskDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReferanceDownloadTaskDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load referanceDownloadTask on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ReferanceDownloadTaskDetailComponent);

      // THEN
      expect(instance.referanceDownloadTask()).toEqual(expect.objectContaining({ id: 'ABC' }));
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
