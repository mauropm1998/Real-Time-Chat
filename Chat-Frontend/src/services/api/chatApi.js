import instance from "../axios";

export async function getMyChats() {
    return await instance.get("/chats");  
}

export async function getChatById(chatId) {
    return await instance.get(`/chats/${chatId}`);  
}

export async function saveChat(senderId, receiverId) {
    return await instance.post(`/chats?senderId=${senderId}&receiverId=${receiverId}`);
}

export async function saveGroup(formData) {
    return await instance.post('/chats/group', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}

export async function updatePicture(formData) {
    return await instance.post('/chats/group/update-picture', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}

export async function updateGroupName(data) {
    return await instance.post('/chats/group/update-name', data);
}

export async function updateGroupDescription(data) {
    return await instance.post('/chats/group/update-description', data);
}

export async function removeGroupMember(data) {
    return await instance.post('/chats/group/members/remove', data);
}

export async function addGroupMember(data) {
    return await instance.post('/chats/group/members/add', data);
}