/**
 * Core API Service Module
 * Handles REST API calls, JWT Bearer token authentication, Profile & Password management, Audit Logs, Analytics & File Export
 */
const API_BASE = '/api';

export const ApiService = {
    getToken() {
        return localStorage.getItem('taskcraft_token');
    },

    setToken(token) {
        if (token) {
            localStorage.setItem('taskcraft_token', token);
        } else {
            localStorage.removeItem('taskcraft_token');
        }
    },

    getHeaders(extraHeaders = {}) {
        const headers = {
            'Content-Type': 'application/json',
            ...extraHeaders
        };
        const token = this.getToken();
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }
        return headers;
    },

    // Auth & Profile endpoints
    async login(username, password) {
        const res = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        const json = await res.json();
        if (json.success && json.data && json.data.token) {
            this.setToken(json.data.token);
        }
        return json;
    },

    async register(username, password, fullName, role = 'ROLE_USER') {
        const res = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password, fullName, role })
        });
        const json = await res.json();
        if (json.success && json.data && json.data.token) {
            this.setToken(json.data.token);
        }
        return json;
    },

    async getMe() {
        if (!this.getToken()) return null;
        try {
            const res = await fetch(`${API_BASE}/auth/me`, {
                headers: this.getHeaders()
            });
            if (res.status === 401) {
                this.setToken(null);
                return null;
            }
            const json = await res.json();
            return json.data;
        } catch {
            return null;
        }
    },

    async updateProfile(fullName, avatarColor) {
        const res = await fetch(`${API_BASE}/auth/profile`, {
            method: 'PUT',
            headers: this.getHeaders(),
            body: JSON.stringify({ fullName, avatarColor })
        });
        return await res.json();
    },

    async changePassword(oldPassword, newPassword) {
        const res = await fetch(`${API_BASE}/auth/change-password`, {
            method: 'POST',
            headers: this.getHeaders(),
            body: JSON.stringify({ oldPassword, newPassword })
        });
        return await res.json();
    },

    logout() {
        this.setToken(null);
    },

    // Audit Log Endpoints
    async getAuditLogs() {
        const res = await fetch(`${API_BASE}/audit-logs`, { headers: this.getHeaders() });
        const json = await res.json();
        return json.data || [];
    },

    // Analytics & Export Endpoints
    async getAnalytics() {
        const res = await fetch(`${API_BASE}/analytics`, { headers: this.getHeaders() });
        const json = await res.json();
        return json.data || null;
    },

    async downloadExport(format = 'excel') {
        const token = this.getToken();
        const url = `${API_BASE}/export/${format}`;
        
        const response = await fetch(url, {
            headers: token ? { 'Authorization': `Bearer ${token}` } : {}
        });
        
        const blob = await response.blob();
        const downloadUrl = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = downloadUrl;
        a.download = `TaskCraft_Report.${format === 'excel' ? 'xlsx' : 'csv'}`;
        document.body.appendChild(a);
        a.click();
        a.remove();
    },

    // Task Endpoints
    async getTasks(params = {}) {
        const query = new URLSearchParams();
        if (params.status) query.append('status', params.status);
        if (params.priority) query.append('priority', params.priority);
        if (params.categoryId) query.append('categoryId', params.categoryId);
        if (params.search) query.append('search', params.search);

        const url = `${API_BASE}/tasks${query.toString() ? '?' + query.toString() : ''}`;
        const res = await fetch(url, { headers: this.getHeaders() });
        const json = await res.json();
        return json.data || [];
    },

    async getStats() {
        const res = await fetch(`${API_BASE}/tasks/stats`, { headers: this.getHeaders() });
        const json = await res.json();
        return json.data || {};
    },

    async getCategories() {
        const res = await fetch(`${API_BASE}/categories`, { headers: this.getHeaders() });
        const json = await res.json();
        return json.data || [];
    },

    async createTask(taskData) {
        const res = await fetch(`${API_BASE}/tasks`, {
            method: 'POST',
            headers: this.getHeaders(),
            body: JSON.stringify(taskData)
        });
        const json = await res.json();
        return json.data;
    },

    async updateTask(id, taskData) {
        const res = await fetch(`${API_BASE}/tasks/${id}`, {
            method: 'PUT',
            headers: this.getHeaders(),
            body: JSON.stringify(taskData)
        });
        const json = await res.json();
        return json.data;
    },

    async updateTaskStatus(id, status) {
        const res = await fetch(`${API_BASE}/tasks/${id}/status`, {
            method: 'PATCH',
            headers: this.getHeaders(),
            body: JSON.stringify({ status })
        });
        const json = await res.json();
        return json.data;
    },

    async moveTask(id, status, position) {
        const res = await fetch(`${API_BASE}/tasks/${id}/move`, {
            method: 'PATCH',
            headers: this.getHeaders(),
            body: JSON.stringify({ status, position })
        });
        const json = await res.json();
        return json.data;
    },

    async deleteTask(id) {
        const res = await fetch(`${API_BASE}/tasks/${id}`, {
            method: 'DELETE',
            headers: this.getHeaders()
        });
        const json = await res.json();
        return json.success;
    }
};
