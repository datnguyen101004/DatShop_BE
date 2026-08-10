import { useCallback, useEffect, useState } from 'react'
import { PackageCheck, RefreshCw, Truck } from 'lucide-react'
import { endpoints, errorMessage, unwrap } from '../api/client'
import { Button, EmptyState, Notice, PageLoader, StatusPill } from '../components/ui'
import { formatDate, formatMoney } from '../utils/format'

const canPrepareDelivery = (status) => ['PENDING', 'PREPARING'].includes(status)

export default function ShopOrdersPage() {
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const [busyOrderId, setBusyOrderId] = useState(null)
  const [notice, setNotice] = useState(null)

  const load = useCallback(async () => {
    setLoading(true)
    try {
      setOrders(unwrap(await endpoints.orders.shop()) || [])
    } catch (error) {
      setNotice({ type: 'error', text: errorMessage(error) })
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { load() }, [load])

  const confirmAndCreateDelivery = async (order) => {
    setBusyOrderId(order.orderId)
    setNotice(null)
    try {
      await endpoints.orders.confirm(order.orderId)
      const delivery = unwrap(await endpoints.delivery.create({ orderId: order.orderId, note: '' }))
      if (!delivery?.order_code) throw new Error('Delivery creation failed: shipping code is missing.')
      setNotice({ type: 'success', text: `Order #${order.orderId} confirmed. Delivery ${delivery.order_code} was created.` })
      await load()
    } catch (error) {
      setNotice({ type: 'error', text: errorMessage(error) })
    } finally {
      setBusyOrderId(null)
    }
  }

  const cancelDelivery = async (order) => {
    if (!window.confirm(`Cancel delivery for order #${order.orderId}?`)) return
    setBusyOrderId(order.orderId)
    setNotice(null)
    try {
      const delivery = unwrap(await endpoints.delivery.get(order.orderId))
      if (!delivery?.order_code) throw new Error(`Shipping code was not found for order #${order.orderId}.`)
      await endpoints.delivery.cancel([delivery.order_code])
      setNotice({ type: 'success', text: `Delivery ${delivery.order_code} for order #${order.orderId} was cancelled.` })
      await load()
    } catch (error) {
      setNotice({ type: 'error', text: errorMessage(error) })
    } finally {
      setBusyOrderId(null)
    }
  }

  if (loading && !orders.length) return <PageLoader />

  return (
    <div className="page page-pad studio-page">
      <div className="page-heading">
        <div><p className="eyebrow">Order operations</p><h1>Orders & delivery</h1><p>Confirm customer orders and arrange delivery in one action.</p></div>
        <Button variant="secondary" onClick={load} loading={loading}><RefreshCw size={17} /> Refresh</Button>
      </div>
      {notice && <Notice type={notice.type} onClose={() => setNotice(null)}>{notice.text}</Notice>}

      <div className="studio-orders studio-orders--single">
        <section className="panel">
          <div className="panel-heading"><div><h2>Shop orders</h2><p>Review and manage orders placed with your store.</p></div></div>
          {orders.length ? (
            <div className="order-mini-list">
              {orders.map((order) => (
                <article key={order.orderId}>
                  <div><small>Order #{order.orderId} · customer #{order.userId}</small><strong>{formatMoney(order.totalPrice)}</strong></div>
                  <StatusPill value={order.orderStatus} />
                  <div className="shop-order-actions">
                    <span>{order.productItems?.length || 0} products · {formatDate(order.createdAt)}</span>
                    {canPrepareDelivery(order.orderStatus) && (
                      <Button onClick={() => confirmAndCreateDelivery(order)} loading={busyOrderId === order.orderId}>
                        <Truck size={16} /> {order.orderStatus === 'PENDING' ? 'Confirm & create delivery' : 'Create delivery'}
                      </Button>
                    )}
                    {order.orderStatus === 'WAITING_FOR_PAYMENT' && <small>Waiting for successful payment before delivery can be created.</small>}
                    {order.orderStatus === 'SHIPPING' && (
                      <>
                        <small>Delivery has been created and is in transit.</small>
                        <Button variant="danger" onClick={() => cancelDelivery(order)} loading={busyOrderId === order.orderId}>Cancel delivery</Button>
                      </>
                    )}
                  </div>
                </article>
              ))}
            </div>
          ) : <EmptyState icon={PackageCheck} title="No shop orders" text="New orders for your products will appear here." />}
        </section>

      </div>
    </div>
  )
}
