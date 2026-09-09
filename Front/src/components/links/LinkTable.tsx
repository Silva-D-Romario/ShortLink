import type { ShortLink } from '../../types/link'

interface LinkTableProps {
  links: ShortLink[]
}

export function LinkTable({ links }: LinkTableProps) {
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
            <td><a className="short-code" href={`http://localhost:8080/${link.shortCode}`}>sl.link/{link.shortCode}</a></td>
            <td className="original-url">{link.originalUrl}</td>
            <td>{link.clicks.toLocaleString('pt-BR')}</td>
            <td>{link.createdAt}</td>
          </tr>)}</tbody>
        </table>
      </div>
    </section>
  )
}