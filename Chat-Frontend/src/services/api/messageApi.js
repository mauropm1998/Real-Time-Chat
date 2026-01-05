import instance from "../axios";

export async function sendMessage(message) {
    return await instance.post("/messages", message);  
}

export async function sendMediaFile(chatId, formData, callback) {
  return await instance.post(`/messages/upload-media/${chatId}`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
    onUploadProgress: callback,
  })
}

export async function deleteOneMessage(request) {
  return await instance.post(`/messages/delete`, request)
}

export async function reactOneMessage(request) {
  return await instance.post(`/messages/react`, request)
}