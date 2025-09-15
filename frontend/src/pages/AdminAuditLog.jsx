import { useState, useEffect } from 'react'
import { admin } from '../api/client'

export default function AdminAuditLog() {
  const [logs, setLogs] = useState([])
  const [total, setTotal] = useState(0)
  const [page, setPage] = useState(0)
  const perPage = 20

  const load = () => {
    admin.auditLogs(page, perPage).then(res => {
      setLogs(res.content || [])
      setTotal(res.totalElements || 0)
    }).catch(() => {})
  }

  useEffect(() => { load() }, [page])

  const totalPages = Math.ceil(total / perPage)

  return (
    <div>
      <h3 className="mb-4">Audit Log ({total})</h3>
      <div className="table-responsive">
        <table className="table table-sm table-striped">
          <thead className="table-dark">
            <tr><th>ID</th><th>Member</th><th>Action</th><th>Details</th><th>Timestamp</th></tr>
          </thead>
          <tbody>
            {logs.map(log => (
              <tr key={log.logId}>
                <td>{log.logId}</td>
                <td>{log.member?.name || 'System'}</td>
                <td><code>{log.action}</code></td>
                <td className="small">{log.details}</td>
                <td>{new Date(log.timestamp).toLocaleString()}</td>
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
