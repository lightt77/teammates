import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import {
  GqrRqgViewResponsesModule,
} from '../../components/question-responses/gqr-rqg-view-responses/gqr-rqg-view-responses.module';
import { InstructorSessionResultGqrViewComponent } from './instructor-session-result-gqr-view.component';

describe('InstructorSessionResultGqrViewComponent', () => {
  let component: InstructorSessionResultGqrViewComponent;
  let fixture: ComponentFixture<InstructorSessionResultGqrViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorSessionResultGqrViewComponent],
      imports: [GqrRqgViewResponsesModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorSessionResultGqrViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
