import { useEffect, useMemo, useState } from 'react'
import { Heart, ShoppingBag, Star, Trash2, UserRound } from 'lucide-react'
import { endpoints, errorMessage, unwrap } from '../api/client'
import { ProductCard } from '../components/ProductCard'
import { Button, EmptyState, Modal, Notice, PageLoader, StatusPill } from '../components/ui'
import { formatDate, formatMoney } from '../utils/format'

export default function CommunityPage() {
  const [profile, setProfile] = useState(null)
  const [products, setProducts] = useState([])
  const [follows, setFollows] = useState([])
  const [votes, setVotes] = useState([])
  const [selected, setSelected] = useState(null)
  const [favouriteBusyId, setFavouriteBusyId] = useState(null)
  const [cartBusyId, setCartBusyId] = useState(null)
  const [notice, setNotice] = useState(null)
  const [loading, setLoading] = useState(true)

  const load = async () => {
    const responses = await Promise.allSettled([
      endpoints.users.me(),
      endpoints.follows.all(),
      endpoints.products.all(),
      endpoints.votes.all(),
    ])
    if (responses[0].status === 'fulfilled') setProfile(unwrap(responses[0].value))
    if (responses[1].status === 'fulfilled') setFollows(unwrap(responses[1].value) || [])
    if (responses[2].status === 'fulfilled') setProducts(unwrap(responses[2].value) || [])
    if (responses[3].status === 'fulfilled') setVotes(unwrap(responses[3].value) || [])
    const failed = responses.find((response) => response.status === 'rejected')
    if (failed) setNotice({ type: 'error', text: errorMessage(failed.reason) })
    setLoading(false)
  }
  useEffect(() => { load() }, [])

  const favouriteProductIds = useMemo(() => new Set(follows
    .filter((item) => String(item.followType).toUpperCase() === 'PRODUCT' && !item.deleted && !item.isDeleted)
    .map((item) => Number(item.targetId))), [follows])
  const favouriteProducts = useMemo(() => products.filter((product) => favouriteProductIds.has(Number(product.id))), [products, favouriteProductIds])

  const toggleFavourite = async (product) => {
    setFavouriteBusyId(product.id)
    try {
      await endpoints.follows.create({ targetId: product.id, followType: 'PRODUCT' })
      const nextFollows = unwrap(await endpoints.follows.all()) || []
      setFollows(nextFollows)
      const remainsFavourite = nextFollows.some((item) => String(item.followType).toUpperCase() === 'PRODUCT' && Number(item.targetId) === Number(product.id) && !item.deleted && !item.isDeleted)
      setNotice({ type: 'success', text: remainsFavourite ? `${product.name} was added to your favourites.` : `${product.name} was removed from your favourites.` })
    } catch (error) {
      setNotice({ type: 'error', text: errorMessage(error) })
    } finally {
      setFavouriteBusyId(null)
    }
  }

  const addToCart = async (product) => {
    setCartBusyId(product.id)
    try {
      await endpoints.cart.add({ productId: product.id, quantity: 1 })
      setNotice({ type: 'success', text: `${product.name} is in your cart.` })
    } catch (error) {
      setNotice({ type: 'error', text: errorMessage(error) })
    } finally {
      setCartBusyId(null)
    }
  }

  const deleteVote = async (id) => {
    try { await endpoints.votes.remove(id); setVotes((current) => current.filter((item) => item.id !== id)) }
    catch (error) { setNotice({ type: 'error', text: errorMessage(error) }) }
  }

  if (loading) return <PageLoader />
  return <div className="page page-pad community-page">
    <div className="page-heading"><div><p className="eyebrow">Account and preferences</p><h1>Your profile</h1><p>Review your account details and the products you love.</p></div></div>
    {notice && <Notice type={notice.type} onClose={() => setNotice(null)}>{notice.text}</Notice>}

    <div className="profile-grid profile-grid--single">
      <section className="profile-card panel">
        <div className="profile-card__avatar">{profile?.avatarUrl ? <img src={profile.avatarUrl} alt="" /> : profile?.name?.charAt(0) || <UserRound />}</div>
        <div><StatusPill value={profile?.role || 'USER'} /><h2>{profile?.name}</h2><p>{profile?.email}</p></div>
        <dl><div><dt>Phone</dt><dd>{profile?.phoneNumber || 'Not added'}</dd></div><div><dt>Address</dt><dd>{profile?.address || 'Not added'}</dd></div><div><dt>Balance</dt><dd>{formatMoney(profile?.balance)}</dd></div><div><dt>Member since</dt><dd>{formatDate(profile?.createdAt)}</dd></div></dl>
      </section>
    </div>

    <section className="panel favourite-products">
      <div className="panel-heading"><div><p className="eyebrow">Saved for later</p><h2>Favourite products</h2><p>Products you have added to your favourites.</p></div></div>
      {favouriteProducts.length ? (
        <div className="product-grid">{favouriteProducts.map((product) => <ProductCard key={product.id} product={product} onAdd={addToCart} onFollow={toggleFavourite} onOpen={setSelected} busy={cartBusyId === product.id} favourite favouriteBusy={favouriteBusyId === product.id} />)}</div>
      ) : <EmptyState icon={Heart} title="No favourite products yet" text="Tap the heart on a product and it will appear here." />}
    </section>

    <section className="panel reviews-panel"><div className="panel-heading"><div><h2>Your reviews</h2><p>Feedback you have shared with the marketplace.</p></div></div>{votes.length ? <div className="review-list">{votes.map((item) => <article key={item.id}><div className="stars">{Array.from({ length: 5 }, (_, index) => <Star key={index} size={15} fill={index < item.voteValue ? 'currentColor' : 'none'} />)}</div><p>{item.comment}</p><small>{item.voteType.toLowerCase()} #{item.targetId}</small><button className="icon-button" onClick={() => deleteVote(item.id)} aria-label="Delete review"><Trash2 size={17} /></button></article>)}</div> : <EmptyState icon={Star} title="No reviews yet" text="Reviews you publish will be collected here." />}</section>

    {selected && <Modal title={selected.name} eyebrow={selected.category || 'Product details'} onClose={() => setSelected(null)} wide>
      <div className="product-detail">
        <div className="product-detail__image">{selected.imageUrl ? <img src={selected.imageUrl} alt={selected.name} /> : <span>{selected.name?.slice(0, 2).toUpperCase()}</span>}</div>
        <div><p className="product-detail__price">{formatMoney(selected.price)}</p><p>{selected.description || 'No description has been provided yet.'}</p><dl><div><dt>Stock</dt><dd>{selected.stockQuantity} available</dd></div><div><dt>Seller ID</dt><dd>#{selected.authorId}</dd></div><div><dt>Category</dt><dd>{selected.category || 'General'}</dd></div></dl><div className="button-row"><Button onClick={() => addToCart(selected)} disabled={!selected.stockQuantity} loading={cartBusyId === selected.id}><ShoppingBag size={17} /> Add to cart</Button><Button variant="secondary" onClick={() => toggleFavourite(selected)} loading={favouriteBusyId === selected.id}><Heart size={17} fill="currentColor" /> Favourited</Button></div></div>
      </div>
    </Modal>}
  </div>
}
