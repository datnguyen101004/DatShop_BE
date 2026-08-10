import { useEffect, useMemo, useRef, useState } from 'react'
import { Headphones, Inbox, RefreshCw, Search, Send, UserRound } from 'lucide-react'
import { endpoints, errorMessage, unwrap } from '../api/client'
import { Button, EmptyState, Notice, PageLoader } from '../components/ui'
import { formatDate } from '../utils/format'
import { ADMIN_SUPPORT_ID, appendUniqueMessage, connectToConversation } from '../utils/supportChat'

export default function AdminSupportPage() {
  const [rooms, setRooms] = useState([])
  const [active, setActive] = useState(null)
  const [draft, setDraft] = useState('')
  const [query, setQuery] = useState('')
  const [loading, setLoading] = useState(true)
  const [connected, setConnected] = useState(false)
  const [notice, setNotice] = useState(null)
  const clientRef = useRef(null)
  const streamRef = useRef(null)

  const loadInbox = async (showLoader = false) => {
    if (showLoader) setLoading(true)
    try {
      const data = unwrap(await endpoints.conversations.supportInbox()) || []
      setRooms(data)
      setActive((current) => current || data[0] || null)
      setNotice(null)
    } catch (requestError) {
      setNotice({ type: 'error', text: errorMessage(requestError) })
    } finally {
      if (showLoader) setLoading(false)
    }
  }

  useEffect(() => {
    loadInbox(true)
    const timer = window.setInterval(() => loadInbox(false), 10000)
    return () => window.clearInterval(timer)
  }, [])

  useEffect(() => {
    streamRef.current?.scrollTo({ top: streamRef.current.scrollHeight, behavior: 'smooth' })
  }, [active?.listMessages])

  useEffect(() => {
    if (!active?.conversationId) return undefined
    const client = connectToConversation({
      conversationId: active.conversationId,
      onConnectionChange: setConnected,
      onError: (text) => setNotice({ type: 'error', text }),
      onMessage: (incoming) => {
        setActive((current) => current
          ? { ...current, lastMessageAt: incoming.sentAt, listMessages: appendUniqueMessage(current.listMessages, incoming) }
          : current)
        setRooms((current) => current.map((room) => room.conversationId === active.conversationId
          ? { ...room, lastMessageAt: incoming.sentAt, listMessages: appendUniqueMessage(room.listMessages, incoming) }
          : room))
      },
    })
    clientRef.current = client
    return () => { clientRef.current = null; client.deactivate() }
  }, [active?.conversationId])

  const openRoom = async (room) => {
    setNotice(null)
    try {
      setActive(unwrap(await endpoints.conversations.one(room.conversationId)))
    } catch (requestError) {
      setNotice({ type: 'error', text: errorMessage(requestError) })
    }
  }

  const send = (event) => {
    event.preventDefault()
    const message = draft.trim()
    if (!message || !connected || !active) return
    clientRef.current.publish({
      destination: `/app/chat/${active.conversationId}`,
      body: JSON.stringify({ receiverId: active.otherUserId, message }),
    })
    setDraft('')
  }

  const filteredRooms = useMemo(() => {
    const normalized = query.trim().toLowerCase()
    if (!normalized) return rooms
    return rooms.filter((room) => `${room.otherUserName || ''} ${room.otherUserId}`.toLowerCase().includes(normalized))
  }, [query, rooms])

  if (loading) return <PageLoader />
  return (
    <div className="messages-page admin-support-page page-pad">
      <div className="page-heading">
        <div><p className="eyebrow">Trung tâm hỗ trợ</p><h1>Hộp thư khách hàng</h1><p>Tiếp nhận và trả lời nhiều cuộc trò chuyện từ một nơi.</p></div>
        <Button variant="secondary" onClick={() => loadInbox(false)}><RefreshCw size={16} /> Làm mới</Button>
      </div>
      {notice && <Notice type={notice.type} onClose={() => setNotice(null)}>{notice.text}</Notice>}
      <div className="messenger admin-support panel">
        <aside className="room-list admin-support__rooms">
          <div className="admin-support__search"><Search size={17} /><input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Tìm khách hàng…" aria-label="Tìm khách hàng" /></div>
          <div className="admin-support__room-count"><span>{filteredRooms.length} cuộc trò chuyện</span><small>Tự động cập nhật mỗi 10 giây</small></div>
          <div>{filteredRooms.map((room) => {
            const lastMessage = room.listMessages?.at(-1)
            return <button key={room.conversationId} className={active?.conversationId === room.conversationId ? 'active' : ''} onClick={() => openRoom(room)}>
              <span>{room.otherUserAvatarUrl ? <img src={room.otherUserAvatarUrl} alt="" /> : <UserRound />}</span>
              <div><strong>{room.otherUserName || `Khách hàng #${room.otherUserId}`}</strong><small>{lastMessage?.message || 'Chưa có tin nhắn'}</small><time>{lastMessage?.sentAt ? formatDate(lastMessage.sentAt) : `ID #${room.otherUserId}`}</time></div>
            </button>
          })}</div>
        </aside>
        <section className="chat-window">
          {active ? <>
            <header><span>{active.otherUserAvatarUrl ? <img src={active.otherUserAvatarUrl} alt="" /> : <UserRound />}</span><div><strong>{active.otherUserName || `Khách hàng #${active.otherUserId}`}</strong><small>Khách hàng ID #{active.otherUserId}</small></div><i className={connected ? 'online' : ''} /></header>
            <div className="message-stream" ref={streamRef}>
              {(active.listMessages || []).length ? active.listMessages.map((item, index) => <div key={item.id || index} className={`message ${item.senderId === ADMIN_SUPPORT_ID ? 'message--mine' : ''}`}><p>{item.message}</p><small>{formatDate(item.sentAt)}</small></div>) : <EmptyState icon={Headphones} title="Bắt đầu hỗ trợ" text="Hãy gửi lời chào đến khách hàng này." />}
            </div>
            <form className="message-compose" onSubmit={send}><input value={draft} maxLength={2000} onChange={(event) => setDraft(event.target.value)} placeholder="Trả lời khách hàng…" aria-label="Tin nhắn trả lời" /><Button type="submit" disabled={!draft.trim() || !connected}><Send size={17} /><span>Gửi</span></Button></form>
          </> : <EmptyState icon={rooms.length ? Inbox : Headphones} title={rooms.length ? 'Chọn một cuộc trò chuyện' : 'Chưa có yêu cầu hỗ trợ'} text={rooms.length ? 'Chọn khách hàng ở danh sách bên trái để trả lời.' : 'Tin nhắn mới của khách hàng sẽ xuất hiện tại đây.'} />}
        </section>
      </div>
    </div>
  )
}
