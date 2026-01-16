// LAB 12 - REQUIREMENT 10: Scene - Announcements page
import { useState, useEffect } from 'react';
import api from '../api/client';
import '../styles/Announcements.css';

function Announcements() {
  const [announcements, setAnnouncements] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadAnnouncements();
  }, []);

  const loadAnnouncements = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await api.get('/announcements');
      setAnnouncements(data);
    } catch (err) {
      setError('Failed to load announcements: ' + (err.message || 'Unknown error'));
    }
    setLoading(false);
  };

  const getPriorityColor = (priority) => {
    switch (priority) {
      case 'URGENT':
        return '#ef4444';
      case 'HIGH':
        return '#f59e0b';
      case 'NORMAL':
        return '#3b82f6';
      case 'LOW':
        return '#6b7280';
      default:
        return '#6b7280';
    }
  };

  const isExpired = (expiryDate) => {
    if (!expiryDate) return false;
    return new Date(expiryDate) < new Date();
  };

  return (
    <div className="announcements-container">
      <div className="announcements-header">
        <h1>📢 Announcements</h1>
        <p>Stay updated with the latest news and important notices</p>
      </div>

      {error && (
        <div className="error-banner">{error}</div>
      )}

      {loading ? (
        <div className="loading-state">
          <div className="spinner"></div>
          <p>Loading announcements...</p>
        </div>
      ) : announcements.length === 0 ? (
        <div className="empty-state">
          <div className="empty-icon">📭</div>
          <h2>No Announcements Yet</h2>
          <p>Check back soon for updates!</p>
        </div>
      ) : (
        <div className="announcements-grid">
          {announcements.map(announcement => (
            <div
              key={announcement.id}
              className={`announcement-card ${isExpired(announcement.expiryDate) ? 'expired' : ''}`}
            >
              <div className="announcement-header">
                <div className="priority-indicator" style={{ backgroundColor: getPriorityColor(announcement.priority) }}></div>
                <div className="announcement-meta">
                  <h2>{announcement.title}</h2>
                  <div className="announcement-info">
                    <span className="priority-badge" style={{ backgroundColor: getPriorityColor(announcement.priority) }}>
                      {announcement.priority}
                    </span>
                    <span className="date">📅 {new Date(announcement.createdAt).toLocaleDateString()}</span>
                  </div>
                </div>
              </div>

              <div className="announcement-content">
                {announcement.content}
              </div>

              <div className="announcement-footer">
                {announcement.expiryDate && (
                  <>
                    {isExpired(announcement.expiryDate) ? (
                      <span className="expired-badge">⏰ Expired on {new Date(announcement.expiryDate).toLocaleDateString()}</span>
                    ) : (
                      <span className="expiry-date">⏰ Expires: {new Date(announcement.expiryDate).toLocaleDateString()}</span>
                    )}
                  </>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default Announcements;
