import type { ReactNode } from 'react'

interface AppShellProps {
  children: ReactNode
  activeSection: string
  userEmail?: string
  onNavigate: (sectionId: string) => void
  onOpenAuth: () => void
  onLogout: () => void
}

export function AppShell({ children, activeSection, userEmail, onNavigate, onOpenAuth, onLogout }: AppShellProps) {
  const menuItems = [
    { id: 'dashboard', label: 'Visão geral' },
    { id: 'links', label: 'Meus links' },
    { id: 'analytics', label: 'Analytics' },
  ]

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <a className="brand" href="/" aria-label="ShortLink início">
          <span className="brand-mark">SL</span>
          <span>ShortLink</span>
        </a>
        <nav aria-label="Navegação principal">
          {menuItems.map((item) => (
            <button
              className={`nav-link ${activeSection === item.id ? 'nav-link-active' : ''}`}
              key={item.id}
              type="button"
              onClick={() => onNavigate(item.id)}
            >
              {item.label}
            </button>
          ))}
        </nav>
        <div className="sidebar-footer">
          <span className="avatar">SL</span>
          <span className="account-details">
            <strong>{userEmail ?? 'Visitante'}</strong>
            <button type="button" onClick={userEmail ? onLogout : onOpenAuth}>
              {userEmail ? 'Sair' : 'Entrar ou cadastrar'}
            </button>
          </span>
        </div>
      </aside>
      <main className="main-content">{children}</main>
    </div>
  )
}
