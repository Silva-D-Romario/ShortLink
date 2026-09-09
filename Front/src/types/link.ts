export interface ShortLink {
  id: number
  originalUrl: string
  shortCode: string
  clicks: number
  createdAt: string
}

export interface ShortLinkRequest {
  originalUrl: string
}