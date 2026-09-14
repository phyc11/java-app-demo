import { ApiService } from './core/api.js';
import { renderNavbar } from './core/Navbar.js';
import { renderStatsCards } from './modules/task/StatsCard.js';
import { renderTaskFilter } from './modules/task/TaskFilter.js';
import { renderTaskCard } from './modules/task/TaskCard.js';
import { renderTaskModal } from './modules/task/TaskModal.js';
import { renderKanbanBoard } from './modules/kanban/KanbanBoard.js';
import { renderAuthModal } from './modules/auth/AuthModal.js';
import { renderProfileModal } from './modules/auth/ProfileModal.js';
import { renderChartDashboard } from './modules/analytics/ChartDashboard.js';

class Application {
    constructor() {
        this.appContainer = document.getElementById('app');
        this.currentUser = null;
        this.currentView = 'kanban';
        this.categories = [];
        this.tasks = [];
        this.stats = {};
        this.currentFilter = {};
    }

    async init() {
        try {
            // Check Auth Token & Current User
            this.currentUser = await ApiService.getMe();

            // Initialize Modals & Drawers
            this.authModal = renderAuthModal({
                onLoginSuccess: (user) => {
                    this.currentUser = user;
                    this.refreshAll();
                }
            });
            document.body.appendChild(this.authModal.element);

            this.profileModal = renderProfileModal({
                currentUser: this.currentUser,
                onProfileUpdated: (updatedUser) => {
                    this.currentUser = updatedUser;
                    this.refreshAll();
                }
            });
            document.body.appendChild(this.profileModal.element);

            this.chartDashboard = renderChartDashboard();
            document.body.appendChild(this.chartDashboard.element);

            this.categories = await ApiService.getCategories();

            this.taskModal = renderTaskModal(this.categories, (data) => this.handleSaveTask(data));
            document.body.appendChild(this.taskModal.element);

            // If not logged in, show Auth Modal automatically
            if (!this.currentUser) {
                this.authModal.open();
            }

            // Build UI Layout
            this.renderLayout();
            await this.refreshData();

        } catch (err) {
            console.error("Initialization error:", err);
        }
    }

    renderLayout() {
        this.appContainer.innerHTML = '';

        // Render Navbar
        const navbar = renderNavbar({
            currentUser: this.currentUser,
            currentView: this.currentView,
            onViewChange: (view) => {
                this.currentView = view;
                this.renderLayout();
                this.renderContent();
            },
            onNewTaskClick: () => this.taskModal.open(),
            onAnalyticsClick: () => this.chartDashboard.open(),
            onProfileClick: () => this.profileModal.open(this.currentUser),
            onAuthClick: () => this.authModal.open(),
            onLogoutClick: () => {
                ApiService.logout();
                this.currentUser = null;
                this.refreshAll();
            }
        });
        this.appContainer.appendChild(navbar);

        // Stats Container Slot
        this.statsSlot = document.createElement('div');
        this.appContainer.appendChild(this.statsSlot);

        // Filter Bar Slot
        const filterBar = renderTaskFilter(this.categories, (filters) => this.handleFilterChange(filters));
        this.appContainer.appendChild(filterBar);

        // Content Area Slot (Grid or Kanban)
        this.contentSlot = document.createElement('div');
        this.appContainer.appendChild(this.contentSlot);
    }

    async refreshAll() {
        this.categories = await ApiService.getCategories();
        this.renderLayout();
        await this.refreshData();
    }

    async refreshData() {
        if (!this.currentUser) {
            this.stats = {};
            this.tasks = [];
            this.renderStats();
            this.renderContent();
            return;
        }

        this.stats = await ApiService.getStats();
        this.tasks = await ApiService.getTasks(this.currentFilter);
        this.renderStats();
        this.renderContent();
    }

    renderStats() {
        if (this.statsSlot) {
            this.statsSlot.innerHTML = '';
            this.statsSlot.appendChild(renderStatsCards(this.stats));
        }
    }

    renderContent() {
        if (!this.contentSlot) return;
        this.contentSlot.innerHTML = '';

        if (!this.currentUser) {
            this.contentSlot.innerHTML = `
                <div class="empty-state">
                    <div class="empty-state-icon">🔒</div>
                    <h3>Yêu cầu đăng nhập</h3>
                    <p>Vui lòng đăng nhập hoặc đăng ký tài khoản để xem và quản lý công việc của bạn!</p>
                </div>
            `;
            return;
        }

        if (this.currentView === 'kanban') {
            const kanbanEl = renderKanbanBoard(this.tasks, {
                onTaskDrop: (id, targetStatus) => this.handleTaskDrop(id, targetStatus),
                onEdit: (taskData) => this.taskModal.open(taskData),
                onDelete: (id) => this.handleDeleteTask(id)
            });
            this.contentSlot.appendChild(kanbanEl);
        } else {
            // Render Grid Cards
            const gridContainer = document.createElement('div');
            gridContainer.className = 'tasks-grid';

            if (this.tasks.length === 0) {
                gridContainer.innerHTML = `
                    <div class="empty-state">
                        <div class="empty-state-icon">📌</div>
                        <h3>Không tìm thấy công việc nào</h3>
                        <p>Hãy bấm nút <strong>"Tạo Task"</strong> để bắt đầu!</p>
                    </div>
                `;
            } else {
                this.tasks.forEach(task => {
                    const card = renderTaskCard(task, {
                        onStatusToggle: (id, newStatus) => this.handleStatusToggle(id, newStatus),
                        onEdit: (taskData) => this.taskModal.open(taskData),
                        onDelete: (id) => this.handleDeleteTask(id)
                    });
                    gridContainer.appendChild(card);
                });
            }
            this.contentSlot.appendChild(gridContainer);
        }
    }

    async handleFilterChange(filters) {
        this.currentFilter = filters;
        if (this.currentUser) {
            this.tasks = await ApiService.getTasks(this.currentFilter);
            this.renderContent();
        }
    }

    async handleSaveTask(taskData) {
        if (taskData.id) {
            await ApiService.updateTask(taskData.id, taskData);
        } else {
            await ApiService.createTask(taskData);
        }
        await this.refreshData();
    }

    async handleStatusToggle(id, newStatus) {
        await ApiService.updateTaskStatus(id, newStatus);
        await this.refreshData();
    }

    async handleTaskDrop(id, targetStatus) {
        await ApiService.moveTask(id, targetStatus);
        await this.refreshData();
    }

    async handleDeleteTask(id) {
        if (confirm("Bạn có chắc chắn muốn xóa task này?")) {
            await ApiService.deleteTask(id);
            await this.refreshData();
        }
    }
}

// Bootstrap Application
document.addEventListener('DOMContentLoaded', () => {
    const app = new Application();
    app.init();
});
