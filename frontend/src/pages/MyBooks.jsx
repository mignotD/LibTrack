import { useState, useEffect } from 'react'
import { transactions, exportCsv } from '../api/client'

export default function MyBooks() {
  const [loans, setLoans] = useState([])
  const [msg, setMsg] = useState('')
  const [error, setError] = useState('')

  const load = () => transactions.my().then(setLoans).catch(() => {})

  useEffect(() => { load() }, [])

  const handleReturn = async (id) => {
    setError('')
    try {
      // Member request return — admin must approve, but for demo we show request
      setMsg('Return requested for transaction ' + id)
    } catch (err) { setError(err.response?.data?.message || 'Failed') }
  }

  const handleRenew = async (id) => {
    setError('')
    try {
      const res = await transactions.renew(id)
      setMsg('Book renewed! New due date: ' + res.dueDate)
      load()
    } catch (err) { setError(err.response?.data?.message || 'Renewal failed') }
  }

  const active = loans.filter(l => !l.returnDate)
  const history = loans.filter(l => l.returnDate)

  return (
    <div>
      {error && <div className="alert alert-danger">{error}</div>}
      {msg && <div className="alert alert-success">{msg}</div>}

      <h4 className="mb-3">Currently Borrowed</h4>
      {active.length === 0 && <p className="text-muted">No active loans.</p>}
      <div className="table-responsive mb-4">
        <table className="table table-striped">
          <thead className="table-dark">
            <tr><th>Book</th><th>Borrowed</th><th>Due</th><th>Fine</th><th>Actions</th></tr>
          </thead>
          <tbody>
            {active.map(t => (
              <tr key={t.transactionId}>
                <td>{t.bookTitle}</td>
                <td>{t.borrowDate}</td>
                <td className={new Date(t.dueDate) < new Date() ? 'text-danger fw-bold' : ''}>{t.dueDate}</td>
                <td>{t.fine > 0 ? `$${t.fine}` : '-'}</td>
                <td>
                  <button className="btn btn-sm btn-outline-success me-1" onClick={() => handleRenew(t.transactionId)}>Renew</button>
                  <button className="btn btn-sm btn-outline-secondary" onClick={() => handleReturn(t.transactionId)}>Request Return</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="d-flex justify-content-between align-items-center mb-3">
        <h4 className="mb-0">Borrowing History</h4>
        <button className="btn btn-outline-success btn-sm" onClick={exportCsv.myTransactions}>Export CSV</button>
      </div>
      {history.length === 0 && <p className="text-muted">No history.</p>}
      <div className="table-responsive">
        <table className="table table-sm">
          <thead className="table-light">
            <tr><th>Book</th><th>Borrowed</th><th>Returned</th><th>Fine</th><th>Paid</th></tr>
          </thead>
          <tbody>
            {history.map(t => (
              <tr key={t.transactionId}>
                <td>{t.bookTitle}</td>
                <td>{t.borrowDate}</td>
                <td>{t.returnDate}</td>
                <td>{t.fine > 0 ? `$${t.fine}` : '-'}</td>
                <td>{t.finePaid ? '✅' : t.fine > 0 ? '❌' : '-'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
