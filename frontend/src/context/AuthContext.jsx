import { useMemo, useState } from 'react'
import { endpoints, errorMessage, getStoredSession, unwrap } from '../api/client'
import { AuthContext } from './auth-context'

export function AuthProvider({ children }) {
  const [session, setSession] = useState(getStoredSession)
  const [busy, setBusy] = useState(false)

  const persist = (value) => {
    setSession(value)
    if (value) localStorage.setItem('datshop-session', JSON.stringify(value))
    else localStorage.removeItem('datshop-session')
  }

  const login = async (credentials) => {
    setBusy(true)
    try {
      const data = unwrap(await endpoints.auth.login(credentials))
      persist(data)
      return data
    } finally {
      setBusy(false)
    }
  }

  const register = async (details) => {
    setBusy(true)
    try {
      return unwrap(await endpoints.auth.register(details))
    } finally {
      setBusy(false)
    }
  }

  const logout = async () => {
    try { await endpoints.auth.logout() } catch { /* local logout still applies */ }
    persist(null)
  }

  const value = useMemo(() => ({ session, user: session, busy, login, register, logout, errorMessage }), [session, busy])
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
