import { createContext, useContext, useState, useEffect } from 'react'
import { auth as authApi } from '../api/client'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('user')
    return stored ? JSON.parse(stored) : null
  })
  const [token, setToken] = useState(() => localStorage.getItem('token'))

  useEffect(() => {
    if (token) {
      localStorage.setItem('token', token)
      localStorage.setItem('user', JSON.stringify(user))
    } else {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }, [token, user])

  const login = async (email, password) => {
    const res = await authApi.login({ email, password })
    setToken(res.token)
    setUser({ memberId: res.memberId, name: res.name, email: res.email, role: res.role })
    return res
  }

  const register = async (data) => {
    const res = await authApi.register(data)
    setToken(res.token)
    setUser({ memberId: res.memberId, name: res.name, email: res.email, role: res.role })
    return res
  }

  const logout = () => {
    setToken(null)
    setUser(null)
  }

  const isAdmin = user?.role === 'admin'
  const isLoggedIn = !!user

  return (
    <AuthContext.Provider value={{ user, token, login, register, logout, isAdmin, isLoggedIn }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
