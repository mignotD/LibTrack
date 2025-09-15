import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import { books } from '../api/client'

export default function BrowseGenre() {
  const { genre } = useParams()
  const [bookList, setBookList] = useState([])

  useEffect(() => {
    books.byGenre(genre).then(setBookList).catch(() => {})
  }, [genre])

  return (
    <div>
      <h3 className="mb-4">Genre: {genre}</h3>
      <div className="row">
        {bookList.map(book => (
          <div key={book.isbn} className="col-md-3 mb-3">
            <div className="card h-100 shadow-sm">
              <div className="card-body">
                <h6><Link to={`/books/${book.isbn}`} className="text-decoration-none">{book.title}</Link></h6>
                <p className="text-muted small mb-1">{book.author}</p>
                <span className={`badge ${book.available > 0 ? 'bg-success' : 'bg-danger'}`}>
                  {book.available > 0 ? 'In Stock' : 'Out of Stock'}
                </span>
              </div>
            </div>
          </div>
        ))}
        {bookList.length === 0 && <p className="text-muted">No books found in this genre.</p>}
      </div>
    </div>
  )
}
