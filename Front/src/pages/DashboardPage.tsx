import { useEffect, useState } from 'react'
import { AppShell } from '../components/layout/AppShell'
import { LinkTable } from '../components/links/LinkTable'
import { ShortenForm } from '../components/links/ShortenForm'
import { createShortLink, getApiHealth, getShortLinks } from '../services/api'
import type { ShortLink } from '../types/link'

export function DashboardPage() {
  const [links, setLinks] = useState<ShortLink[]>([])
  const [error, setError] = useState('')
  const [isApiOnline, setIsApiOnline] = useState<boolean | null>(null)

  useEffect(() => {
    getApiHealth()
      .then(setIsApiOnline)
      .catch(() => setIsApiOnline(false))

    getShortLinks()
      .then(setLinks)
      .catch(() => setError('Não foi possível carregar os links. Verifique se a API está em execução.'))
  }, [])

  async function addLink(url: string) {
    setError('')
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

  return <AppShell>
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
  </AppShell>
}
