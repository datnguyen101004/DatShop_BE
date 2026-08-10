import { useState } from 'react'
import { ArrowRight, LockKeyhole, ShieldCheck, Store } from 'lucide-react'
import { Navigate, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/useAuth'
import { Button, Field, Notice } from '../components/ui'

export default function AuthPage() {
  const { user, login, register, busy, errorMessage } = useAuth()
  const [mode, setMode] = useState('login')
  const [form, setForm] = useState({ name: '', email: '', password: '' })
  const [notice, setNotice] = useState(null)
  const navigate = useNavigate()
  const location = useLocation()
  if (user) return <Navigate to={location.state?.from || '/'} replace />

  const submit = async (event) => {
    event.preventDefault(); setNotice(null)
    try {
      if (mode === 'register') {
        const message = await register(form)
        setNotice({ type: 'success', text: message })
        setMode('login')
      } else {
        await login({ email: form.email, password: form.password })
        navigate(location.state?.from || '/')
      }
    } catch (error) { setNotice({ type: 'error', text: errorMessage(error) }) }
  }

  return (
    <div className="auth-page page-pad">
      <section className="auth-story">
        <p className="eyebrow">Your neighborhood marketplace</p>
        <h1>Find good things.<br />Meet good shops.</h1>
        <p>One account for thoughtful shopping, selling, live conversations, delivery, and a smarter way to discover products.</p>
        <div className="auth-points">
          <span><ShieldCheck /><b>Protected checkout</b><small>Secure account access</small></span>
          <span><Store /><b>Independent sellers</b><small>Follow the shops you love</small></span>
          <span><LockKeyhole /><b>Your data, yours</b><small>Simple and transparent</small></span>
        </div>
      </section>
      <section className="auth-card">
        <div className="segmented"><button className={mode === 'login' ? 'active' : ''} onClick={() => setMode('login')}>Sign in</button><button className={mode === 'register' ? 'active' : ''} onClick={() => setMode('register')}>Create account</button></div>
        <div><p className="eyebrow">Welcome to DatShop</p><h2>{mode === 'login' ? 'Good to see you again.' : 'Start your marketplace journey.'}</h2></div>
        {notice && <Notice type={notice.type}>{notice.text}</Notice>}
        <form onSubmit={submit} className="form-stack">
          {mode === 'register' && <Field label="Full name" required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} placeholder="Nguyen Van Dat" />}
          <Field label="Email address" type="email" required value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} placeholder="you@example.com" />
          <Field label="Password" type="password" minLength="6" required value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} placeholder="At least 6 characters" />
          <Button type="submit" loading={busy}>{mode === 'login' ? 'Sign in' : 'Create account'} <ArrowRight size={17} /></Button>
        </form>
        <small className="auth-terms">By continuing, you agree to DatShop’s marketplace terms and privacy policy.</small>
      </section>
    </div>
  )
}
