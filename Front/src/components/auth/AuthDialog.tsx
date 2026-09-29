import { useState } from 'react'
import type { FormEvent } from 'react'
import { authenticate } from '../../services/api'
import type { AuthResponse } from '../../types/link'

interface AuthDialogProps {
  onClose: () => void
  onAuthenticated: (session: AuthResponse) => void
}

export function AuthDialog({ onClose, onAuthenticated }: AuthDialogProps) {
  const [mode, setMode] = useState<'login' | 'register'>('login')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [fullName, setFullName] = useState('')
  const [error, setError] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')
    setIsSubmitting(true)

    try {
      const session = await authenticate(mode, email.trim(), password, fullName.trim() || undefined)
      onAuthenticated(session)
    } catch {
      setError(mode === 'login' ? 'E-mail ou senha inválidos.' : 'Não foi possível criar a conta.')
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div className="dialog-backdrop" role="presentation" onMouseDown={onClose}>
      <section className="auth-dialog" role="dialog" aria-modal="true" aria-labelledby="auth-title" onMouseDown={(event) => event.stopPropagation()}>
        <button className="dialog-close" type="button" onClick={onClose} aria-label="Fechar">×</button>
        <p className="eyebrow">Sua conta</p>
        <h2 id="auth-title">{mode === 'login' ? 'Entrar no ShortLink' : 'Criar uma conta'}</h2>
        <div className="auth-tabs">
          <button className={mode === 'login' ? 'active' : ''} type="button" onClick={() => setMode('login')}>Entrar</button>
          <button className={mode === 'register' ? 'active' : ''} type="button" onClick={() => setMode('register')}>Cadastrar</button>
        </div>
        <form onSubmit={handleSubmit}>
          {mode === 'register' && <label>Nome completo<input value={fullName} onChange={(event) => setFullName(event.target.value)} required /></label>}
          <label>E-mail<input type="email" value={email} onChange={(event) => setEmail(event.target.value)} required /></label>
          <label>Senha<input type="password" value={password} onChange={(event) => setPassword(event.target.value)} minLength={6} required /></label>
          {error && <p className="form-error" role="alert">{error}</p>}
          <button className="auth-submit" type="submit" disabled={isSubmitting}>{isSubmitting ? 'Aguarde...' : mode === 'login' ? 'Entrar' : 'Criar conta'}</button>
        </form>
      </section>
    </div>
  )
}
