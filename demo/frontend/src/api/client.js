const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

async function request(path, options = {}) {
  const url = `${API_BASE_URL}${path}`;
  const token = localStorage.getItem('token');
  
  console.log('🌐 API Request:', { url, method: options.method || 'GET', hasToken: !!token });
  
  const headers = {
    ...(token && { 'Authorization': `Bearer ${token}` }),
    ...(options.headers || {})
  };

  // Don't set Content-Type for FormData
  if (!(options.body instanceof FormData)) {
    headers['Content-Type'] = 'application/json';
  }
  
  const response = await fetch(url, {
    headers,
    credentials: 'include',
    ...options,
  });

  console.log('📡 API Response:', { url, status: response.status, ok: response.ok });

  const isJson = response.headers.get('content-type')?.includes('application/json');
  const data = isJson ? await response.json().catch(() => null) : null;

  if (!response.ok) {
    console.error('❌ API Error:', { url, status: response.status, data });
    const error = new Error(data?.message || `Request failed with status ${response.status}`);
    error.status = response.status;
    error.data = data;
    error.response = { data, status: response.status };
    throw error;
  }

  return { data, status: response.status };
}

const apiClient = {
  get: async (path) => {
    const result = await request(path);
    return result; // Returns { data, status }
  },
  post: async (path, data, config = {}) => {
    const body = data instanceof FormData ? data : JSON.stringify(data);
    const result = await request(path, { 
      method: 'POST', 
      body,
      headers: config.headers || {}
    });
    return result; // Returns { data, status }
  },
  put: async (path, data) => {
    const result = await request(path, { method: 'PUT', body: JSON.stringify(data) });
    return result; // Returns { data, status }
  },
  delete: async (path) => {
    const result = await request(path, { method: 'DELETE' });
    return result; // Returns { data, status }
  },
  // Legacy methods that return data directly for backward compatibility
  getUsers: async () => {
    const result = await request('/api/users');
    return result.data;
  },
  createUser: async (payload) => {
    const result = await request('/api/users', { method: 'POST', body: JSON.stringify(payload) });
    return result.data;
  },
  deleteUser: async (id) => {
    const result = await request(`/api/users/${id}`, { method: 'DELETE' });
    return result.data;
  },
  search: async (query) => {
    const result = await request(`/api/search?q=${encodeURIComponent(query)}`);
    return result.data;
  },
  getContacts: async () => {
    const result = await request('/api/contacts');
    return result.data;
  },
  getEvents: async () => {
    const result = await request('/api/events');
    return result.data;
  },
  getLocations: async () => {
    const result = await request('/api/locations');
    return result.data;
  },
  getChecklistItems: async () => {
    const result = await request('/api/checklist');
    return result.data;
  },
  getChecklistStatuses: async () => {
    const result = await request('/api/checklist-status');
    return result.data;
  },
  checkItem: async (userId, itemId) => {
    const result = await request(`/api/users/${userId}/checklist/${itemId}/check`, { method: 'POST' });
    return result.data;
  },
  uncheckItem: async (userId, itemId) => {
    const result = await request(`/api/users/${userId}/checklist/${itemId}/uncheck`, { method: 'POST' });
    return result.data;
  },
  getChecklistProgress: async (userId) => {
    const result = await request(`/api/users/${userId}/checklist-progress`);
    return result.data;
  },
};

// Export as both default and named export for backward compatibility
export default apiClient;
export { apiClient as api };
export { API_BASE_URL };
