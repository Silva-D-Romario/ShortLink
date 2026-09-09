import type { ShortLink, ShortLinkRequest } from '../types/link'

const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api'

export async function createShortLink(payload: ShortLinkRequest): Promise<ShortLink> {
  const response = await fetch(`${API_URL}/links`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })

  if (!response.ok) throw new Error('Não foi possível criar o link.')
  return response.json() as Promise<ShortLink>
}