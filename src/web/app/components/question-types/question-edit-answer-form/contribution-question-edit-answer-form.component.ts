import { Component, Input } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import {
  FeedbackContributionQuestionDetails,
  FeedbackContributionResponseDetails,
} from '../../../../types/api-output';
import {
  DEFAULT_CONTRIBUTION_QUESTION_DETAILS,
  DEFAULT_CONTRIBUTION_RESPONSE_DETAILS,
} from '../../../../types/default-question-structs';
import {
  CONTRIBUTION_POINT_NOT_SUBMITTED,
  CONTRIBUTION_POINT_NOT_SURE,
} from '../../../../types/feedback-response-details';
import { QuestionEditAnswerFormComponent } from './question-edit-answer-form';

/**
 * The contribution question submission form for a recipient.
 */
@Component({
  selector: 'tm-contribution-question-edit-answer-form',
  templateUrl: './contribution-question-edit-answer-form.component.html',
  styleUrls: ['./contribution-question-edit-answer-form.component.scss'],
})
export class ContributionQuestionEditAnswerFormComponent
    extends QuestionEditAnswerFormComponent
        <FeedbackContributionQuestionDetails, FeedbackContributionResponseDetails> {

  @Input()
  shouldShowHelpLink: boolean = true;

  CONTRIBUTION_POINT_NOT_SUBMITTED: number = CONTRIBUTION_POINT_NOT_SUBMITTED;
  CONTRIBUTION_POINT_NOT_SURE: number = CONTRIBUTION_POINT_NOT_SURE;

  constructor(private modalService: NgbModal) {
    super(DEFAULT_CONTRIBUTION_QUESTION_DETAILS(), DEFAULT_CONTRIBUTION_RESPONSE_DETAILS());
  }

  get contributionQuestionPoints(): number[] {
    const points: number[] = [];
    for (let i: number = 200; i >= 0; i -= 10) {
      points.push(i);
    }
    return points;
  }

  /**
   * Opens a modal.
   */
  openModal(modal: any): void {
    this.modalService.open(modal);
  }
}
