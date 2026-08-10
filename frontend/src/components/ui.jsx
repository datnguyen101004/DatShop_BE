import { AlertCircle, CheckCircle2, LoaderCircle, X } from 'lucide-react'

export function Button({ children, variant = 'primary', className = '', loading, ...props }) {
  return (
    <button className={`button button--${variant} ${className}`} disabled={loading || props.disabled} {...props}>
      {loading && <LoaderCircle size={16} className="spin" />}
      {children}
    </button>
  )
}

export function Field({ label, hint, as = 'input', options = [], className = '', ...props }) {
  const Tag = as
  return (
    <label className={`field ${className}`}>
      <span>{label}</span>
      {as === 'select' ? (
        <select {...props}>{options.map((option) => <option key={option.value ?? option} value={option.value ?? option}>{option.label ?? option}</option>)}</select>
      ) : <Tag {...props} />}
      {hint && <small>{hint}</small>}
    </label>
  )
}

export function Modal({ title, eyebrow, children, onClose, wide = false }) {
  return (
    <div className="modal-backdrop" onMouseDown={(event) => event.target === event.currentTarget && onClose()}>
      <section className={`modal ${wide ? 'modal--wide' : ''}`} role="dialog" aria-modal="true" aria-label={title}>
        <div className="modal__head">
          <div>{eyebrow && <p className="eyebrow">{eyebrow}</p>}<h2>{title}</h2></div>
          <button className="icon-button" onClick={onClose} aria-label="Close"><X size={20} /></button>
        </div>
        {children}
      </section>
    </div>
  )
}

export function Notice({ type = 'success', children, onClose }) {
  return (
    <div className={`notice notice--${type}`}>
      {type === 'error' ? <AlertCircle size={18} /> : <CheckCircle2 size={18} />}
      <span>{children}</span>
      {onClose && <button onClick={onClose} aria-label="Dismiss"><X size={16} /></button>}
    </div>
  )
}

export function EmptyState({ icon: Icon, title, text, action }) {
  return (
    <div className="empty-state">
      {Icon && <span className="empty-state__icon"><Icon size={25} /></span>}
      <h3>{title}</h3><p>{text}</p>{action}
    </div>
  )
}

export function PageLoader() {
  return <div className="page-loader"><LoaderCircle className="spin" /><span>Loading your shop…</span></div>
}

export function StatusPill({ value = 'UNKNOWN' }) {
  const normalized = String(value).toLowerCase().replaceAll('_', '-')
  return <span className={`status status--${normalized}`}>{String(value).replaceAll('_', ' ')}</span>
}
