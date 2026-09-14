/**
 * Navbar Core Component
 */
export function renderNavbar({ currentUser, currentView, onViewChange, onNewTaskClick, onAnalyticsClick, onAuthClick, onLogoutClick, onProfileClick }) {
    const header = document.createElement('header');
    header.className = 'header-navbar';

    header.innerHTML = `
        <div class="brand">
            <div class="brand-icon">TC</div>
            <div>
                <span class="brand-title">TaskCraft</span>
                <span class="brand-badge">Modular System</span>
            </div>
        </div>

        <div class="navbar-center">
            <div class="view-switch">
                <button class="view-btn ${currentView === 'grid' ? 'active' : ''}" id="btn-view-grid">
                    📊 Danh Sách
                </button>
                <button class="view-btn ${currentView === 'kanban' ? 'active' : ''}" id="btn-view-kanban">
                    📋 Kanban Drag & Drop
                </button>
            </div>
        </div>

        <div class="navbar-actions">
            ${currentUser ? `
                <button class="btn btn-secondary btn-sm" id="btn-analytics" title="Xem biểu đồ phân tích & xuất báo cáo Excel/CSV">
                    📈 Analytics & Báo Cáo
                </button>
                <div class="user-profile-badge" id="btn-user-profile" title="Xem hồ sơ cá nhân & đổi mật khẩu" style="cursor:pointer">
                    <span class="user-avatar" style="background:${currentUser.avatarColor || '#6366f1'}">👤</span>
                    <div class="user-details">
                        <span class="user-name">${escapeHtml(currentUser.fullName || currentUser.username)}</span>
                        <span class="user-role-badge ${currentUser.role === 'ROLE_ADMIN' ? 'role-admin' : 'role-user'}">
                            ${currentUser.role === 'ROLE_ADMIN' ? '👑 ADMIN' : '👤 USER'}
                        </span>
                    </div>
                </div>
                <button class="btn btn-primary" id="btn-create-task">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
                    Tạo Task
                </button>
                <button class="btn btn-secondary btn-sm" id="btn-logout" title="Đăng xuất">
                    🚪 Thoát
                </button>
            ` : `
                <button class="btn btn-primary" id="btn-login-trigger">
                    🔑 Đăng Nhập / Đăng Ký
                </button>
            `}
        </div>
    `;

    header.querySelector('#btn-view-grid').addEventListener('click', () => onViewChange('grid'));
    header.querySelector('#btn-view-kanban').addEventListener('click', () => onViewChange('kanban'));

    if (currentUser) {
        header.querySelector('#btn-analytics').addEventListener('click', onAnalyticsClick);
        header.querySelector('#btn-user-profile').addEventListener('click', onProfileClick);
        header.querySelector('#btn-create-task').addEventListener('click', onNewTaskClick);
        header.querySelector('#btn-logout').addEventListener('click', onLogoutClick);
    } else {
        header.querySelector('#btn-login-trigger').addEventListener('click', onAuthClick);
    }

    return header;
}

function escapeHtml(str) {
    return str.replace(/[&<>'"]/g, 
        tag => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;' }[tag] || tag)
    );
}
