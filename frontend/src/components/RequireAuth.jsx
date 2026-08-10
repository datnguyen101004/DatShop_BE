import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/useAuth'

export function RequireAuth({ children }) {
  const { user } = useAuth()
  const location = useLocation()
  return user ? children : <Navigate to="/auth" state={{ from: location.pathname }} replace />
}
