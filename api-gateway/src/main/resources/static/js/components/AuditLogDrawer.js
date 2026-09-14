/**
 * Audit Log Slide-Out Drawer Component
 */
export function renderAuditLogDrawer() {
    const drawer = document.createElement('div');
    drawer.className = 'audit-drawer-overlay';

    drawer.innerHTML = `
        <div class="audit-drawer-container">
            <div class="audit-drawer-header">
                <div class="audit-header-title">
                    <h3>📜 Lịch Sử Hoạt Động System</h3>
                    <span class="audit-subtitle">Nhật ký thay đổi dữ liệu realtime</span>
                </div>
                <button class="icon-btn" id="audit-close-btn">✕</button>
            </div>
            
            <div class="audit-drawer-body">
                <div class="audit-timeline" id="audit-timeline-container">
                    <div class="audit-loading">Đang tải lịch sử...</div>
                </div>
            </div>
        </div>
    `;

    const closeBtn = drawer.querySelector('#audit-close-btn');
    const closeDrawer = () => drawer.classList.remove('active');

    closeBtn.addEventListener('click', closeDrawer);
    drawer.addEventListener('click', (e) => {
        if (e.target === drawer) closeDrawer();
    });

    return {
        element: drawer,
        open: async () => {
            drawer.classList.add('active');
            const timelineContainer = drawer.querySelector('#audit-timeline-container');
            timelineContainer.innerHTML = '<div class="audit-loading">Đang tải lịch sử...</div>';

            try {
                const { ApiService } = await import('../api.js');
                const logs = await ApiService.getAuditLogs();

                if (!logs || logs.length === 0) {
                    timelineContainer.innerHTML = `
                        <div class="audit-empty">
                            <span>📭</span>
                            <p>Chưa có lịch sử hoạt động nào được ghi lại.</p>
                        </div>
                    `;
                    return;
                }

                timelineContainer.innerHTML = logs.map(log => {
                    const actionConfig = getActionConfig(log.action);
                    return `
                        <div class="audit-timeline-item">
                            <div class="audit-timeline-icon ${actionConfig.cssClass}">
                                ${actionConfig.icon}
                            </div>
                            <div class="audit-timeline-content">
                                <div class="audit-item-top">
                                    <span class="audit-action-tag ${actionConfig.cssClass}">${actionConfig.label}</span>
                                    <span class="audit-time">${formatTime(log.timestamp)}</span>
                                </div>
                                <div class="audit-details">${escapeHtml(log.details || '')}</div>
                                ${(log.oldState || log.newState) ? `
                                    <div class="audit-state-box">
                                        ${log.oldState ? `<div><span class="state-label">Cũ:</span> <code>${escapeHtml(log.oldState)}</code></div>` : ''}
                                        ${log.newState ? `<div><span class="state-label">Mới:</span> <code>${escapeHtml(log.newState)}</code></div>` : ''}
                                    </div>
                                ` : ''}
                                <div class="audit-performer">
                                    <span>👤 Thực hiện: <strong>${escapeHtml(log.performedBy || 'System')}</strong></span>
                                </div>
                            </div>
                        </div>
                    `;
                }).join('');

            } catch (err) {
                timelineContainer.innerHTML = `<div class="audit-empty" style="color:var(--accent-red)">Lỗi khi tải lịch sử: ${err.message}</div>`;
            }
        },
        close: closeDrawer
    };
}

function getActionConfig(action) {
    switch (action) {
        case 'CREATE':
            return { label: 'TẠO MỚI', icon: '➕', cssClass: 'action-create' };
        case 'UPDATE':
            return { label: 'CHỈNH SỬA', icon: '✏️', cssClass: 'action-update' };
        case 'STATUS_CHANGE':
            return { label: 'ĐỔI TRẠNG THÁI', icon: '⚡', cssClass: 'action-status' };
        case 'MOVE':
            return { label: 'KÉO THẢ KANBAN', icon: '📋', cssClass: 'action-move' };
        case 'DELETE':
            return { label: 'XÓA TASK', icon: '🗑️', cssClass: 'action-delete' };
        default:
            return { label: action, icon: '📝', cssClass: 'action-default' };
    }
}

function formatTime(timestampStr) {
    if (!timestampStr) return '';
    const date = new Date(timestampStr);
    return date.toLocaleString('vi-VN', {
        hour: '2-digit',
        minute: '2-digit',
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
    });
}

function escapeHtml(str) {
    return str.replace(/[&<>'"]/g, 
        tag => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;' }[tag] || tag)
    );
}
