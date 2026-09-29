import { useState } from 'react'
import type { FormEvent } from 'react'

interface ShortenFormProps {
  onSubmit: (url: string) => Promise<boolean>
}

export function ShortenForm({ onSubmit }: ShortenFormProps) {
  const [url, setUrl] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!url.trim()) return

    setIsSubmitting(true)
    try {
      const created = await onSubmit(url.trim())
      if (created) setUrl('')
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <form className="shorten-form" onSubmit={handleSubmit}>
      <label htmlFor="long-url">Cole uma URL para encurtar</label>
      <div className="input-row">
        <input
          id="long-url"
          type="url"
          placeholder="https://seu-site.com/artigo..."
          value={url}
          onChange={(event) => setUrl(event.target.value)}
          required
        />
        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Encurtando...' : 'Encurtar link'} <span aria-hidden="true">→</span>
        </button>
      </div>
    </form>
  )
}
