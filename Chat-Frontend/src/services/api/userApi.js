import instance from "../axios";

export async function getUsers() {
    return await instance.get("/users");  
}

export async function getUsersByName(name) {
    return await instance.get(`/users/search?name=${name}`);  
}