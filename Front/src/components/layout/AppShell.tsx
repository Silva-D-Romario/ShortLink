import type { ReactNode } from 'react'

interface AppShellProps {
  children: ReactNode
}

export function AppShell({ children }: AppShellProps) {
  return (
    <div className="app-shell">
      <aside className="sidebar">
        <a className="brand" href="/" aria-label="ShortLink início">
          <span className="brand-mark">SL</span>
          <span>ShortLink</span>
        </a>
        <nav aria-label="Navegação principal">
          <a className="nav-link nav-link-active" href="#dashboard">Visão geral</a>
          <a className="nav-link" href="#links">Meus links</a>
          <a className="nav-link" href="#analytics">Analytics</a>
        </nav>
        <div className="sidebar-footer">
          <span className="avatar">RM</span>
          <span><strong>Romário</strong><small>Plano gratuito</small></span>
        </div>
      </aside>
      <main className="main-content">{children}</main>
    </div>
  )
}