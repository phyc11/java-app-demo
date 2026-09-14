import { ApiService } from '../../core/api.js';

export function renderAuthModal({ onLoginSuccess }) {
    const modal = document.createElement('div');
    modal.className = 'modal-overlay';

    modal.innerHTML = `
        <div class="modal-container">
            <div class="auth-tabs">
                <button type="button" class="auth-tab-btn active" id="tab-login-btn">🔑 Đăng Nhập</button>
                <button type="button" class="auth-tab-btn" id="tab-register-btn">📝 Đăng Ký</button>
            </div>

            <!-- Login Form -->
            <form id="login-form" class="auth-form active">
                <div class="form-group">
                    <label for="login-username">Tài khoản</label>
                    <input type="text" id="login-username" placeholder="Nhập username..." required>
                </div>
                <div class="form-group">
                    <label for="login-password">Mật khẩu</label>
                    <input type="password" id="login-password" placeholder="Nhập mật khẩu..." required>
                </div>
                <div class="auth-error-msg" id="login-error"></div>

                <div class="auth-hint">
                    💡 <strong>Tài khoản thử nghiệm sẵn có:</strong><br>
                    - Admin: <code>admin</code> / pass: <code>admin123</code><br>
                    - User: <code>user</code> / pass: <code>user123</code>
                </div>

                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary" style="width:100%">Đăng Nhập</button>
                </div>
            </form>

            <!-- Register Form -->
            <form id="register-form" class="auth-form">
                <div class="form-group">
                    <label for="reg-username">Tài khoản</label>
                    <input type="text" id="reg-username" placeholder="Chọn username..." required>
                </div>
                <div class="form-group">
                    <label for="reg-fullname">Họ và Tên</label>
                    <input type="text" id="reg-fullname" placeholder="Nhập họ tên..." required>
                </div>
                <div class="form-group">
                    <label for="reg-password">Mật khẩu</label>
                    <input type="password" id="reg-password" placeholder="Tạo mật khẩu..." required>
                </div>
                <div class="form-group">
                    <label for="reg-role">Vai Trò System</label>
                    <select id="reg-role">
                        <option value="ROLE_USER">USER (Quản lý task cá nhân)</option>
                        <option value="ROLE_ADMIN">ADMIN (Quản lý toàn bộ system)</option>
                    </select>
                </div>
                <div class="auth-error-msg" id="reg-error"></div>

                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary" style="width:100%">Đăng Ký Tài Khoản</button>
                </div>
            </form>
        </div>
    `;

    const tabLogin = modal.querySelector('#tab-login-btn');
    const tabRegister = modal.querySelector('#tab-register-btn');
    const formLogin = modal.querySelector('#login-form');
    const formRegister = modal.querySelector('#register-form');

    tabLogin.addEventListener('click', () => {
        tabLogin.classList.add('active');
        tabRegister.classList.remove('active');
        formLogin.classList.add('active');
        formRegister.classList.remove('active');
    });

    tabRegister.addEventListener('click', () => {
        tabRegister.classList.add('active');
        tabLogin.classList.remove('active');
        formRegister.classList.add('active');
        formLogin.classList.remove('active');
    });

    formLogin.addEventListener('submit', async (e) => {
        e.preventDefault();
        const errorEl = modal.querySelector('#login-error');
        errorEl.textContent = '';

        const username = modal.querySelector('#login-username').value.trim();
        const password = modal.querySelector('#login-password').value;

        try {
            const res = await ApiService.login(username, password);
            if (res.success && res.data) {
                modal.classList.remove('active');
                onLoginSuccess(res.data);
            } else {
                errorEl.textContent = res.message || 'Đăng nhập thất bại!';
            }
        } catch (err) {
            errorEl.textContent = 'Lỗi kết nối máy chủ!';
        }
    });

    formRegister.addEventListener('submit', async (e) => {
        e.preventDefault();
        const errorEl = modal.querySelector('#reg-error');
        errorEl.textContent = '';

        const username = modal.querySelector('#reg-username').value.trim();
        const fullName = modal.querySelector('#reg-fullname').value.trim();
        const password = modal.querySelector('#reg-password').value;
        const role = modal.querySelector('#reg-role').value;

        try {
            const res = await ApiService.register(username, password, fullName, role);
            if (res.success && res.data) {
                modal.classList.remove('active');
                onLoginSuccess(res.data);
            } else {
                errorEl.textContent = res.message || 'Đăng ký thất bại!';
            }
        } catch (err) {
            errorEl.textContent = 'Lỗi kết nối máy chủ!';
        }
    });

    return {
        element: modal,
        open: () => modal.classList.add('active'),
        close: () => modal.classList.remove('active')
    };
}
