import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { DatasetService } from '../../services/dataset.service';
import { DashboardService } from '../../services/dashboard.service';
import { DashboardSummary } from '../../models/financial-record.model';
import { Dataset } from '../../models/dataset.model';
import { HttpClient } from '@angular/common/http';


@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {

  summary: DashboardSummary = {
    totalRecords: 0,
    datasetCount: 0,
    ratingDistribution: {}
  };

  recentDatasets: Dataset[] = [];
  selectedFile: File | null = null;
  uploadProgress = false;
  uploadMessage = '';
  uploadError = false;
  datasetsLoaded = false;

  constructor(
      public authService: AuthService,
      private datasetService: DatasetService,
      private dashboardService: DashboardService,
      private router: Router,
      private cdr: ChangeDetectorRef,
      private http: HttpClient
) {}

  ngOnInit(): void {
    // Remove the setTimeout - it's not needed
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.dashboardService.getSummary().subscribe({
      next: (data) => {
        console.log('Summary data received:', data);
        if (data) {
          this.summary = data;
          this.cdr.detectChanges();  // ✅ TRIGGER CHANGE DETECTION
        }
      },
      error: (err) => {
        console.error('Error loading summary', err);
      }
    });

    this.datasetService.getUserDatasets().subscribe({
      next: (datasets) => {
        console.log('DATASETS FROM API:', datasets);

        this.recentDatasets = datasets
            .filter(d => d.status === 'COMPLETED')
            .slice(0, 5);

        this.datasetsLoaded = true;
        this.cdr.detectChanges();  // ✅ TRIGGER CHANGE DETECTION
      },
      error: (err) => {
        console.error('Error loading datasets', err);
        this.datasetsLoaded = true;
        this.cdr.detectChanges();  // ✅ TRIGGER EVEN ON ERROR
      }
    });
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (!file) return;

    const validTypes = [
      'text/csv',
      'application/vnd.ms-excel',
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    ];

    const validExtensions = ['.csv', '.xls', '.xlsx'];
    const fileExtension = file.name.substring(file.name.lastIndexOf('.')).toLowerCase();

    if (!validTypes.includes(file.type) && !validExtensions.includes(fileExtension)) {
      this.uploadMessage = 'Invalid file type. Please upload CSV or Excel files only.';
      this.uploadError = true;
      this.selectedFile = null;
      event.target.value = '';
      return;
    }

    if (file.size > 50 * 1024 * 1024) {
      this.uploadMessage = 'File is too large. Maximum size is 50MB.';
      this.uploadError = true;
      this.selectedFile = null;
      event.target.value = '';
      return;
    }

    this.selectedFile = file;
    this.uploadMessage = '';
    this.uploadError = false;
  }

  uploadFile(): void {
    if (!this.selectedFile) {
      this.uploadMessage = 'Please select a file first.';
      this.uploadError = true;
      return;
    }

    this.uploadProgress = true;
    this.uploadMessage = '';
    this.uploadError = false;

    this.datasetService.uploadDataset(this.selectedFile).subscribe({
      next: (response) => {
        this.uploadMessage = response.message || 'Upload successful! Processing your data...';
        this.uploadError = false;
        this.uploadProgress = false;
        this.selectedFile = null;

        const fileInput = document.getElementById('fileInput') as HTMLInputElement;
        if (fileInput) fileInput.value = '';

        this.loadDashboardData();
      },
      error: (err) => {
        console.error('Upload error:', err);
        this.uploadMessage = err.message || 'Upload failed. Please try again.';
        this.uploadError = true;
        this.uploadProgress = false;
      }
    });
  }

  clearMessage(): void {
    this.uploadMessage = '';
    this.uploadError = false;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  navigateToDashboard(): void {
    this.router.navigate(['/dashboard']);
  }

  getRatingCount(): number {
    if (!this.summary?.ratingDistribution) return 0;
    return Object.values(this.summary.ratingDistribution)
        .reduce((sum, count) => sum + count, 0);
  }
  useSampleData(): void {
    this.uploadProgress = true;
    this.uploadMessage = '';
    this.uploadError = false;

    this.http.get('/assets/sample-ratings.csv', { responseType: 'blob' })
        .subscribe({
          next: (blob) => {
            const sampleFile = new File(
                [blob],
                'sample-ratings.csv',
                { type: 'text/csv' }
            );

            this.datasetService.uploadDataset(sampleFile).subscribe({
              next: (response) => {
                this.uploadMessage = 'Sample data loaded successfully!';
                this.uploadError = false;
                this.uploadProgress = false;

                this.loadDashboardData();

                // Optional: auto-navigate
                // this.router.navigate(['/dashboard']);
              },
              error: (err) => {
                this.uploadMessage = 'Failed to load sample data.';
                this.uploadError = true;
                this.uploadProgress = false;
              }
            });
          },
          error: () => {
            this.uploadMessage = 'Sample file not found.';
            this.uploadError = true;
            this.uploadProgress = false;
          }
        });
  }

}