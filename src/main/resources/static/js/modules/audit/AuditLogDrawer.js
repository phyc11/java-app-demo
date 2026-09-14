import { ApiService } from '../../core/api.js';

export function renderAuditLogDrawer() {
    const drawerOverlay = document.createElement('div');
    drawerOverlay.className = 'audit-drawer-overlay';

    drawerOverlay.innerHTML = `
        <div class="audit-drawer-container">
            <div class="audit-drawer-header">
                <div class="audit-header-title">
                    <h3>📜 Lịch Sử Hoạt Động (Audit Logs)</h3>
                    <span class="audit-subtitle">Theo dõi mọi thay đổi dữ liệu theo mốc thời gian</span>
                </div>
                <button class="icon-btn" id="audit-close-btn">✕</button>
            </div>
            <div class="audit-drawer-body" id="audit-logs-list">
                <div class="audit-loading">Đang tải lịch sử...</div>
            </div>
        </div>
    `;

    const closeBtn = drawerOverlay.querySelector('#audit-close-btn');
    const logsList = drawerOverlay.querySelector('#audit-logs-list');

    const closeDrawer = () => drawerOverlay.classList.remove('active');
    closeBtn.addEventListener('click', closeDrawer);

    drawerOverlay.addEventListener('click', (e) => {
        if (e.target === drawerOverlay) closeDrawer();
    });

    return {
        element: drawerOverlay,
        open: async () => {
            drawerOverlay.classList.add('active');
            logsList.innerHTML = '<div class="audit-loading">Đang tải lịch sử...</div>';

            try {
                const logs = await ApiService.getAuditLogs();
                if (!logs || logs.length === 0) {
                    logsList.innerHTML = '<div class="audit-empty">Chưa có lịch sử thao tác nào.</div>';
                    return;
                }

                logsList.innerHTML = `
                    <div class="audit-timeline">
                        ${logs.map(log => renderLogItem(log)).join('')}
                    </div>
                `;
            } catch (err) {
                logsList.innerHTML = `<div class="audit-empty" style="color:var(--accent-red)">Lỗi khi tải lịch sử: ${err.message}</div>`;
            }
        },
        close: closeDrawer
    };
}

function renderLogItem(log) {
    const actionClass = getActionBadgeClass(log.action);
    const timeFormatted = formatDate(log.timestamp);
    const actionIcon = getActionIcon(log.action);

    return `
        <div class="audit-timeline-item">
            <div class="audit-timeline-icon">${actionIcon}</div>
            <div class="audit-timeline-content">
                <div class="audit-item-top">
                    <span class="audit-action-tag ${actionClass}">${log.action}</span>
                    <span class="audit-time">${timeFormatted}</span>
                </div>
                <div class="audit-details">${escapeHtml(log.details || '')}</div>
                ${log.oldState || log.newState ? `
                    <div class="audit-state-box">
                        ${log.oldState ? `<div><span class="state-label">Trước:</span> ${escapeHtml(log.oldState)}</div>` : ''}
                        ${log.newState ? `<div><span class="state-label">Sau:</span> ${escapeHtml(log.newState)}</div>` : ''}
                    </div>
                ` : ''}
                <div class="audit-performer">👤 Thực hiện bởi: <strong>${escapeHtml(log.performedBy || 'System')}</strong></div>
            </div>
        </div>
    `;
}

function getActionBadgeClass(action) {
    switch (action) {
        case 'CREATE': return 'action-create';
        case 'UPDATE': return 'action-update';
        case 'STATUS_CHANGE': return 'action-status';
        case 'MOVE': return 'action-move';
        case 'DELETE': return 'action-delete';
        default: return 'action-default';
    }
}

function getActionIcon(action) {
    switch (action) {
        case 'CREATE': return '➕';
        case 'UPDATE': return '✏️';
        case 'STATUS_CHANGE': return '🔄';
        case 'MOVE': return '🖐️';
        case 'DELETE': return '🗑️';
        default: return '📜';
    }
}

function formatDate(isoStr) {
    if (!isoStr) return '';
    const date = new Date(isoStr);
    return date.toLocaleString('vi-VN', { hour: '2-digit', minute: '2-digit', day: '2-digit', month: '2-digit' });
}

function escapeHtml(str) {
    return str.replace(/[&<>'"]/g, 
        tag => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;' }[tag] || tag)
    );
}
