import { useState, useEffect } from 'react'
import { admin } from '../api/client'

export default function AdminLimits() {
  const [limits, setLimits] = useState([])
  const [msg, setMsg] = useState('')

  const load = () => admin.borrowingLimits().then(setLimits).catch(() => {})

  useEffect(() => { load() }, [])

  const handleUpdate = async (id, maxBooks, loanDurationDays) => {
    try {
      const params = {}
      if (maxBooks) params.maxBooks = maxBooks
      if (loanDurationDays) params.loanDurationDays = loanDurationDays
      await admin.updateLimit(id, params)
      setMsg('Limit updated!')
      load()
    } catch { setMsg('Update failed') }
  }

  return (
    <div>
      <h3 className="mb-4">Borrowing Limits</h3>
      {msg && <div className="alert alert-info">{msg}</div>}
      <div className="table-responsive">
        <table className="table table-striped">
          <thead className="table-dark">
            <tr><th>Member Type</th><th>Max Books</th><th>Loan Duration (days)</th><th>Actions</th></tr>
          </thead>
          <tbody>
            {limits.map(limit => (
              <LimitRow key={limit.limitId} limit={limit} onUpdate={handleUpdate} />
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}

function LimitRow({ limit, onUpdate }) {
  const [maxBooks, setMaxBooks] = useState(limit.maxBooks)
  const [duration, setDuration] = useState(limit.loanDurationDays)

  return (
    <tr>
      <td><span className="badge bg-info">{limit.memberType}</span></td>
      <td>
        <input type="number" className="form-control form-control-sm w-50" value={maxBooks}
          min={1} onChange={e => setMaxBooks(Number(e.target.value))} />
      </td>
      <td>
        <input type="number" className="form-control form-control-sm w-50" value={duration}
          min={1} onChange={e => setDuration(Number(e.target.value))} />
      </td>
      <td>
        <button className="btn btn-sm btn-primary"
          onClick={() => onUpdate(limit.limitId, maxBooks, duration)}>Save</button>
      </td>
    </tr>
  )
}
