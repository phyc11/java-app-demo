export function renderKanbanBoard(tasks = [], { onTaskDrop, onEdit, onDelete }) {
    const board = document.createElement('div');
    board.className = 'kanban-board';

    const columnsData = [
        { id: 'TODO', title: '⏳ Cần Làm', tasks: tasks.filter(t => t.status === 'TODO') },
        { id: 'IN_PROGRESS', title: '⚡ Đang Thực Hiện', tasks: tasks.filter(t => t.status === 'IN_PROGRESS') },
        { id: 'COMPLETED', title: '✅ Đã Hoàn Thành', tasks: tasks.filter(t => t.status === 'COMPLETED') }
    ];

    columnsData.forEach(col => {
        const colEl = document.createElement('div');
        colEl.className = 'kanban-column';
        colEl.dataset.status = col.id;

        colEl.innerHTML = `
            <div class="kanban-column-header">
                <span class="column-title">${col.title}</span>
                <span class="column-count">${col.tasks.length}</span>
            </div>
            <div class="kanban-cards-container"></div>
        `;

        const cardsContainer = colEl.querySelector('.kanban-cards-container');

        if (col.tasks.length === 0) {
            cardsContainer.innerHTML = '<div class="kanban-empty-drop">Thả task vào đây...</div>';
        } else {
            col.tasks.forEach(task => {
                const cardEl = createKanbanCard(task, { onEdit, onDelete });
                cardsContainer.appendChild(cardEl);
            });
        }

        // Dragover & Drop Listeners
        colEl.addEventListener('dragover', (e) => {
            e.preventDefault();
            colEl.classList.add('kanban-column-over');
        });

        colEl.addEventListener('dragleave', () => {
            colEl.classList.remove('kanban-column-over');
        });

        colEl.addEventListener('drop', (e) => {
            e.preventDefault();
            colEl.classList.remove('kanban-column-over');
            const taskId = e.dataTransfer.getData('text/plain');
            if (taskId) {
                onTaskDrop(parseInt(taskId), col.id);
            }
        });

        board.appendChild(colEl);
    });

    return board;
}

function createKanbanCard(task, { onEdit, onDelete }) {
    const card = document.createElement('div');
    card.className = 'kanban-card';
    card.draggable = true;

    const categoryTag = task.categoryName ? `<span class="badge" style="background:${task.categoryColor}20; color:${task.categoryColor}">${escapeHtml(task.categoryName)}</span>` : '';

    card.innerHTML = `
        <div class="kanban-card-top">
            ${categoryTag}
            <div class="task-actions">
                <button class="icon-btn" id="kanban-edit" title="Sửa">✏️</button>
                <button class="icon-btn icon-btn-danger" id="kanban-delete" title="Xóa">🗑️</button>
            </div>
        </div>
        <div class="kanban-card-title">${escapeHtml(task.title)}</div>
        <div class="kanban-card-desc">${escapeHtml(task.description || '')}</div>
        <div class="kanban-card-footer">
            <span>${task.priority ? getPriorityLabel(task.priority) : ''}</span>
            <span class="kanban-user-tag">👤 ${escapeHtml(task.createdBy || 'User')}</span>
        </div>
    `;

    card.addEventListener('dragstart', (e) => {
        e.dataTransfer.setData('text/plain', task.id);
        card.classList.add('dragging');
    });

    card.addEventListener('dragend', () => {
        card.classList.remove('dragging');
    });

    card.querySelector('#kanban-edit').addEventListener('click', (e) => {
        e.stopPropagation();
        onEdit(task);
    });

    card.querySelector('#kanban-delete').addEventListener('click', (e) => {
        e.stopPropagation();
        onDelete(task.id);
    });

    return card;
}

function getPriorityLabel(priority) {
    switch (priority) {
        case 'LOW': return '🟢 Thấp';
        case 'MEDIUM': return '🔵 Vừa';
        case 'HIGH': return '🟠 Cao';
        case 'URGENT': return '🔴 Khẩn Cấp';
        default: return '';
    }
}

function escapeHtml(str) {
    return str.replace(/[&<>'"]/g, 
        tag => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;' }[tag] || tag)
    );
}
