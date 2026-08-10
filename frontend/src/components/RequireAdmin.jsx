import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/useAuth'

export function RequireAdmin({ children }) {
  const { user } = useAuth()
  const location = useLocation()

  if (!user) return <Navigate to="/auth" state={{ from: location.pathname }} replace />
  if (user.role?.toUpperCase() !== 'ADMIN') return <Navigate to="/" replace />

  return children
}
