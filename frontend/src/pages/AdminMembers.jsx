import { useState, useEffect } from 'react'
import { admin } from '../api/client'

export default function AdminMembers() {
  const [members, setMembers] = useState([])
  const [total, setTotal] = useState(0)
  const [page, setPage] = useState(0)
  const perPage = 20

  const load = () => {
    admin.members(page, perPage).then(setMembers).catch(() => {})
    admin.memberCount().then(setTotal).catch(() => {})
  }

  useEffect(() => { load() }, [page])

  const handleToggle = async (id) => {
    try {
      await admin.toggleMember(id)
      load()
    } catch {}
  }

  const totalPages = Math.ceil(total / perPage)

  return (
    <div>
      <h3 className="mb-4">Members ({total})</h3>
      <div className="table-responsive">
        <table className="table table-striped">
          <thead className="table-dark">
            <tr><th>ID</th><th>Name</th><th>Email</th><th>Role</th><th>Status</th><th>Joined</th><th>Action</th></tr>
          </thead>
          <tbody>
            {members.map(m => (
              <tr key={m.memberId}>
                <td>{m.memberId}</td>
                <td>{m.name}</td>
                <td>{m.email}</td>
                <td><span className="badge bg-info">{m.role}</span></td>
                <td><span className={`badge ${m.status === 'Active' ? 'bg-success' : 'bg-secondary'}`}>{m.status}</span></td>
                <td>{m.joinDate}</td>
                <td>
                  <button className={`btn btn-sm ${m.status === 'Active' ? 'btn-warning' : 'btn-success'}`}
                    onClick={() => handleToggle(m.memberId)}>
                    {m.status === 'Active' ? 'Disable' : 'Enable'}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {totalPages > 1 && (
        <nav>
          <ul className="pagination">
            {Array.from({ length: totalPages }, (_, i) => (
              <li key={i} className={`page-item ${i === page ? 'active' : ''}`}>
                <button className="page-link" onClick={() => setPage(i)}>{i + 1}</button>
              </li>
            ))}
          </ul>
        </nav>
      )}
    </div>
  )
}
