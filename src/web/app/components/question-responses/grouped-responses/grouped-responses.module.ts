import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { QuestionTextWithInfoModule } from '../../question-text-with-info/question-text-with-info.module';
import { SingleResponseModule } from '../single-response/single-response.module';
import { GroupedResponsesComponent } from './grouped-responses.component';

/**
 * Module for a list of responses grouped in GRQ/RGQ mode.
 */
@NgModule({
  declarations: [GroupedResponsesComponent],
  exports: [GroupedResponsesComponent],
  imports: [
    CommonModule,
    QuestionTextWithInfoModule,
    SingleResponseModule,
  ],
})
export class GroupedResponsesModule { }
