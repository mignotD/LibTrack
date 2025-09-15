import { useState } from 'react'
import { useSearchParams, useNavigate, Link } from 'react-router-dom'
import { auth } from '../api/client'

export default function ResetPassword() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    const token = searchParams.get('token')
    if (!token) { setError('Invalid reset link'); return }
    try {
      await auth.resetPassword({ token, password })
      setSuccess(true)
    } catch (err) {
      setError(err.response?.data?.message || 'Reset failed')
    }
  }

  if (success) {
    return (
      <div className="row justify-content-center mt-5">
        <div className="col-md-4">
          <div className="alert alert-success text-center">Password reset successfully!</div>
          <div className="text-center"><Link to="/login">Login</Link></div>
        </div>
      </div>
    )
  }

  return (
    <div className="row justify-content-center mt-5">
      <div className="col-md-4">
        <div className="card shadow">
          <div className="card-body p-4">
            <h3 className="text-center mb-4">Reset Password</h3>
            {error && <div className="alert alert-danger">{error}</div>}
            <form onSubmit={handleSubmit}>
              <div className="mb-3">
                <label className="form-label">New Password</label>
                <input type="password" className="form-control" value={password}
                  onChange={e => setPassword(e.target.value)} required minLength={6} />
              </div>
              <button type="submit" className="btn btn-primary w-100">Reset</button>
            </form>
          </div>
        </div>
      </div>
    </div>
  )
}
