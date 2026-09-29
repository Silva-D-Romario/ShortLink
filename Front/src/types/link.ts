export interface ShortLink {
  id: number
  originalUrl: string
  shortCode: string
  shortUrl: string
  clickCount: number
  createdAt: string
  expiresAt: string | null
  isActive: boolean
  notes: string | null
}

export interface ShortLinkRequest {
  originalUrl: string
  domain?: string
  expiresInDays?: number
  notes?: string
}

export interface Page<T> {
  content: T[]
  totalElements: number
}

export interface AuthResponse {
  userId: number
  email: string
  token: string
}
