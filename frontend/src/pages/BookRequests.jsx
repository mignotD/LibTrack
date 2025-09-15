import { useState, useEffect } from 'react'
import { bookRequests } from '../api/client'

export default function BookRequests() {
  const [requests, setRequests] = useState([])
  const [form, setForm] = useState({ title: '', author: '', publisher: '', isbn: '' })
  const [msg, setMsg] = useState('')
  const [error, setError] = useState('')

  const load = () => bookRequests.my().then(setRequests).catch(() => {})

  useEffect(() => { load() }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    try {
      await bookRequests.submit(form)
      setMsg('Request submitted!')
      setForm({ title: '', author: '', publisher: '', isbn: '' })
      load()
    } catch (err) { setError(err.response?.data?.message || 'Failed') }
  }

  return (
    <div className="row">
      <div className="col-md-5">
        <div className="card shadow-sm mb-4">
          <div className="card-body">
            <h4>Request a Book</h4>
            {error && <div className="alert alert-danger">{error}</div>}
            {msg && <div className="alert alert-success">{msg}</div>}
            <form onSubmit={handleSubmit}>
              <div className="mb-2">
                <input className="form-control" placeholder="Title *" value={form.title}
                  onChange={e => setForm({...form, title: e.target.value})} required />
              </div>
              <div className="mb-2">
                <input className="form-control" placeholder="Author" value={form.author}
                  onChange={e => setForm({...form, author: e.target.value})} />
              </div>
              <div className="mb-2">
                <input className="form-control" placeholder="Publisher" value={form.publisher}
                  onChange={e => setForm({...form, publisher: e.target.value})} />
              </div>
              <div className="mb-2">
                <input className="form-control" placeholder="ISBN" value={form.isbn}
                  onChange={e => setForm({...form, isbn: e.target.value})} />
              </div>
              <button type="submit" className="btn btn-primary">Submit</button>
            </form>
          </div>
        </div>
      </div>
      <div className="col-md-7">
        <h4>My Requests</h4>
        {requests.length === 0 && <p className="text-muted">No requests yet.</p>}
        <div className="table-responsive">
          <table className="table table-striped">
            <thead className="table-dark">
              <tr><th>Title</th><th>Author</th><th>Status</th><th>Date</th></tr>
            </thead>
            <tbody>
              {requests.map(r => (
                <tr key={r.requestId}>
                  <td>{r.title}</td>
                  <td>{r.author || '-'}</td>
                  <td><span className={`badge ${r.status === 'Approved' ? 'bg-success' : r.status === 'Rejected' ? 'bg-danger' : 'bg-warning'}`}>{r.status}</span></td>
                  <td>{new Date(r.requestDate).toLocaleDateString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
