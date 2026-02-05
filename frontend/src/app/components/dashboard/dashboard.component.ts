import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { DashboardService } from '../../services/dashboard.service';
import { FinancialRecord, FilterRequest } from '../../models/financial-record.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {

  records: FinancialRecord[] = [];
  totalRecords = 0;
  currentPage = 0;
  pageSize = 20;
  searchKeyword = '';
  Math = Math;

  constructor(
      public authService: AuthService,
      private dashboardService: DashboardService,
      private router: Router,
      private cdr: ChangeDetectorRef  // ✅ ADD THIS
  ) {}

  ngOnInit(): void {
    this.loadRecords();
  }

  // ---------------- LOAD RECORDS ----------------
  loadRecords(): void {
    this.dashboardService.getRecords(this.currentPage, this.pageSize).subscribe({
      next: (response: any) => {
        console.log('Records loaded:', response); // Keep this for debugging
        this.records = response.content ?? [];
        this.totalRecords = response.totalElements ?? 0;
        this.cdr.detectChanges();  // ✅ TRIGGER CHANGE DETECTION
      },
      error: (err) => {
        console.error('Error loading records', err);
        this.cdr.detectChanges();  // ✅ TRIGGER EVEN ON ERROR
      }
    });
  }

  // ---------------- SEARCH ----------------
  search(): void {
    const filterRequest: FilterRequest = {
      searchKeyword: this.searchKeyword,
      page: 0,
      size: this.pageSize,
      sortBy: 'issuerName',
      sortDirection: 'ASC'
    };

    this.dashboardService.filterRecords(filterRequest).subscribe({
      next: (response: any) => {
        console.log('Filtered records:', response); // Keep this for debugging
        this.records = response.content ?? [];
        this.totalRecords = response.totalElements ?? 0;
        this.currentPage = 0;
        this.cdr.detectChanges();  // ✅ TRIGGER CHANGE DETECTION
      },
      error: (err) => {
        console.error('Error filtering records', err);
        this.cdr.detectChanges();  // ✅ TRIGGER EVEN ON ERROR
      }
    });
  }

  // ---------------- PAGINATION ----------------
  nextPage(): void {
    if ((this.currentPage + 1) * this.pageSize < this.totalRecords) {
      this.currentPage++;
      this.loadRecords();
    }
  }

  prevPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadRecords();
    }
  }

  // ---------------- NAVIGATION ----------------
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  goHome(): void {
    this.router.navigate(['/home']);
  }

  // ---------------- UI HELPERS ----------------
  getRatingClass(rating: string): string {
    if (['AAA', 'AA_PLUS', 'AA', 'AA_MINUS'].includes(rating)) return 'excellent';
    if (['A_PLUS', 'A', 'A_MINUS'].includes(rating)) return 'good';
    if (['BBB_PLUS', 'BBB', 'BBB_MINUS'].includes(rating)) return 'fair';
    return 'poor';
  }
}