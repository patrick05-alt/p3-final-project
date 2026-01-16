// LAB 12 - REQUIREMENT 10: Scene - Contacts management page
import { useEffect, useState } from 'react'
import { api } from '../api/client'

function Contacts() {
  const [contacts, setContacts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const load = async () => {
      try {
        const data = await api.getContacts()
        const items = Object.entries(data || {}).map(([key, value]) => ({ id: key, ...value }))
        setContacts(items)
      } catch (err) {
        setError(err.message || 'Failed to load contacts')
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [])

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <p className="eyebrow">Connect</p>
          <h1>Contacts</h1>
          <p className="muted">University contacts directory.</p>
        </div>
      </div>

      {loading ? (
        <div className="skeleton">Loading contacts…</div>
      ) : error ? (
        <div className="error">{error}</div>
      ) : (
        <div className="grid cards">
          {contacts.map((c) => (
            <div key={c.id} className="panel">
              <h3>{c.name}</h3>
              <p className="muted">{c.email}</p>
              <p>{c.phone}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

export default Contacts
