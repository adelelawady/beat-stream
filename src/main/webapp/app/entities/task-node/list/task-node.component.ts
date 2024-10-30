/* eslint-disable */
import { Component, NgZone, OnDestroy, OnInit, inject } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { Observable, Subscription, combineLatest, filter, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { ItemCountComponent } from 'app/shared/pagination';
import { FormsModule } from '@angular/forms';

import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { ITaskNode } from '../task-node.model';
import { EntityArrayResponseType, TaskNodeService } from '../service/task-node.service';
import { TaskNodeDeleteDialogComponent } from '../delete/task-node-delete-dialog.component';
import { ToastService } from 'app/toast/toast.service';
import { DownloadStatus } from 'app/entities/enumerations/download-status.model';

@Component({
  standalone: true,
  selector: 'jhi-task-node',
  templateUrl: './task-node.component.html',
  styles: `
    .table-entities thead th .d-flex > * {
      font-size: 10px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      margin: auto 0;
    }
    .table th,
    .table td {
      padding: 0.5rem;
    }
    .modal-backdrop {
      z-index: 0;
    }
  `,
  imports: [
    RouterModule,
    FormsModule,
    SharedModule,
    SortDirective,
    SortByDirective,
    DurationPipe,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
    ItemCountComponent,
  ],
})
export class TaskNodeComponent implements OnInit, OnDestroy {
  subscription: Subscription | null = null;
  taskNodes?: ITaskNode[];
  isLoading = false;

  sortState = sortStateSignal({});

  itemsPerPage = ITEMS_PER_PAGE;
  totalItems = 0;
  page = 1;

  public router = inject(Router);
  protected taskNodeService = inject(TaskNodeService);
  protected activatedRoute = inject(ActivatedRoute);
  protected sortService = inject(SortService);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);
  private ToastService = inject(ToastService);
  trackId = (item: ITaskNode): string => this.taskNodeService.getTaskNodeIdentifier(item);
  intervalId: any; // To hold the interval ID
  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => this.load()),
      )
      .subscribe();

    this.ToastService.toastSubject.subscribe(() => {
      this.load();
    });
    // Start the interval when the component is initialized
    this.intervalId = setInterval(() => {
      this.load();
    }, 4000); // 3000 milliseconds = 3 seconds
  }

  delete(taskNode: ITaskNode): void {
    const modalRef = this.modalService.open(TaskNodeDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.taskNode = taskNode;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  load(): void {
    this.queryBackend().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }
  ngOnDestroy(): void {
    // Clear the interval when the component is destroyed to prevent memory leaks
    clearInterval(this.intervalId);
  }
  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(this.page, event);
  }

  navigateToPage(page: number): void {
    this.handleNavigation(page, this.sortState());
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page = +(page ?? 1);
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.taskNodes = dataFromBody;
  }

  protected fillComponentAttributesFromResponseBody(data: ITaskNode[] | null): ITaskNode[] {
    return data ?? [];
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    this.totalItems = Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER));
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    const { page } = this;

    this.isLoading = true;
    const pageToLoad: number = page;
    const queryObject: any = {
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    return this.taskNodeService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(page: number, sortState: SortState): void {
    const queryParamsObj = {
      page,
      size: this.itemsPerPage,
      sort: this.sortService.buildSortParam(sortState),
    };

    this.ngZone.run(() => {
      this.router.navigate(['./'], {
        relativeTo: this.activatedRoute,
        queryParams: queryParamsObj,
      });
    });
  }

  retryTask(task: ITaskNode): void {
    // alert("BETA MIGHT NOT WORK");
    task.status = DownloadStatus.PENDING;
    task.retryCount = 0;
    task.maxRetryCount = 5;
    task.canRetry = true;
    // @ts-ignore
    task.scheduledStartTime = '';

    this.taskNodeService.partialUpdate(task).subscribe(() => {
      this.load();
    });
  }
}
