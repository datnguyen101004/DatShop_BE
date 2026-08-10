import { Heart, ShoppingBag, Star } from 'lucide-react'
import { formatMoney } from '../utils/format'
import { Button } from './ui'

export function ProductCard({ product, onAdd, onOpen, onFollow, busy, favourite = false, favouriteBusy = false }) {
  return (
    <article className="product-card">
      <button className="product-card__image" onClick={() => onOpen?.(product)} aria-label={`View ${product.name}`}>
        {product.imageUrl ? <img src={product.imageUrl} alt={product.name} /> : <span>{product.name?.slice(0, 2).toUpperCase()}</span>}
        {product.stockQuantity <= 5 && <small>{product.stockQuantity ? `Only ${product.stockQuantity} left` : 'Sold out'}</small>}
      </button>
      <div className="product-card__body">
        <div className="product-card__meta"><span>{product.category || 'Everyday goods'}</span><span><Star size={13} fill="currentColor" />4.8</span></div>
        <button className="text-button product-card__title" onClick={() => onOpen?.(product)}>{product.name}</button>
        <p>{product.description || 'A useful find from an independent seller.'}</p>
        <div className="product-card__footer">
          <strong>{formatMoney(product.price)}</strong>
          <div>
            <button
              className={`icon-button favourite-button ${favourite ? 'is-favourite' : ''}`}
              onClick={() => onFollow?.(product)}
              aria-label={favourite ? `Remove ${product.name} from favourites` : `Add ${product.name} to favourites`}
              disabled={favouriteBusy}
            ><Heart size={18} fill={favourite ? 'currentColor' : 'none'} /></button>
            <Button onClick={() => onAdd?.(product)} disabled={!product.stockQuantity} loading={busy}><ShoppingBag size={16} /> Add</Button>
          </div>
        </div>
      </div>
    </article>
  )
}
