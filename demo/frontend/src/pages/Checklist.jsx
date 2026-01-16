// LAB 12 - REQUIREMENT 10: Scene - Checklist management page
import { useEffect, useMemo, useState } from 'react'
import { api } from '../api/client'

function Checklist() {
  const [users, setUsers] = useState([])
  const [selectedUser, setSelectedUser] = useState('')
  const [items, setItems] = useState([])
  const [statuses, setStatuses] = useState({})
  const [progress, setProgress] = useState({ checked: 0, total: 0 })
  const [loading, setLoading] = useState(true)
  const [message, setMessage] = useState('')

  const loadUsers = async () => {
    const list = await api.getUsers()
    setUsers(list)
    if (!selectedUser && list.length > 0) {
      setSelectedUser(String(list[0].id))
    }
  }

  const loadChecklist = async (userId) => {
    setLoading(true)
    try {
      const [checklistItems, checklistStatuses, checklistProgress] = await Promise.all([
        api.getChecklistItems(),
        api.getChecklistStatuses(),
        api.getChecklistProgress(userId),
      ])

      setItems(checklistItems)
      const map = {}
      checklistStatuses
        .filter((s) => s.user && String(s.user.id) === String(userId))
        .forEach((s) => {
          map[s.checklistItemId] = s.checked ?? s.isChecked
        })
      setStatuses(map)
      setProgress({ checked: checklistProgress.checkedCount ?? 0, total: checklistProgress.totalCount ?? checklistItems.length })
    } catch (err) {
      setMessage(err.message || 'Failed to load checklist')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadUsers().catch(() => {})
  }, [])

  useEffect(() => {
    if (selectedUser) {
      loadChecklist(selectedUser)
    }
  }, [selectedUser])

  const handleToggle = async (itemId, isChecked) => {
    if (!selectedUser) return
    setMessage('')
    try {
      const nextStatus = !isChecked
      if (nextStatus) {
        await api.checkItem(selectedUser, itemId)
      } else {
        await api.uncheckItem(selectedUser, itemId)
      }
      setStatuses((prev) => ({ ...prev, [itemId]: nextStatus }))
      setProgress((prev) => ({
        checked: prev.checked + (nextStatus ? 1 : -1),
        total: prev.total || items.length,
      }))
    } catch (err) {
      setMessage(err.message || 'Could not update item')
    }
  }

  const percent = useMemo(() => {
    if (!progress.total) return 0
    return Math.round((progress.checked / progress.total) * 100)
  }, [progress])

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <p className="eyebrow">Stay on track</p>
          <h1>User Checklist</h1>
          <p className="muted">Toggle onboarding items for a selected user.</p>
        </div>
        <div className="pill">Progress: {progress.checked}/{progress.total} ({percent}%)</div>
      </div>

      <div className="panel">
        <label className="inline">
          User
          <select value={selectedUser} onChange={(e) => setSelectedUser(e.target.value)}>
            {users.map((u) => (
              <option key={u.id} value={u.id}>
                {u.username} (#{u.id})
              </option>
            ))}
          </select>
        </label>
      </div>

      <div className="panel">
        <h3>Checklist Items</h3>
        {loading ? (
          <div className="skeleton">Loading checklist…</div>
        ) : items.length === 0 ? (
          <p className="muted">No checklist items available.</p>
        ) : (
          <div className="list">
            {items.map((item) => {
              const checked = Boolean(statuses[item.id])
              return (
                <label key={item.id} className="list-item" style={{ cursor: 'pointer' }}>
                  <input
                    type="checkbox"
                    checked={checked}
                    onChange={() => handleToggle(item.id, checked)}
                    style={{ cursor: 'pointer' }}
                  />
                  <div style={{ flex: 1, cursor: 'pointer' }}>
                    <div className="list-title" style={{ fontSize: '16px', fontWeight: '600', color: '#e5e7eb' }}>
                      {item.task}
                    </div>
                    <div className="list-sub" style={{ fontSize: '13px', color: '#94a3b8', marginTop: '4px' }}>
                      ID: {item.id}
                    </div>
                  </div>
                  <span className={`badge ${checked ? 'success' : ''}`} style={{ cursor: 'default', userSelect: 'none' }}>
                    {checked ? '\u2713 Done' : '\u23F3 Pending'}
                  </span>
                </label>
              )
            })}
          </div>
        )}
        {message && <p className="error">{message}</p>}
      </div>
    </div>
  )
}

export default Checklist
