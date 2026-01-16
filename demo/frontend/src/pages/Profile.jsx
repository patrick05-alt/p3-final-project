// LAB 12 - REQUIREMENT 10: Scene - User profile management page
import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';
import { useLanguage } from '../context/LanguageContext';
import apiClient from '../api/client';
import '../styles/Profile.css';

const Profile = () => {
  const { user } = useAuth();
  const { theme, toggleTheme } = useTheme();
  const { language, setLanguage, t } = useLanguage();
  const [activeTab, setActiveTab] = useState('profile');
  const [profileData, setProfileData] = useState(null);
  const [editing, setEditing] = useState(false);
  const [formData, setFormData] = useState({});
  const [activityLogs, setActivityLogs] = useState([]);
  const [myRSVPs, setMyRSVPs] = useState([]);
  const [myFavorites, setMyFavorites] = useState([]);
  const [sharedWithMe, setSharedWithMe] = useState([]);
  const [sharedByMe, setSharedByMe] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchProfileData();
  }, []);

  useEffect(() => {
    if (activeTab === 'activity') {
      fetchActivityLogs();
    } else if (activeTab === 'rsvps') {
      fetchMyRSVPs();
    } else if (activeTab === 'favorites') {
      fetchMyFavorites();
    } else if (activeTab === 'shared') {
      fetchSharedChecklists();
    }
  }, [activeTab]);

  const fetchProfileData = async () => {
    try {
      const response = await apiClient.get('/api/user/profile');
      console.log('Profile data received:', response.data);
      setProfileData(response.data);
      setFormData(response.data);
    } catch (error) {
      console.error('Failed to fetch profile:', error);
      alert('Failed to load profile. Please try refreshing the page.');
    } finally {
      setLoading(false);
    }
  };

  const fetchActivityLogs = async () => {
    try {
      const response = await apiClient.get('/api/user/activity-logs');
      setActivityLogs(response.data.logs);
    } catch (error) {
      console.error('Failed to fetch activity logs:', error);
    }
  };

  const fetchMyRSVPs = async () => {
    try {
      const response = await apiClient.get('/api/events/my-rsvps');
      setMyRSVPs(response.data);
    } catch (error) {
      console.error('Failed to fetch RSVPs:', error);
    }
  };

  const fetchMyFavorites = async () => {
    try {
      const response = await apiClient.get('/api/locations/favorites');
      setMyFavorites(response.data);
    } catch (error) {
      console.error('Failed to fetch favorites:', error);
    }
  };

  const fetchSharedChecklists = async () => {
    try {
      const [withMe, byMe] = await Promise.all([
        apiClient.get('/api/checklist/shared-with-me'),
        apiClient.get('/api/checklist/shared-by-me')
      ]);
      setSharedWithMe(withMe.data);
      setSharedByMe(byMe.data);
    } catch (error) {
      console.error('Failed to fetch shared checklists:', error);
    }
  };

  const handleInputChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleUpdateProfile = async (e) => {
    e.preventDefault();
    try {
      console.log('Updating profile with data:', formData);
      const response = await apiClient.put('/api/user/profile', formData);
      console.log('Profile updated:', response.data);
      setProfileData(response.data);
      setFormData(response.data);
      setEditing(false);
      alert('✓ Profile updated successfully!');
    } catch (error) {
      console.error('Failed to update profile:', error);
      const errorMsg = error.response?.data?.message || error.message || 'Failed to update profile';
      alert('✗ ' + errorMsg);
    }
  };

  const handleProfilePictureUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    if (file.size > 5 * 1024 * 1024) {
      alert('File size must be less than 5MB');
      return;
    }

    const formData = new FormData();
    formData.append('file', file);

    try {
      const response = await apiClient.post('/api/user/profile/picture', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      setProfileData({ ...profileData, profilePicture: response.data.imageUrl });
      alert('✓ Profile picture updated!');
    } catch (error) {
      console.error('Failed to upload profile picture:', error);
      alert('✗ Failed to upload profile picture');
    }
  };

  if (loading) {
    return <div className="loading">Loading profile...</div>;
  }

  return (
    <div className="profile-container">
      <div className="profile-header">
        <div className="profile-avatar">
          {profileData.profilePicture ? (
            <img src={profileData.profilePicture} alt="Profile" />
          ) : (
            <div className="avatar-placeholder">
              {profileData.username?.charAt(0).toUpperCase()}
            </div>
          )}
          <input 
            type="file" 
            id="profilePicture" 
            accept="image/*"
            onChange={handleProfilePictureUpload}
            style={{ display: 'none' }}
          />
          <label htmlFor="profilePicture" className="upload-btn">
            📷 Change Photo
          </label>
        </div>
        <div className="profile-info">
          <h1>{profileData.firstName} {profileData.lastName}</h1>
          <p>@{profileData.username}</p>
          <span className="role-badge">{profileData.role}</span>
        </div>
      </div>

      <div className="profile-tabs">
        <button 
          className={activeTab === 'profile' ? 'active' : ''} 
          onClick={() => setActiveTab('profile')}
        >
          {t('profile')}
        </button>
        <button 
          className={activeTab === 'rsvps' ? 'active' : ''} 
          onClick={() => setActiveTab('rsvps')}
        >
          {t('myRSVPs')}
        </button>
        <button 
          className={activeTab === 'favorites' ? 'active' : ''} 
          onClick={() => setActiveTab('favorites')}
        >
          {t('myFavorites')}
        </button>
        <button 
          className={activeTab === 'shared' ? 'active' : ''} 
          onClick={() => setActiveTab('shared')}
        >
          {t('sharedChecklists')}
        </button>
        <button 
          className={activeTab === 'activity' ? 'active' : ''} 
          onClick={() => setActiveTab('activity')}
        >
          {t('activityLogs')}
        </button>
        <button 
          className={activeTab === 'settings' ? 'active' : ''} 
          onClick={() => setActiveTab('settings')}
        >
          {t('settings')}
        </button>
      </div>

      <div className="profile-content">
        {activeTab === 'profile' && (
          <div className="profile-tab">
            {!editing ? (
              <div className="profile-view">
                <button className="edit-btn" onClick={() => setEditing(true)}>
                  {t('edit')} Profile
                </button>
                <div className="profile-field">
                  <label>Email:</label>
                  <span>{profileData.email}</span>
                </div>
                <div className="profile-field">
                  <label>Phone:</label>
                  <span>{profileData.phone || 'Not set'}</span>
                </div>
                <div className="profile-field">
                  <label>Location:</label>
                  <span>{profileData.city && profileData.country 
                    ? `${profileData.city}, ${profileData.country}` 
                    : 'Not set'}</span>
                </div>
                <div className="profile-field">
                  <label>Bio:</label>
                  <span>{profileData.bio || 'No bio yet'}</span>
                </div>
              </div>
            ) : (
              <form onSubmit={handleUpdateProfile} className="profile-edit">
                <div className="form-row">
                  <div className="form-group">
                    <label>First Name</label>
                    <input 
                      type="text" 
                      name="firstName"
                      value={formData.firstName || ''} 
                      onChange={handleInputChange}
                    />
                  </div>
                  <div className="form-group">
                    <label>Last Name</label>
                    <input 
                      type="text" 
                      name="lastName"
                      value={formData.lastName || ''} 
                      onChange={handleInputChange}
                    />
                  </div>
                </div>
                <div className="form-group">
                  <label>Email</label>
                  <input 
                    type="email" 
                    name="email"
                    value={formData.email || ''} 
                    onChange={handleInputChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Phone</label>
                  <input 
                    type="tel" 
                    name="phone"
                    value={formData.phone || ''} 
                    onChange={handleInputChange}
                  />
                </div>
                <div className="form-row">
                  <div className="form-group">
                    <label>City</label>
                    <input 
                      type="text" 
                      name="city"
                      value={formData.city || ''} 
                      onChange={handleInputChange}
                    />
                  </div>
                  <div className="form-group">
                    <label>Country</label>
                    <input 
                      type="text" 
                      name="country"
                      value={formData.country || ''} 
                      onChange={handleInputChange}
                    />
                  </div>
                </div>
                <div className="form-group">
                  <label>Bio</label>
                  <textarea 
                    name="bio"
                    value={formData.bio || ''} 
                    onChange={handleInputChange}
                    rows="4"
                  />
                </div>
                <div className="form-actions">
                  <button type="submit" className="save-btn">{t('save')}</button>
                  <button type="button" className="cancel-btn" onClick={() => setEditing(false)}>
                    {t('cancel')}
                  </button>
                </div>
              </form>
            )}
          </div>
        )}

        {activeTab === 'rsvps' && (
          <div className="rsvps-tab">
            <h2>My Event RSVPs</h2>
            {myRSVPs.length === 0 ? (
              <p>No RSVPs yet</p>
            ) : (
              <div className="rsvp-list">
                {myRSVPs.map(rsvp => (
                  <div key={rsvp.id} className="rsvp-card">
                    <h3>{rsvp.event.name}</h3>
                    <p>📍 {rsvp.event.location}</p>
                    <p>📅 {rsvp.event.date}</p>
                    <span className={`status-badge ${rsvp.status.toLowerCase()}`}>
                      {rsvp.status}
                    </span>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {activeTab === 'favorites' && (
          <div className="favorites-tab">
            <h2>My Favorite Locations</h2>
            {myFavorites.length === 0 ? (
              <p>No favorites yet</p>
            ) : (
              <div className="favorites-list">
                {myFavorites.map(fav => (
                  <div key={fav.id} className="favorite-card">
                    <h3>{fav.location.name}</h3>
                    <p>📍 {fav.location.category}</p>
                    {fav.notes && <p className="notes">📝 {fav.notes}</p>}
                    <span className="date-added">Added: {new Date(fav.addedDate).toLocaleDateString()}</span>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {activeTab === 'shared' && (
          <div className="shared-tab">
            <h2>Shared Checklists</h2>
            <div className="shared-section">
              <h3>Shared With Me</h3>
              {sharedWithMe.length === 0 ? (
                <p>No checklists shared with you</p>
              ) : (
                <div className="shared-list">
                  {sharedWithMe.map(shared => (
                    <div key={shared.id} className="shared-card">
                      <p><strong>From:</strong> {shared.owner}</p>
                      <p><strong>Items:</strong> {shared.completedCount}/{shared.itemCount}</p>
                      <span className="permission-badge">
                        {shared.canEdit ? '✏️ Can Edit' : '👁️ View Only'}
                      </span>
                    </div>
                  ))}
                </div>
              )}
            </div>

            <div className="shared-section">
              <h3>Shared By Me</h3>
              {sharedByMe.length === 0 ? (
                <p>You haven't shared your checklist yet</p>
              ) : (
                <div className="shared-list">
                  {sharedByMe.map(shared => (
                    <div key={shared.id} className="shared-card">
                      <p><strong>To:</strong> {shared.sharedWith}</p>
                      <span className="permission-badge">
                        {shared.canEdit ? '✏️ Can Edit' : '👁️ View Only'}
                      </span>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        )}

        {activeTab === 'activity' && (
          <div className="activity-tab">
            <h2>Activity Logs</h2>
            {activityLogs.length === 0 ? (
              <p>No activity yet</p>
            ) : (
              <div className="activity-list">
                {activityLogs.map(log => (
                  <div key={log.id} className="activity-item">
                    <span className="activity-action">{log.action}</span>
                    <span className="activity-entity">{log.entity}</span>
                    <span className="activity-details">{log.details}</span>
                    <span className="activity-time">
                      {new Date(log.timestamp).toLocaleString()}
                    </span>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {activeTab === 'settings' && (
          <div className="settings-tab">
            <h2>{t('settings')}</h2>
            
            <div className="setting-group">
              <label>{t('darkMode')}</label>
              <button onClick={toggleTheme} className="theme-toggle">
                {theme === 'light' ? '🌙 Switch to Dark' : '☀️ Switch to Light'}
              </button>
            </div>

            <div className="setting-group">
              <label>{t('language')}</label>
              <select 
                value={language} 
                onChange={(e) => setLanguage(e.target.value)}
                className="language-select"
              >
                <option value="en">English</option>
                <option value="es">Español</option>
                <option value="fr">Français</option>
              </select>
            </div>

            <div className="setting-group">
              <label>Email Notifications</label>
              <label className="switch">
                <input 
                  type="checkbox" 
                  checked={formData.emailNotifications || false}
                  onChange={(e) => {
                    const newFormData = { ...formData, emailNotifications: e.target.checked };
                    setFormData(newFormData);
                    apiClient.put('/api/user/profile', newFormData);
                  }}
                />
                <span className="slider"></span>
              </label>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Profile;
