export function renderTaskFilter(categories = [], onFilterChange) {
    const filterContainer = document.createElement('div');
    filterContainer.className = 'filter-bar';

    filterContainer.innerHTML = `
        <div class="search-box">
            <span class="search-icon">🔍</span>
            <input type="text" id="filter-search" placeholder="Tìm kiếm công việc theo từ khóa...">
        </div>

        <div class="filter-group">
            <select class="select-filter" id="filter-status">
                <option value="">Tất cả Trạng Thái</option>
                <option value="TODO">⏳ Cần Làm</option>
                <option value="IN_PROGRESS">⚡ Đang Làm</option>
                <option value="COMPLETED">✅ Hoàn Thành</option>
            </select>

            <select class="select-filter" id="filter-priority">
                <option value="">Tất cả Độ Ưu Tiên</option>
                <option value="LOW">Thấp</option>
                <option value="MEDIUM">Trung Bình</option>
                <option value="HIGH">Cao</option>
                <option value="URGENT">Khẩn Cấp</option>
            </select>

            <select class="select-filter" id="filter-category">
                <option value="">Tất cả Danh Mục</option>
                ${categories.map(c => `<option value="${c.id}">${escapeHtml(c.name)}</option>`).join('')}
            </select>
        </div>
    `;

    const searchInput = filterContainer.querySelector('#filter-search');
    const statusSelect = filterContainer.querySelector('#filter-status');
    const prioritySelect = filterContainer.querySelector('#filter-priority');
    const categorySelect = filterContainer.querySelector('#filter-category');

    let debounceTimer;

    const emitFilter = () => {
        onFilterChange({
            search: searchInput.value.trim(),
            status: statusSelect.value,
            priority: prioritySelect.value,
            categoryId: categorySelect.value ? parseInt(categorySelect.value) : null
        });
    };

    searchInput.addEventListener('input', () => {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(emitFilter, 300);
    });

    statusSelect.addEventListener('change', emitFilter);
    prioritySelect.addEventListener('change', emitFilter);
    categorySelect.addEventListener('change', emitFilter);

    return filterContainer;
}

function escapeHtml(str) {
    return str.replace(/[&<>'"]/g, 
        tag => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;' }[tag] || tag)
    );
}
