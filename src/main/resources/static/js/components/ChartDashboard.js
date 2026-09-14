/**
 * Chart Dashboard Component (Analytics & Report Export Modal)
 */
export function renderChartDashboard() {
    const modal = document.createElement('div');
    modal.className = 'modal-overlay';

    modal.innerHTML = `
        <div class="modal-container analytics-modal-container">
            <div class="analytics-header">
                <div>
                    <h3 class="analytics-title">📊 Analytics & Báo Cáo Thống Kê</h3>
                    <span class="analytics-subtitle">Phân tích dữ liệu hiệu suất làm việc & xuất báo cáo</span>
                </div>
                <button class="icon-btn" id="analytics-close-btn">✕</button>
            </div>

            <div class="analytics-actions-bar">
                <button type="button" class="btn btn-primary btn-sm" id="btn-export-excel">
                    📥 Xuất Báo Cáo Excel (.xlsx)
                </button>
                <button type="button" class="btn btn-secondary btn-sm" id="btn-export-csv">
                    📄 Xuất Báo Cáo CSV (.csv)
                </button>
            </div>

            <div class="analytics-body" id="analytics-body-container">
                <div class="analytics-loading">Đang tổng hợp dữ liệu...</div>
            </div>
        </div>
    `;

    const closeBtn = modal.querySelector('#analytics-close-btn');
    const btnExcel = modal.querySelector('#btn-export-excel');
    const btnCsv = modal.querySelector('#btn-export-csv');

    const closeModal = () => modal.classList.remove('active');
    closeBtn.addEventListener('click', closeModal);

    modal.addEventListener('click', (e) => {
        if (e.target === modal) closeModal();
    });

    btnExcel.addEventListener('click', async () => {
        const { ApiService } = await import('../api.js');
        ApiService.downloadExport('excel');
    });

    btnCsv.addEventListener('click', async () => {
        const { ApiService } = await import('../api.js');
        ApiService.downloadExport('csv');
    });

    return {
        element: modal,
        open: async () => {
            modal.classList.add('active');
            const bodyContainer = modal.querySelector('#analytics-body-container');
            bodyContainer.innerHTML = '<div class="analytics-loading">Đang tổng hợp dữ liệu...</div>';

            try {
                const { ApiService } = await import('../api.js');
                const analytics = await ApiService.getAnalytics();

                if (!analytics) {
                    bodyContainer.innerHTML = '<div class="analytics-empty">Không có dữ liệu phân tích.</div>';
                    return;
                }

                bodyContainer.innerHTML = `
                    <!-- Progress Card -->
                    <div class="analytics-card progress-overview-card">
                        <div class="analytics-card-title">🎯 Tiến Độ Hoàn Thành Hệ Thống</div>
                        <div class="progress-ring-wrapper">
                            <div class="progress-number">${analytics.completionRate}%</div>
                            <div class="progress-label">Đã hoàn thành (${analytics.totalTasks} công việc)</div>
                            <div class="progress-bar-bg">
                                <div class="progress-bar-fill" style="width: ${analytics.completionRate}%"></div>
                            </div>
                        </div>
                    </div>

                    <div class="analytics-grid">
                        <!-- Category Distribution -->
                        <div class="analytics-card">
                            <div class="analytics-card-title">📁 Công Việc Theo Danh Mục</div>
                            <div class="chart-bars-list">
                                ${renderBarList(analytics.categoryDistribution, analytics.totalTasks, '#6366f1')}
                            </div>
                        </div>

                        <!-- Status Distribution -->
                        <div class="analytics-card">
                            <div class="analytics-card-title">⚡ Phân Bổ Trạng Thái</div>
                            <div class="chart-bars-list">
                                ${renderBarList(analytics.statusDistribution, analytics.totalTasks, '#10b981')}
                            </div>
                        </div>

                        <!-- Priority Distribution -->
                        <div class="analytics-card full-width">
                            <div class="analytics-card-title">🔥 Phân Bổ Theo Mức Độ Ưu Tiên</div>
                            <div class="chart-bars-list">
                                ${renderBarList(analytics.priorityDistribution, analytics.totalTasks, '#ef4444')}
                            </div>
                        </div>
                    </div>
                `;

            } catch (err) {
                bodyContainer.innerHTML = `<div class="analytics-empty" style="color:var(--accent-red)">Lỗi khi tải biểu đồ: ${err.message}</div>`;
            }
        },
        close: closeModal
    };
}

function renderBarList(distributionObj = {}, total = 1, themeColor = '#6366f1') {
    const keys = Object.keys(distributionObj);
    if (keys.length === 0) return '<div class="analytics-empty">Chưa có dữ liệu</div>';

    return keys.map(key => {
        const count = distributionObj[key];
        const percent = total > 0 ? Math.round((count / total) * 100) : 0;
        return `
            <div class="chart-bar-item">
                <div class="chart-bar-header">
                    <span class="chart-bar-label">${escapeHtml(key)}</span>
                    <span class="chart-bar-val">${count} task (${percent}%)</span>
                </div>
                <div class="chart-bar-bg">
                    <div class="chart-bar-inner" style="width:${percent}%; background-color:${themeColor}"></div>
                </div>
            </div>
        `;
    }).join('');
}

function escapeHtml(str) {
    return str.replace(/[&<>'"]/g, 
        tag => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;' }[tag] || tag)
    );
}
