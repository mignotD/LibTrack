import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Navbar() {
  const { user, logout, isLoggedIn, isAdmin } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark">
      <div className="container">
        <Link className="navbar-brand" to="/">📚 LibTrack</Link>
        <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMenu">
          <span className="navbar-toggler-icon"></span>
        </button>
        <div className="collapse navbar-collapse" id="navMenu">
          <ul className="navbar-nav me-auto">
            <li className="nav-item"><Link className="nav-link" to="/dashboard">Home</Link></li>
            <li className="nav-item"><Link className="nav-link" to="/books">Books</Link></li>
            <li className="nav-item"><Link className="nav-link" to="/search">Search</Link></li>
            {isLoggedIn && (
              <>
                <li className="nav-item"><Link className="nav-link" to="/my-books">My Books</Link></li>
                <li className="nav-item"><Link className="nav-link" to="/wishlist">Wishlist</Link></li>
                <li className="nav-item"><Link className="nav-link" to="/book-requests">Requests</Link></li>
              </>
            )}
            {isAdmin && (
              <li className="nav-item dropdown">
                <a className="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">Admin</a>
                <ul className="dropdown-menu">
                  <li><Link className="dropdown-item" to="/admin">Dashboard</Link></li>
                  <li><Link className="dropdown-item" to="/admin/members">Members</Link></li>
                  <li><Link className="dropdown-item" to="/admin/requests">Book Requests</Link></li>
                  <li><Link className="dropdown-item" to="/admin/audit">Audit Log</Link></li>
                  <li><Link className="dropdown-item" to="/admin/limits">Borrowing Limits</Link></li>
                </ul>
              </li>
            )}
          </ul>
          <ul className="navbar-nav">
            {isLoggedIn ? (
              <>
                <li className="nav-item"><Link className="nav-link" to="/profile">{user?.name}</Link></li>
                <li className="nav-item"><button className="btn btn-outline-light btn-sm ms-2" onClick={handleLogout}>Logout</button></li>
              </>
            ) : (
              <>
                <li className="nav-item"><Link className="nav-link" to="/login">Login</Link></li>
                <li className="nav-item"><Link className="nav-link" to="/register">Register</Link></li>
              </>
            )}
          </ul>
        </div>
      </div>
    </nav>
  )
}
