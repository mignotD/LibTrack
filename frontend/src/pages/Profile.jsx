import { useState, useEffect } from 'react'
import { profile } from '../api/client'
import { useAuth } from '../context/AuthContext'

export default function Profile() {
  const { user } = useAuth()
  const [data, setData] = useState(null)
  const [currPass, setCurrPass] = useState('')
  const [newPass, setNewPass] = useState('')
  const [newEmail, setNewEmail] = useState('')
  const [msg, setMsg] = useState('')
  const [error, setError] = useState('')

  useEffect(() => {
    profile.get().then(setData).catch(() => {})
  }, [])

  const handlePassword = async (e) => {
    e.preventDefault()
    setError(''); setMsg('')
    try {
      await profile.changePassword({ currentPassword: currPass, newPassword: newPass, confirmPassword: newPass })
      setMsg('Password changed!')
      setCurrPass(''); setNewPass('')
    } catch (err) { setError(err.response?.data?.message || 'Failed') }
  }

  const handleEmail = async (e) => {
    e.preventDefault()
    setError(''); setMsg('')
    try {
      await profile.updateEmail(newEmail)
      setMsg('Email updated!')
      setNewEmail('')
    } catch (err) { setError(err.response?.data?.message || 'Failed') }
  }

  if (!data) return <div className="text-center mt-5"><div className="spinner-border"></div></div>

  return (
    <div className="row">
      <div className="col-md-6">
        <div className="card shadow-sm mb-4">
          <div className="card-body">
            <h4>Profile</h4>
            {error && <div className="alert alert-danger">{error}</div>}
            {msg && <div className="alert alert-success">{msg}</div>}
            <table className="table">
              <tbody>
                <tr><td><strong>Name</strong></td><td>{data.name}</td></tr>
                <tr><td><strong>Email</strong></td><td>{data.email}</td></tr>
                <tr><td><strong>Phone</strong></td><td>{data.phone || '-'}</td></tr>
                <tr><td><strong>Address</strong></td><td>{data.address || '-'}</td></tr>
                <tr><td><strong>Joined</strong></td><td>{data.joinDate}</td></tr>
                <tr><td><strong>Status</strong></td><td><span className="badge bg-success">{data.status}</span></td></tr>
                <tr><td><strong>Active Loans</strong></td><td>{data.activeLoans}</td></tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
      <div className="col-md-6">
        <div className="card shadow-sm mb-4">
          <div className="card-body">
            <h5>Change Password</h5>
            <form onSubmit={handlePassword}>
              <div className="mb-2">
                <input type="password" className="form-control" placeholder="Current password"
                  value={currPass} onChange={e => setCurrPass(e.target.value)} required />
              </div>
              <div className="mb-2">
                <input type="password" className="form-control" placeholder="New password (min 6 chars)"
                  value={newPass} onChange={e => setNewPass(e.target.value)} required minLength={6} />
              </div>
              <button type="submit" className="btn btn-primary btn-sm">Update Password</button>
            </form>
          </div>
        </div>
        <div className="card shadow-sm">
          <div className="card-body">
            <h5>Update Email</h5>
            <form onSubmit={handleEmail}>
              <div className="mb-2">
                <input type="email" className="form-control" placeholder="New email"
                  value={newEmail} onChange={e => setNewEmail(e.target.value)} required />
              </div>
              <button type="submit" className="btn btn-primary btn-sm">Update Email</button>
            </form>
          </div>
        </div>
      </div>
    </div>
  )
}
