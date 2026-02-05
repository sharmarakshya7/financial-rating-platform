import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AuthComponent } from '../auth/auth.component';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, AuthComponent],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.css'
})
export class LandingComponent {
  showAuthModal = false;
  isLoginMode = true;

  constructor(
    private router: Router,
    public authService: AuthService
  ) {
    if (authService.isAuthenticated()) {
      this.router.navigate(['/home']);
    }
  }

  toggleAuthMode() {
    this.isLoginMode = !this.isLoginMode;
  }

  closeModal() {
    this.showAuthModal = false;
  }

  openModal() {
    this.showAuthModal = true;
  }

  navigateToAbout() {
    this.router.navigate(['/about']);
  }
  currentYear = new Date().getFullYear();

}

