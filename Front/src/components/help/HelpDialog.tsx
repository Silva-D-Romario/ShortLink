interface HelpDialogProps {
  onClose: () => void
}

export function HelpDialog({ onClose }: HelpDialogProps) {
  return (
    <div className="dialog-backdrop" role="presentation" onMouseDown={onClose}>
      <section className="auth-dialog help-dialog" role="dialog" aria-modal="true" aria-labelledby="help-title" onMouseDown={(event) => event.stopPropagation()}>
        <button className="dialog-close" type="button" onClick={onClose} aria-label="Fechar">×</button>
        <p className="eyebrow">Central de ajuda</p>
        <h2 id="help-title">Como usar o ShortLink</h2>
        <ol>
          <li><strong>Entre ou crie sua conta.</strong><span>Seus links ficam separados dos links de outros usuários.</span></li>
          <li><strong>Cole uma URL completa.</strong><span>Use endereços iniciados por http:// ou https://.</span></li>
          <li><strong>Compartilhe o link curto.</strong><span>Os acessos serão contabilizados automaticamente.</span></li>
        </ol>
        <p className="help-note">Para alterar nome ou e-mail, use <strong>Editar perfil</strong> no menu lateral.</p>
        <button className="auth-submit" type="button" onClick={onClose}>Entendi</button>
      </section>
    </div>
  )
}
