import { useEffect, useState } from 'react'
import { ExternalLink, PackageCheck, Search } from 'lucide-react'
import { useLocation } from 'react-router-dom'
import { endpoints, errorMessage, unwrap } from '../api/client'
import { Button, EmptyState, Field, Notice, PageLoader, StatusPill } from '../components/ui'
import { formatDate, formatMoney } from '../utils/format'

export default function OrdersPage() {
  const [orders, setOrders] = useState([])
  const [deliveryId, setDeliveryId] = useState('')
  const [delivery, setDelivery] = useState(null)
  const [notice, setNotice] = useState(null)
  const [loading, setLoading] = useState(true)
  const location = useLocation()
  useEffect(() => {
    if (location.state?.message) setNotice({ type: 'success', text: location.state.message })
    endpoints.orders.mine().then((response) => setOrders(unwrap(response) || [])).catch((error) => setNotice({ type: 'error', text: errorMessage(error) })).finally(() => setLoading(false))
  }, [location.state])

  const track = async (event) => {
    event.preventDefault(); setDelivery(null)
    try { setDelivery(unwrap(await endpoints.delivery.get(deliveryId))) }
    catch (error) { setNotice({ type: 'error', text: errorMessage(error) }) }
  }
  if (loading) return <PageLoader />
  return (
    <div className="page page-pad">
      <div className="page-heading"><div><p className="eyebrow">Purchases</p><h1>Your orders</h1><p>Follow every order from payment to delivery.</p></div></div>
      {notice && <Notice type={notice.type} onClose={() => setNotice(null)}>{notice.text}</Notice>}
      <section className="tracking-strip panel">
        <div><Search size={20} /><div><strong>Track a delivery</strong><small>Enter the DatShop order ID.</small></div></div>
        <form onSubmit={track}><Field aria-label="Order ID" type="number" min="1" required value={deliveryId} onChange={(e) => setDeliveryId(e.target.value)} placeholder="Order ID" /><Button type="submit" variant="secondary">Track</Button></form>
        {delivery && <div className="tracking-result"><span><small>Shipping code</small><strong>{delivery.order_code}</strong></span><span><small>Delivery fee</small><strong>{formatMoney(delivery.total_fee)}</strong></span><span><small>Expected</small><strong>{formatDate(delivery.expected_delivery_time)}</strong></span></div>}
      </section>
      {!orders.length ? <EmptyState icon={PackageCheck} title="No orders yet" text="When you place your first order, its progress will appear here." /> : <div className="order-list">{orders.map((order) => (
        <article className="order-card panel" key={order.orderId}>
          <header><div><small>Order #{order.orderId}</small><h3>{formatMoney(order.totalPrice)}</h3></div><StatusPill value={order.orderStatus} /></header>
          <div className="order-card__meta"><span><small>Placed</small>{formatDate(order.createdAt)}</span><span><small>Payment</small>{String(order.paymentMethod).replaceAll('_', ' ')}</span><span><small>Items</small>{order.productItems?.reduce((sum, item) => sum + item.quantity, 0) || 0}</span></div>
          {order.note && <p className="order-note">“{order.note}”</p>}
          <footer><span>Delivery: {String(order.requiredNote || '').replaceAll('_', ' ').toLowerCase()}</span>{order.paymentUrl && <a className="button button--secondary" href={order.paymentUrl} target="_blank" rel="noreferrer">Continue payment <ExternalLink size={16} /></a>}</footer>
        </article>
      ))}</div>}
    </div>
  )
}
