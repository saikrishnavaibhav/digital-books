import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { RegisterComponent } from './register/register.component';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { ProfileComponent } from './profile/profile.component';
import { BoardUserComponent } from './board-user/board-user.component';
import { CreatebookComponent } from './createbook/createbook.component';
import { AllmybooksComponent } from './allmybooks/allmybooks.component';
import { BookdetailsComponent } from './bookdetails/bookdetails.component';
import { UpdatebookComponent } from './updatebook/updatebook.component';

const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'user', component: BoardUserComponent },
  { path: 'createBook', component: CreatebookComponent },
  { path: 'allMybooks', component: AllmybooksComponent },
  { path: 'book/:Id', component: BookdetailsComponent },
  { path: 'updateBook', component: UpdatebookComponent },
  { path: '', redirectTo: 'home', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
