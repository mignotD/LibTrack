import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { books, genres } from '../api/client'

export default function Dashboard() {
  const [popular, setPopular] = useState([])
  const [genreList, setGenreList] = useState([])

  useEffect(() => {
    books.popular().then(setPopular).catch(() => {})
    genres.getAll().then(setGenreList).catch(() => {})
  }, [])

  return (
    <div>
      <div className="jumbotron bg-light p-5 rounded mb-4">
        <h1 className="display-4">Welcome to the Library</h1>
        <p className="lead">Browse, borrow, and manage your library books online.</p>
        <Link to="/books" className="btn btn-primary btn-lg me-2">Browse Books</Link>
        <Link to="/search" className="btn btn-outline-secondary btn-lg">Search</Link>
      </div>

      {genreList.length > 0 && (
        <div className="mb-4">
          <h4>Browse by Genre</h4>
          <div className="d-flex flex-wrap gap-2">
            {genreList.map(g => (
              <Link key={g} to={`/browse/${encodeURIComponent(g)}`}
                className="btn btn-outline-primary btn-sm">{g}</Link>
            ))}
          </div>
        </div>
      )}

      <h4 className="mb-3">Popular Books</h4>
      <div className="row">
        {popular.slice(0, 8).map(book => (
          <div key={book.isbn} className="col-md-3 mb-3">
            <div className="card h-100 shadow-sm">
              <div className="card-body">
                <h6 className="card-title">
                  <Link to={`/books/${book.isbn}`} className="text-decoration-none">{book.title}</Link>
                </h6>
                <p className="card-text text-muted small">{book.author}</p>
                {book.averageRating > 0 && (
                  <p className="mb-1 small">⭐ {book.averageRating.toFixed(1)} ({book.reviewCount})</p>
                )}
                <span className={`badge ${book.available > 0 ? 'bg-success' : 'bg-danger'}`}>
                  {book.available > 0 ? 'Available' : 'Out of Stock'}
                </span>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
