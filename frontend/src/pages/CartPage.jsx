import { useEffect, useMemo, useState } from 'react'
import { ArrowRight, Minus, Plus, ShoppingBag, Trash2 } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import { endpoints, errorMessage, unwrap } from '../api/client'
import { Button, EmptyState, Field, Notice, PageLoader } from '../components/ui'
import { formatMoney } from '../utils/format'

export default function CartPage() {
  const [items, setItems] = useState([])
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [notice, setNotice] = useState(null)
  const [checkout, setCheckout] = useState({ note: '', couponCode: '', paymentMethod: 'COD', requiredNote: 'CHOXEMHANGKHONGTHU' })
  const [contact, setContact] = useState({ name: '', phoneNumber: '', address: '', wardName: '', districtName: '', provinceName: '' })
  const navigate = useNavigate()

  const load = async () => {
    try {
      const [cart, catalog, profileResponse] = await Promise.all([endpoints.cart.all(), endpoints.products.all(), endpoints.users.me()])
      setItems(unwrap(cart) || []); setProducts(unwrap(catalog) || [])
      const profile = unwrap(profileResponse) || {}
      setContact({
        name: profile.name || '',
        phoneNumber: profile.phoneNumber || '',
        address: profile.address || '',
        wardName: profile.wardName || '',
        districtName: profile.districtName || '',
        provinceName: profile.provinceName || '',
      })
    } catch (error) { setNotice({ type: 'error', text: errorMessage(error) }) }
    finally { setLoading(false) }
  }
  useEffect(() => { load() }, [])

  const detailed = useMemo(() => items.map((item) => ({ ...item, product: products.find((product) => product.id === item.productId) })).filter((item) => item.product), [items, products])
  const subtotal = detailed.reduce((sum, item) => sum + Number(item.product.price) * item.quantity, 0)

  const changeQuantity = async (item, quantity) => {
    const nextQuantity = Math.max(0, Math.min(quantity, item.product.stockQuantity))
    setItems((current) => current.map((row) => row.productId === item.productId ? { ...row, quantity: nextQuantity } : row).filter((row) => row.quantity > 0))
    try { await endpoints.cart.update([{ productId: item.productId, quantity: nextQuantity }]) }
    catch (error) { setNotice({ type: 'error', text: errorMessage(error) }); load() }
  }
  const remove = async (productId) => {
    try { await endpoints.cart.remove(productId); setItems((current) => current.filter((item) => item.productId !== productId)) }
    catch (error) { setNotice({ type: 'error', text: errorMessage(error) }) }
  }
  const clear = async () => {
    try { await endpoints.cart.clear(); setItems([]) }
    catch (error) { setNotice({ type: 'error', text: errorMessage(error) }) }
  }
  const placeOrder = async (event) => {
    event.preventDefault(); setSaving(true); setNotice(null)
    try {
      await endpoints.users.updateContact(contact)
      const order = unwrap(await endpoints.orders.create({
        ...checkout,
        couponCode: checkout.couponCode.trim() || null,
        productItems: detailed.map((item) => ({ productId: item.productId, quantity: item.quantity })),
      }))
      try { await endpoints.cart.clear() } catch { /* order is already created */ }
      setItems([])
      if (order.paymentUrl) window.open(order.paymentUrl, '_blank', 'noopener,noreferrer')
      navigate('/orders', { state: { message: order.paymentUrl ? 'Order created. Complete payment in the new tab.' : 'Order placed successfully.' } })
    } catch (error) { setNotice({ type: 'error', text: errorMessage(error) }) }
    finally { setSaving(false) }
  }

  if (loading) return <PageLoader />
  return (
    <div className="page page-pad">
      <div className="page-heading"><div><p className="eyebrow">Your selection</p><h1>Shopping cart</h1><p>Review quantities and choose how you’d like to pay.</p></div>{items.length > 0 && <Button variant="ghost" onClick={clear}><Trash2 size={16} /> Clear cart</Button>}</div>
      {notice && <Notice type={notice.type}>{notice.text}</Notice>}
      {!detailed.length ? <EmptyState icon={ShoppingBag} title="Your cart is waiting" text="Discover something useful and it will show up here." action={<Button onClick={() => navigate('/')}>Browse products <ArrowRight size={17} /></Button>} /> : (
        <div className="cart-layout">
          <section className="cart-list panel">
            {detailed.map((item) => <article className="cart-item" key={item.productId}>
              <div className="cart-item__image">{item.product.imageUrl ? <img src={item.product.imageUrl} alt="" /> : item.product.name?.slice(0, 2)}</div>
              <div className="cart-item__main"><small>{item.product.category}</small><h3>{item.product.name}</h3><p>{formatMoney(item.product.price)} each</p></div>
              <div className="quantity"><button onClick={() => changeQuantity(item, item.quantity - 1)} aria-label="Decrease"><Minus size={15} /></button><span>{item.quantity}</span><button onClick={() => changeQuantity(item, item.quantity + 1)} aria-label="Increase"><Plus size={15} /></button></div>
              <strong>{formatMoney(item.product.price * item.quantity)}</strong>
              <button className="icon-button" onClick={() => remove(item.productId)} aria-label="Remove"><Trash2 size={18} /></button>
            </article>)}
          </section>
          <form className="checkout-card panel" onSubmit={placeOrder}>
            <div><p className="eyebrow">Order summary</p><h2>Ready to check out?</h2></div>
            <div className="summary-lines"><span>Subtotal <strong>{formatMoney(subtotal)}</strong></span><span>Delivery <strong>Calculated by shop</strong></span><span className="summary-total">Estimated total <strong>{formatMoney(subtotal)}</strong></span></div>
            <div className="checkout-contact">
              <div><strong>Delivery contact</strong><small>Saved to your profile for delivery.</small></div>
              <div className="form-grid">
                <Field className="span-2" label="Recipient name" required autoComplete="name" value={contact.name} onChange={(e) => setContact({ ...contact, name: e.target.value })} />
                <Field className="span-2" label="Phone number" type="tel" required autoComplete="tel" value={contact.phoneNumber} onChange={(e) => setContact({ ...contact, phoneNumber: e.target.value })} placeholder="0901234567" />
                <Field className="span-2" label="Street address" required autoComplete="street-address" value={contact.address} onChange={(e) => setContact({ ...contact, address: e.target.value })} placeholder="House number and street" />
                <Field label="Ward / commune" required value={contact.wardName} onChange={(e) => setContact({ ...contact, wardName: e.target.value })} />
                <Field label="District" required value={contact.districtName} onChange={(e) => setContact({ ...contact, districtName: e.target.value })} />
                <Field className="span-2" label="Province / city" required value={contact.provinceName} onChange={(e) => setContact({ ...contact, provinceName: e.target.value })} />
              </div>
            </div>
            <Field label="Coupon code" value={checkout.couponCode} onChange={(e) => setCheckout({ ...checkout, couponCode: e.target.value })} placeholder="Enter a coupon code (optional)" autoComplete="off" />
            <Field label="Payment method" as="select" value={checkout.paymentMethod} onChange={(e) => setCheckout({ ...checkout, paymentMethod: e.target.value })} options={[{ value: 'COD', label: 'Cash on delivery' }, { value: 'BANK_TRANSFER', label: 'Bank transfer' }, { value: 'CREDIT_CARD', label: 'Credit card' }, { value: 'OTHER', label: 'Other' }]} />
            <Field label="Delivery preference" as="select" value={checkout.requiredNote} onChange={(e) => setCheckout({ ...checkout, requiredNote: e.target.value })} options={[{ value: 'CHOXEMHANGKHONGTHU', label: 'Inspect, no trial' }, { value: 'CHOTHUHANG', label: 'Allow trial' }, { value: 'KHONGCHOXEMHANG', label: 'No inspection' }]} />
            <Field label="Order note" as="textarea" rows="3" value={checkout.note} onChange={(e) => setCheckout({ ...checkout, note: e.target.value })} placeholder="Anything the seller should know?" />
            <Button type="submit" loading={saving}>Place order <ArrowRight size={17} /></Button>
            <small>Orders containing products from different sellers may be split by the shop.</small>
          </form>
        </div>
      )}
    </div>
  )
}
