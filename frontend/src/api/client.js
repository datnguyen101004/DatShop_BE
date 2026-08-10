import axios from 'axios'

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

export const api = axios.create({
  baseURL: API_URL,
  headers: { 'Content-Type': 'application/json' },
})

export const getStoredSession = () => {
  try {
    return JSON.parse(localStorage.getItem('datshop-session'))
  } catch {
    return null
  }
}

api.interceptors.request.use((config) => {
  const token = getStoredSession()?.accessToken
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

let refreshing = null

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config
    const session = getStoredSession()
    if (error.response?.status === 401 && session?.refreshToken && !original?._retry && !original?.url?.includes('/refresh')) {
      original._retry = true
      refreshing ||= axios
        .post(`${API_URL}/api/v1/auth/refresh`, session.refreshToken, { headers: { 'Content-Type': 'text/plain' } })
        .then(({ data }) => {
          const tokens = data?.data || data
          const next = { ...session, ...tokens }
          localStorage.setItem('datshop-session', JSON.stringify(next))
          return next.accessToken
        })
        .finally(() => { refreshing = null })
      const token = await refreshing
      original.headers.Authorization = `Bearer ${token}`
      return api(original)
    }
    return Promise.reject(error)
  },
)

export const unwrap = (response) => {
  const body = response?.data
  return body && Object.prototype.hasOwnProperty.call(body, 'data') ? body.data : body
}

export const errorMessage = (error) =>
  error?.response?.data?.message || error?.response?.data?.error || error?.message || 'Something went wrong.'

export const endpoints = {
  auth: {
    login: (payload) => api.post('/api/v1/auth/login', payload),
    register: (payload) => api.post('/api/v1/auth/register', payload),
    logout: () => api.post('/api/v1/auth/logout'),
  },
  home: () => api.get('/api/v1/home/'),
  users: {
    me: () => api.get('/api/v1/user/profile'),
    byId: (id) => api.get(`/api/v1/user/profile/${id}`),
    updateContact: (payload) => api.put('/api/v1/user/profile/contact', payload),
  },
  products: {
    all: () => api.get('/api/v1/user/product/all'),
    one: (id) => api.get(`/api/v1/user/product/${id}`),
    create: (payload) => api.post('/api/v1/user/product/add', payload),
    update: (id, payload) => api.put(`/api/v1/user/product/edit/${id}`, payload),
    remove: (id) => api.delete(`/api/v1/user/product/delete/${id}`),
  },
  cart: {
    all: () => api.get('/api/v1/user/cart/'),
    add: (payload) => api.post('/api/v1/user/cart/add', payload),
    update: (items) => api.put('/api/v1/user/cart/update', { items }),
    remove: (productId) => api.delete(`/api/v1/user/cart/${productId}`),
    clear: () => api.delete('/api/v1/user/cart/clear'),
  },
  orders: {
    create: (payload) => api.post('/api/v1/user/order/create', payload),
    mine: () => api.get('/api/v1/user/order/all'),
    shop: () => api.get('/api/v1/user/order/shop/all'),
    confirm: (orderId) => api.post(`/api/v1/user/order/shop/${orderId}/confirm`),
  },
  coupons: {
    all: () => api.get('/api/v1/coupon/all'),
    create: (payload) => api.post('/api/v1/coupon/create', payload),
  },
  follows: {
    all: () => api.get('/api/v1/user/follow/all'),
    create: (payload) => api.post('/api/v1/user/follow/create', payload),
  },
  votes: {
    all: () => api.get('/api/v1/user/vote/all'),
    create: (payload) => api.post('/api/v1/user/vote/create', payload),
    remove: (id) => api.delete(`/api/v1/user/vote/delete/${id}`),
  },
  conversations: {
    all: () => api.get('/api/v1/chat/rooms/all'),
    one: (id) => api.get(`/api/v1/chat/rooms/${id}`),
    create: (receiverId) => api.post('/api/v1/chat/rooms/create', receiverId),
    support: () => api.post('/api/v1/chat/rooms/support'),
    supportInbox: () => api.get('/api/v1/chat/rooms/support/inbox'),
  },
  chatbot: {
    askGemini: (question) => api.post('/api/v1/chatbot/gemini/ask', question, { headers: { 'Content-Type': 'text/plain' } }),
    addInformation: (payload) => api.post('/api/v1/information/create', payload),
  },
  crawl: (url) => api.post('/api/v1/crawl/start', { url }),
  delivery: {
    create: (payload) => api.post('/api/v1/shop/delivery/create', payload),
    get: (orderId) => api.get(`/api/v1/shop/delivery/${orderId}`),
    cancel: (orderCodes) => api.post('/api/v1/shop/delivery/cancel', { order_codes: orderCodes }),
  },
}

export { API_URL }
