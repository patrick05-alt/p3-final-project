// LAB 12 - REQUIREMENT 10: Scene - Admin user management page
import { useEffect, useState } from 'react'
import { api } from '../api/client'

function Users() {
  const [users, setUsers] = useState([])
  const [form, setForm] = useState({ username: '', email: '', password: '' })
  const [loading, setLoading] = useState(true)
  const [message, setMessage] = useState('')

  const load = async () => {
    setLoading(true)
    try {
      const data = await api.getUsers()
      setUsers(data)
    } catch (err) {
      setMessage(err.message || 'Failed to load users')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [])

  const onChange = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const onSubmit = async (e) => {
    e.preventDefault()
    setMessage('')
    try {
      await api.createUser(form)
      setForm({ username: '', email: '', password: '' })
      await load()
      setMessage('User created!')
    } catch (err) {
      setMessage(err.message || 'Could not create user')
    }
  }

  const onDelete = async (id) => {
    if (!window.confirm('Delete this user?')) return
    try {
      await api.deleteUser(id)
      await load()
    } catch (err) {
      setMessage(err.message || 'Delete failed')
    }
  }

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <p className="eyebrow">Manage</p>
          <h1>Users</h1>
          <p className="muted">Create, list, and delete users via the Spring API.</p>
        </div>
      </div>

      <div className="panel">
        <h3>Create User</h3>
        <form className="form" onSubmit={onSubmit}>
          <label>
            Username
            <input name="username" value={form.username} onChange={onChange} required />
          </label>
          <label>
            Email
            <input name="email" type="email" value={form.email} onChange={onChange} required />
          </label>
          <label>
            Password
            <input name="password" type="password" value={form.password} onChange={onChange} required />
          </label>
          <button type="submit">Create</button>
        </form>
        {message && <p className="info">{message}</p>}
      </div>

      <div className="panel">
        <h3>All Users</h3>
        {loading ? (
          <div className="skeleton">Loading users…</div>
        ) : users.length === 0 ? (
          <p className="muted">No users yet.</p>
        ) : (
          <div className="table">
            <div className="table-head">
              <span>ID</span>
              <span>Username</span>
              <span>Email</span>
              <span></span>
            </div>
            {users.map((user) => (
              <div key={user.id} className="table-row">
                <span>{user.id}</span>
                <span>{user.username}</span>
                <span>{user.email}</span>
                <span>
                  <button className="ghost" onClick={() => onDelete(user.id)}>Delete</button>
                </span>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}

export default Users
