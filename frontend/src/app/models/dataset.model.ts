// frontend/src/app/models/dataset.model.ts

export interface Dataset {
  id: number;
  name: string;

  fileName: string;
  fileType: string;
  fileSize: number;

  status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED';

  recordCount?: number | null;

  // Spring Boot LocalDateTime → array format
  uploadedAt: number[];

  // Optional (only set after processing)
  processedAt?: number[] | null;
}

export interface DatasetUploadResponse {
  id: number;
  name: string;
  status: string;
  message: string;
}
