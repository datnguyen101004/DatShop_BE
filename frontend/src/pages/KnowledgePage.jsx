import { useState } from 'react'
import { Database, Sparkles } from 'lucide-react'
import { endpoints, errorMessage } from '../api/client'
import { Button, Field, Notice } from '../components/ui'

export default function KnowledgePage() {
  const [knowledge, setKnowledge] = useState({ name: '', description: '', type: 'PRODUCT' })
  const [busy, setBusy] = useState(false)
  const [notice, setNotice] = useState(null)

  const submit = async (event) => {
    event.preventDefault()
    setBusy(true)
    setNotice(null)

    try {
      await endpoints.chatbot.addInformation(knowledge)
      setKnowledge({ name: '', description: '', type: 'PRODUCT' })
      setNotice({ type: 'success', text: 'Đã thêm thông tin vào kho kiến thức của DatShop.' })
    } catch (error) {
      setNotice({ type: 'error', text: errorMessage(error) })
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="knowledge-page page page-pad">
      <div className="knowledge-page__content">
        <div className="page-heading">
          <div>
            <p className="eyebrow">Dành cho cửa hàng và quản trị viên</p>
            <h1>Teach the assistant</h1>
            <p>Thêm thông tin đáng tin cậy để trợ lý tư vấn chính xác hơn cho khách hàng.</p>
          </div>
        </div>

        {notice && <Notice type={notice.type} onClose={() => setNotice(null)}>{notice.text}</Notice>}

        <section className="knowledge-card knowledge-card--page panel">
          <span className="knowledge-card__icon"><Database /></span>
          <div>
            <p className="eyebrow">Knowledge manager</p>
            <h2>Thông tin cho AI</h2>
            <p>Nội dung được thêm vào kho kiến thức để trợ lý sử dụng khi tư vấn.</p>
          </div>
          <form className="form-stack" onSubmit={submit}>
            <Field label="Tên chủ đề" required value={knowledge.name} onChange={(event) => setKnowledge({ ...knowledge, name: event.target.value })} placeholder="Chính sách đổi trả" />
            <Field label="Loại thông tin" as="select" value={knowledge.type} onChange={(event) => setKnowledge({ ...knowledge, type: event.target.value })} options={['PRODUCT', 'PAYMENT', 'DELIVERY', 'POLICY']} />
            <Field label="Mô tả" as="textarea" rows="7" required value={knowledge.description} onChange={(event) => setKnowledge({ ...knowledge, description: event.target.value })} placeholder="Nhập nội dung rõ ràng và chính xác…" />
            <Button type="submit" loading={busy}><Sparkles size={17} /> Add to knowledge base</Button>
          </form>
          <small>Chức năng này dành cho tài khoản SHOP hoặc ADMIN.</small>
        </section>
      </div>
    </div>
  )
}
