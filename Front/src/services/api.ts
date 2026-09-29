import type { AuthResponse, Page, ShortLink, ShortLinkRequest } from '../types/link'

const API_URL = import.meta.env.VITE_API_URL ?? '/api'

export class ApiError extends Error {
  readonly status: number

  constructor(message: string, status: number) {
    super(message)
    this.status = status
  }
}

function authHeaders(): HeadersInit {
  const storedSession = localStorage.getItem('shortlink-session')
  if (!storedSession) return {}

  try {
    const session = JSON.parse(storedSession) as AuthResponse
    return { Authorization: `Bearer ${session.token}` }
  } catch {
    localStorage.removeItem('shortlink-session')
    return {}
  }
}

export async function authenticate(
  mode: 'login' | 'register',
  email: string,
  password: string,
  fullName?: string,
): Promise<AuthResponse> {
  const response = await fetch(`${API_URL}/auth/${mode}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', ...authHeaders() },
    body: JSON.stringify({ email, password, fullName }),
  })

  return parseResponse<AuthResponse>(response)
}

export async function updateProfile(email: string, fullName: string): Promise<AuthResponse> {
  const response = await fetch(`${API_URL}/auth/profile`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json', ...authHeaders() },
    body: JSON.stringify({ email, fullName }),
  })

  return parseResponse<AuthResponse>(response)
}

async function parseResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const message = await response.text()
    throw new ApiError(message || `Erro ${response.status} ao acessar a API.`, response.status)
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
  const response = await fetch(`${API_URL}/shortlinks/my-links?size=20&sort=createdAt,desc`, { headers: authHeaders() })
  const page = await parseResponse<Page<ShortLink>>(response)
  return page.content
}

export async function getApiHealth(): Promise<boolean> {
  const response = await fetch('/health')
  return response.ok
}
