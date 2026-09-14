/**
 * Kanban Board Component with Native HTML5 Drag & Drop
 */
export function renderKanbanBoard(tasks = [], { onTaskDrop, onEdit, onDelete }) {
    const container = document.createElement('div');
    container.className = 'kanban-board';

    const columns = [
        { status: 'TODO', title: '⏳ Cần làm (To-Do)', color: 'var(--accent-amber)' },
        { status: 'IN_PROGRESS', title: '⚡ Đang thực hiện', color: 'var(--accent-blue)' },
        { status: 'COMPLETED', title: '✅ Đã hoàn thành', color: 'var(--accent-green)' }
    ];

    container.innerHTML = columns.map(col => {
        const colTasks = tasks.filter(t => t.status === col.status);
        return `
            <div class="kanban-column" data-status="${col.status}">
                <div class="kanban-column-header" style="border-top-color: ${col.color}">
                    <span class="column-title">${col.title}</span>
                    <span class="column-count">${colTasks.length}</span>
                </div>
                <div class="kanban-cards-container" data-status="${col.status}">
                    ${colTasks.length === 0 ? '<div class="kanban-empty-drop">Kéo task vào đây</div>' : ''}
                </div>
            </div>
        `;
    }).join('');

    // Attach Task Cards and Drag/Drop Event Listeners to each column
    columns.forEach(col => {
        const colContainer = container.querySelector(`.kanban-cards-container[data-status="${col.status}"]`);
        const colTasks = tasks.filter(t => t.status === col.status);

        colTasks.forEach((task, index) => {
            const cardEl = createDraggableCard(task, { onEdit, onDelete });
            colContainer.appendChild(cardEl);
        });

        // Setup Drop Target Events
        const colElement = container.querySelector(`.kanban-column[data-status="${col.status}"]`);
        
        colElement.addEventListener('dragover', (e) => {
            e.preventDefault();
            e.dataTransfer.dropEffect = 'move';
            colElement.classList.add('kanban-column-over');
        });

        colElement.addEventListener('dragleave', (e) => {
            if (!colElement.contains(e.relatedTarget)) {
                colElement.classList.remove('kanban-column-over');
            }
        });

        colElement.addEventListener('drop', (e) => {
            e.preventDefault();
            colElement.classList.remove('kanban-column-over');
            const taskIdStr = e.dataTransfer.getData('text/plain');
            if (taskIdStr) {
                const taskId = parseInt(taskIdStr, 10);
                onTaskDrop(taskId, col.status);
            }
        });
    });

    return container;
}

function createDraggableCard(task, { onEdit, onDelete }) {
    const card = document.createElement('div');
    card.className = `kanban-card priority-${task.priority}`;
    card.setAttribute('draggable', 'true');
    card.setAttribute('data-id', task.id);

    const priorityBadgeClass = {
        LOW: 'badge-low',
        MEDIUM: 'badge-medium',
        HIGH: 'badge-high',
        URGENT: 'badge-urgent'
    }[task.priority] || 'badge-low';

    card.innerHTML = `
        <div class="kanban-card-top">
            <span class="badge ${priorityBadgeClass}">${task.priority}</span>
            <div class="task-actions">
                <button class="icon-btn btn-edit-task" title="Sửa">✏️</button>
                <button class="icon-btn icon-btn-danger btn-delete-task" title="Xóa">🗑️</button>
            </div>
        </div>
        <h4 class="kanban-card-title">${escapeHtml(task.title)}</h4>
        ${task.description ? `<p class="kanban-card-desc">${escapeHtml(task.description)}</p>` : ''}
        <div class="kanban-card-footer">
            <span class="task-meta">${task.categoryName ? '📁 ' + escapeHtml(task.categoryName) : '📁 Chung'}</span>
            ${task.username ? `<span class="kanban-user-tag">👤 ${escapeHtml(task.username)}</span>` : ''}
        </div>
    `;

    // Drag start
    card.addEventListener('dragstart', (e) => {
        e.dataTransfer.setData('text/plain', task.id);
        card.classList.add('dragging');
    });

    // Drag end
    card.addEventListener('dragend', () => {
        card.classList.remove('dragging');
    });

    card.querySelector('.btn-edit-task').addEventListener('click', (e) => {
        e.stopPropagation();
        onEdit(task);
    });

    card.querySelector('.btn-delete-task').addEventListener('click', (e) => {
        e.stopPropagation();
        onDelete(task.id);
    });

    return card;
}

function escapeHtml(str) {
    return str.replace(/[&<>'"]/g, 
        tag => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;' }[tag] || tag)
    );
}
