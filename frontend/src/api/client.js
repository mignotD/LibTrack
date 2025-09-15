import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(err)
  }
)

export const auth = {
  login: (data) => api.post('/auth/login', data).then(r => r.data),
  register: (data) => api.post('/auth/register', data).then(r => r.data),
  forgotPassword: (email) => api.post('/auth/forgot-password', { email }).then(r => r.data),
  resetPassword: (data) => api.post('/auth/reset-password', data).then(r => r.data),
}

export const books = {
  getAll: () => api.get('/books').then(r => r.data),
  get: (isbn) => api.get(`/books/${isbn}`).then(r => r.data),
  search: (params) => api.get('/books/search', { params }).then(r => r.data),
  byGenre: (genre) => api.get(`/books/genre/${genre}`).then(r => r.data),
  popular: () => api.get('/books/popular').then(r => r.data),
  recommendations: (isbn) => api.get(`/books/${isbn}/recommendations`).then(r => r.data),
  create: (data) => api.post('/books', data).then(r => r.data),
  update: (isbn, data) => api.put(`/books/${isbn}`, data).then(r => r.data),
  delete: (isbn) => api.delete(`/books/${isbn}`).then(r => r.data),
}

export const genres = {
  getAll: () => api.get('/genres').then(r => r.data),
}

export const transactions = {
  my: () => api.get('/transactions/my').then(r => r.data),
  myActive: () => api.get('/transactions/my/active').then(r => r.data),
  all: () => api.get('/transactions').then(r => r.data),
  overdue: () => api.get('/transactions/overdue').then(r => r.data),
  borrow: (isbn) => api.post('/transactions/borrow', { isbn }).then(r => r.data),
  return: (transactionId, fine) => api.post('/transactions/return', { transactionId, fine }).then(r => r.data),
  renew: (id) => api.post(`/transactions/${id}/renew`).then(r => r.data),
  payFine: (id) => api.post(`/transactions/${id}/pay-fine`).then(r => r.data),
  byMember: (memberId) => api.get(`/transactions/member/${memberId}`).then(r => r.data),
}

export const reviews = {
  byBook: (isbn) => api.get(`/reviews/book/${isbn}`).then(r => r.data),
  add: (data) => api.post('/reviews', data).then(r => r.data),
}

export const wishlist = {
  get: () => api.get('/wishlist').then(r => r.data),
  add: (isbn) => api.post('/wishlist', { isbn }).then(r => r.data),
  remove: (isbn) => api.delete(`/wishlist/${isbn}`).then(r => r.data),
}

export const bookRequests = {
  my: () => api.get('/book-requests').then(r => r.data),
  submit: (data) => api.post('/book-requests', data).then(r => r.data),
}

export const profile = {
  get: () => api.get('/profile').then(r => r.data),
  changePassword: (data) => api.post('/profile/change-password', data).then(r => r.data),
  updateEmail: (email) => api.post('/profile/update-email', email, {
    headers: { 'Content-Type': 'text/plain' }
  }).then(r => r.data),
}

export const admin = {
  stats: () => api.get('/admin/stats').then(r => r.data),
  members: (page, size) => api.get('/admin/members', { params: { page, size } }).then(r => r.data),
  memberCount: () => api.get('/admin/members/count').then(r => r.data),
  toggleMember: (id) => api.post(`/admin/members/${id}/toggle-status`).then(r => r.data),
  bookRequests: (status) => api.get('/admin/book-requests', { params: { status } }).then(r => r.data),
  approveRequest: (id) => api.post(`/admin/book-requests/${id}/approve`).then(r => r.data),
  rejectRequest: (id) => api.post(`/admin/book-requests/${id}/reject`).then(r => r.data),
  auditLogs: (page, size) => api.get('/admin/audit-logs', { params: { page, size } }).then(r => r.data),
  auditLogCount: () => api.get('/admin/audit-logs/count').then(r => r.data),
  borrowingLimits: () => api.get('/admin/borrowing-limits').then(r => r.data),
  updateLimit: (id, data) => api.put(`/admin/borrowing-limits/${id}`, null, { params: data }).then(r => r.data),
  popularBooks: () => api.get('/admin/popular-books').then(r => r.data),
}

export const exportCsv = {
  books: () => download('/api/export/books', 'libtrack_books.csv'),
  myTransactions: () => download('/api/export/transactions/my', 'libtrack_transactions.csv'),
}

function download(url, filename) {
  const token = localStorage.getItem('token')
  fetch(url, { headers: { Authorization: `Bearer ${token}` } })
    .then(r => r.blob())
    .then(blob => {
      const a = document.createElement('a')
      a.href = URL.createObjectURL(blob)
      a.download = filename
      a.click()
      URL.revokeObjectURL(a.href)
    })
    .catch(console.error)
}

export default api
