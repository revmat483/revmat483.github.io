// ========================== НАСТРОЙКИ FIRESTORE ==========================
const FIREBASE_URL = 'https://gamep-79cef-default-rtdb.firebaseio.com/';

// ========================== ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ ДЛЯ РАБОТЫ С FIREBASE (REST) ==========================
async function fbGet(path) {
    const res = await fetch(FIREBASE_URL + path + '.json');
    return res.json();
}

async function fbPut(path, data) {
    const res = await fetch(FIREBASE_URL + path + '.json', {
        method: 'PUT',
        body: JSON.stringify(data)
    });
    return res.json();
}

async function fbPost(path, data) {
    const res = await fetch(FIREBASE_URL + path + '.json', {
        method: 'POST',
        body: JSON.stringify(data)
    });
    return res.json();
}

// ========================== ГЛОБАЛЬНОЕ СОСТОЯНИЕ ПРИЛОЖЕНИЯ ==========================
const App = {
    currentUser: null,          // { id, name }
    users: [],                  // список всех пользователей (кроме себя)
    selectedChatUser: null,     // выбранный собеседник { id, name }
    messages: [],               // сообщения текущего чата
    pollingInterval: null,
    mobileChatVisible: false,   // для мобильной версии (показывать чат или список)
};

// ========================== РЕНДЕРИНГ ИНТЕРФЕЙСА ==========================
function render() {
    if (!App.currentUser) {
        renderLoginScreen();
    } else {
        renderChatInterface();
    }
}

function renderLoginScreen() {
    const appEl = document.getElementById('app');
    appEl.innerHTML = `
        <div class="login-screen">
            <div class="login-card">
                <h2><i class="fab fa-whatsapp"></i> Firebase Chat</h2>
                <input type="text" id="loginName" placeholder="Имя пользователя" autocomplete="off">
                <input type="password" id="loginPassword" placeholder="Пароль">
                <div class="login-actions">
                    <button class="btn btn-primary" id="loginBtn">Войти</button>
                    <button class="btn btn-secondary" id="registerBtn">Регистрация</button>
                </div>
                <div class="error-message" id="loginError"></div>
            </div>
        </div>
    `;
    document.getElementById('loginBtn').addEventListener('click', handleLogin);
    document.getElementById('registerBtn').addEventListener('click', handleRegister);
}

function renderChatInterface() {
    const isMobile = window.innerWidth <= 700;
    const usersListHtml = App.users.map(user => {
        const activeClass = (App.selectedChatUser && App.selectedChatUser.id === user.id) ? 'active' : '';
        const avatarLetter = user.name.charAt(0).toUpperCase();
        return `
            <div class="user-item ${activeClass}" data-userid="${user.id}" data-username="${user.name}">
                <div class="user-avatar">${avatarLetter}</div>
                <span class="user-name">${user.name}</span>
            </div>
        `;
    }).join('');

    let mainContent = '';
    if (isMobile) {
        if (App.mobileChatVisible && App.selectedChatUser) {
            mainContent = `<div class="chat-panel">${renderChatPanel()}</div>`;
        } else {
            mainContent = `
                <div class="users-panel">
                    <h3>Контакты</h3>
                    ${usersListHtml}
                </div>
            `;
        }
    } else {
        mainContent = `
            <div class="users-panel">
                <h3>Контакты</h3>
                ${usersListHtml}
            </div>
            <div class="chat-panel">
                ${App.selectedChatUser ? renderChatPanel() : '<div class="chat-placeholder">Выберите собеседника</div>'}
            </div>
        `;
    }

    const appEl = document.getElementById('app');
    appEl.innerHTML = `
        <div class="chat-interface">
            <div class="chat-header">
                <div class="user-info">
                    <i class="fas fa-user-circle"></i>
                    <span>${App.currentUser.name}</span>
                </div>
                <button class="logout-btn" id="logoutBtn">Выйти</button>
            </div>
            <div class="chat-main">
                ${mainContent}
            </div>
        </div>
    `;

    // Кнопка "назад" для мобильной версии (добавляется в шапку чата)
    if (isMobile && App.mobileChatVisible && App.selectedChatUser) {
        const chatWithBar = document.querySelector('.chat-panel .chat-with-bar');
        if (chatWithBar) {
            const backBtn = document.createElement('button');
            backBtn.innerHTML = '<i class="fas fa-arrow-left"></i>';
            backBtn.className = 'back-btn';
            backBtn.onclick = () => {
                App.mobileChatVisible = false;
                render();
            };
            chatWithBar.prepend(backBtn);
        }
    }

    // Обработчики на пользователей
    document.querySelectorAll('.user-item').forEach(el => {
        el.addEventListener('click', () => {
            const userId = el.dataset.userid;
            const userName = el.dataset.username;
            selectChatUser(userId, userName);
            if (window.innerWidth <= 700) {
                App.mobileChatVisible = true;
                render();
            }
        });
    });

    // Обработчики отправки сообщения
    if (App.selectedChatUser) {
        const messagesContainer = document.getElementById('messagesContainer');
        if (messagesContainer) messagesContainer.scrollTop = messagesContainer.scrollHeight;

        const input = document.getElementById('messageInput');
        if (input) {
            input.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') sendMessage();
            });
        }
        document.getElementById('sendBtn')?.addEventListener('click', sendMessage);
    }

    document.getElementById('logoutBtn')?.addEventListener('click', logout);
}

function renderChatPanel() {
    const chatUser = App.selectedChatUser;
    if (!chatUser) return '';

    const messagesHtml = App.messages.map(msg => {
        const isOwn = msg.senderId === App.currentUser.id;
        const time = new Date(msg.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
        return `
            <div class="message ${isOwn ? 'own' : 'other'}">
                <div>${msg.text}</div>
                <div class="message-info">
                    <span class="message-sender">${isOwn ? 'Вы' : msg.senderName}</span>
                    <span class="message-time">${time}</span>
                </div>
            </div>
        `;
    }).join('');

    return `
        <div class="chat-with-bar">
            Чат с ${chatUser.name}
        </div>
        <div class="messages-container" id="messagesContainer">
            ${messagesHtml}
        </div>
        <div class="message-input-area">
            <input type="text" id="messageInput" placeholder="Сообщение" autocomplete="off">
            <button id="sendBtn"><i class="fas fa-paper-plane"></i></button>
        </div>
    `;
}

// ========================== АВТОРИЗАЦИЯ ==========================
async function handleLogin() {
    const name = document.getElementById('loginName').value.trim();
    const pwd = document.getElementById('loginPassword').value.trim();
    const errorEl = document.getElementById('loginError');

    if (!name || !pwd) {
        errorEl.textContent = 'Заполните все поля';
        return;
    }

    try {
        const users = await fbGet('users') || {};
        const found = Object.entries(users).find(([id, u]) => u.name === name && u.password === pwd);
        if (found) {
            const [id, userData] = found;
            App.currentUser = { id, name: userData.name };
            localStorage.setItem('messenger_user', JSON.stringify(App.currentUser));
            await loadUsers();
            startPollingIfNeeded();
            render();
        } else {
            errorEl.textContent = 'Неверное имя или пароль';
        }
    } catch (e) {
        errorEl.textContent = 'Ошибка соединения';
    }
}

async function handleRegister() {
    const name = document.getElementById('loginName').value.trim();
    const pwd = document.getElementById('loginPassword').value.trim();
    const errorEl = document.getElementById('loginError');

    if (!name || !pwd) {
        errorEl.textContent = 'Заполните все поля';
        return;
    }

    try {
        const users = await fbGet('users') || {};
        const existing = Object.values(users).some(u => u.name === name);
        if (existing) {
            errorEl.textContent = 'Имя уже занято';
            return;
        }

        const newId = 'user_' + Date.now() + '_' + Math.random().toString(36).substr(2, 5);
        await fbPut(`users/${newId}`, { name, password: pwd });
        App.currentUser = { id: newId, name };
        localStorage.setItem('messenger_user', JSON.stringify(App.currentUser));
        await loadUsers();
        startPollingIfNeeded();
        render();
    } catch (e) {
        errorEl.textContent = 'Ошибка регистрации';
    }
}

function logout() {
    stopPolling();
    localStorage.removeItem('messenger_user');
    App.currentUser = null;
    App.selectedChatUser = null;
    App.users = [];
    App.messages = [];
    App.mobileChatVisible = false;
    render();
}

// ========================== ЗАГРУЗКА ПОЛЬЗОВАТЕЛЕЙ ==========================
async function loadUsers() {
    if (!App.currentUser) return;
    const users = await fbGet('users') || {};
    App.users = Object.entries(users)
        .filter(([id, u]) => id !== App.currentUser.id)
        .map(([id, u]) => ({ id, name: u.name }))
        .sort((a, b) => a.name.localeCompare(b.name));
}

// ========================== РАБОТА С ЧАТОМ ==========================
function selectChatUser(userId, userName) {
    stopPolling(); // остановить polling предыдущего чата
    App.selectedChatUser = { id: userId, name: userName };
    loadMessages();
    startPollingIfNeeded();
}

async function loadMessages() {
    if (!App.currentUser || !App.selectedChatUser) return;
    const chatId = getChatId(App.currentUser.id, App.selectedChatUser.id);
    const msgs = await fbGet(`messages/${chatId}`) || {};
    App.messages = Object.entries(msgs)
        .map(([msgId, msg]) => ({
            id: msgId,
            ...msg,
            senderName: msg.senderId === App.currentUser.id ? App.currentUser.name : App.selectedChatUser.name
        }))
        .sort((a, b) => a.timestamp - b.timestamp);
    render(); // обновим интерфейс
}

async function sendMessage() {
    const input = document.getElementById('messageInput');
    const text = input.value.trim();
    if (!text || !App.selectedChatUser) return;
    input.value = '';

    const chatId = getChatId(App.currentUser.id, App.selectedChatUser.id);
    const message = {
        text,
        senderId: App.currentUser.id,
        timestamp: Date.now()
    };
    try {
        await fbPost(`messages/${chatId}`, message);
        // сообщение появится при следующем polling
    } catch (e) {
        console.error('Send error', e);
    }
}

function getChatId(uid1, uid2) {
    return uid1 < uid2 ? `chat_${uid1}_${uid2}` : `chat_${uid2}_${uid1}`;
}

// ========================== POLLING НОВЫХ СООБЩЕНИЙ ==========================
function startPollingIfNeeded() {
    if (App.pollingInterval) clearInterval(App.pollingInterval);
    if (!App.selectedChatUser) return;

    App.pollingInterval = setInterval(async () => {
        if (!App.currentUser || !App.selectedChatUser) return;
        const chatId = getChatId(App.currentUser.id, App.selectedChatUser.id);
        const msgs = await fbGet(`messages/${chatId}`) || {};
        const messagesArray = Object.entries(msgs)
            .map(([msgId, msg]) => ({
                id: msgId,
                ...msg,
                senderName: msg.senderId === App.currentUser.id ? App.currentUser.name : App.selectedChatUser.name
            }))
            .sort((a, b) => a.timestamp - b.timestamp);

        // Если появились новые сообщения (сравниваем по количеству или последнему)
        if (JSON.stringify(App.messages.map(m => m.id)) !== JSON.stringify(messagesArray.map(m => m.id))) {
            App.messages = messagesArray;
            render();
        }
    }, 2000);
}

function stopPolling() {
    if (App.pollingInterval) {
        clearInterval(App.pollingInterval);
        App.pollingInterval = null;
    }
}

// ========================== ИНИЦИАЛИЗАЦИЯ И ВОССТАНОВЛЕНИЕ СЕССИИ ==========================
async function init() {
    const saved = localStorage.getItem('messenger_user');
    if (saved) {
        try {
            App.currentUser = JSON.parse(saved);
            await loadUsers();
            startPollingIfNeeded();
        } catch (e) {
            localStorage.removeItem('messenger_user');
        }
    }
    render();
}

// Слушаем изменение размера окна для адаптивного рендера
window.addEventListener('resize', () => {
    render();
});

// Запуск

init();
