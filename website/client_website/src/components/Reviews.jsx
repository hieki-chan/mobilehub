// src/components/Reviews.jsx
import React, { useState } from 'react'
import useReviews from '../hooks/useReviews'

export default function Reviews({ productId }) {
  const { list, add } = useReviews(productId)
  const [open, setOpen] = useState(false)
  const [form, setForm] = useState({ name:'', rating:5, comment:'' })

  const submit = () => {
    if (!form.name) return alert('Nhập tên'); add({ ...form }); setForm({ name:'', rating:5, comment:'' }); setOpen(false); alert('Cảm ơn đánh giá của bạn!')
  }

  return (
    <div className="reviews">
      <h3>Đánh giá</h3>
      <div id="reviewsList">
        {!list.length ? <div className="muted">Chưa có đánh giá nào. Hãy là người đầu tiên!</div> :
          list.map((r,i) => (
            <div key={i} style={{ background:'#fff', padding:12, borderRadius:8, boxShadow:'0 6px 18px rgba(16,24,40,0.04)', marginBottom:8 }}>
              <div style={{ display:'flex', gap:8, alignItems:'center' }}><strong>{r.name}</strong><div className="muted" style={{ fontSize:13 }}> - {'★'.repeat(r.rating)}</div></div>
              <div className="muted" style={{ marginTop:6 }}>{r.comment}</div>
            </div>
          ))
        }
      </div>

      <div className="write-review" style={{ marginTop:12 }}>
        <button className="btn btn-primary" onClick={() => setOpen(true)}>Viết đánh giá</button>
      </div>

      {open && (
        <div className="modal-overlay open" onClick={(e)=> e.target.classList.contains('modal-overlay') && setOpen(false)}>
          <div className="modal">
            <button className="modal-close" onClick={()=>setOpen(false)}>&times;</button>
            <div className="modal-body">
              <div style={{ display:'flex', flexDirection:'column', gap:8 }}>
                <input placeholder="Tên của bạn" value={form.name} onChange={e=>setForm(f=>({...f, name:e.target.value}))} />
                <input type="number" min="1" max="5" value={form.rating} onChange={e=>setForm(f=>({...f, rating: Number(e.target.value)}))} />
                <textarea placeholder="Nội dung đánh giá" value={form.comment} onChange={e=>setForm(f=>({...f, comment:e.target.value}))} />
                <div style={{ display:'flex', gap:8 }}>
                  <button className="btn btn-primary" onClick={submit}>Gửi</button>
                  <button className="btn btn-secondary" onClick={()=>setOpen(false)}>Hủy</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
