import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { wishlist, transactions } from '../api/client'

export default function WishlistPage() {
  const [items, setItems] = useState([])
  const [msg, setMsg] = useState('')
  const [error, setError] = useState('')

  const load = () => wishlist.get().then(setItems).catch(() => {})

  useEffect(() => { load() }, [])

  const handleRemove = async (isbn) => {
    try {
      await wishlist.remove(isbn)
      setMsg('Removed from wishlist')
      load()
    } catch (err) { setError(err.response?.data?.message || 'Failed') }
  }

  const handleBorrow = async (isbn) => {
    try {
      await transactions.borrow(isbn)
      setMsg('Book borrowed!')
      load()
    } catch (err) { setError(err.response?.data?.message || 'Borrow failed') }
  }

  return (
    <div>
      {error && <div className="alert alert-danger">{error}</div>}
      {msg && <div className="alert alert-success">{msg}</div>}
      <h3 className="mb-4">My Wishlist</h3>
      {items.length === 0 && <p className="text-muted">Your wishlist is empty.</p>}
      <div className="row">
        {items.map(book => (
          <div key={book.isbn} className="col-md-3 mb-3">
            <div className="card h-100 shadow-sm">
              <div className="card-body">
                <h6><Link to={`/books/${book.isbn}`} className="text-decoration-none">{book.title}</Link></h6>
                <p className="text-muted small mb-2">{book.author}</p>
                <span className={`badge ${book.available > 0 ? 'bg-success' : 'bg-danger'} mb-2`}>
                  {book.available > 0 ? 'Available' : 'Out of Stock'}
                </span>
                <div>
                  {book.available > 0 && (
                    <button className="btn btn-sm btn-success me-1" onClick={() => handleBorrow(book.isbn)}>Borrow</button>
                  )}
                  <button className="btn btn-sm btn-outline-danger" onClick={() => handleRemove(book.isbn)}>Remove</button>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
