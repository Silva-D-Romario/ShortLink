import type { ReactNode } from 'react'

interface AppShellProps {
  children: ReactNode
  userEmail?: string
  onOpenAuth: () => void
  onEditProfile: () => void
  onLogout: () => void
}

export function AppShell({ children, userEmail, onOpenAuth, onEditProfile, onLogout }: AppShellProps) {
  return (
    <div className="app-shell">
      <aside className="sidebar">
        <a className="brand" href="/" aria-label="ShortLink início">
          <span className="brand-mark">SL</span>
          <span>ShortLink</span>
        </a>
        <nav aria-label="Conta">
          {!userEmail && <button className="nav-link nav-link-active" type="button" onClick={onOpenAuth}>Entrar ou cadastrar</button>}
          {userEmail && <>
            <button className="nav-link" type="button" onClick={onEditProfile}>Editar perfil</button>
            <button className="nav-link" type="button" onClick={onLogout}>Sair</button>
          </>}
        </nav>
        <div className="sidebar-footer">
          <span className="avatar">SL</span>
          <span className="account-details">
            <strong>{userEmail ?? 'Visitante'}</strong>
            <small>{userEmail ? 'Conta conectada' : 'Entre para criar links'}</small>
          </span>
        </div>
      </aside>
      <main className="main-content">{children}</main>
    </div>
  )
}
