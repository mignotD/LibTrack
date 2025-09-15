import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { admin } from '../api/client'

export default function AdminDashboard() {
  const [stats, setStats] = useState({})

  useEffect(() => { admin.stats().then(setStats).catch(() => {}) }, [])

  const cards = [
    { label: 'Total Books', value: stats.totalBooks, color: 'primary', link: '/books' },
    { label: 'Members', value: stats.totalMembers, color: 'success', link: '/admin/members' },
    { label: 'Active Loans', value: stats.activeLoans, color: 'info', link: '/my-books' },
    { label: 'Pending Requests', value: stats.pendingRequests, color: 'warning', link: '/admin/requests' },
    { label: 'Unpaid Fines', value: `$${stats.unpaidFines || 0}`, color: 'danger', link: '/pay-fine' },
  ]

  return (
    <div>
      <h3 className="mb-4">Admin Dashboard</h3>
      <div className="row mb-4">
        {cards.map(c => (
          <div key={c.label} className="col-md-3 mb-3">
            <Link to={c.link} className="text-decoration-none">
              <div className={`card bg-${c.color} text-white shadow-sm`}>
                <div className="card-body text-center">
                  <h2 className="mb-0">{c.value ?? '-'}</h2>
                  <div>{c.label}</div>
                </div>
              </div>
            </Link>
          </div>
        ))}
      </div>

      <div className="row">
        <div className="col-md-6">
          <div className="card shadow-sm">
            <div className="card-body">
              <h5>Quick Links</h5>
              <ul>
                <li><Link to="/admin/members">Manage Members</Link></li>
                <li><Link to="/admin/requests">Review Book Requests</Link></li>
                <li><Link to="/admin/audit">View Audit Log</Link></li>
                <li><Link to="/admin/limits">Configure Borrowing Limits</Link></li>
                <li><Link to="/books">Manage Books</Link></li>
              </ul>
            </div>
          </div>
        </div>
        <div className="col-md-6">
          <div className="card shadow-sm">
            <div className="card-body">
              <h5>Popular Books</h5>
              <PopularBooks />
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

function PopularBooks() {
  const [popular, setPopular] = useState([])
  useEffect(() => { admin.popularBooks().then(setPopular).catch(() => {}) }, [])
  return (
    <ul className="list-group">
      {popular.slice(0, 10).map(b => (
        <li key={b.isbn} className="list-group-item d-flex justify-content-between align-items-center">
          {b.title}
          <span className="badge bg-warning rounded-pill">⭐ {b.avgRating.toFixed(1)}</span>
        </li>
      ))}
    </ul>
  )
}
