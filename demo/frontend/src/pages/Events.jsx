// LAB 12 - REQUIREMENT 10: Scene - Events management page
import { useEffect, useState } from 'react'
import { api } from '../api/client'

function Events() {
  const [events, setEvents] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const load = async () => {
      try {
        const data = await api.getEvents()
        setEvents(data || [])
      } catch (err) {
        setError(err.message || 'Failed to load events')
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
          <p className="eyebrow">Stay informed</p>
          <h1>Events</h1>
          <p className="muted">Upcoming newcomer events.</p>
        </div>
      </div>

      {loading ? (
        <div className="skeleton">Loading events…</div>
      ) : error ? (
        <div className="error">{error}</div>
      ) : events.length === 0 ? (
        <p className="muted">No events available.</p>
      ) : (
        <div className="list">
          {events.map((event, idx) => (
            <div key={`${event.name}-${idx}`} className="list-item">
              <div>
                <div className="list-title">{event.name}</div>
                <div className="list-sub">{event.date} · {event.location}</div>
              </div>
              <span className="badge">Event</span>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

export default Events
