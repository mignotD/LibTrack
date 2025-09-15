import { useState, useEffect } from 'react'
import { transactions } from '../api/client'

export default function PayFine() {
  const [loans, setLoans] = useState([])
  const [msg, setMsg] = useState('')
  const [error, setError] = useState('')

  const load = () => transactions.my().then(setLoans).catch(() => {})

  useEffect(() => { load() }, [])

  const unpaid = loans.filter(l => l.fine > 0 && !l.finePaid)

  const handlePay = async (id) => {
    try {
      await transactions.payFine(id)
      setMsg('Fine paid!')
      load()
    } catch (err) { setError(err.response?.data?.message || 'Payment failed') }
  }

  return (
    <div>
      {error && <div className="alert alert-danger">{error}</div>}
      {msg && <div className="alert alert-success">{msg}</div>}
      <h3 className="mb-4">Pay Fines</h3>
      {unpaid.length === 0 && <p className="text-muted">No unpaid fines.</p>}
      <div className="table-responsive">
        <table className="table table-striped">
          <thead className="table-dark">
            <tr><th>Book</th><th>Due Date</th><th>Fine</th><th></th></tr>
          </thead>
          <tbody>
            {unpaid.map(t => (
              <tr key={t.transactionId}>
                <td>{t.bookTitle}</td>
                <td>{t.dueDate}</td>
                <td className="text-danger fw-bold">${t.fine}</td>
                <td><button className="btn btn-sm btn-success" onClick={() => handlePay(t.transactionId)}>Pay Now</button></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
