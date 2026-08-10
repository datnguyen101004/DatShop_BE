import { Route, Routes } from 'react-router-dom'
import { AppShell } from './components/AppShell'
import { RequireAuth } from './components/RequireAuth'
import { RequireAdmin } from './components/RequireAdmin'
import { RequireShop } from './components/RequireShop'
import AdminSupportPage from './pages/AdminSupportPage'
import AssistantPage from './pages/AssistantPage'
import AuthPage from './pages/AuthPage'
import CartPage from './pages/CartPage'
import CommunityPage from './pages/CommunityPage'
import HomePage from './pages/HomePage'
import KnowledgePage from './pages/KnowledgePage'
import OrdersPage from './pages/OrdersPage'
import PaymentCallbackPage from './pages/PaymentCallbackPage'
import ShopOrdersPage from './pages/ShopOrdersPage'
import StudioPage from './pages/StudioPage'

const Private = ({ children }) => <RequireAuth>{children}</RequireAuth>
const AdminOnly = ({ children }) => <RequireAdmin>{children}</RequireAdmin>
const ShopOnly = ({ children }) => <RequireShop>{children}</RequireShop>

export default function App() {
  return <Routes><Route element={<AppShell />}><Route index element={<HomePage />} /><Route path="auth" element={<AuthPage />} /><Route path="cart" element={<Private><CartPage /></Private>} /><Route path="orders" element={<Private><OrdersPage /></Private>} /><Route path="payment/callback" element={<PaymentCallbackPage />} /><Route path="studio" element={<ShopOnly><StudioPage /></ShopOnly>} /><Route path="shop/orders" element={<ShopOnly><ShopOrdersPage /></ShopOnly>} /><Route path="community" element={<Private><CommunityPage /></Private>} /><Route path="admin/support" element={<AdminOnly><AdminSupportPage /></AdminOnly>} /><Route path="assistant" element={<AssistantPage />} /><Route path="assistant/knowledge" element={<ShopOnly><KnowledgePage /></ShopOnly>} /><Route path="*" element={<HomePage />} /></Route></Routes>
}
