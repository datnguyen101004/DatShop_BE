import { useEffect, useState } from 'react'
import { Bot, Boxes, Database, House, LogOut, Menu, MessageCircle, PackageCheck, PackagePlus, ShoppingBag, Store, X } from 'lucide-react'
import { NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/useAuth'
import { SupportChatbox } from './SupportChatbox'

const links = [
  { to: '/', label: 'Discover', icon: House },
  { to: '/cart', label: 'Cart', icon: ShoppingBag, private: true, hiddenFor: ['ADMIN'] },
  { to: '/orders', label: 'Orders', icon: PackageCheck, private: true, hiddenFor: ['ADMIN'] },
  { to: '/studio', label: 'Studio', icon: Boxes, private: true, roles: ['SHOP'] },
  { to: '/studio?newProduct=1', label: 'Add product', icon: PackagePlus, private: true, roles: ['ADMIN'] },
  { to: '/shop/orders', label: 'Orders & delivery', icon: PackageCheck, private: true, roles: ['SHOP', 'ADMIN'] },
  { to: '/admin/support', label: 'Support inbox', icon: MessageCircle, private: true, roles: ['ADMIN'] },
  { to: '/assistant', label: 'AI assistant', icon: Bot },
  { to: '/assistant/knowledge', label: 'Teach AI', icon: Database, private: true, roles: ['SHOP'], action: true },
  { to: '/assistant/knowledge', label: 'Add AI info', icon: Database, private: true, roles: ['ADMIN'] },
]

export function AppShell() {
  const { user, logout } = useAuth()
  const [open, setOpen] = useState(false)
  const location = useLocation()
  const navigate = useNavigate()
  const role = user?.role?.toUpperCase()
  const visibleLinks = links.filter((item) => (
    (!item.private || user)
    && (!item.roles || item.roles.includes(role))
    && (!item.hiddenFor || !item.hiddenFor.includes(role))
  ))
  useEffect(() => setOpen(false), [location.pathname])

  const signOut = async () => { await logout(); navigate('/') }

  return (
    <div className="app-shell">
      <header className="topbar">
        <NavLink to="/" className="brand" aria-label="DatShop home">
          <span className="brand__mark"><Store size={19} /></span>
          <span>DatShop</span>
        </NavLink>
        <nav className="desktop-nav" aria-label="Primary navigation">
          {visibleLinks.map(({ to, label, action }) => (
            <NavLink key={to} to={to} className={({ isActive }) => [isActive ? 'active' : '', action ? 'nav-action' : ''].filter(Boolean).join(' ')}>{label}</NavLink>
          ))}
        </nav>
        <div className="topbar__actions">
          {user ? (
            <>
              <NavLink to="/community" className="user-chip">
                <span>{user.avatarUrl ? <img src={user.avatarUrl} alt="" /> : user.name?.charAt(0) || 'D'}</span>
                <div><strong>{user.name || 'Member'}</strong><small>{user.role || 'USER'}</small></div>
              </NavLink>
              <button className="icon-button desktop-only" onClick={signOut} aria-label="Sign out"><LogOut size={19} /></button>
            </>
          ) : <NavLink to="/auth" className="button button--primary">Sign in</NavLink>}
          <button className="icon-button menu-button" onClick={() => setOpen((value) => !value)} aria-label="Toggle menu">{open ? <X /> : <Menu />}</button>
        </div>
      </header>

      {open && (
        <nav className="mobile-nav" aria-label="Mobile navigation">
          {visibleLinks.map(({ to, label, icon: Icon }) => <NavLink key={to} to={to}><Icon size={18} />{label}</NavLink>)}
          {user && <button onClick={signOut}><LogOut size={18} />Sign out</button>}
        </nav>
      )}

      <main><Outlet /></main>
      {user && role !== 'ADMIN' && <SupportChatbox />}
      <footer className="footer">
        <div><span className="brand"><span className="brand__mark"><Store size={17} /></span>DatShop</span><p>Useful goods, independent shops, one thoughtful marketplace.</p></div>
        <div><strong>Shop</strong><NavLink to="/">Discover</NavLink>{role !== 'ADMIN' && <NavLink to="/cart">Your cart</NavLink>}{role === 'ADMIN' && <NavLink to="/studio?newProduct=1">Add product</NavLink>}</div>
        <div><strong>Connect</strong>{role === 'ADMIN' && <NavLink to="/admin/support">Support inbox</NavLink>}{['SHOP', 'ADMIN'].includes(role) && <NavLink to="/assistant/knowledge">Add AI info</NavLink>}<NavLink to="/assistant">AI assistant</NavLink></div>
        <small>© {new Date().getFullYear()} DatShop. Built for local commerce.</small>
      </footer>
    </div>
  )
}
