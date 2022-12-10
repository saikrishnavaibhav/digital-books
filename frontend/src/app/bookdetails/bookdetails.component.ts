import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BoardUserComponent } from '../board-user/board-user.component';

@Component({
  selector: 'app-bookdetails',
  templateUrl: './bookdetails.component.html',
  styleUrls: ['./bookdetails.component.css']
})
export class BookdetailsComponent implements OnInit{


  ngOnInit(): void {
  }

  constructor(private route: ActivatedRoute) { }

}
