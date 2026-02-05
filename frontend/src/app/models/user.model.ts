export interface User {
  id: number;
  email: string;
  firstName?: string;
  lastName?: string;
  role: string;
}

export interface AuthRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
}

export interface AuthResponse {
  token: string;
  email: string;
  role: string;
  firstName?: string;
  lastName?: string;
}
