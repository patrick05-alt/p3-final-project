// LAB 12 - REQUIREMENT 10: Scene - Home page showing dashboard with statistics
import { useEffect, useState } from 'react'
import { api, API_BASE_URL } from '../api/client'

const Card = ({ title, value, accent }) => (
  <div className="card">
    <div className="card-accent" style={{ background: accent }} />
    <div className="card-body">
      <p className="card-title">{title}</p>
      <p className="card-value">{value}</p>
    </div>
  </div>
)

function Home() {
  const [stats, setStats] = useState({ users: 0, contacts: 0, events: 0, locations: 0 })
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const load = async () => {
      try {
        const [users, contacts, events, locations] = await Promise.all([
          api.getUsers(),
          api.getContacts(),
          api.getEvents(),
          api.getLocations(),
        ])
        setStats({
          users: users?.length ?? 0,
          contacts: contacts ? Object.keys(contacts).length : 0,
          events: events?.length ?? 0,
          locations: locations?.length ?? 0,
        })
      } catch (err) {
        setError(err.message || 'Failed to load stats')
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
          <p className="eyebrow">Welcome</p>
          <h1>UVT Newcomer Assistant</h1>
          <p className="muted">Your comprehensive resource for campus information and services.</p>
        </div>
      </div>

      {loading ? (
        <div className="skeleton">Loading dashboard…</div>
      ) : error ? (
        <div className="error">{error}</div>
      ) : (
        <div className="grid">
          <Card title="Users" value={stats.users} accent="#7c3aed" />
          <Card title="Contacts" value={stats.contacts} accent="#2563eb" />
          <Card title="Events" value={stats.events} accent="#059669" />
          <Card title="Locations" value={stats.locations} accent="#ea580c" />
        </div>
      )}
    </div>
  )
}

export default Home
