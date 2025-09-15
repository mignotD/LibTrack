import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Register() {
  const { register } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ name: '', email: '', password: '', phone: '', address: '' })
  const [error, setError] = useState('')

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    try {
      await register(form)
      navigate('/dashboard')
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed')
    }
  }

  return (
    <div className="row justify-content-center mt-4">
      <div className="col-md-5">
        <div className="card shadow">
          <div className="card-body p-4">
            <h3 className="text-center mb-4">Register</h3>
            {error && <div className="alert alert-danger">{error}</div>}
            <form onSubmit={handleSubmit}>
              <div className="mb-3">
                <label className="form-label">Name</label>
                <input name="name" className="form-control" value={form.name} onChange={handleChange} required />
              </div>
              <div className="mb-3">
                <label className="form-label">Email</label>
                <input name="email" type="email" className="form-control" value={form.email} onChange={handleChange} required />
              </div>
              <div className="mb-3">
                <label className="form-label">Password</label>
                <input name="password" type="password" className="form-control" value={form.password} onChange={handleChange} required minLength={6} />
              </div>
              <div className="mb-3">
                <label className="form-label">Phone</label>
                <input name="phone" className="form-control" value={form.phone} onChange={handleChange} />
              </div>
              <div className="mb-3">
                <label className="form-label">Address</label>
                <textarea name="address" className="form-control" value={form.address} onChange={handleChange} rows={2} />
              </div>
              <button type="submit" className="btn btn-primary w-100">Register</button>
            </form>
            <div className="mt-3 text-center">
              <Link to="/login">Already have an account? Login</Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
