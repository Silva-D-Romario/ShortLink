import type { ShortLink } from '../../types/link'

interface LinkTableProps {
  links: ShortLink[]
}

export function LinkTable({ links }: LinkTableProps) {
  function formatDate(value: string) {
    return new Intl.DateTimeFormat('pt-BR', {
      dateStyle: 'short',
      timeStyle: 'short',
    }).format(new Date(value))
  }

  return (
    <section className="links-section" id="links">
      <div className="section-heading">
        <div><p className="eyebrow">Biblioteca</p><h2>Seus links recentes</h2></div>
        <button className="text-button" type="button">Ver todos <span aria-hidden="true">↗</span></button>
      </div>
      <div className="table-wrap">
        <table>
          <thead><tr><th>Link curto</th><th>URL original</th><th>Cliques</th><th>Criado em</th></tr></thead>
          <tbody>{links.map((link) => <tr key={link.id}>
            <td><a className="short-code" href={link.shortUrl} target="_blank" rel="noreferrer">sl.link/{link.shortCode}</a></td>
            <td className="original-url">{link.originalUrl}</td>
            <td>{link.clickCount.toLocaleString('pt-BR')}</td>
            <td>{formatDate(link.createdAt)}</td>
          </tr>)}</tbody>
        </table>
      </div>
    </section>
  )
}
