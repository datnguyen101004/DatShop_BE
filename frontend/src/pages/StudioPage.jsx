import { useEffect, useMemo, useState } from 'react'
import { Edit3, PackagePlus, Plus, Search, Store, TicketPercent, Trash2, WandSparkles } from 'lucide-react'
import { useSearchParams } from 'react-router-dom'
import { endpoints, errorMessage, unwrap } from '../api/client'
import { useAuth } from '../context/useAuth'
import { Button, EmptyState, Field, Modal, Notice, PageLoader, StatusPill } from '../components/ui'
import { formatDate, formatMoney } from '../utils/format'

const emptyProduct = { name: '', description: '', imageUrl: '', price: '', stockQuantity: '', category: '' }
const emptyCoupon = { discountAmount: '', quantity: '', expirationDate: '', couponType: 'PERCENT' }

export default function StudioPage() {
  const { user } = useAuth()
  const isShop = user?.role?.toUpperCase() === 'SHOP'
  const [searchParams, setSearchParams] = useSearchParams()
  const [tab, setTab] = useState('products')
  const [products, setProducts] = useState([])
  const [coupons, setCoupons] = useState([])
  const [loading, setLoading] = useState(true)
  const [notice, setNotice] = useState(null)
  const [productModal, setProductModal] = useState(null)
  const [saving, setSaving] = useState(false)

  const load = async () => {
    setLoading(true)
    const results = await Promise.allSettled([endpoints.products.all(), endpoints.coupons.all()])
    if (results[0].status === 'fulfilled') setProducts(unwrap(results[0].value) || [])
    if (results[1].status === 'fulfilled') {
      const couponData = unwrap(results[1].value)
      setCoupons(Array.isArray(couponData) ? couponData : [])
    }
    setLoading(false)
  }
  useEffect(() => { load() }, [])
  useEffect(() => {
    if (searchParams.get('newProduct') === '1') {
      setTab('products')
      setProductModal({ ...emptyProduct })
      setSearchParams({}, { replace: true })
    }
  }, [searchParams, setSearchParams])
  const mine = useMemo(() => products.filter((product) => !user?.userId || product.authorId === user.userId), [products, user])

  const saveProduct = async (event) => {
    event.preventDefault(); setSaving(true)
    const payload = { ...productModal, price: Number(productModal.price), stockQuantity: Number(productModal.stockQuantity) }
    try {
      if (productModal.id) await endpoints.products.update(productModal.id, payload)
      else await endpoints.products.create(payload)
      setProductModal(null); setNotice({ type: 'success', text: `Product ${payload.id ? 'updated' : 'saved'} successfully.` }); load()
    } catch (error) { setNotice({ type: 'error', text: errorMessage(error) }) }
    finally { setSaving(false) }
  }
  const removeProduct = async (id) => {
    if (!window.confirm('Delete this product? This cannot be undone.')) return
    try { await endpoints.products.remove(id); setProducts((current) => current.filter((item) => item.id !== id)) }
    catch (error) { setNotice({ type: 'error', text: errorMessage(error) }) }
  }

  if (loading) return <PageLoader />
  return (
    <div className="page page-pad studio-page">
      <div className="page-heading"><div><p className="eyebrow">Seller workspace</p><h1>Your shop studio</h1><p>{isShop ? 'Manage the catalog, coupons, and product imports.' : 'Manage the catalog and coupons.'}</p></div><Button onClick={() => setProductModal({ ...emptyProduct })}><Plus size={17} /> New product</Button></div>
      {!['SHOP', 'ADMIN'].includes(user?.role?.toUpperCase()) && <Notice type="error">Your account role is {user?.role || 'USER'}. Product management requires a SHOP or ADMIN account.</Notice>}
      {notice && <Notice type={notice.type} onClose={() => setNotice(null)}>{notice.text}</Notice>}
      <div className="tab-bar">
        <button className={tab === 'products' ? 'active' : ''} onClick={() => setTab('products')}><Store size={17} /> Products</button>
        <button className={tab === 'coupons' ? 'active' : ''} onClick={() => setTab('coupons')}><TicketPercent size={17} /> Coupons</button>
        {isShop && <button className={tab === 'import' ? 'active' : ''} onClick={() => setTab('import')}><WandSparkles size={17} /> Product importer</button>}
      </div>
      {tab === 'products' && <ProductPanel products={mine} onEdit={(product) => setProductModal({ ...product })} onRemove={removeProduct} />}
      {tab === 'coupons' && <CouponPanel coupons={coupons} reload={load} notice={setNotice} />}
      {isShop && tab === 'import' && <ImportPanel onUse={(product) => { setProductModal({ ...emptyProduct, name: product.name, imageUrl: product.imgUrl, price: String(product.price || '').replace(/\D/g, '') }); }} notice={setNotice} />}

      {productModal && <Modal title={productModal.id ? 'Edit product' : 'Add a product'} eyebrow="Catalog" onClose={() => setProductModal(null)} wide>
        <form className="form-grid" onSubmit={saveProduct}>
          <Field label="Product name" required value={productModal.name} onChange={(e) => setProductModal({ ...productModal, name: e.target.value })} />
          <Field label="Category" required value={productModal.category} onChange={(e) => setProductModal({ ...productModal, category: e.target.value })} placeholder="Home, Fashion, Tech…" />
          <Field label="Price (VND)" type="number" min="0" required value={productModal.price} onChange={(e) => setProductModal({ ...productModal, price: e.target.value })} />
          <Field label="Stock quantity" type="number" min="0" required value={productModal.stockQuantity} onChange={(e) => setProductModal({ ...productModal, stockQuantity: e.target.value })} />
          <Field className="span-2" label="Image URL" type="url" value={productModal.imageUrl || ''} onChange={(e) => setProductModal({ ...productModal, imageUrl: e.target.value })} placeholder="https://…" />
          <Field className="span-2" label="Description" as="textarea" rows="4" required value={productModal.description} onChange={(e) => setProductModal({ ...productModal, description: e.target.value })} />
          <div className="form-actions span-2"><Button type="button" variant="ghost" onClick={() => setProductModal(null)}>Cancel</Button><Button type="submit" loading={saving}>{productModal.id ? 'Save changes' : 'Publish product'}</Button></div>
        </form>
      </Modal>}
    </div>
  )
}

function ProductPanel({ products, onEdit, onRemove }) {
  return <section className="panel studio-panel"><div className="panel-heading"><div><h2>Catalog</h2><p>{products.length} products managed by your shop.</p></div></div>{products.length ? <div className="data-table"><div className="data-table__head"><span>Product</span><span>Price</span><span>Stock</span><span>Category</span><span /></div>{products.map((product) => <div className="data-table__row" key={product.id}><span className="product-cell"><span>{product.imageUrl ? <img src={product.imageUrl} alt="" /> : product.name?.slice(0, 2)}</span><b>{product.name}</b></span><span>{formatMoney(product.price)}</span><span>{product.stockQuantity}</span><span>{product.category}</span><span className="table-actions"><button onClick={() => onEdit(product)} aria-label="Edit"><Edit3 size={17} /></button><button onClick={() => onRemove(product.id)} aria-label="Delete"><Trash2 size={17} /></button></span></div>)}</div> : <EmptyState icon={PackagePlus} title="Build your catalog" text="Add your first product to start selling." />}</section>
}

function CouponPanel({ coupons, reload, notice }) {
  const [form, setForm] = useState(emptyCoupon)
  const [saving, setSaving] = useState(false)
  const submit = async (event) => {
    event.preventDefault(); setSaving(true)
    try { await endpoints.coupons.create({ ...form, discountAmount: Number(form.discountAmount), quantity: Number(form.quantity) }); setForm(emptyCoupon); notice({ type: 'success', text: 'Coupon created.' }); reload() }
    catch (error) { notice({ type: 'error', text: errorMessage(error) }) }
    finally { setSaving(false) }
  }
  return <div className="studio-split"><form className="panel form-stack" onSubmit={submit}><div><p className="eyebrow">New promotion</p><h2>Create a coupon</h2></div><Field label="Discount type" as="select" value={form.couponType} onChange={(e) => setForm({ ...form, couponType: e.target.value })} options={[{ value: 'PERCENT', label: 'Percentage' }, { value: 'MONEY', label: 'Fixed amount' }, { value: 'FREE_SHIPPING', label: 'Free shipping' }]} /><Field label="Discount amount" type="number" min="0" required value={form.discountAmount} onChange={(e) => setForm({ ...form, discountAmount: e.target.value })} /><Field label="Available uses" type="number" min="1" required value={form.quantity} onChange={(e) => setForm({ ...form, quantity: e.target.value })} /><Field label="Expiration" type="date" required value={form.expirationDate} onChange={(e) => setForm({ ...form, expirationDate: e.target.value })} /><Button type="submit" loading={saving}>Create coupon</Button></form><section className="panel"><div className="panel-heading"><div><h2>Active offers</h2><p>Share the generated code with customers.</p></div></div><div className="coupon-list">{coupons.map((coupon) => <article key={coupon.id}><div><small>{coupon.couponType}</small><strong>{coupon.code}</strong></div><span>{coupon.couponType === 'PERCENT' ? `${coupon.discountAmount}%` : formatMoney(coupon.discountAmount)}</span><div><StatusPill value={coupon.active ? 'ACTIVE' : 'INACTIVE'} /><small>{coupon.usedCount}/{coupon.quantity} used · expires {formatDate(coupon.expirationDate)}</small></div></article>)}</div></section></div>
}

function ImportPanel({ onUse, notice }) {
  const [url, setUrl] = useState('')
  const [results, setResults] = useState([])
  const [busy, setBusy] = useState(false)
  const crawl = async (event) => { event.preventDefault(); setBusy(true); try { setResults(unwrap(await endpoints.crawl(url)) || []) } catch (error) { notice({ type: 'error', text: errorMessage(error) }) } finally { setBusy(false) } }
  return <section className="panel import-panel"><div className="panel-heading"><div><p className="eyebrow">Product importer</p><h2>Bring in product ideas from a URL</h2><p>Import product details that you can refine before publishing.</p></div></div><form className="inline-form" onSubmit={crawl}><Field aria-label="Page URL" type="url" required value={url} onChange={(e) => setUrl(e.target.value)} placeholder="https://example.com/collection" /><Button type="submit" loading={busy}><Search size={17} /> Import</Button></form>{results.length > 0 && <div className="import-grid">{results.map((product) => <article key={product.id}><div>{product.imgUrl ? <img src={product.imgUrl} alt="" /> : <WandSparkles />}</div><h3>{product.name}</h3><p>{product.price}</p><Button variant="secondary" onClick={() => onUse(product)}>Use as draft</Button></article>)}</div>}</section>
}
