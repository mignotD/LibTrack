import { useState, useEffect } from 'react'
import { admin } from '../api/client'

export default function AdminRequests() {
  const [requests, setRequests] = useState([])
  const [filter, setFilter] = useState('Pending')

  const load = () => admin.bookRequests(filter || undefined).then(setRequests).catch(() => {})

  useEffect(() => { load() }, [filter])

  const handleApprove = async (id) => {
    try { await admin.approveRequest(id); load() } catch {}
  }

  const handleReject = async (id) => {
    try { await admin.rejectRequest(id); load() } catch {}
  }

  return (
    <div>
      <h3 className="mb-4">Book Requests</h3>
      <div className="mb-3">
        <select className="form-select w-auto" value={filter} onChange={e => setFilter(e.target.value)}>
          <option value="">All</option>
          <option value="Pending">Pending</option>
          <option value="Approved">Approved</option>
          <option value="Rejected">Rejected</option>
        </select>
      </div>
      <div className="table-responsive">
        <table className="table table-striped">
          <thead className="table-dark">
            <tr><th>ID</th><th>Member</th><th>Title</th><th>Author</th><th>ISBN</th><th>Status</th><th>Date</th><th>Actions</th></tr>
          </thead>
          <tbody>
            {requests.map(r => (
              <tr key={r.requestId}>
                <td>{r.requestId}</td>
                <td>{r.member?.name}</td>
                <td>{r.title}</td>
                <td>{r.author || '-'}</td>
                <td>{r.isbn || '-'}</td>
                <td><span className={`badge ${r.status === 'Approved' ? 'bg-success' : r.status === 'Rejected' ? 'bg-danger' : 'bg-warning'}`}>{r.status}</span></td>
                <td>{new Date(r.requestDate).toLocaleDateString()}</td>
                <td>
                  {r.status === 'Pending' && (
                    <>
                      <button className="btn btn-sm btn-success me-1" onClick={() => handleApprove(r.requestId)}>Approve</button>
                      <button className="btn btn-sm btn-danger" onClick={() => handleReject(r.requestId)}>Reject</button>
                    </>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
