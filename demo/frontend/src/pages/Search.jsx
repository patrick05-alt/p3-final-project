// LAB 12 - REQUIREMENT 10: Scene - Search page
import { useState } from 'react'
import { api } from '../api/client'

function Search() {
  const [q, setQ] = useState('')
  const [results, setResults] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const onSearch = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const data = await api.search(q)
      setResults(data)
    } catch (err) {
      setError(err.message || 'Search failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <p className="eyebrow">Find anything</p>
          <h1>Search</h1>
          <p className="muted">Search contacts, events, and locations.</p>
        </div>
      </div>

      <form className="form" onSubmit={onSearch}>
        <label>
          Query
          <input value={q} onChange={(e) => setQ(e.target.value)} placeholder="Type a name, event, or place" />
        </label>
        <button type="submit" disabled={loading}>Search</button>
      </form>

      {error && <p className="error">{error}</p>}

      {loading ? (
        <div className="skeleton">Searching…</div>
      ) : (
          <div className="list">
            {results.map((item, idx) => (
              <div key={idx} className="list-item">
                <div>
                  <div className="list-title">{item.title || item.name}</div>
                  <div className="list-sub">{item.description || item.details || item.email || item.location || item.category}</div>
                </div>
                <span className="badge">{item.type || 'Result'}</span>
              </div>
            ))}
          {results.length === 0 && <p className="muted">No results yet.</p>}
        </div>
      )}
    </div>
  )
}

export default Search
