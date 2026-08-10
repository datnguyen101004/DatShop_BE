import { useEffect, useMemo, useState } from 'react'
import { ArrowRight, Bot, Heart, PackageSearch, Search, ShieldCheck, ShoppingBag, Sparkles, Store } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import { endpoints, errorMessage, unwrap } from '../api/client'
import { useAuth } from '../context/useAuth'
import { ProductCard } from '../components/ProductCard'
import { Button, EmptyState, Modal, Notice, PageLoader } from '../components/ui'
import { formatMoney } from '../utils/format'

export default function HomePage() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [products, setProducts] = useState([])
  const [categories, setCategories] = useState([])
  const [category, setCategory] = useState('All')
  const [query, setQuery] = useState('')
  const [selected, setSelected] = useState(null)
  const [follows, setFollows] = useState([])
  const [loading, setLoading] = useState(true)
  const [busyId, setBusyId] = useState(null)
  const [favouriteBusyId, setFavouriteBusyId] = useState(null)
  const [notice, setNotice] = useState(null)

  useEffect(() => {
    const load = async () => {
      setLoading(true)
      const requests = [endpoints.home(), endpoints.products.all()]
      if (user) requests.push(endpoints.follows.all())
      const [home, all, followResponse] = await Promise.allSettled(requests)
      if (home.status === 'fulfilled') {
        const homeData = unwrap(home.value) || {}
        setCategories(homeData.categories || [])
        if (all.status !== 'fulfilled') setProducts(homeData.products || [])
      }
      if (all.status === 'fulfilled') setProducts(unwrap(all.value) || [])
      if (followResponse?.status === 'fulfilled') setFollows(unwrap(followResponse.value) || [])
      else setFollows([])
      if (home.status === 'rejected' && all.status === 'rejected') setNotice({ type: 'error', text: errorMessage(all.reason) })
      setLoading(false)
    }
    load()
  }, [user])

  const favouriteProductIds = useMemo(() => new Set(follows
    .filter((item) => String(item.followType).toUpperCase() === 'PRODUCT' && !item.deleted && !item.isDeleted)
    .map((item) => Number(item.targetId))), [follows])

  const visible = useMemo(() => products.filter((product) => {
    const matchesCategory = category === 'All' || product.category === category
    const haystack = `${product.name} ${product.description} ${product.category}`.toLowerCase()
    return matchesCategory && haystack.includes(query.toLowerCase())
  }), [products, category, query])

  const requireUser = () => {
    if (!user) { navigate('/auth', { state: { from: '/' } }); return false }
    return true
  }
  const addToCart = async (product) => {
    if (!requireUser()) return
    setBusyId(product.id)
    try { await endpoints.cart.add({ productId: product.id, quantity: 1 }); setNotice({ type: 'success', text: `${product.name} is in your cart.` }) }
    catch (error) { setNotice({ type: 'error', text: errorMessage(error) }) }
    finally { setBusyId(null) }
  }
  const follow = async (product) => {
    if (!requireUser()) return
    setFavouriteBusyId(product.id)
    try {
      await endpoints.follows.create({ targetId: product.id, followType: 'PRODUCT' })
      const nextFollows = unwrap(await endpoints.follows.all()) || []
      setFollows(nextFollows)
      const isFavourite = nextFollows.some((item) => String(item.followType).toUpperCase() === 'PRODUCT' && Number(item.targetId) === Number(product.id) && !item.deleted && !item.isDeleted)
      setNotice({ type: 'success', text: isFavourite ? `${product.name} was added to your favourites.` : `${product.name} was removed from your favourites.` })
    }
    catch (error) { setNotice({ type: 'error', text: errorMessage(error) }) }
    finally { setFavouriteBusyId(null) }
  }
  const openProduct = async (product) => {
    setSelected(product)
    try { setSelected(unwrap(await endpoints.products.one(product.id))) } catch { /* card data is enough */ }
  }

  if (loading) return <PageLoader />

  return (
    <div>
      <section className="hero page-pad">
        <div className="hero__copy">
          <p className="eyebrow">Made nearby. Chosen carefully.</p>
          <h1>A better place to find your <em>next favorite thing.</em></h1>
          <p>Shop original goods from independent sellers, talk directly with makers, and get smart help whenever you need it.</p>
          <div className="hero__actions"><a href="#products" className="button button--primary">Explore the collection <ArrowRight size={17} /></a><Button variant="light" onClick={() => navigate('/assistant')}><Bot size={17} /> Ask the assistant</Button></div>
          <div className="hero__proof"><span><ShieldCheck />Protected payments</span><span><Store />Independent shops</span><span><Sparkles />AI discovery</span></div>
        </div>
        <div className="hero__visual" aria-hidden="true">
          <div className="hero-card hero-card--main"><span>Editor’s pick</span><strong>Everyday pieces,<br />made memorable.</strong><small>New collection · 2026</small></div>
          <div className="hero-card hero-card--float"><span className="mini-icon"><ShoppingBag /></span><div><strong>{products.length || 'Fresh'} finds</strong><small>Ready to discover</small></div></div>
          <div className="hero-shape hero-shape--one" /><div className="hero-shape hero-shape--two" />
        </div>
      </section>

      {notice && <div className="floating-notice"><Notice type={notice.type} onClose={() => setNotice(null)}>{notice.text}</Notice></div>}

      <section className="catalog page-pad" id="products">
        <div className="section-heading"><div><p className="eyebrow">Explore the marketplace</p><h2>Find something worth keeping.</h2></div><p>{visible.length} items from independent sellers</p></div>
        <div className="catalog-tools">
          <label className="search-box"><Search size={18} /><input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Search products, categories…" /></label>
          <div className="chip-row"><button className={category === 'All' ? 'active' : ''} onClick={() => setCategory('All')}>All</button>{categories.map((item) => <button className={category === item ? 'active' : ''} key={item} onClick={() => setCategory(item)}>{item}</button>)}</div>
        </div>
        {visible.length ? <div className="product-grid">{visible.map((product) => <ProductCard key={product.id} product={product} onAdd={addToCart} onFollow={follow} onOpen={openProduct} busy={busyId === product.id} favourite={favouriteProductIds.has(Number(product.id))} favouriteBusy={favouriteBusyId === product.id} />)}</div> : <EmptyState icon={PackageSearch} title="No products found" text="Try another search or choose a different category." />}
      </section>

      <section className="assistant-banner page-pad"><div><span className="assistant-banner__icon"><Bot /></span><div><p className="eyebrow">DatShop guide</p><h2>Not sure what fits? Ask in plain language.</h2><p>Our assistant searches the marketplace context and answers with relevant recommendations.</p></div></div><Button variant="light" onClick={() => navigate('/assistant')}>Start a conversation <ArrowRight size={17} /></Button></section>

      {selected && <Modal title={selected.name} eyebrow={selected.category || 'Product details'} onClose={() => setSelected(null)} wide>
        <div className="product-detail">
          <div className="product-detail__image">{selected.imageUrl ? <img src={selected.imageUrl} alt={selected.name} /> : <span>{selected.name?.slice(0, 2).toUpperCase()}</span>}</div>
          <div><p className="product-detail__price">{formatMoney(selected.price)}</p><p>{selected.description || 'No description has been provided yet.'}</p><dl><div><dt>Stock</dt><dd>{selected.stockQuantity} available</dd></div><div><dt>Seller ID</dt><dd>#{selected.authorId}</dd></div><div><dt>Category</dt><dd>{selected.category || 'General'}</dd></div></dl><div className="button-row"><Button onClick={() => addToCart(selected)} disabled={!selected.stockQuantity}><ShoppingBag size={17} /> Add to cart</Button><Button variant="secondary" onClick={() => follow(selected)} loading={favouriteBusyId === selected.id}><Heart size={17} fill={favouriteProductIds.has(Number(selected.id)) ? 'currentColor' : 'none'} /> {favouriteProductIds.has(Number(selected.id)) ? 'Favourited' : 'Favourite'}</Button></div></div>
        </div>
      </Modal>}
    </div>
  )
}
