export function renderTaskModal(categories = [], onSave) {
    const modal = document.createElement('div');
    modal.className = 'modal-overlay';

    modal.innerHTML = `
        <div class="modal-container">
            <h3 id="modal-title" style="margin-bottom:1.25rem; font-size:1.2rem; font-weight:700">Tạo Task Mới</h3>
            <form id="task-form">
                <input type="hidden" id="task-id">
                
                <div class="form-group">
                    <label for="task-title-input">Tiêu đề Công việc</label>
                    <input type="text" id="task-title-input" placeholder="Nhập tiêu đề task..." required>
                </div>

                <div class="form-group">
                    <label for="task-desc-input">Mô tả chi tiết</label>
                    <textarea id="task-desc-input" rows="3" placeholder="Nhập nội dung mô tả..."></textarea>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="task-status-select">Trạng thái</label>
                        <select id="task-status-select">
                            <option value="TODO">⏳ Cần Làm (To-Do)</option>
                            <option value="IN_PROGRESS">⚡ Đang Làm (In Progress)</option>
                            <option value="COMPLETED">✅ Hoàn Thành (Completed)</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="task-priority-select">Mức độ ưu tiên</label>
                        <select id="task-priority-select">
                            <option value="LOW">Thấp (Low)</option>
                            <option value="MEDIUM" selected>Trung Bình (Medium)</option>
                            <option value="HIGH">Cao (High)</option>
                            <option value="URGENT">Khẩn Cấp (Urgent)</option>
                        </select>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="task-category-select">Danh mục</label>
                        <select id="task-category-select">
                            <option value="">-- Chọn danh mục --</option>
                            ${categories.map(c => `<option value="${c.id}">${escapeHtml(c.name)}</option>`).join('')}
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="task-duedate-input">Hạn hoàn thành</label>
                        <input type="datetime-local" id="task-duedate-input">
                    </div>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" id="modal-cancel-btn">Hủy Bỏ</button>
                    <button type="submit" class="btn btn-primary" id="modal-save-btn">Lưu Task</button>
                </div>
            </form>
        </div>
    `;

    const form = modal.querySelector('#task-form');
    const cancelBtn = modal.querySelector('#modal-cancel-btn');

    const closeModal = () => modal.classList.remove('active');
    cancelBtn.addEventListener('click', closeModal);

    modal.addEventListener('click', (e) => {
        if (e.target === modal) closeModal();
    });

    form.addEventListener('submit', (e) => {
        e.preventDefault();
        const id = modal.querySelector('#task-id').value;
        const title = modal.querySelector('#task-title-input').value.trim();
        const description = modal.querySelector('#task-desc-input').value.trim();
        const status = modal.querySelector('#task-status-select').value;
        const priority = modal.querySelector('#task-priority-select').value;
        const categoryId = modal.querySelector('#task-category-select').value;
        const dueDate = modal.querySelector('#task-duedate-input').value;

        onSave({
            id: id ? parseInt(id) : null,
            title,
            description,
            status,
            priority,
            categoryId: categoryId ? parseInt(categoryId) : null,
            dueDate: dueDate ? dueDate : null
        });

        closeModal();
    });

    return {
        element: modal,
        open: (taskData = null) => {
            modal.classList.add('active');
            if (taskData) {
                modal.querySelector('#modal-title').textContent = 'Chỉnh Sửa Task';
                modal.querySelector('#task-id').value = taskData.id;
                modal.querySelector('#task-title-input').value = taskData.title || '';
                modal.querySelector('#task-desc-input').value = taskData.description || '';
                modal.querySelector('#task-status-select').value = taskData.status || 'TODO';
                modal.querySelector('#task-priority-select').value = taskData.priority || 'MEDIUM';
                modal.querySelector('#task-category-select').value = taskData.categoryId || '';
                modal.querySelector('#task-duedate-input').value = taskData.dueDate ? taskData.dueDate.substring(0, 16) : '';
            } else {
                modal.querySelector('#modal-title').textContent = 'Tạo Task Mới';
                form.reset();
                modal.querySelector('#task-id').value = '';
            }
        },
        close: closeModal
    };
}

function escapeHtml(str) {
    return str.replace(/[&<>'"]/g, 
        tag => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;' }[tag] || tag)
    );
}
