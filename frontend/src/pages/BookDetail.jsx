import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import { books, reviews, transactions, wishlist as wishlistApi, bookRequests } from '../api/client'
import { useAuth } from '../context/AuthContext'

export default function BookDetail() {
  const { isbn } = useParams()
  const { isLoggedIn, user } = useAuth()
  const [book, setBook] = useState(null)
  const [reviewList, setReviewList] = useState([])
  const [recommendations, setRecommendations] = useState([])
  const [rating, setRating] = useState(5)
  const [comment, setComment] = useState('')
  const [error, setError] = useState('')
  const [msg, setMsg] = useState('')
  const [inWishlist, setInWishlist] = useState(false)

  const load = () => {
    books.get(isbn).then(setBook).catch(() => {})
    reviews.byBook(isbn).then(setReviewList).catch(() => {})
    books.recommendations(isbn).then(setRecommendations).catch(() => {})
  }

  useEffect(() => { load() }, [isbn])

  const handleBorrow = async () => {
    try {
      const res = await transactions.borrow(isbn)
      setMsg('Book borrowed successfully!')
      load()
    } catch (err) { setError(err.response?.data?.message || 'Borrow failed') }
  }

  const handleReview = async (e) => {
    e.preventDefault()
    setError('')
    try {
      await reviews.add({ isbn, rating, comment })
      setRating(5); setComment('')
      setMsg('Review added!')
      load()
    } catch (err) { setError(err.response?.data?.message || 'Review failed') }
  }

  const handleWishlist = async () => {
    try {
      if (inWishlist) {
        await wishlistApi.remove(isbn)
        setInWishlist(false)
        setMsg('Removed from wishlist')
      } else {
        await wishlistApi.add(isbn)
        setInWishlist(true)
        setMsg('Added to wishlist')
      }
    } catch (err) { setError(err.response?.data?.message || 'Failed') }
  }

  if (!book) return <div className="text-center mt-5"><div className="spinner-border"></div></div>

  return (
    <div>
      {error && <div className="alert alert-danger">{error}</div>}
      {msg && <div className="alert alert-success">{msg}</div>}

      <div className="row mb-4">
        <div className="col-md-4">
          {book.coverUrl
            ? <img src={book.coverUrl} alt={book.title} className="img-fluid rounded shadow" />
            : <div className="bg-light p-5 text-center rounded">No Cover</div>}
        </div>
        <div className="col-md-8">
          <h2>{book.title}</h2>
          <p className="text-muted">by {book.author}</p>
          <p><strong>ISBN:</strong> {book.isbn}</p>
          <p><strong>Genre:</strong> <Link to={`/browse/${book.genre}`}>{book.genre}</Link></p>
          <p><strong>Publisher:</strong> {book.publisher}</p>
          <p><strong>Year:</strong> {book.publicationYear}</p>
          <p><strong>Stock:</strong> {book.stock} | <strong>Available:</strong>
            <span className={`badge ms-1 ${book.available > 0 ? 'bg-success' : 'bg-danger'}`}>{book.available}</span></p>
          {book.averageRating > 0 && <p>⭐ {book.averageRating.toFixed(1)} ({book.reviewCount} reviews)</p>}

          <div className="mt-3">
            {isLoggedIn && book.available > 0 && (
              <button className="btn btn-success me-2" onClick={handleBorrow}>Borrow</button>
            )}
            {isLoggedIn && (
              <button className="btn btn-outline-warning" onClick={handleWishlist}>
                {inWishlist ? 'Remove from Wishlist' : 'Add to Wishlist'}
              </button>
            )}
          </div>
        </div>
      </div>

      <div className="row">
        <div className="col-md-6">
          <h4>Reviews</h4>
          {reviewList.length === 0 && <p className="text-muted">No reviews yet.</p>}
          {reviewList.map(r => (
            <div key={r.reviewId} className="card mb-2">
              <div className="card-body py-2">
                <strong>{r.memberName}</strong>
                <span className="ms-2">{'⭐'.repeat(r.rating)}</span>
                <p className="mb-0 mt-1">{r.comment}</p>
                <small className="text-muted">{new Date(r.reviewDate).toLocaleDateString()}</small>
              </div>
            </div>
          ))}
          {isLoggedIn && (
            <form onSubmit={handleReview} className="mt-3">
              <h5>Write a Review</h5>
              <div className="mb-2">
                <label className="form-label">Rating</label>
                <select className="form-select" value={rating} onChange={e => setRating(Number(e.target.value))}>
                  {[1,2,3,4,5].map(n => <option key={n} value={n}>{n}</option>)}
                </select>
              </div>
              <div className="mb-2">
                <textarea className="form-control" rows={2} placeholder="Comment (optional)"
                  value={comment} onChange={e => setComment(e.target.value)} />
              </div>
              <button type="submit" className="btn btn-primary btn-sm">Submit Review</button>
            </form>
          )}
        </div>
        <div className="col-md-6">
          {recommendations.length > 0 && (
            <>
              <h4>Recommendations</h4>
              {recommendations.map(r => (
                <div key={r.isbn} className="card mb-2">
                  <div className="card-body py-2">
                    <Link to={`/books/${r.isbn}`} className="text-decoration-none">{r.title}</Link>
                    <small className="d-block text-muted">{r.author}</small>
                  </div>
                </div>
              ))}
            </>
          )}
        </div>
      </div>
    </div>
  )
}
