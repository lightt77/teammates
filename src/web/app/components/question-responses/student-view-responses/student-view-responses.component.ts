import { Component, Input, OnInit } from '@angular/core';

/**
 * Feedback response in student results page view.
 */
@Component({
  selector: 'tm-student-view-responses',
  templateUrl: './student-view-responses.component.html',
  styleUrls: ['./student-view-responses.component.scss'],
})
export class StudentViewResponsesComponent implements OnInit {

  @Input() questionDetails: any = {};
  @Input() responses: any[] = [];
  @Input() isSelfResponses: boolean = false;

  recipient: string = '';

  constructor() { }

  ngOnInit(): void {
    this.recipient = this.responses.length ? this.responses[0].recipient : '';
  }

}
