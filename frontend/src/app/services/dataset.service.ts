import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, timeout, catchError } from 'rxjs';
import { Dataset, DatasetUploadResponse } from '../models/dataset.model';

@Injectable({ providedIn: 'root' })
export class DatasetService {
  private apiUrl = 'http://localhost:8080/api/datasets';
  private uploadTimeout = 60000; // 60 seconds timeout

  constructor(private http: HttpClient) {}

  uploadDataset(file: File): Observable<DatasetUploadResponse> {
    const formData = new FormData();
    formData.append('file', file);
    
    return this.http.post<DatasetUploadResponse>(`${this.apiUrl}/upload`, formData)
      .pipe(
        timeout(this.uploadTimeout),
        catchError(this.handleError)
      );
  }

  getUserDatasets(): Observable<Dataset[]> {
    return this.http.get<Dataset[]>(this.apiUrl)
      .pipe(catchError(this.handleError));
  }

  deleteDataset(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`)
      .pipe(catchError(this.handleError));
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An error occurred';
    
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Error: ${error.error.message}`;
    } else if (error.status === 0) {
      // Network error or CORS issue
      errorMessage = 'Cannot connect to server. Please check if the backend is running.';
    } else if (error.status === 401) {
      errorMessage = 'Unauthorized. Please login again.';
    } else if (error.status === 413) {
      errorMessage = 'File is too large. Maximum size is 50MB.';
    } else if (error.error?.message) {
      // Server error with message
      errorMessage = error.error.message;
    } else {
      // Other server errors
      errorMessage = `Server error: ${error.status}`;
    }
    
    console.error('Dataset Service Error:', errorMessage, error);
    return throwError(() => new Error(errorMessage));
  }
}
