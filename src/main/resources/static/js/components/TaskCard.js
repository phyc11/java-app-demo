/**
 * Task Card Component
 */
export function renderTaskCard(task, { onStatusToggle, onEdit, onDelete }) {
    const card = document.createElement('div');
    card.className = `task-card status-${task.status}`;

    const priorityBadgeClass = {
        LOW: 'badge-low',
        MEDIUM: 'badge-medium',
        HIGH: 'badge-high',
        URGENT: 'badge-urgent'
    }[task.priority] || 'badge-low';

    const statusBadgeClass = {
        TODO: 'badge-todo',
        IN_PROGRESS: 'badge-progress',
        COMPLETED: 'badge-completed'
    }[task.status] || 'badge-todo';

    const statusText = {
        TODO: 'Cần làm',
        IN_PROGRESS: 'Đang làm',
        COMPLETED: 'Hoàn thành'
    }[task.status];

    card.innerHTML = `
        <div>
            <div class="task-header">
                <span class="badge ${statusBadgeClass}">${statusText}</span>
                <span class="badge ${priorityBadgeClass}">${task.priority}</span>
            </div>
            <h3 class="task-title" style="margin-top: 0.75rem;">${escapeHtml(task.title)}</h3>
            <p class="task-desc">${task.description ? escapeHtml(task.description) : 'Không có mô tả.'}</p>
        </div>
        
        <div class="task-footer">
            <div class="task-meta">
                ${task.categoryName ? `<span style="color:${task.categoryColor || '#818cf8'}">📁 ${escapeHtml(task.categoryName)}</span>` : '📁 Chung'}
            </div>
            <div class="task-actions">
                <button class="icon-btn btn-status-toggle" title="Đổi trạng thái">
                    ${task.status === 'COMPLETED' ? '↩️' : '✅'}
                </button>
                <button class="icon-btn btn-edit-task" title="Chỉnh sửa">✏️</button>
                <button class="icon-btn icon-btn-danger btn-delete-task" title="Xóa">🗑️</button>
            </div>
        </div>
    `;

    card.querySelector('.btn-status-toggle').addEventListener('click', () => {
        const nextStatus = task.status === 'COMPLETED' ? 'TODO' : (task.status === 'TODO' ? 'IN_PROGRESS' : 'COMPLETED');
        onStatusToggle(task.id, nextStatus);
    });

    card.querySelector('.btn-edit-task').addEventListener('click', () => onEdit(task));
    card.querySelector('.btn-delete-task').addEventListener('click', () => onDelete(task.id));

    return card;
}

function escapeHtml(str) {
    return str.replace(/[&<>'"]/g, 
        tag => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;' }[tag] || tag)
    );
}
