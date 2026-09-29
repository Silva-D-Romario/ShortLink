import { useState } from 'react'
import type { FormEvent } from 'react'
import { updateProfile } from '../../services/api'
import type { AuthResponse } from '../../types/link'

interface ProfileDialogProps {
  session: AuthResponse
  onClose: () => void
  onUpdated: (session: AuthResponse) => void
}

export function ProfileDialog({ session, onClose, onUpdated }: ProfileDialogProps) {
  const [fullName, setFullName] = useState(session.fullName)
  const [email, setEmail] = useState(session.email)
  const [error, setError] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')
    setIsSubmitting(true)
    try {
      onUpdated(await updateProfile(email.trim(), fullName.trim()))
    } catch {
      setError('Não foi possível atualizar o perfil. Verifique os dados.')
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div className="dialog-backdrop" role="presentation" onMouseDown={onClose}>
      <section className="auth-dialog" role="dialog" aria-modal="true" aria-labelledby="profile-title" onMouseDown={(event) => event.stopPropagation()}>
        <button className="dialog-close" type="button" onClick={onClose} aria-label="Fechar">×</button>
        <p className="eyebrow">Sua conta</p>
        <h2 id="profile-title">Editar perfil</h2>
        <form onSubmit={handleSubmit}>
          <label>Nome completo<input value={fullName} onChange={(event) => setFullName(event.target.value)} required /></label>
          <label>E-mail<input type="email" value={email} onChange={(event) => setEmail(event.target.value)} required /></label>
          {error && <p className="form-error" role="alert">{error}</p>}
          <button className="auth-submit" type="submit" disabled={isSubmitting}>{isSubmitting ? 'Salvando...' : 'Salvar alterações'}</button>
        </form>
      </section>
    </div>
  )
}
