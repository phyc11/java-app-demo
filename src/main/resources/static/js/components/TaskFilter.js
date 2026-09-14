/**
 * Task Filter Component
 */
export function renderTaskFilter(categories = [], onFilterChange) {
    const div = document.createElement('div');
    div.className = 'filter-bar';

    div.innerHTML = `
        <div class="search-box">
            <span class="search-icon">🔍</span>
            <input type="text" id="search-input" placeholder="Tìm kiếm công việc theo tiêu đề hoặc mô tả...">
        </div>
        <div class="filter-group">
            <select id="filter-status" class="select-filter">
                <option value="">-- Tất cả trạng thái --</option>
                <option value="TODO">To-Do (Cần làm)</option>
                <option value="IN_PROGRESS">In Progress (Đang làm)</option>
                <option value="COMPLETED">Completed (Hoàn thành)</option>
            </select>
            <select id="filter-priority" class="select-filter">
                <option value="">-- Tất cả mức ưu tiên --</option>
                <option value="LOW">Low (Thấp)</option>
                <option value="MEDIUM">Medium (Trung bình)</option>
                <option value="HIGH">High (Cao)</option>
                <option value="URGENT">Urgent (Khẩn cấp)</option>
            </select>
            <select id="filter-category" class="select-filter">
                <option value="">-- Tất cả danh mục --</option>
                ${categories.map(c => `<option value="${c.id}">${c.name}</option>`).join('')}
            </select>
        </div>
    `;

    const getValues = () => ({
        search: div.querySelector('#search-input').value.trim(),
        status: div.querySelector('#filter-status').value,
        priority: div.querySelector('#filter-priority').value,
        categoryId: div.querySelector('#filter-category').value
    });

    let timeout = null;
    div.querySelector('#search-input').addEventListener('input', () => {
        clearTimeout(timeout);
        timeout = setTimeout(() => onFilterChange(getValues()), 300);
    });

    div.querySelector('#filter-status').addEventListener('change', () => onFilterChange(getValues()));
    div.querySelector('#filter-priority').addEventListener('change', () => onFilterChange(getValues()));
    div.querySelector('#filter-category').addEventListener('change', () => onFilterChange(getValues()));

    return div;
}
