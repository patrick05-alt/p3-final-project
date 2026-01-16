// LAB 12 - REQUIREMENT 10: Scene - Admin panel for system management
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/client';
import '../styles/AdminPanel.css';

function AdminPanel() {
  const { user, isAdmin } = useAuth();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('users');
  const [users, setUsers] = useState([]);
  const [events, setEvents] = useState([]);
  const [locations, setLocations] = useState([]);
  const [contacts, setContacts] = useState([]);
  const [announcements, setAnnouncements] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!isAdmin) {
      navigate('/');
      return;
    }
    loadData();
  }, [isAdmin, navigate, activeTab]);

  const loadData = async () => {
    setLoading(true);
    setError('');
    try {
      switch (activeTab) {
        case 'users':
          const usersRes = await api.get('/admin/users');
          setUsers(usersRes);
          break;
        case 'events':
          const eventsRes = await api.get('/admin/events');
          setEvents(eventsRes);
          break;
        case 'locations':
          const locationsRes = await api.get('/admin/locations');
          setLocations(locationsRes);
          break;
        case 'contacts':
          const contactsRes = await api.get('/admin/contacts');
          setContacts(contactsRes);
          break;
        case 'announcements':
          const announcementsRes = await api.get('/admin/announcements');
          setAnnouncements(announcementsRes);
          break;
      }
    } catch (err) {
      setError('Failed to load data: ' + (err.response?.data?.message || err.message));
    }
    setLoading(false);
  };

  const deleteUser = async (id) => {
    if (!confirm('Are you sure you want to delete this user?')) return;
    try {
      await api.delete(`/admin/users/${id}`);
      loadData();
    } catch (err) {
      setError('Failed to delete user');
    }
  };

  const deleteEvent = async (id) => {
    if (!confirm('Are you sure you want to delete this event?')) return;
    try {
      await api.delete(`/admin/events/${id}`);
      loadData();
    } catch (err) {
      setError('Failed to delete event');
    }
  };

  const deleteLocation = async (id) => {
    if (!confirm('Are you sure you want to delete this location?')) return;
    try {
      await api.delete(`/admin/locations/${id}`);
      loadData();
    } catch (err) {
      setError('Failed to delete location');
    }
  };
  
  const deleteContact = async (id) => {
    if (!confirm('Are you sure you want to delete this contact?')) return;
    try {
      await api.delete(`/admin/contacts/${id}`);
      loadData();
    } catch (err) {
      setError('Failed to delete contact');
    }
  };

  const deleteAnnouncement = async (id) => {
    if (!confirm('Are you sure you want to delete this announcement?')) return;
    try {
      await api.delete(`/admin/announcements/${id}`);
      loadData();
    } catch (err) {
      setError('Failed to delete announcement');
    }
  };

  return (
    <div className="admin-panel">
      <div className="admin-header">
        <h1>🛠️ Admin Panel</h1>
        <p className="welcome-text">Welcome, <strong>{user?.username}</strong>!</p>
      </div>

      <div className="admin-tabs">
        <button 
          className={activeTab === 'users' ? 'active' : ''}
          onClick={() => setActiveTab('users')}
        >
          👥 Users
        </button>
        <button 
          className={activeTab === 'events' ? 'active' : ''}
          onClick={() => setActiveTab('events')}
        >
          📅 Events
        </button>
        <button 
          className={activeTab === 'locations' ? 'active' : ''}
          onClick={() => setActiveTab('locations')}
        >
          📍 Locations
        </button>
        <button 
          className={activeTab === 'contacts' ? 'active' : ''}
          onClick={() => setActiveTab('contacts')}
        >
          📞 Contacts
        </button>
        <button 
          className={activeTab === 'announcements' ? 'active' : ''}
          onClick={() => setActiveTab('announcements')}
        >
          📢 Announcements
        </button>
      </div>

      {error && <div className="error-banner">{error}</div>}

      <div className="admin-content">
        {loading ? (
          <div className="loading">⏳ Loading...</div>
        ) : (
          <>
            {activeTab === 'users' && <UsersTab users={users} onDelete={deleteUser} onReload={loadData} />}
            {activeTab === 'events' && <EventsTab events={events} onDelete={deleteEvent} onReload={loadData} />}
            {activeTab === 'locations' && <LocationsTab locations={locations} onDelete={deleteLocation} onReload={loadData} />}
            {activeTab === 'contacts' && <ContactsTab contacts={contacts} onDelete={deleteContact} onReload={loadData} />}
            {activeTab === 'announcements' && <AnnouncementsTab announcements={announcements} onDelete={deleteAnnouncement} onReload={loadData} />}
          </>
        )}
      </div>
    </div>
  );
}

function UsersTab({ users, onDelete, onReload }) {
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({ username: '', password: '', role: 'USER' });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.post('/admin/users', formData);
      setShowForm(false);
      setFormData({ username: '', password: '', role: 'USER' });
      onReload();
    } catch (err) {
      alert('Failed to create user');
    }
  };

  return (
    <div className="tab-content">
      <div className="tab-header">
        <h2>User Management</h2>
        <button onClick={() => setShowForm(!showForm)} className="add-button">
          {showForm ? '❌ Cancel' : '➕ Add User'}
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} className="admin-form">
          <input
            type="text"
            placeholder="Username"
            value={formData.username}
            onChange={(e) => setFormData({...formData, username: e.target.value})}
            required
          />
          <input
            type="password"
            placeholder="Password"
            value={formData.password}
            onChange={(e) => setFormData({...formData, password: e.target.value})}
            required
          />
          <select
            value={formData.role}
            onChange={(e) => setFormData({...formData, role: e.target.value})}
          >
            <option value="USER">User</option>
            <option value="ADMIN">Admin</option>
          </select>
          <button type="submit">Create User</button>
        </form>
      )}

      <table className="admin-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Username</th>
            <th>Role</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {users.map(user => (
            <tr key={user.id}>
              <td>{user.id}</td>
              <td>{user.username}</td>
              <td><span className={`role-badge ${user.role}`}>{user.role}</span></td>
              <td>
                <button onClick={() => onDelete(user.id)} className="delete-button">Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function EventsTab({ events, onDelete, onReload }) {
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    title: '', description: '', date: '', location: '', category: ''
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.post('/admin/events', formData);
      setShowForm(false);
      setFormData({ title: '', description: '', date: '', location: '', category: '' });
      onReload();
    } catch (err) {
      alert('Failed to create event');
    }
  };

  return (
    <div className="tab-content">
      <div className="tab-header">
        <h2>Event Management</h2>
        <button onClick={() => setShowForm(!showForm)} className="add-button">
          {showForm ? '❌ Cancel' : '➕ Add Event'}
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} className="admin-form">
          <input
            type="text"
            placeholder="Event Title"
            value={formData.title}
            onChange={(e) => setFormData({...formData, title: e.target.value})}
            required
          />
          <textarea
            placeholder="Description"
            value={formData.description}
            onChange={(e) => setFormData({...formData, description: e.target.value})}
            required
          />
          <input
            type="date"
            value={formData.date}
            onChange={(e) => setFormData({...formData, date: e.target.value})}
            required
          />
          <input
            type="text"
            placeholder="Location"
            value={formData.location}
            onChange={(e) => setFormData({...formData, location: e.target.value})}
            required
          />
          <input
            type="text"
            placeholder="Category"
            value={formData.category}
            onChange={(e) => setFormData({...formData, category: e.target.value})}
            required
          />
          <button type="submit">Create Event</button>
        </form>
      )}

      <table className="admin-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Title</th>
            <th>Date</th>
            <th>Location</th>
            <th>Category</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {events.map(event => (
            <tr key={event.id}>
              <td>{event.id}</td>
              <td>{event.title}</td>
              <td>{event.date}</td>
              <td>{event.location}</td>
              <td>{event.category}</td>
              <td>
                <button onClick={() => onDelete(event.id)} className="delete-button">Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function LocationsTab({ locations, onDelete, onReload }) {
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    name: '', address: '', category: '', description: '', phone: '', website: ''
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.post('/admin/locations', formData);
      setShowForm(false);
      setFormData({ name: '', address: '', category: '', description: '', phone: '', website: '' });
      onReload();
    } catch (err) {
      alert('Failed to create location');
    }
  };

  return (
    <div className="tab-content">
      <div className="tab-header">
        <h2>Location Management</h2>
        <button onClick={() => setShowForm(!showForm)} className="add-button">
          {showForm ? '❌ Cancel' : '➕ Add Location'}
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} className="admin-form">
          <input
            type="text"
            placeholder="Name"
            value={formData.name}
            onChange={(e) => setFormData({...formData, name: e.target.value})}
            required
          />
          <input
            type="text"
            placeholder="Address"
            value={formData.address}
            onChange={(e) => setFormData({...formData, address: e.target.value})}
            required
          />
          <input
            type="text"
            placeholder="Category"
            value={formData.category}
            onChange={(e) => setFormData({...formData, category: e.target.value})}
            required
          />
          <textarea
            placeholder="Description"
            value={formData.description}
            onChange={(e) => setFormData({...formData, description: e.target.value})}
          />
          <input
            type="text"
            placeholder="Phone"
            value={formData.phone}
            onChange={(e) => setFormData({...formData, phone: e.target.value})}
          />
          <input
            type="text"
            placeholder="Website"
            value={formData.website}
            onChange={(e) => setFormData({...formData, website: e.target.value})}
          />
          <button type="submit">Create Location</button>
        </form>
      )}

      <table className="admin-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Address</th>
            <th>Category</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {locations.map(location => (
            <tr key={location.id}>
              <td>{location.id}</td>
              <td>{location.name}</td>
              <td>{location.address}</td>
              <td>{location.category}</td>
              <td>
                <button onClick={() => onDelete(location.id)} className="delete-button">Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function AnnouncementsTab({ announcements, onDelete, onReload }) {
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    title: '', content: '', priority: 'NORMAL', expiryDate: ''
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.post('/admin/announcements', formData);
      setShowForm(false);
      setFormData({ title: '', content: '', priority: 'NORMAL', expiryDate: '' });
      onReload();
    } catch (err) {
      alert('Failed to create announcement');
    }
  };

  return (
    <div className="tab-content">
      <div className="tab-header">
        <h2>Announcement Management</h2>
        <button onClick={() => setShowForm(!showForm)} className="add-button">
          {showForm ? '❌ Cancel' : '➕ Add Announcement'}
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} className="admin-form">
          <input
            type="text"
            placeholder="Title"
            value={formData.title}
            onChange={(e) => setFormData({...formData, title: e.target.value})}
            required
          />
          <textarea
            placeholder="Content"
            value={formData.content}
            onChange={(e) => setFormData({...formData, content: e.target.value})}
            required
          />
          <select
            value={formData.priority}
            onChange={(e) => setFormData({...formData, priority: e.target.value})}
          >
            <option value="LOW">Low</option>
            <option value="NORMAL">Normal</option>
            <option value="HIGH">High</option>
            <option value="URGENT">Urgent</option>
          </select>
          <input
            type="date"
            placeholder="Expiry Date (optional)"
            value={formData.expiryDate}
            onChange={(e) => setFormData({...formData, expiryDate: e.target.value})}
          />
          <button type="submit">Create Announcement</button>
        </form>
      )}

      <table className="admin-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Title</th>
            <th>Priority</th>
            <th>Created</th>
            <th>Expires</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {announcements.map(announcement => (
            <tr key={announcement.id}>
              <td>{announcement.id}</td>
              <td>{announcement.title}</td>
              <td><span className={`priority-badge ${announcement.priority}`}>{announcement.priority}</span></td>
              <td>{new Date(announcement.createdAt).toLocaleDateString()}</td>
              <td>{announcement.expiryDate || 'N/A'}</td>
              <td>
                <button onClick={() => onDelete(announcement.id)} className="delete-button">Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

// Contacts Tab Component
function ContactsTab({ contacts, onDelete, onReload }) {
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({ name: '', email: '', phone: '' });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.post('/admin/contacts', formData);
      setFormData({ name: '', email: '', phone: '' });
      setShowForm(false);
      onReload();
    } catch (err) {
      alert('Failed to create contact');
    }
  };

  return (
    <div className="tab-content">
      <div className="tab-header">
        <h2>📞 Contact Management</h2>
        <button onClick={() => setShowForm(!showForm)} className="add-button">
          {showForm ? 'Cancel' : '+ Add Contact'}
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} className="admin-form">
          <input
            type="text"
            placeholder="Name"
            value={formData.name}
            onChange={(e) => setFormData({...formData, name: e.target.value})}
            required
          />
          <input
            type="email"
            placeholder="Email"
            value={formData.email}
            onChange={(e) => setFormData({...formData, email: e.target.value})}
            required
          />
          <input
            type="tel"
            placeholder="Phone"
            value={formData.phone}
            onChange={(e) => setFormData({...formData, phone: e.target.value})}
            required
          />
          <button type="submit">Create Contact</button>
        </form>
      )}

      <table className="admin-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Phone</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {contacts.map(contact => (
            <tr key={contact.id}>
              <td>{contact.id}</td>
              <td>{contact.name}</td>
              <td>{contact.email}</td>
              <td>{contact.phone}</td>
              <td>
                <button onClick={() => onDelete(contact.id)} className="delete-button">Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default AdminPanel;
