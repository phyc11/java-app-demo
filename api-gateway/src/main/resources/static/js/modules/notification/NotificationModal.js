/**
 * Notification Modal & Drawer Component
 * Handles real-time notifications list, mark as read, and SSE live push alerts
 */
import { ApiService } from '../../core/api.js';

export function createNotificationModal({ onNotificationRead }) {
    const modal = document.createElement('div');
    modal.className = 'modal-backdrop';
    modal.id = 'notification-modal';
    modal.style.display = 'none';

    modal.innerHTML = `
        <div class="modal-content" style="max-width: 520px; width: 95%;">
            <div class="modal-header">
                <div style="display:flex; align-items:center; gap:8px;">
                    <span style="font-size:1.4rem;">🔔</span>
                    <h3 class="modal-title">Thông Báo Hệ Thống</h3>
                </div>
                <button class="btn-icon" id="btn-close-notifications">✕</button>
            </div>
            
            <div class="modal-body" style="padding:16px;">
                <div style="display:flex; justify-between; align-items:center; margin-bottom:12px; gap:8px;">
                    <span style="font-size:0.85rem; color:#64748b;" id="notification-subtitle">Danh sách thông báo gần đây</span>
                    <button class="btn btn-secondary btn-sm" id="btn-mark-all-read" style="font-size:0.8rem; padding:4px 8px;">
                        ✓ Đánh dấu tất cả đã đọc
                    </button>
                </div>

                <div id="notifications-list" style="max-height: 380px; overflow-y: auto; display: flex; flex-direction: column; gap: 8px;">
                    <div style="text-align:center; padding: 24px; color:#94a3b8;">Đang tải thông báo...</div>
                </div>
            </div>
        </div>
    `;

    const closeBtn = modal.querySelector('#btn-close-notifications');
    closeBtn.addEventListener('click', () => hide());
    modal.addEventListener('click', (e) => {
        if (e.target === modal) hide();
    });

    const markAllBtn = modal.querySelector('#btn-mark-all-read');
    markAllBtn.addEventListener('click', async () => {
        await ApiService.markAllNotificationsAsRead();
        loadNotifications();
        if (onNotificationRead) onNotificationRead();
    });

    async function loadNotifications() {
        const listEl = modal.querySelector('#notifications-list');
        try {
            const list = await ApiService.getNotifications();
            if (!list || list.length === 0) {
                listEl.innerHTML = `
                    <div style="text-align:center; padding: 32px; color:#94a3b8;">
                        <p style="font-size:1.8rem; margin-bottom:8px;">🔕</p>
                        <p>Bạn chưa có thông báo nào!</p>
                    </div>
                `;
                return;
            }

            listEl.innerHTML = list.map(item => `
                <div class="notification-item ${item.read ? 'read' : 'unread'}" data-id="${item.id}" 
                     style="padding: 12px; border-radius: 8px; border: 1px solid ${item.read ? '#e2e8f0' : '#818cf8'}; background: ${item.read ? '#ffffff' : '#f0f3ff'}; transition: all 0.2s ease;">
                    <div style="display:flex; justify-content:space-between; align-items:start; margin-bottom:4px;">
                        <strong style="font-size:0.95rem; color:#1e293b;">
                            ${getTypeIcon(item.type)} ${escapeHtml(item.title)}
                        </strong>
                        <span style="font-size:0.75rem; color:#94a3b8;">${formatTime(item.timestamp)}</span>
                    </div>
                    <p style="font-size:0.88rem; color:#475569; margin:4px 0 8px 0; line-height:1.4;">
                        ${escapeHtml(item.message)}
                    </p>
                    ${!item.read ? `
                        <button class="btn-mark-read" data-id="${item.id}" 
                                style="font-size:0.75rem; color:#4f46e5; background:none; border:none; padding:0; cursor:pointer; font-weight:600;">
                            ✓ Đánh dấu đã đọc
                        </button>
                    ` : ''}
                </div>
            `).join('');

            listEl.querySelectorAll('.btn-mark-read').forEach(btn => {
                btn.addEventListener('click', async (e) => {
                    e.stopPropagation();
                    const id = btn.getAttribute('data-id');
                    await ApiService.markNotificationAsRead(id);
                    loadNotifications();
                    if (onNotificationRead) onNotificationRead();
                });
            });

        } catch (err) {
            listEl.innerHTML = `<div style="color:#ef4444; padding:16px;">Lỗi tải thông báo: ${err.message}</div>`;
        }
    }

    function show() {
        modal.style.display = 'flex';
        loadNotifications();
    }

    function hide() {
        modal.style.display = 'none';
    }

    return {
        element: modal,
        show,
        hide,
        loadNotifications
    };
}

function getTypeIcon(type) {
    switch (type) {
        case 'WELCOME': return '🎉';
        case 'DUE_SOON': return '⏰';
        case 'OVERDUE': return '⚠️';
        case 'ASSIGNED': return '👤';
        default: return '📢';
    }
}

function formatTime(isoStr) {
    if (!isoStr) return '';
    try {
        const d = new Date(isoStr);
        return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) + ' ' + d.toLocaleDateString();
    } catch {
        return isoStr;
    }
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>'"]/g, 
        tag => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;' }[tag] || tag)
    );
}
