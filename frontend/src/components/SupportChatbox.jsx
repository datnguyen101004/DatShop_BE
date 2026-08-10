import { useEffect, useRef, useState } from 'react'
import { Headphones, LoaderCircle, MessageCircle, Send, X } from 'lucide-react'
import { endpoints, errorMessage, unwrap } from '../api/client'
import { useAuth } from '../context/useAuth'
import { formatDate } from '../utils/format'
import { ADMIN_SUPPORT_ID, appendUniqueMessage, connectToConversation } from '../utils/supportChat'

export function SupportChatbox() {
  const { user } = useAuth()
  const [open, setOpen] = useState(false)
  const [room, setRoom] = useState(null)
  const [draft, setDraft] = useState('')
  const [loading, setLoading] = useState(false)
  const [connected, setConnected] = useState(false)
  const [error, setError] = useState('')
  const [unread, setUnread] = useState(0)
  const clientRef = useRef(null)
  const streamRef = useRef(null)
  const openRef = useRef(false)

  useEffect(() => { openRef.current = open }, [open])
  useEffect(() => {
    streamRef.current?.scrollTo({ top: streamRef.current.scrollHeight, behavior: 'smooth' })
  }, [room?.listMessages, open])

  useEffect(() => {
    if (!room?.conversationId) return undefined
    const client = connectToConversation({
      conversationId: room.conversationId,
      onConnectionChange: setConnected,
      onError: setError,
      onMessage: (incoming) => {
        setRoom((current) => current
          ? { ...current, listMessages: appendUniqueMessage(current.listMessages, incoming) }
          : current)
        if (!openRef.current && incoming.senderId === ADMIN_SUPPORT_ID) {
          setUnread((current) => current + 1)
        }
      },
    })
    clientRef.current = client
    return () => { clientRef.current = null; client.deactivate() }
  }, [room?.conversationId])

  const loadConversation = async () => {
    setLoading(true)
    setError('')
    try {
      setRoom(unwrap(await endpoints.conversations.support()))
    } catch (requestError) {
      setError(errorMessage(requestError))
    } finally {
      setLoading(false)
    }
  }

  const toggle = () => {
    const nextOpen = !open
    setOpen(nextOpen)
    if (nextOpen) {
      setUnread(0)
      if (!room && !loading) loadConversation()
    }
  }

  const send = (event) => {
    event.preventDefault()
    const message = draft.trim()
    if (!message || !connected || !room) return
    clientRef.current.publish({
      destination: `/app/chat/${room.conversationId}`,
      body: JSON.stringify({ receiverId: ADMIN_SUPPORT_ID, message }),
    })
    setDraft('')
  }

  return (
    <div className={`support-chatbox ${open ? 'support-chatbox--open' : ''}`}>
      {open && (
        <section className="support-chatbox__panel" aria-label="Trò chuyện với quản trị viên">
          <header>
            <span><Headphones size={20} /></span>
            <div><strong>Hỗ trợ DatShop</strong><small><i className={connected ? 'online' : ''} /> {connected ? 'Đang trực tuyến' : 'Đang kết nối'}</small></div>
            <button onClick={toggle} aria-label="Đóng trò chuyện"><X size={19} /></button>
          </header>
          <div className="support-chatbox__messages" ref={streamRef}>
            {loading && <div className="support-chatbox__loading"><LoaderCircle className="spin" /> Đang mở cuộc trò chuyện…</div>}
            {!loading && error && <div className="support-chatbox__error">{error}<button onClick={loadConversation}>Thử lại</button></div>}
            {!loading && !error && !(room?.listMessages?.length) && (
              <div className="support-chatbox__welcome"><Headphones /><strong>Xin chào {user?.name || 'bạn'}!</strong><p>Hãy để lại câu hỏi. Quản trị viên DatShop sẽ trả lời ngay tại đây.</p></div>
            )}
            {(room?.listMessages || []).map((item, index) => (
              <div key={item.id || index} className={`message ${item.senderId === user?.userId ? 'message--mine' : ''}`}>
                <p>{item.message}</p><small>{formatDate(item.sentAt)}</small>
              </div>
            ))}
          </div>
          <form className="support-chatbox__compose" onSubmit={send}>
            <input value={draft} maxLength={2000} onChange={(event) => setDraft(event.target.value)} placeholder="Nhập tin nhắn…" aria-label="Tin nhắn hỗ trợ" disabled={!room} />
            <button type="submit" aria-label="Gửi tin nhắn" disabled={!draft.trim() || !connected}><Send size={18} /></button>
          </form>
        </section>
      )}
      <button className="support-chatbox__toggle" onClick={toggle} aria-label={open ? 'Đóng hỗ trợ' : 'Mở hỗ trợ'}>
        {open ? <X /> : <MessageCircle />}
        {!open && <span>Chat với admin</span>}
        {unread > 0 && <b>{unread > 9 ? '9+' : unread}</b>}
      </button>
    </div>
  )
}
