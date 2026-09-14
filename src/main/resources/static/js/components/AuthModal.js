/**
 * Auth Modal Component (Login & Registration)
 */
export function renderAuthModal({ onLoginSuccess }) {
    const overlay = document.createElement('div');
    overlay.className = 'modal-overlay';

    overlay.innerHTML = `
        <div class="modal-container auth-modal-container">
            <div class="auth-tabs">
                <button type="button" class="auth-tab-btn active" id="tab-login">Đăng Nhập</button>
                <button type="button" class="auth-tab-btn" id="tab-register">Đăng Ký Tài Khoản</button>
            </div>

            <!-- Login Form -->
            <form id="form-login" class="auth-form active">
                <div class="form-group">
                    <label for="login-username">Tên đăng nhập</label>
                    <input type="text" id="login-username" required placeholder="Nhập username (ví dụ: user hoặc admin)...">
                </div>
                <div class="form-group">
                    <label for="login-password">Mật khẩu</label>
                    <input type="password" id="login-password" required placeholder="Nhập password (ví dụ: user123)...">
                </div>
                <div class="auth-hint">
                    💡 <strong>Tài khoản dùng thử:</strong><br>
                    - User: <code>user</code> / pass: <code>user123</code><br>
                    - Admin: <code>admin</code> / pass: <code>admin123</code>
                </div>
                <div id="login-error" class="auth-error-msg"></div>
                <button type="submit" class="btn btn-primary" style="width:100%; justify-content:center; margin-top:1rem;">
                    🔑 Đăng Nhập
                </button>
            </form>

            <!-- Register Form -->
            <form id="form-register" class="auth-form">
                <div class="form-group">
                    <label for="reg-fullname">Họ và tên</label>
                    <input type="text" id="reg-fullname" placeholder="Nguyễn Văn A...">
                </div>
                <div class="form-group">
                    <label for="reg-username">Tên đăng nhập *</label>
                    <input type="text" id="reg-username" required placeholder="Tên tài khoản...">
                </div>
                <div class="form-group">
                    <label for="reg-password">Mật khẩu *</label>
                    <input type="password" id="reg-password" required placeholder="Tạo mật khẩu...">
                </div>
                <div class="form-group">
                    <label for="reg-role">Vai trò</label>
                    <select id="reg-role">
                        <option value="ROLE_USER">USER (Quản lý task cá nhân)</option>
                        <option value="ROLE_ADMIN">ADMIN (Quản trị toàn bộ)</option>
                    </select>
                </div>
                <div id="reg-error" class="auth-error-msg"></div>
                <button type="submit" class="btn btn-primary" style="width:100%; justify-content:center; margin-top:1rem;">
                    ✨ Đăng Ký Ngay
                </button>
            </form>
        </div>
    `;

    const tabLogin = overlay.querySelector('#tab-login');
    const tabRegister = overlay.querySelector('#tab-register');
    const formLogin = overlay.querySelector('#form-login');
    const formRegister = overlay.querySelector('#form-register');
    const loginError = overlay.querySelector('#login-error');
    const regError = overlay.querySelector('#reg-error');

    tabLogin.addEventListener('click', () => {
        tabLogin.classList.add('active');
        tabRegister.classList.remove('active');
        formLogin.classList.add('active');
        formRegister.classList.remove('active');
        loginError.textContent = '';
    });

    tabRegister.addEventListener('click', () => {
        tabRegister.classList.add('active');
        tabLogin.classList.remove('active');
        formRegister.classList.add('active');
        formLogin.classList.remove('active');
        regError.textContent = '';
    });

    // Handle Login Submit
    formLogin.addEventListener('submit', async (e) => {
        e.preventDefault();
        loginError.textContent = '';
        const username = overlay.querySelector('#login-username').value.trim();
        const password = overlay.querySelector('#login-password').value;

        const { ApiService } = await import('../api.js');
        const res = await ApiService.login(username, password);
        if (res.success && res.data) {
            overlay.classList.remove('active');
            onLoginSuccess(res.data.user);
        } else {
            loginError.textContent = res.message || 'Đăng nhập thất bại. Vui lòng kiểm tra lại!';
        }
    });

    // Handle Register Submit
    formRegister.addEventListener('submit', async (e) => {
        e.preventDefault();
        regError.textContent = '';
        const fullName = overlay.querySelector('#reg-fullname').value.trim();
        const username = overlay.querySelector('#reg-username').value.trim();
        const password = overlay.querySelector('#reg-password').value;
        const role = overlay.querySelector('#reg-role').value;

        const { ApiService } = await import('../api.js');
        const res = await ApiService.register(username, password, fullName, role);
        if (res.success && res.data) {
            overlay.classList.remove('active');
            onLoginSuccess(res.data.user);
        } else {
            regError.textContent = res.message || 'Đăng ký thất bại!';
        }
    });

    return {
        element: overlay,
        open() {
            overlay.classList.add('active');
        },
        close() {
            overlay.classList.remove('active');
        }
    };
}
