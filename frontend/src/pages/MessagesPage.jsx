import { useEffect, useRef, useState } from 'react'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { MessageCircle, Plus, Send, UserRound } from 'lucide-react'
import { useLocation } from 'react-router-dom'
import { API_URL, endpoints, errorMessage, getStoredSession, unwrap } from '../api/client'
import { useAuth } from '../context/useAuth'
import { Button, EmptyState, Field, Notice, PageLoader } from '../components/ui'
import { formatDate } from '../utils/format'

export default function MessagesPage() {
  const { user } = useAuth()
  const location = useLocation()
  const [rooms, setRooms] = useState([])
  const [active, setActive] = useState(null)
  const [message, setMessage] = useState('')
  const [receiverId, setReceiverId] = useState('')
  const [notice, setNotice] = useState(null)
  const [loading, setLoading] = useState(true)
  const clientRef = useRef(null)

  const openRoom = async (id) => { try { setActive(unwrap(await endpoints.conversations.one(id))) } catch (error) { setNotice({ type: 'error', text: errorMessage(error) }) } }
  useEffect(() => {
    endpoints.conversations.all().then((response) => {
      const data = unwrap(response) || []; setRooms(data)
      const requested = location.state?.conversationId
      if (requested) openRoom(requested); else if (data[0]) setActive(data[0])
    }).catch((error) => setNotice({ type: 'error', text: errorMessage(error) })).finally(() => setLoading(false))
  }, [location.state])

  useEffect(() => {
    if (!active?.conversationId) return
    const token = getStoredSession()?.accessToken
    const client = new Client({
      webSocketFactory: () => new SockJS(`${API_URL}/ws`),
      connectHeaders: { Authorization: `Bearer ${token}` },
      reconnectDelay: 4000,
      onConnect: () => client.subscribe(`/topic/${active.conversationId}`, (frame) => {
        let incoming
        try { incoming = JSON.parse(frame.body)?.data ?? JSON.parse(frame.body) } catch { incoming = frame.body }
        setActive((current) => current ? { ...current, listMessages: [...(current.listMessages || []), { id: Date.now(), senderId: null, message: String(incoming), sentAt: new Date().toISOString() }] } : current)
      }),
      onStompError: (frame) => setNotice({ type: 'error', text: frame.headers.message || 'Chat connection failed.' }),
    })
    client.activate(); clientRef.current = client
    return () => client.deactivate()
  }, [active?.conversationId])

  const createRoom = async (event) => { event.preventDefault(); try { const room = unwrap(await endpoints.conversations.create(Number(receiverId))); setRooms((current) => [room, ...current.filter((item) => item.conversationId !== room.conversationId)]); setActive(room); setReceiverId('') } catch (error) { setNotice({ type: 'error', text: errorMessage(error) }) } }
  const send = (event) => {
    event.preventDefault(); if (!message.trim() || !clientRef.current?.connected) return
    const target = active.user1Id === user.userId ? active.user2Id : active.user1Id
    clientRef.current.publish({ destination: `/app/chat/${active.conversationId}`, body: JSON.stringify({ receiverId: target, message: message.trim() }) })
    setMessage('')
  }
  if (loading) return <PageLoader />
  return <div className="messages-page page-pad">
    <div className="page-heading"><div><p className="eyebrow">Direct connection</p><h1>Messages</h1><p>Talk with shoppers and sellers in real time.</p></div></div>
    {notice && <Notice type={notice.type}>{notice.text}</Notice>}
    <div className="messenger panel">
      <aside className="room-list"><form onSubmit={createRoom}><Field aria-label="Receiver user ID" type="number" min="1" required value={receiverId} onChange={(e) => setReceiverId(e.target.value)} placeholder="User ID" /><button aria-label="Start conversation"><Plus size={18} /></button></form><div>{rooms.map((room) => { const other = room.user1Id === user.userId ? room.user2Id : room.user1Id; return <button key={room.conversationId} className={active?.conversationId === room.conversationId ? 'active' : ''} onClick={() => openRoom(room.conversationId)}><span><UserRound /></span><div><strong>User #{other || 'new'}</strong><small>{room.listMessages?.at(-1)?.message || 'Start the conversation'}</small></div></button> })}</div></aside>
      <section className="chat-window">{active ? <><header><span><UserRound /></span><div><strong>Conversation</strong><small>Room {active.conversationId?.slice(0, 10)}…</small></div><i className={clientRef.current?.connected ? 'online' : ''} /></header><div className="message-stream">{(active.listMessages || []).length ? active.listMessages.map((item, index) => <div key={item.id || index} className={`message ${item.senderId === user.userId ? 'message--mine' : ''}`}><p>{item.message}</p><small>{formatDate(item.sentAt)}</small></div>) : <EmptyState icon={MessageCircle} title="Say hello" text="This is the beginning of your conversation." />}</div><form className="message-compose" onSubmit={send}><input value={message} onChange={(e) => setMessage(e.target.value)} placeholder="Write a message…" aria-label="Message" /><Button type="submit" disabled={!message.trim()}><Send size={17} /><span>Send</span></Button></form></> : <EmptyState icon={MessageCircle} title="Choose a conversation" text="Select a room or start one with a user ID." />}</section>
    </div>
  </div>
}
