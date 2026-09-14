import { ApiService } from '../../core/api.js';

export function renderProfileModal({ currentUser, onProfileUpdated }) {
    const modal = document.createElement('div');
    modal.className = 'modal-overlay';

    modal.innerHTML = `
        <div class="modal-container">
            <div class="auth-tabs">
                <button type="button" class="auth-tab-btn active" id="tab-profile-btn">👤 Hồ Sơ Cá Nhân</button>
                <button type="button" class="auth-tab-btn" id="tab-pass-btn">🔐 Đổi Mật Khẩu</button>
            </div>

            <!-- Profile Form -->
            <form id="profile-form" class="auth-form active">
                <div class="form-group">
                    <label for="prof-username">Tài Khoản (Username)</label>
                    <input type="text" id="prof-username" value="${escapeHtml(currentUser ? currentUser.username : '')}" disabled style="opacity:0.7">
                </div>
                <div class="form-group">
                    <label for="prof-fullname">Họ và Tên</label>
                    <input type="text" id="prof-fullname" value="${escapeHtml(currentUser ? currentUser.fullName || '' : '')}" placeholder="Nhập họ tên mới..." required>
                </div>
                <div class="form-group">
                    <label for="prof-avatar-color">Màu Thẻ Đại Diện (Avatar Color)</label>
                    <input type="color" id="prof-avatar-color" value="${currentUser ? currentUser.avatarColor || '#6366f1' : '#6366f1'}" style="height:42px; padding:2px; cursor:pointer">
                </div>
                <div class="auth-error-msg" id="prof-error"></div>
                <div class="auth-success-msg" id="prof-success" style="color:var(--accent-green); font-size:0.85rem; margin-bottom:0.5rem"></div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" id="prof-cancel-btn">Hủy</button>
                    <button type="submit" class="btn btn-primary">Lưu Thay Đổi</button>
                </div>
            </form>

            <!-- Change Password Form -->
            <form id="password-form" class="auth-form">
                <div class="form-group">
                    <label for="pass-old">Mật Khẩu Hiện Tại</label>
                    <input type="password" id="pass-old" placeholder="Nhập mật khẩu hiện tại..." required>
                </div>
                <div class="form-group">
                    <label for="pass-new">Mật Khẩu Mới</label>
                    <input type="password" id="pass-new" placeholder="Nhập mật khẩu mới (tối thiểu 6 ký tự)..." required>
                </div>
                <div class="form-group">
                    <label for="pass-confirm">Xác Nhận Mật Khẩu Mới</label>
                    <input type="password" id="pass-confirm" placeholder="Nhập lại mật khẩu mới..." required>
                </div>
                <div class="auth-error-msg" id="pass-error"></div>
                <div class="auth-success-msg" id="pass-success" style="color:var(--accent-green); font-size:0.85rem; margin-bottom:0.5rem"></div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" id="pass-cancel-btn">Hủy</button>
                    <button type="submit" class="btn btn-primary">Đổi Mật Khẩu</button>
                </div>
            </form>
        </div>
    `;

    const tabProfile = modal.querySelector('#tab-profile-btn');
    const tabPass = modal.querySelector('#tab-pass-btn');
    const formProfile = modal.querySelector('#profile-form');
    const formPass = modal.querySelector('#password-form');

    const closeModal = () => modal.classList.remove('active');

    modal.querySelector('#prof-cancel-btn').addEventListener('click', closeModal);
    modal.querySelector('#pass-cancel-btn').addEventListener('click', closeModal);

    modal.addEventListener('click', (e) => {
        if (e.target === modal) closeModal();
    });

    tabProfile.addEventListener('click', () => {
        tabProfile.classList.add('active');
        tabPass.classList.remove('active');
        formProfile.classList.add('active');
        formPass.classList.remove('active');
    });

    tabPass.addEventListener('click', () => {
        tabPass.classList.add('active');
        tabProfile.classList.remove('active');
        formPass.classList.add('active');
        formProfile.classList.remove('active');
    });

    formProfile.addEventListener('submit', async (e) => {
        e.preventDefault();
        const errEl = modal.querySelector('#prof-error');
        const succEl = modal.querySelector('#prof-success');
        errEl.textContent = '';
        succEl.textContent = '';

        const fullName = modal.querySelector('#prof-fullname').value.trim();
        const avatarColor = modal.querySelector('#prof-avatar-color').value;

        try {
            const res = await ApiService.updateProfile(fullName, avatarColor);
            if (res.success && res.data) {
                succEl.textContent = '✅ ' + (res.message || 'Cập nhật thành công!');
                setTimeout(() => {
                    closeModal();
                    onProfileUpdated(res.data);
                }, 1000);
            } else {
                errEl.textContent = res.message || 'Cập nhật thất bại!';
            }
        } catch (err) {
            errEl.textContent = 'Lỗi máy chủ!';
        }
    });

    formPass.addEventListener('submit', async (e) => {
        e.preventDefault();
        const errEl = modal.querySelector('#pass-error');
        const succEl = modal.querySelector('#pass-success');
        errEl.textContent = '';
        succEl.textContent = '';

        const oldPassword = modal.querySelector('#pass-old').value;
        const newPassword = modal.querySelector('#pass-new').value;
        const confirmPassword = modal.querySelector('#pass-confirm').value;

        if (newPassword !== confirmPassword) {
            errEl.textContent = 'Mật khẩu mới và nhập lại mật khẩu không khớp!';
            return;
        }

        try {
            const res = await ApiService.changePassword(oldPassword, newPassword);
            if (res.success) {
                succEl.textContent = '✅ ' + (res.message || 'Đổi mật khẩu thành công!');
                formPass.reset();
                setTimeout(closeModal, 1200);
            } else {
                errEl.textContent = res.message || 'Đổi mật khẩu thất bại!';
            }
        } catch (err) {
            errEl.textContent = 'Lỗi máy chủ!';
        }
    });

    return {
        element: modal,
        open: (user) => {
            modal.classList.add('active');
            if (user) {
                modal.querySelector('#prof-username').value = user.username;
                modal.querySelector('#prof-fullname').value = user.fullName || '';
                modal.querySelector('#prof-avatar-color').value = user.avatarColor || '#6366f1';
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
