// LAB 12 - REQUIREMENT 10: Scene - Locations management page
import { useEffect, useState } from 'react'
import { api } from '../api/client'

function Locations() {
  const [locations, setLocations] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const load = async () => {
      try {
        const data = await api.getLocations()
        setLocations(data || [])
      } catch (err) {
        setError(err.message || 'Failed to load locations')
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
          <p className="eyebrow">Explore</p>
          <h1>Locations</h1>
          <p className="muted">Important places for newcomers.</p>
        </div>
      </div>

      {loading ? (
        <div className="skeleton">Loading locations…</div>
      ) : error ? (
        <div className="error">{error}</div>
      ) : locations.length === 0 ? (
        <p className="muted">No locations available.</p>
      ) : (
        <div className="grid cards">
          {locations.map((loc, idx) => (
            <div key={`${loc.name}-${idx}`} className="panel">
              <h3>{loc.name}</h3>
              <p className="muted">{loc.category}</p>
              <p>{loc.details}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

export default Locations
