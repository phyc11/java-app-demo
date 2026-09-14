/**
 * Task Modal Component (Create / Edit Task)
 */
export function renderTaskModal(categories = [], onSubmit) {
    const overlay = document.createElement('div');
    overlay.className = 'modal-overlay';
    
    overlay.innerHTML = `
        <div class="modal-container">
            <div class="modal-header">
                <h3 id="modal-title">Tạo Task Mới</h3>
                <button class="icon-btn" id="modal-close">✕</button>
            </div>
            <form id="task-form">
                <input type="hidden" id="task-id">
                <div class="form-group">
                    <label for="task-title-input">Tiêu đề *</label>
                    <input type="text" id="task-title-input" required placeholder="Nhập tiêu đề công việc...">
                </div>
                <div class="form-group">
                    <label for="task-desc-input">Mô tả</label>
                    <textarea id="task-desc-input" rows="3" placeholder="Chi tiết công việc..."></textarea>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label for="task-status-input">Trạng thái</label>
                        <select id="task-status-input">
                            <option value="TODO">To-Do (Cần làm)</option>
                            <option value="IN_PROGRESS">In Progress (Đang làm)</option>
                            <option value="COMPLETED">Completed (Hoàn thành)</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="task-priority-input">Độ ưu tiên</label>
                        <select id="task-priority-input">
                            <option value="LOW">Low (Thấp)</option>
                            <option value="MEDIUM" selected>Medium (Trung bình)</option>
                            <option value="HIGH">High (Cao)</option>
                            <option value="URGENT">Urgent (Khẩn cấp)</option>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <label for="task-category-input">Danh mục</label>
                    <select id="task-category-input">
                        <option value="">-- Không chọn --</option>
                        ${categories.map(c => `<option value="${c.id}">${c.name}</option>`).join('')}
                    </select>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" id="modal-cancel">Hủy</button>
                    <button type="submit" class="btn btn-primary" id="modal-submit">Lưu công việc</button>
                </div>
            </form>
        </div>
    `;

    const closeModal = () => overlay.classList.remove('active');

    overlay.querySelector('#modal-close').addEventListener('click', closeModal);
    overlay.querySelector('#modal-cancel').addEventListener('click', closeModal);
    overlay.addEventListener('click', (e) => {
        if (e.target === overlay) closeModal();
    });

    const form = overlay.querySelector('#task-form');
    form.addEventListener('submit', (e) => {
        e.preventDefault();
        const id = overlay.querySelector('#task-id').value;
        const payload = {
            id: id ? parseInt(id, 10) : null,
            title: overlay.querySelector('#task-title-input').value.trim(),
            description: overlay.querySelector('#task-desc-input').value.trim(),
            status: overlay.querySelector('#task-status-input').value,
            priority: overlay.querySelector('#task-priority-input').value,
            categoryId: overlay.querySelector('#task-category-input').value ? parseInt(overlay.querySelector('#task-category-input').value, 10) : null
        };
        onSubmit(payload);
        closeModal();
    });

    return {
        element: overlay,
        open(task = null) {
            form.reset();
            if (task) {
                overlay.querySelector('#modal-title').textContent = 'Chỉnh Sửa Task';
                overlay.querySelector('#task-id').value = task.id;
                overlay.querySelector('#task-title-input').value = task.title || '';
                overlay.querySelector('#task-desc-input').value = task.description || '';
                overlay.querySelector('#task-status-input').value = task.status || 'TODO';
                overlay.querySelector('#task-priority-input').value = task.priority || 'MEDIUM';
                overlay.querySelector('#task-category-input').value = task.categoryId || '';
            } else {
                overlay.querySelector('#modal-title').textContent = 'Tạo Task Mới';
                overlay.querySelector('#task-id').value = '';
            }
            overlay.classList.add('active');
        },
        close: closeModal
    };
}
