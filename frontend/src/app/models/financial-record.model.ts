export interface FinancialRecord {
  id: number;
  issuerName: string;
  industry: string;
  country: string;
  revenue: number;
  ebitda: number;
  totalDebt: number;
  interestExpense?: number;
  currentAssets?: number;
  currentLiabilities?: number;
  debtToEbitda?: number;
  interestCoverageRatio?: number;
  liquidityCoverageRatio?: number;
  rating: string;
  category: string;
  calculatedAt?: string;
}

export interface DashboardSummary {
  totalRecords: number;
  ratingDistribution: { [key: string]: number };
  categoryDistribution?: { [key: string]: number };
  datasetCount: number;
}

export interface FilterRequest {
  industries?: string[];
  countries?: string[];
  ratings?: string[];
  categories?: string[];
  minRevenue?: number;
  maxRevenue?: number;
  searchKeyword?: string;
  page: number;
  size: number;
  sortBy: string;
  sortDirection: string;
}
