import { useEffect, useState } from 'react'
import { AppShell } from '../components/layout/AppShell'
import { AuthDialog } from '../components/auth/AuthDialog'
import { LinkTable } from '../components/links/LinkTable'
import { ShortenForm } from '../components/links/ShortenForm'
import { createShortLink, getApiHealth, getShortLinks } from '../services/api'
import type { AuthResponse, ShortLink } from '../types/link'

export function DashboardPage() {
  const [links, setLinks] = useState<ShortLink[]>([])
  const [error, setError] = useState('')
  const [isApiOnline, setIsApiOnline] = useState<boolean | null>(null)
  const [activeSection, setActiveSection] = useState('dashboard')
  const [isAuthOpen, setIsAuthOpen] = useState(false)
  const [session, setSession] = useState<AuthResponse | null>(() => {
    const storedSession = localStorage.getItem('shortlink-session')
    if (!storedSession) return null
    try {
      return JSON.parse(storedSession) as AuthResponse
    } catch {
      localStorage.removeItem('shortlink-session')
      return null
    }
  })

  useEffect(() => {
    getApiHealth()
      .then(setIsApiOnline)
      .catch(() => setIsApiOnline(false))

    if (session) {
      getShortLinks()
        .then(setLinks)
        .catch(() => setError('Sua sessão expirou. Entre novamente para acessar seus links.'))
    }
  }, [session])

  async function addLink(url: string) {
    setError('')
    if (!session) {
      setIsAuthOpen(true)
      setError('Entre na sua conta para criar um link.')
      return false
    }
    try {
      const link = await createShortLink({ originalUrl: url })
      setLinks((current) => [link, ...current])
      return true
    } catch {
      setError('Não foi possível encurtar a URL. Verifique os dados e tente novamente.')
      return false
    }
  }

  const now = new Date()
  const currentDate = new Intl.DateTimeFormat('pt-BR', {
    weekday: 'long',
    day: '2-digit',
    month: 'long',
  }).format(now)
  const linksCreatedThisMonth = links.filter((link) => {
    const createdAt = new Date(link.createdAt)
    return createdAt.getMonth() === now.getMonth() && createdAt.getFullYear() === now.getFullYear()
  }).length
  const activeLinks = links.filter((link) => link.isActive).length

  function navigateTo(sectionId: string) {
    setActiveSection(sectionId)
    document.getElementById(sectionId)?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }

  function saveSession(authSession: AuthResponse) {
    localStorage.setItem('shortlink-session', JSON.stringify(authSession))
    setSession(authSession)
    setIsAuthOpen(false)
  }

  function logout() {
    localStorage.removeItem('shortlink-session')
    setSession(null)
    setLinks([])
  }

  return <AppShell activeSection={activeSection} userEmail={session?.email} onNavigate={navigateTo} onOpenAuth={() => setIsAuthOpen(true)} onLogout={logout}>
    <header className="topbar"><span className="mobile-brand">ShortLink</span><button className="help-button" type="button">Ajuda <span aria-hidden="true">?</span></button></header>
    <div className="page-intro" id="dashboard"><div><p className="eyebrow">{currentDate}</p><h1>Olá<span>.</span></h1><p className="intro-copy">Tudo o que você precisa para compartilhar melhor.</p></div><div className={`status-pill ${isApiOnline === false ? 'status-pill-offline' : ''}`}><span /> {isApiOnline === null ? 'Verificando sistema' : isApiOnline ? 'Sistema operacional' : 'Sistema indisponível'}</div></div>
    <ShortenForm onSubmit={addLink} />
    {error && <p className="form-error" role="alert">{error}</p>}
    <section className="stats-grid" id="analytics" aria-label="Resumo dos links">
      <article><span className="stat-label">Links criados</span><strong>{links.length}</strong><span className="stat-note">{linksCreatedThisMonth} este mês</span></article>
      <article><span className="stat-label">Cliques totais</span><strong>{links.reduce((total, link) => total + link.clickCount, 0).toLocaleString('pt-BR')}</strong><span className="stat-note">Todos os links</span></article>
      <article><span className="stat-label">Links ativos</span><strong>{activeLinks}</strong><span className="stat-note">Disponíveis para acesso</span></article>
    </section>
    <LinkTable links={links} />
    {isAuthOpen && <AuthDialog onClose={() => setIsAuthOpen(false)} onAuthenticated={saveSession} />}
  </AppShell>
}
