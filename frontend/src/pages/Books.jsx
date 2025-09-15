import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { books, exportCsv } from '../api/client'

export default function Books() {
  const [bookList, setBookList] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    books.getAll().then(data => { setBookList(data); setLoading(false) })
      .catch(() => setLoading(false))
  }, [])

  if (loading) return <div className="text-center mt-5"><div className="spinner-border"></div></div>

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h3 className="mb-0">All Books</h3>
        <button className="btn btn-outline-success btn-sm" onClick={exportCsv.books}>Export CSV</button>
      </div>
      <div className="table-responsive">
        <table className="table table-striped table-hover">
          <thead className="table-dark">
            <tr>
              <th>ISBN</th><th>Title</th><th>Author</th><th>Genre</th><th>Stock</th><th>Available</th><th>Rating</th><th></th>
            </tr>
          </thead>
          <tbody>
            {bookList.map(book => (
              <tr key={book.isbn}>
                <td>{book.isbn}</td>
                <td><Link to={`/books/${book.isbn}`}>{book.title}</Link></td>
                <td>{book.author}</td>
                <td>{book.genre}</td>
                <td>{book.stock}</td>
                <td><span className={`badge ${book.available > 0 ? 'bg-success' : 'bg-danger'}`}>{book.available}</span></td>
                <td>{book.averageRating > 0 ? `⭐ ${book.averageRating.toFixed(1)}` : '-'}</td>
                <td><Link to={`/books/${book.isbn}`} className="btn btn-sm btn-outline-primary">View</Link></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
