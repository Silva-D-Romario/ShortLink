import { useState } from 'react'
import { AppShell } from '../components/layout/AppShell'
import { LinkTable } from '../components/links/LinkTable'
import { ShortenForm } from '../components/links/ShortenForm'
import type { ShortLink } from '../types/link'

const initialLinks: ShortLink[] = [
  { id: 1, originalUrl: 'youtube.com/watch?v=design-talk', shortCode: 'design', clicks: 1248, createdAt: 'Hoje, 09:42' },
  { id: 2, originalUrl: 'medium.com/@romario/produto-digital', shortCode: 'produto', clicks: 836, createdAt: 'Ontem, 16:20' },
  { id: 3, originalUrl: 'github.com/romario/shortlink', shortCode: 'codigo', clicks: 421, createdAt: '12 jun 2024' },
]

export function DashboardPage() {
  const [links, setLinks] = useState(initialLinks)

  function addLink(url: string) {
    setLinks((current) => [{ id: Date.now(), originalUrl: url.replace(/^https?:\/\//, ''), shortCode: 'novo-link', clicks: 0, createdAt: 'Agora' }, ...current])
  }

  return <AppShell>
    <header className="topbar"><span className="mobile-brand">ShortLink</span><button className="help-button" type="button">Ajuda <span aria-hidden="true">?</span></button></header>
    <div className="page-intro" id="dashboard"><div><p className="eyebrow">Quarta-feira, 12 de junho</p><h1>Olá, Romário<span>.</span></h1><p className="intro-copy">Tudo o que você precisa para compartilhar melhor.</p></div><div className="status-pill"><span /> Sistema operacional</div></div>
    <ShortenForm onSubmit={addLink} />
    <section className="stats-grid" id="analytics" aria-label="Resumo dos links">
      <article><span className="stat-label">Links criados</span><strong>{links.length}</strong><span className="stat-note">+2 este mês</span></article>
      <article><span className="stat-label">Cliques totais</span><strong>{links.reduce((total, link) => total + link.clicks, 0).toLocaleString('pt-BR')}</strong><span className="stat-note">+18,4% este mês</span></article>
      <article><span className="stat-label">Taxa de retorno</span><strong>68<span className="stat-unit">%</span></strong><span className="stat-note">Pessoas que voltaram</span></article>
    </section>
    <LinkTable links={links} />
  </AppShell>
}