import type { Page, ShortLink, ShortLinkRequest } from '../types/link'

const API_URL = import.meta.env.VITE_API_URL ?? '/api'

async function parseResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const message = await response.text()
    throw new Error(message || `Erro ${response.status} ao acessar a API.`)
  }

  return response.json() as Promise<T>
}

export async function createShortLink(payload: ShortLinkRequest): Promise<ShortLink> {
  const response = await fetch(`${API_URL}/shortlinks/shorten`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })

  return parseResponse<ShortLink>(response)
}

export async function getShortLinks(): Promise<ShortLink[]> {
  const response = await fetch(`${API_URL}/shortlinks/my-links?size=20&sort=createdAt,desc`)
  const page = await parseResponse<Page<ShortLink>>(response)
  return page.content
}

export async function getApiHealth(): Promise<boolean> {
  const response = await fetch('/health')
  return response.ok
}
