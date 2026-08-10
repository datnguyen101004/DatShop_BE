import { useState } from 'react'
import { Bot, Send, Sparkles, UserRound } from 'lucide-react'
import Markdown from 'react-markdown'
import remarkGfm from 'remark-gfm'
import { endpoints, errorMessage, unwrap } from '../api/client'
import { Button } from '../components/ui'

const starters = ['Gợi ý món quà thiết thực dưới 500.000₫', 'Tôi cần biết gì về giao hàng?', 'Giúp tôi so sánh sản phẩm dùng hằng ngày']
const welcomeMessage = `Chào bạn! Mình là **trợ lý DatShop**.

Bạn có thể hỏi mình về:

- Sản phẩm và gợi ý mua sắm
- Đơn hàng, thanh toán và giao hàng
- So sánh các lựa chọn trong cửa hàng`

const formatAnswer = (answer) => {
  if (typeof answer === 'string') return answer
  if (answer?.message) return answer.message
  return JSON.stringify(answer, null, 2)
}

function MessageContent({ message }) {
  if (message.role === 'user' || message.error) return <p>{message.text}</p>

  return (
    <div className="ai-message__content">
      <Markdown
        remarkPlugins={[remarkGfm]}
        components={{
          a: ({ children, ...props }) => <a {...props} target="_blank" rel="noreferrer">{children}</a>,
        }}
      >
        {message.text}
      </Markdown>
    </div>
  )
}

export default function AssistantPage() {
  const [question, setQuestion] = useState('')
  const [messages, setMessages] = useState([{ role: 'assistant', text: welcomeMessage }])
  const [busy, setBusy] = useState(false)

  const ask = async (event, starter) => {
    event?.preventDefault()
    const text = starter || question.trim()
    if (!text) return
    setMessages((current) => [...current, { role: 'user', text }]); setQuestion(''); setBusy(true)
    try {
      const response = await endpoints.chatbot.askGemini(text)
      const answer = unwrap(response)
      setMessages((current) => [...current, { role: 'assistant', text: formatAnswer(answer) }])
    } catch (error) { setMessages((current) => [...current, { role: 'assistant', error: true, text: errorMessage(error) }]) }
    finally { setBusy(false) }
  }

  return <div className="assistant-page page-pad">
    <div className="page-heading assistant-heading"><div><p className="eyebrow">Context-aware help</p><h1>DatShop assistant</h1><p>Ask about products, shopping, delivery, and store policies in natural language.</p></div></div>
    <div className="assistant-content">
      <section className="assistant-chat panel">
        <header><span><Bot /></span><div><strong>Marketplace guide</strong></div></header>
        <div className="assistant-messages">{messages.map((message, index) => <article key={index} className={`ai-message ai-message--${message.role} ${message.error ? 'ai-message--error' : ''}`}><span>{message.role === 'assistant' ? <Bot /> : <UserRound />}</span><MessageContent message={message} /></article>)}{busy && <article className="ai-message ai-message--assistant"><span><Bot /></span><div className="ai-message__content thinking"><i /><i /><i /></div></article>}</div>
        {messages.length === 1 && <div className="prompt-starters">{starters.map((starter) => <button key={starter} onClick={() => ask(null, starter)}>{starter}<Sparkles size={15} /></button>)}</div>}
        <form className="assistant-compose" onSubmit={ask}><textarea rows="2" value={question} onChange={(e) => setQuestion(e.target.value)} placeholder="Ask about a product, payment, or delivery…" /><Button type="submit" loading={busy}><Send size={17} /> Ask</Button></form>
      </section>
    </div>
  </div>
}
