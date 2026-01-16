import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useLanguage } from '../context/LanguageContext';
import '../styles/Navigation.css';

function Navigation() {
  const { user, logout, isAdmin } = useAuth();
  const { t } = useLanguage();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <nav className="navigation">
      <div className="nav-container">
        <div className="nav-brand">
          <Link to="/">Newcomer Assistant</Link>
        </div>
        
        <div className="nav-links">
          <Link to="/">{t('home')}</Link>
          <Link to="/announcements">{t('announcements')}</Link>
          <Link to="/events">{t('events')}</Link>
          <Link to="/locations">{t('locations')}</Link>
          <Link to="/contacts">{t('contacts')}</Link>
          <Link to="/checklist">{t('checklist')}</Link>
          <Link to="/search">{t('search')}</Link>
          {isAdmin && <Link to="/admin" className="admin-link">{t('adminPanel')}</Link>}
        </div>

        <div className="nav-user">
          <Link to="/profile" className="profile-link">
            <span className="user-info">
              👤 {user?.username} 
              {isAdmin && <span className="admin-badge">ADMIN</span>}
            </span>
          </Link>
          <button onClick={handleLogout} className="logout-button">
            {t('logout')}
          </button>
        </div>
      </div>
    </nav>
  );
}

export default Navigation;
