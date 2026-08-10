import { useEffect } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { Notice } from '../components/ui'

export default function PaymentCallbackPage() {
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const isSuccessful = searchParams.get('vnp_ResponseCode') === '00'
    && searchParams.get('vnp_TransactionStatus') === '00'

  useEffect(() => {
    const redirectTimer = window.setTimeout(() => {
      navigate('/orders', { replace: true })
    }, 1000)

    return () => window.clearTimeout(redirectTimer)
  }, [navigate])

  return (
    <div className="payment-callback-overlay" role="status" aria-live="polite">
      <div className="payment-callback-popup">
        <Notice type={isSuccessful ? 'success' : 'error'}>
          {isSuccessful ? 'Thanh toán thành công.' : 'Thanh toán không thành công.'}
        </Notice>
      </div>
    </div>
  )
}
