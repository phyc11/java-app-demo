/**
 * Stats Summary Component
 */
export function renderStatsCards(stats = {}) {
    const container = document.createElement('div');
    container.className = 'stats-grid';

    const items = [
        { title: 'Tổng công việc', value: stats.total || 0, icon: '📋', iconClass: 'stat-icon-total' },
        { title: 'Cần làm (To-Do)', value: stats.todo || 0, icon: '⏳', iconClass: 'stat-icon-todo' },
        { title: 'Đang thực hiện', value: stats.inProgress || 0, icon: '⚡', iconClass: 'stat-icon-progress' },
        { title: 'Đã hoàn thành', value: stats.completed || 0, icon: '✅', iconClass: 'stat-icon-completed' }
    ];

    container.innerHTML = items.map(item => `
        <div class="stat-card">
            <div class="stat-info">
                <h4>${item.title}</h4>
                <div class="stat-value">${item.value}</div>
            </div>
            <div class="stat-icon-wrapper ${item.iconClass}">${item.icon}</div>
        </div>
    `).join('');

    return container;
}
