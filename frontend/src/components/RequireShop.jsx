import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/useAuth'

export function RequireShop({ children }) {
  const { user } = useAuth()
  const location = useLocation()

  if (!user) return <Navigate to="/auth" state={{ from: location.pathname }} replace />
  if (!['SHOP', 'ADMIN'].includes(user.role?.toUpperCase())) return <Navigate to="/assistant" replace />

  return children
}
