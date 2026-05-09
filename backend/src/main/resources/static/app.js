const API_URL = '/api';

// --- State ---
let state = {
  token: localStorage.getItem('token') || null,
  user: JSON.parse(localStorage.getItem('user')) || null,
  isLoginView: true,
  activeTab: 'friends',
  currency: localStorage.getItem('currency') || '$'
};

// --- API Calls ---
async function fetchAPI(endpoint, options = {}) {
  const headers = {
    'Content-Type': 'application/json',
    ...(state.token ? { 'Authorization': `Bearer ${state.token}` } : {})
  };

  const response = await fetch(`${API_URL}${endpoint}`, { ...options, headers });

  if (!response.ok) {
    const err = await response.text();
    throw new Error(err || 'API Error');
  }

  // Return JSON if it has content, else text
  const contentType = response.headers.get("content-type");
  if (contentType && contentType.indexOf("application/json") !== -1) {
    return response.json();
  } else {
    return response.text();
  }
}

// --- Render Logic ---
function render() {
  const app = document.getElementById('app');
  if (state.token) {
    app.innerHTML = renderDashboard();
  } else {
    app.innerHTML = renderAuth();
  }
}

// --- Views ---
function renderAuth() {
  return `
    <div class="auth-container">
      <div class="card" style="width: 100%; max-width: 400px;">
        <h2>${state.isLoginView ? 'Welcome Back' : 'Create Account'}</h2>
        <p style="margin-bottom: 1rem;">
          ${state.isLoginView ? 'Log in to continue to Splitwise Clone.' : 'Sign up to start splitting expenses.'}
        </p>
        <div id="auth-error" class="text-danger" style="margin-bottom: 1rem; font-size: 0.875rem;"></div>
        
        <form id="auth-form" onsubmit="handleAuthSubmit(event)">
          ${!state.isLoginView ? `
            <div class="input-group">
              <label>Full Name</label>
              <input type="text" id="name" placeholder="John Doe" required>
            </div>
          ` : ''}
          <div class="input-group">
            <label>Email Address</label>
            <input type="email" id="email" placeholder="you@example.com" required>
          </div>
          <div class="input-group">
            <label>Password</label>
            <input type="password" id="password" placeholder="••••••••" required>
          </div>
          <button type="submit" class="btn" style="width: 100%; margin-top: 1rem;">
            ${state.isLoginView ? 'Log In' : 'Sign Up'}
          </button>
        </form>

        <p class="text-center" style="margin-top: 2rem; font-size: 0.875rem;">
          ${state.isLoginView ? "Don't have an account? " : "Already have an account? "}
          <span style="color: var(--primary-color); cursor: pointer; font-weight: 500;" onclick="toggleAuthView()">
            ${state.isLoginView ? 'Sign up' : 'Log in'}
          </span>
        </p>
      </div>
    </div>
  `;
}

function renderDashboard() {
  return `
    <div>
      <header class="flex-between" style="margin-bottom: 3rem;">
        <h2>Splitwise Clone</h2>
        <div style="display: flex; gap: 1rem; align-items: center;">
          <select style="padding: 0.5rem; border-radius: 4px; background: var(--surface-color); color: white; border: 1px solid var(--border-color); cursor: pointer;" onchange="changeCurrency(event)">
            <option value="$" ${state.currency === '$' ? 'selected' : ''}>$ USD</option>
            <option value="€" ${state.currency === '€' ? 'selected' : ''}>€ EUR</option>
            <option value="₹" ${state.currency === '₹' ? 'selected' : ''}>₹ INR</option>
            <option value="£" ${state.currency === '£' ? 'selected' : ''}>£ GBP</option>
          </select>
          <span style="display:flex; align-items:center;">Hello, ${state.user?.name || 'User'}</span>
          <button class="btn btn-secondary" onclick="handleLogout()">Log Out</button>
        </div>
      </header>

      <div class="grid-3" style="margin-bottom: 3rem;">
        <div class="card text-center">
          <p>Total Balance</p>
          <h3 style="color: var(--text-main); font-size: 2rem; margin-top: 0.5rem;">${state.currency}0.00</h3>
        </div>
        <div class="card text-center">
          <p>You Owe</p>
          <h3 class="text-danger" style="font-size: 2rem; margin-top: 0.5rem;">${state.currency}0.00</h3>
        </div>
        <div class="card text-center">
          <p>You Are Owed</p>
          <h3 class="text-success" style="font-size: 2rem; margin-top: 0.5rem;">${state.currency}0.00</h3>
        </div>
      </div>

      <div class="card" style="padding: 0;">
        <div style="display: flex; border-bottom: 1px solid var(--border-color);">
          <div style="padding: 1rem 2rem; cursor: pointer; border-bottom: ${state.activeTab === 'friends' ? '2px solid var(--primary-color)' : 'none'}" onclick="switchTab('friends')">Friends</div>
          <div style="padding: 1rem 2rem; cursor: pointer; border-bottom: ${state.activeTab === 'groups' ? '2px solid var(--primary-color)' : 'none'}" onclick="switchTab('groups')">Groups</div>
        </div>
        
        <div style="padding: 2rem;">
          ${state.activeTab === 'friends' ? renderFriendsTab() : renderGroupsTab()}
        </div>
      </div>
    </div>
  `;
}

function renderFriendsTab() {
  return `
    <div class="flex-between" style="margin-bottom: 1.5rem;">
      <h3>Friends List</h3>
      <button class="btn" onclick="alert('Not implemented yet!')">Add Friend</button>
    </div>
    <div class="text-center" style="padding: 2rem 0; color: var(--text-muted);">
      <p>You have not added any friends yet.</p>
    </div>
  `;
}

function renderGroupsTab() {
  return `
    <div class="flex-between" style="margin-bottom: 1.5rem;">
      <h3>Your Groups</h3>
      <button class="btn" onclick="alert('Not implemented yet!')">Create Group</button>
    </div>
    <div class="text-center" style="padding: 2rem 0; color: var(--text-muted);">
      <p>You are not part of any groups.</p>
    </div>
  `;
}

// --- Actions ---
async function handleAuthSubmit(e) {
  e.preventDefault();
  const email = document.getElementById('email').value;
  const password = document.getElementById('password').value;
  const nameInput = document.getElementById('name');
  const errorDiv = document.getElementById('auth-error');

  errorDiv.innerText = 'Processing...';

  try {
    if (!state.isLoginView) {
      // Register
      await fetchAPI('/auth/register', {
        method: 'POST',
        body: JSON.stringify({ name: nameInput.value, email, password })
      });
    }

    // Login
    const data = await fetchAPI('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password })
    });

    state.token = data.token;
    state.user = { id: data.id, name: data.name, email: data.email };
    localStorage.setItem('token', data.token);
    localStorage.setItem('user', JSON.stringify(state.user));

    render();
  } catch (err) {
    errorDiv.innerText = err.message;
  }
}

function toggleAuthView() {
  state.isLoginView = !state.isLoginView;
  render();
}

function handleLogout() {
  state.token = null;
  state.user = null;
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  render();
}

function changeCurrency(e) {
  state.currency = e.target.value;
  localStorage.setItem('currency', state.currency);
  render();
}

function switchTab(tab) {
  state.activeTab = tab;
  render();
}

// Init
window.onload = render;
