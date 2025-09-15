import { useState } from 'react'
import { Link } from 'react-router-dom'
import { auth } from '../api/client'

export default function ForgotPassword() {
  const [email, setEmail] = useState('')
  const [sent, setSent] = useState(false)
  const [error, setError] = useState('')

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    try {
      await auth.forgotPassword(email)
      setSent(true)
    } catch (err) {
      setError(err.response?.data?.message || 'Something went wrong')
    }
  }

  if (sent) {
    return (
      <div className="row justify-content-center mt-5">
        <div className="col-md-4">
          <div className="alert alert-success text-center">
            If that email is registered, a reset link has been sent.
          </div>
          <div className="text-center"><Link to="/login">Back to Login</Link></div>
        </div>
      </div>
    )
  }

  return (
    <div className="row justify-content-center mt-5">
      <div className="col-md-4">
        <div className="card shadow">
          <div className="card-body p-4">
            <h3 className="text-center mb-4">Forgot Password</h3>
            {error && <div className="alert alert-danger">{error}</div>}
            <form onSubmit={handleSubmit}>
              <div className="mb-3">
                <label className="form-label">Email</label>
                <input type="email" className="form-control" value={email} onChange={e => setEmail(e.target.value)} required />
              </div>
              <button type="submit" className="btn btn-primary w-100">Send Reset Link</button>
            </form>
            <div className="mt-3 text-center"><Link to="/login">Back to Login</Link></div>
          </div>
        </div>
      </div>
    </div>
  )
}
