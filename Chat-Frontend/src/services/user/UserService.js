export class UserService {
    set authenticatedUser(user) {
        localStorage.setItem("chat_authenticated_user", JSON.stringify(user));
    }

    get authenticatedUser() {
        if (localStorage.getItem("chat_authenticated_user")) {
        return JSON.parse(localStorage.getItem("chat_authenticated_user"));
        } else {
        return null;
        }
    }

    removeAuthenticatedUser() {
        localStorage.removeItem("chat_authenticated_user");
    }
}
