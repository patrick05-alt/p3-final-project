// LAB 12 - REQUIREMENT 10: Scene - Password reset request page
import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import apiClient from '../api/client';
import '../styles/Login.css';

const ForgotPassword = () => {
  const [email, setEmail] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [resetToken, setResetToken] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await apiClient.post('/api/auth/forgot-password', { email });
      setSuccess(true);
      setResetToken(response.data.token); // In production, this would be sent via email
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Failed to send reset email');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-box">
        <div className="login-header">
          <h1>Reset Password</h1>
          <p>Enter your email to receive a password reset link</p>
        </div>

        {!success ? (
          <form onSubmit={handleSubmit}>
            {error && <div className="error-message">{error}</div>}

            <div className="form-group">
              <label htmlFor="email">Email</label>
              <input
                type="email"
                id="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                placeholder="your@email.com"
              />
            </div>

            <button type="submit" className="login-button" disabled={loading}>
              {loading ? 'Sending...' : 'Send Reset Link'}
            </button>
          </form>
        ) : (
          <div className="success-message">
            <p>✅ Password reset token generated!</p>
            <div className="token-box">
              <p><strong>Reset Token:</strong></p>
              <code>{resetToken}</code>
              <p style={{ marginTop: '10px', fontSize: '14px' }}>
                Copy this token and use it on the <Link to="/reset-password">Reset Password</Link> page.
              </p>
              <p style={{ fontSize: '12px', color: '#666', marginTop: '10px' }}>
                In a production environment, this would be sent to your email.
              </p>
            </div>
          </div>
        )}

        <div className="login-footer">
          <p>Remember your password? <Link to="/login">Sign In</Link></p>
        </div>
      </div>
    </div>
  );
};

export default ForgotPassword;
