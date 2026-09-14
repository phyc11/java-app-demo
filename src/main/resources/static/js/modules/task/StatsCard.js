export function renderStatsCards(stats = {}) {
    const grid = document.createElement('div');
    grid.className = 'stats-grid';

    grid.innerHTML = `
        <div class="stat-card">
            <div class="stat-info">
                <h4>Tổng số Task</h4>
                <div class="stat-value">${stats.total || 0}</div>
            </div>
            <div class="stat-icon-wrapper stat-icon-total">📊</div>
        </div>

        <div class="stat-card">
            <div class="stat-info">
                <h4>Cần Làm (To-Do)</h4>
                <div class="stat-value">${stats.todo || 0}</div>
            </div>
            <div class="stat-icon-wrapper stat-icon-todo">⏳</div>
        </div>

        <div class="stat-card">
            <div class="stat-info">
                <h4>Đang Thực Hiện</h4>
                <div class="stat-value">${stats.inProgress || 0}</div>
            </div>
            <div class="stat-icon-wrapper stat-icon-progress">⚡</div>
        </div>

        <div class="stat-card">
            <div class="stat-info">
                <h4>Đã Hoàn Thành</h4>
                <div class="stat-value">${stats.completed || 0}</div>
            </div>
            <div class="stat-icon-wrapper stat-icon-completed">✅</div>
        </div>
    `;

    return grid;
}
