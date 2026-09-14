export function renderTaskCard(task, { onStatusToggle, onEdit, onDelete }) {
    const card = document.createElement('div');
    card.className = 'task-card';

    const statusBadge = getStatusBadge(task.status);
    const priorityBadge = getPriorityBadge(task.priority);
    const categoryTag = task.categoryName ? `<span class="badge" style="background:${task.categoryColor}20; color:${task.categoryColor}; border:1px solid ${task.categoryColor}40">${escapeHtml(task.categoryName)}</span>` : '';
    const creatorTag = task.createdBy ? `<span class="kanban-user-tag" title="Người tạo">👤 ${escapeHtml(task.createdBy)}</span>` : '';

    card.innerHTML = `
        <div class="task-header">
            <div>
                <h3 class="task-title">${escapeHtml(task.title)}</h3>
            </div>
            <div class="task-actions">
                <button class="icon-btn" id="edit-btn" title="Chỉnh sửa">✏️</button>
                <button class="icon-btn icon-btn-danger" id="delete-btn" title="Xóa">🗑️</button>
            </div>
        </div>
        
        <p class="task-desc">${escapeHtml(task.description || 'Không có mô tả.')}</p>

        <div style="display:flex; gap:0.5rem; flex-wrap:wrap; align-items:center">
            ${statusBadge}
            ${priorityBadge}
            ${categoryTag}
        </div>

        <div class="kanban-card-footer">
            <span>📅 ${task.dueDate ? formatDate(task.dueDate) : 'Không có hạn'}</span>
            ${creatorTag}
        </div>
    `;

    card.querySelector('#edit-btn').addEventListener('click', () => onEdit(task));
    card.querySelector('#delete-btn').addEventListener('click', () => onDelete(task.id));

    return card;
}

function getStatusBadge(status) {
    switch (status) {
        case 'TODO': return '<span class="badge badge-todo">⏳ Cần Làm</span>';
        case 'IN_PROGRESS': return '<span class="badge badge-progress">⚡ Đang Làm</span>';
        case 'COMPLETED': return '<span class="badge badge-completed">✅ Hoàn Thành</span>';
        default: return '';
    }
}

function getPriorityBadge(priority) {
    switch (priority) {
        case 'LOW': return '<span class="badge badge-low">Thấp</span>';
        case 'MEDIUM': return '<span class="badge badge-medium">Trung Bình</span>';
        case 'HIGH': return '<span class="badge badge-high">Cao</span>';
        case 'URGENT': return '<span class="badge badge-urgent">Khẩn Cấp</span>';
        default: return '';
    }
}

function formatDate(isoStr) {
    if (!isoStr) return '';
    const date = new Date(isoStr);
    return date.toLocaleDateString('vi-VN');
}

function escapeHtml(str) {
    return str.replace(/[&<>'"]/g, 
        tag => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;' }[tag] || tag)
    );
}
