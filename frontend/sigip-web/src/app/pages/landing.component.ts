import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'sigip-landing',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.css']
})
export class LandingComponent {}
