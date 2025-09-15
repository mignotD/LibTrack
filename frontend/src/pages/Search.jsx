import { useState } from 'react'
import { Link } from 'react-router-dom'
import { books, genres } from '../api/client'

export default function Search() {
  const [query, setQuery] = useState({ title: '', author: '', genre: '', isbn: '' })
  const [results, setResults] = useState([])
  const [genreList, setGenreList] = useState([])
  const [searched, setSearched] = useState(false)

  const handleGenreLoad = async () => {
    if (genreList.length === 0) {
      try { setGenreList(await genres.getAll()) } catch {}
    }
  }

  useState(() => handleGenreLoad(), [])

  const handleSearch = async (e) => {
    e.preventDefault()
    setSearched(true)
    try {
      const params = Object.fromEntries(Object.entries(query).filter(([_, v]) => v))
      const data = await books.search(params)
      setResults(data)
    } catch { setResults([]) }
  }

  return (
    <div>
      <h3 className="mb-4">Search Books</h3>
      <form onSubmit={handleSearch} className="card p-3 mb-4 shadow-sm">
        <div className="row g-2">
          <div className="col-md-3">
            <input className="form-control" placeholder="Title" value={query.title}
              onChange={e => setQuery({...query, title: e.target.value})} />
          </div>
          <div className="col-md-3">
            <input className="form-control" placeholder="Author" value={query.author}
              onChange={e => setQuery({...query, author: e.target.value})} />
          </div>
          <div className="col-md-3">
            <select className="form-select" value={query.genre} onChange={e => setQuery({...query, genre: e.target.value})}>
              <option value="">All Genres</option>
              {genreList.map(g => <option key={g} value={g}>{g}</option>)}
            </select>
          </div>
          <div className="col-md-3">
            <input className="form-control" placeholder="ISBN" value={query.isbn}
              onChange={e => setQuery({...query, isbn: e.target.value})} />
          </div>
        </div>
        <button type="submit" className="btn btn-primary mt-2">Search</button>
      </form>

      {searched && (
        <div className="table-responsive">
          <table className="table table-striped">
            <thead className="table-dark">
              <tr><th>ISBN</th><th>Title</th><th>Author</th><th>Genre</th><th>Available</th><th></th></tr>
            </thead>
            <tbody>
              {results.map(book => (
                <tr key={book.isbn}>
                  <td>{book.isbn}</td>
                  <td><Link to={`/books/${book.isbn}`}>{book.title}</Link></td>
                  <td>{book.author}</td>
                  <td>{book.genre}</td>
                  <td><span className={`badge ${book.available > 0 ? 'bg-success' : 'bg-danger'}`}>{book.available}</span></td>
                  <td><Link to={`/books/${book.isbn}`} className="btn btn-sm btn-outline-primary">View</Link></td>
                </tr>
              ))}
              {results.length === 0 && <tr><td colSpan={6} className="text-center text-muted">No results</td></tr>}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
