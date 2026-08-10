import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { API_URL, getStoredSession } from '../api/client'

export const ADMIN_SUPPORT_ID = 0

export function connectToConversation({ conversationId, onMessage, onConnectionChange, onError }) {
  const token = getStoredSession()?.accessToken
  const client = new Client({
    webSocketFactory: () => new SockJS(`${API_URL}/ws`),
    connectHeaders: { Authorization: `Bearer ${token}` },
    reconnectDelay: 4000,
    onConnect: () => {
      onConnectionChange?.(true)
      client.subscribe(`/topic/${conversationId}`, (frame) => {
        try {
          const body = JSON.parse(frame.body)
          onMessage(body?.data ?? body)
        } catch {
          onError?.('Không thể đọc tin nhắn vừa nhận.')
        }
      })
    },
    onDisconnect: () => onConnectionChange?.(false),
    onWebSocketClose: () => onConnectionChange?.(false),
    onStompError: (frame) => {
      onConnectionChange?.(false)
      onError?.(frame.headers.message || 'Kết nối trò chuyện bị gián đoạn.')
    },
  })
  client.activate()
  return client
}

export function appendUniqueMessage(messages = [], incoming) {
  if (!incoming || typeof incoming !== 'object') return messages
  if (incoming.id && messages.some((message) => message.id === incoming.id)) return messages
  return [...messages, incoming]
}
