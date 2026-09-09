import { useState } from 'react'
import type { FormEvent } from 'react'

interface ShortenFormProps {
  onSubmit: (url: string) => void
}

export function ShortenForm({ onSubmit }: ShortenFormProps) {
  const [url, setUrl] = useState('')

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (url.trim()) onSubmit(url.trim())
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
        <button type="submit">Encurtar link <span aria-hidden="true">→</span></button>
      </div>
    </form>
  )
}