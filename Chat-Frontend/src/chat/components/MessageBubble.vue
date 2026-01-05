<template>
  <!-- System Message -->
  <div v-if="message.isNotificationGroup" class="flex justify-center mb-4">
    <div class="bg-gray-100 dark:bg-gray-800 px-4 py-1 rounded-full">
      <p class="text-xs text-gray-600 dark:text-gray-400">{{ message.content }}</p>
    </div>
  </div>
  <div v-else :class="[
    'flex items-start gap-2 mb-4', !renderAvatar ? '-mt-3.5' : '',
    isMyMessage ? 'flex-row-reverse' : 'flex-row'
  ]">
    <div v-if="!isMyMessage && renderAvatar" class="flex-shrink-0">
      <UserPhoto src="/teste.jpg" :alt="chat.name" :online="chat.userOnline" size="w-8 h-8" :showOnlineStatus="false" />
    </div>
    <div v-else-if="!isMyMessage && !renderAvatar" class="flex-shrink-0">
      <div class="w-8 h-8 bg-transparent"></div>
    </div>

    <div :class="[
      'max-w-[80%] sm:max-w-[60%] md:max-w-[65%] relative group flex items-center', message.reactions !== null && message.reactions.length > 0 ? 'mb-3.5' : '',
      message.type === 'VIDEO' || message.type === 'IMAGE' ? 'lg:max-w-[45%] xl:max-w-[35%]' : 'lg:max-w-[50%]'
    ]">
      <div
        v-if="(isMyMessage && message.status !== 'DELETED_FOR_ALL' && message.status !== 'DELETED_FOR_ME') || (isMyMessage && message.status === 'DELETED_FOR_ME' && message.deleteById !== userService.authenticatedUser.id)"
        class="relative">
        <button @click="showDropdown = !showDropdown" :class="showDropdown ? '!opacity-100' : ''"
          class="opacity-0 group-hover:opacity-100 transition-opacity bg-white shadow-sm mr-3 w-9 h-9 rounded-full flex justify-center items-center cursor-pointer hover:bg-white/60 dark:bg-[#192939] hover:dark:bg-[#1e3144] duration-150">
          <EllipsisVerticalIcon class="w-6 h-6 text-gray-500 dark:text-gray-400" />
        </button>
        <MessageDropdown v-if="showDropdown" class="right-3" :isMyMessage="isMyMessage" @delete-for-all="onDeleteForAll"
          @delete-for-me="onDeleteForMe" @react="onReact" @reply="onReply" :message="message" />
      </div>

      <div
        :style="(isMyMessage && message.status !== 'DELETED_FOR_ALL' && message.status !== 'DELETED_FOR_ME') || (isMyMessage && message.status === 'DELETED_FOR_ME' && message.deleteById !== userService.authenticatedUser.id) ? 'width: calc(100% - 3rem)' : 'width: 100%'"
        :class="[
          'rounded-2xl px-4 py-2.5 relative',
          isMyMessage && !errorSending ? 'bg-primary text-white' : isMyMessage && errorSending ? 'bg-red-800 text-white' : 'bg-white dark:bg-gray-800 text-gray-900 dark:text-white border border-gray-200 dark:border-gray-700'
        ]">
        <ReplyMessageBubble v-if="message.messageReplied !== null" :isMyMessage="isMyMessage" :chat="chat"
          :message="message.messageReplied" />
        <div
          v-if="(message.messageReplied === null || (message.messageReplied !== null && message.messageReplied.senderId !== message.senderId)) && (chat.type === 'GROUP' && !isMyMessage) && renderAvatar"
          class="flex mb-1"> 
          <span :class="isMyMessage ? 'text-[#d8a9ff]' : 'text-gray-800 dark:text-white/80'" class="text-xs font-semibold truncate">{{
            message.senderName }}</span>
        </div>
        <div
          v-if="(message.type === 'VIDEO' || message.type === 'IMAGE') && message.status !== 'DELETED_FOR_ALL' && (message.status === null || (message.status === 'DELETED_FOR_ME' && message.deleteById !== userService.authenticatedUser.id))"
          class="group relative my-1" :class="message.type === 'IMAGE' ? 'max-h-80 h-auto overflow-hidden' : ''">
          <div v-if="errorSending || isUploading"
            class="absolute z-10 w-full h-full bg-gray-900/50 transition-opacity duration-300 rounded-lg flex items-center justify-center">
            <div class="absolute inset-0 flex items-center justify-center">
              <!-- Exibe o progresso do upload -->
              <div v-if="!errorSending && isUploading" class="relative">
                <svg class="w-15 h-15 text-white/70 animate-spin" viewBox="0 0 40 40">
                  <circle class="opacity-50 transition-all" cx="20" cy="20" r="18" stroke="currentColor"
                    stroke-width="2" fill="none" />
                  <circle class="text-primary transition-all" cx="20" cy="20" r="18" stroke="currentColor"
                    stroke-width="2" fill="none" stroke-linecap="round" stroke-dasharray="113"
                    :stroke-dashoffset="computedDashoffset"
                    style="transition: stroke-dashoffset 0.5s cubic-bezier(0.4,0,0.2,1);" />
                </svg>
                <button
                  class="absolute inset-0 flex items-center justify-center cursor-pointer text-white hover:text-red-400 focus:outline-none"
                  @click="cancelUpload && cancelUpload(uploadId)" title="Cancelar upload">
                  <svg class="w-6 h-6" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>
              <!-- Enviar Mídia -->
              <div v-if="errorSending && !isUploading" class="relative">
                <button title="Enviar novamente" @click="onSendMessage"
                  class="inline-flex items-center justify-center cursor-pointer rounded-full h-15 w-15 bg-white/30 hover:bg-white/50 focus:ring-4 focus:outline-none dark:text-white focus:ring-gray-50">
                  <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor"
                    class="bi bi-cloud-upload w-7 h-7 text-white" viewBox="0 0 16 16">
                    <path fill-rule="evenodd"
                      d="M4.406 1.342A5.53 5.53 0 0 1 8 0c2.69 0 4.923 2 5.166 4.579C14.758 4.804 16 6.137 16 7.773 16 9.569 14.502 11 12.687 11H10a.5.5 0 0 1 0-1h2.688C13.979 10 15 8.988 15 7.773c0-1.216-1.02-2.228-2.313-2.228h-.5v-.5C12.188 2.825 10.328 1 8 1a4.53 4.53 0 0 0-2.941 1.1c-.757.652-1.153 1.438-1.153 2.055v.448l-.445.049C2.064 4.805 1 5.952 1 7.318 1 8.785 2.23 10 3.781 10H6a.5.5 0 0 1 0 1H3.781C1.708 11 0 9.366 0 7.318c0-1.763 1.266-3.223 2.942-3.593.143-.863.698-1.723 1.464-2.383" />
                    <path fill-rule="evenodd"
                      d="M7.646 4.146a.5.5 0 0 1 .708 0l3 3a.5.5 0 0 1-.708.708L8.5 5.707V14.5a.5.5 0 0 1-1 0V5.707L5.354 7.854a.5.5 0 1 1-.708-.708z" />
                  </svg>
                </button>
              </div>
            </div>
          </div>
          <div v-if="!errorSending && !isUploading"
            class="absolute w-full h-full bg-gray-900/50 opacity-0 cursor-pointer group-hover:opacity-100 transition-opacity duration-300 rounded-lg flex items-center justify-center">

          </div>
          <img v-if="message.type === 'IMAGE'" :src="message.mediaUrl" class="rounded-lg w-full h-full object-cover"
            @load="$emit('mediaLoaded')" />
          <video v-if="message.type === 'VIDEO'" :src="message.mediaUrl" controls></video>
        </div>
        <!-- Anexo de Arquivo -->
        <div
          v-if="message.type === 'DOCUMENT' && message.status !== 'DELETED_FOR_ALL' && (message.status === null || (message.status === 'DELETED_FOR_ME' && message.deleteById !== userService.authenticatedUser.id))"
          class="flex items-start justify-between rounded-xl p-2 mb-1"
          :class="[{ 'bg-[#ab9cff]': isMyMessage, 'bg-slate-100 dark:bg-[#233950]': !isMyMessage }]">
          <div class="me-2">
            <span class="flex items-center gap-2 text-sm font-medium pb-2"
              :class="[{ 'text-gray-800': isMyMessage, 'text-white-50': !isMyMessage }]">
              <svg v-if="message.mediaExtension.toLowerCase() === 'pdf'" fill="none" aria-hidden="true"
                class="w-5 h-5 shrink-0" viewBox="0 0 20 21">
                <g clip-path="url(#clip0_3173_1381)">
                  <path fill="#E2E5E7"
                    d="M5.024.5c-.688 0-1.25.563-1.25 1.25v17.5c0 .688.562 1.25 1.25 1.25h12.5c.687 0 1.25-.563 1.25-1.25V5.5l-5-5h-8.75z" />
                  <path fill="#B0B7BD" d="M15.024 5.5h3.75l-5-5v3.75c0 .688.562 1.25 1.25 1.25z" />
                  <path fill="#CAD1D8" d="M18.774 9.25l-3.75-3.75h3.75v3.75z" />
                  <path fill="#F15642"
                    d="M16.274 16.75a.627.627 0 01-.625.625H1.899a.627.627 0 01-.625-.625V10.5c0-.344.281-.625.625-.625h13.75c.344 0 .625.281.625.625v6.25z" />
                  <path fill="#fff"
                    d="M3.998 12.342c0-.165.13-.345.34-.345h1.154c.65 0 1.235.435 1.235 1.269 0 .79-.585 1.23-1.235 1.23h-.834v.66c0 .22-.14.344-.32.344a.337.337 0 01-.34-.344v-2.814zm.66.284v1.245h.834c.335 0 .6-.295.6-.605 0-.35-.265-.64-.6-.64h-.834zM7.706 15.5c-.165 0-.345-.09-.345-.31v-2.838c0-.18.18-.31.345-.31H8.85c2.284 0 2.234 3.458.045 3.458h-1.19zm.315-2.848v2.239h.83c1.349 0 1.409-2.24 0-2.24h-.83zM11.894 13.486h1.274c.18 0 .36.18.36.355 0 .165-.18.3-.36.3h-1.274v1.049c0 .175-.124.31-.3.31-.22 0-.354-.135-.354-.31v-2.839c0-.18.135-.31.355-.31h1.754c.22 0 .35.13.35.31 0 .16-.13.34-.35.34h-1.455v.795z" />
                  <path fill="#CAD1D8"
                    d="M15.649 17.375H3.774V18h11.875a.627.627 0 00.625-.625v-.625a.627.627 0 01-.625.625z" />
                </g>
                <defs>
                  <clipPath id="clip0_3173_1381">
                    <path fill="#fff" d="M0 0h20v20H0z" transform="translate(0 .5)" />
                  </clipPath>
                </defs>
              </svg>
              <svg v-else xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512" xml:space="preserve"
                class="w-5 h-5 shrink-0">
                <path fill="#6E83B7" d="M439.893 502H101.964V40h338z" />
                <path fill="#EDEFF1" d="M410 472H72V10h258l80 80z" />
                <path fill="#D3D3D3" d="M104 400h274v40H104zM104 360h180v20H104zM104 320h180v20H104zM330 10v80h80z" />
              </svg>
              {{ message.mediaName }}
            </span>
            <span class="flex text-xs font-normal gap-2 pl-1.5"
              :class="[{ 'text-gray-600': isMyMessage, 'text-gray-500 dark:text-gray-400': !isMyMessage }]">
              {{ calculateFileSize(message.mediaSize) }}
              <svg xmlns="http://www.w3.org/2000/svg" aria-hidden="true" class="self-center" width="3" height="4"
                viewBox="0 0 3 4" fill="none">
                <circle cx="1.5" cy="2" r="1.5" fill="#6B7280" />
              </svg>
              {{ message.mediaExtension.toUpperCase() }}
            </span>
          </div>
          <div class="inline-flex self-center items-center ml-2">
            <div v-if="errorSending && !isUploading" class="relative">
              <button title="Enviar novamente" @click="onSendMessage"
                class="inline-flex items-center justify-center cursor-pointer rounded-full h-8 w-8 bg-white/30 hover:bg-white/50 focus:ring-4 focus:outline-none dark:text-white focus:ring-gray-50">
                <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor"
                  class="bi bi-cloud-upload w-4 h-4 text-white" viewBox="0 0 16 16">
                  <path fill-rule="evenodd"
                    d="M4.406 1.342A5.53 5.53 0 0 1 8 0c2.69 0 4.923 2 5.166 4.579C14.758 4.804 16 6.137 16 7.773 16 9.569 14.502 11 12.687 11H10a.5.5 0 0 1 0-1h2.688C13.979 10 15 8.988 15 7.773c0-1.216-1.02-2.228-2.313-2.228h-.5v-.5C12.188 2.825 10.328 1 8 1a4.53 4.53 0 0 0-2.941 1.1c-.757.652-1.153 1.438-1.153 2.055v.448l-.445.049C2.064 4.805 1 5.952 1 7.318 1 8.785 2.23 10 3.781 10H6a.5.5 0 0 1 0 1H3.781C1.708 11 0 9.366 0 7.318c0-1.763 1.266-3.223 2.942-3.593.143-.863.698-1.723 1.464-2.383" />
                  <path fill-rule="evenodd"
                    d="M7.646 4.146a.5.5 0 0 1 .708 0l3 3a.5.5 0 0 1-.708.708L8.5 5.707V14.5a.5.5 0 0 1-1 0V5.707L5.354 7.854a.5.5 0 1 1-.708-.708z" />
                </svg>
              </button>
            </div>
            <div v-if="!errorSending && isUploading" class="relative">
              <svg class="w-8 h-8 text-white/70 animate-spin" viewBox="0 0 40 40">
                <circle class="opacity-50 transition-all" cx="20" cy="20" r="18" stroke="currentColor" stroke-width="2"
                  fill="none" />
                <circle class="text-primary transition-all" cx="20" cy="20" r="18" stroke="currentColor"
                  stroke-width="2" fill="none" stroke-linecap="round" stroke-dasharray="113"
                  :stroke-dashoffset="computedDashoffset"
                  style="transition: stroke-dashoffset 0.5s cubic-bezier(0.4,0,0.2,1);" />
              </svg>
              <button
                class="absolute inset-0 flex items-center justify-center cursor-pointer text-white hover:text-red-400 focus:outline-none"
                @click="cancelUpload && cancelUpload(uploadId)" title="Cancelar upload">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
            <button v-if="!errorSending && !isUploading" @click="downloadMedia"
              class="inline-flex self-center items-center p-2 cursor-pointer text-sm font-medium text-center rounded-lg focus:ring-4 focus:outline-none "
              type="button"
              :class="[{ 'text-gray-900 bg-gray-100 hover:bg-gray-200 focus:ring-gray-100': isMyMessage, 'bg-slate-200 hover:bg-slate-300 dark:text-white dark:bg-gray-600 dark:hover:bg-gray-500 dark:focus:ring-gray-600': !isMyMessage }]">
              <svg :class="[{ 'text-gray-900': isMyMessage, 'text-gray-900 dark:text-white': !isMyMessage }]"
                class="w-4 h-4" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="currentColor"
                viewBox="0 0 20 20">
                <path
                  d="M14.707 7.793a1 1 0 0 0-1.414 0L11 10.086V1.5a1 1 0 0 0-2 0v8.586L6.707 7.793a1 1 0 1 0-1.414 1.414l4 4a1 1 0 0 0 1.416 0l4-4a1 1 0 0 0-.002-1.414Z" />
                <path
                  d="M18 12h-2.55l-2.975 2.975a3.5 3.5 0 0 1-4.95 0L4.55 12H2a2 2 0 0 0-2 2v4a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-4a2 2 0 0 0-2-2Zm-3 5a1 1 0 1 1 0-2 1 1 0 0 1 0 2Z" />
              </svg>
            </button>
          </div>
        </div>
        <!-- Galeria de Imagens -->
        <div v-if="false" class="grid gap-4 grid-cols-2 my-2.5">
          <div class="group relative">
            <div
              class="absolute w-full h-full bg-gray-900/50 opacity-0 group-hover:opacity-100 transition-opacity duration-300 rounded-lg flex items-center justify-center">
              <button data-tooltip-target="download-image-1"
                class="inline-flex items-center justify-center rounded-full h-8 w-8 bg-white/30 hover:bg-white/50 focus:ring-4 focus:outline-none dark:text-white focus:ring-gray-50">
                <svg class="w-4 h-4 text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none"
                  viewBox="0 0 16 18">
                  <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M8 1v11m0 0 4-4m-4 4L4 8m11 4v3a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2v-3" />
                </svg>
              </button>
              <div id="download-image-1" role="tooltip"
                class="absolute z-10 invisible inline-block px-3 py-2 text-sm font-medium text-white transition-opacity duration-300 bg-gray-900 rounded-lg shadow-xs opacity-0 tooltip dark:bg-gray-700">
                Download image
                <div class="tooltip-arrow" data-popper-arrow></div>
              </div>
            </div>
            <img src="" class="rounded-lg" />
          </div>
          <div class="group relative">
            <div
              class="absolute w-full h-full bg-gray-900/50 opacity-0 group-hover:opacity-100 transition-opacity duration-300 rounded-lg flex items-center justify-center">
              <button data-tooltip-target="download-image-2"
                class="inline-flex items-center justify-center rounded-full h-8 w-8 bg-white/30 hover:bg-white/50 focus:ring-4 focus:outline-none dark:text-white focus:ring-gray-50">
                <svg class="w-4 h-4 text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none"
                  viewBox="0 0 16 18">
                  <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M8 1v11m0 0 4-4m-4 4L4 8m11 4v3a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2v-3" />
                </svg>
              </button>
              <div id="download-image-2" role="tooltip"
                class="absolute z-10 invisible inline-block px-3 py-2 text-sm font-medium text-white transition-opacity duration-300 bg-gray-900 rounded-lg shadow-xs opacity-0 tooltip dark:bg-gray-700">
                Download image
                <div class="tooltip-arrow" data-popper-arrow></div>
              </div>
            </div>
            <img src="" class="rounded-lg" />
          </div>
          <div class="group relative">
            <div
              class="absolute w-full h-full bg-gray-900/50 opacity-0 group-hover:opacity-100 transition-opacity duration-300 rounded-lg flex items-center justify-center">
              <button data-tooltip-target="download-image-3"
                class="inline-flex items-center justify-center rounded-full h-8 w-8 bg-white/30 hover:bg-white/50 focus:ring-4 focus:outline-none dark:text-white focus:ring-gray-50">
                <svg class="w-4 h-4 text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none"
                  viewBox="0 0 16 18">
                  <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M8 1v11m0 0 4-4m-4 4L4 8m11 4v3a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2v-3" />
                </svg>
              </button>
              <div id="download-image-3" role="tooltip"
                class="absolute z-10 invisible inline-block px-3 py-2 text-sm font-medium text-white transition-opacity duration-300 bg-gray-900 rounded-lg shadow-xs opacity-0 tooltip dark:bg-gray-700">
                Download image
                <div class="tooltip-arrow" data-popper-arrow></div>
              </div>
            </div>
            <img src="" class="rounded-lg" />
          </div>
          <div class="group relative">
            <button
              class="absolute w-full h-full bg-gray-900/90 hover:bg-gray-900/50 transition-all duration-300 rounded-lg flex items-center justify-center">
              <span class="text-xl font-medium text-white">+7</span>
              <div id="download-image" role="tooltip"
                class="absolute z-10 invisible inline-block px-3 py-2 text-sm font-medium text-white transition-opacity duration-300 bg-gray-900 rounded-lg shadow-xs opacity-0 tooltip dark:bg-gray-700">
                Download image
                <div class="tooltip-arrow" data-popper-arrow></div>
              </div>
            </button>
            <img src="" class="rounded-lg" />
          </div>
        </div>

        <div v-if="message.content !== null && message.content !== 'undefined'" class="text-[.92rem]">
          <p v-if="message.status !== 'DELETED_FOR_ALL' && (message.status === null || (message.status === 'DELETED_FOR_ME' && message.deleteById !== userService.authenticatedUser.id))"
            class="whitespace-break-spaces">{{ message.content }}</p>
          <p v-if="message.status === 'DELETED_FOR_ALL' || (message.status === 'DELETED_FOR_ME' && message.deleteById === userService.authenticatedUser.id)"
            class="flex items-center gap-1 text-sm dark:text-gray-400 "
            :class="isMyMessage ? 'text-white' : 'text-gray-500'">
            <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" class="bi bi-ban w-4 h-4" viewBox="0 0 16 16">
              <path
                d="M15 8a6.97 6.97 0 0 0-1.71-4.584l-9.874 9.875A7 7 0 0 0 15 8M2.71 12.584l9.874-9.875a7 7 0 0 0-9.874 9.874ZM16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0" />
            </svg>
            <span>Apagou esta mensagem</span>
          </p>
        </div>
        <div class="flex items-center justify-end gap-1 mt-1">
          <span v-if="errorSending" class="text-xs text-white/90 ">Mensagem não enviada</span>
          <span v-if="!errorSending" :class="!isMyMessage ? 'dark:!text-white/60 !text-gray-400' : ''"
            class="text-xs text-white/80 ">{{ formattedTime }}</span>
          <div
            v-if="isMyMessage && !errorSending && !isUploading && message.status !== 'DELETED_FOR_ALL' && message.status !== 'DELETED_FOR_ME'"
            class="flex">
            <span v-if="message.state === 'SENT'" class="text-xs text-white/80">Enviada</span>
            <span v-if="message.state === 'RECEIVED'" class="text-xs text-white/80">Recebida</span>
            <span v-if="message.state === 'SEEN'" class="text-xs text-white/80">Visualizada</span>
          </div>
        </div>
        <button
          v-if="(message.reactions !== null && message.reactions.length > 0) && (message.status !== 'DELETED_FOR_ALL' && (message.status === null || (message.status === 'DELETED_FOR_ME' && message.deleteById !== userService.authenticatedUser.id)))"
          class="w-6 h-6 rounded-full flex cursor-pointer justify-center items-center bg-white shadow absolute -bottom-3.5 left-1 -mt-2 -mr-2 text-gray-500 dark:text-gray-400">
          <template v-for="(reaction, index) in message.reactions" :key="index">
            <span v-if="reaction.type === 'LIKE'">👍</span>
            <span v-if="reaction.type === 'LOVE'">❤️</span>
            <span v-if="reaction.type === 'HAHA'">😂</span>
            <span v-if="reaction.type === 'WOW'">😲</span>
            <span v-if="reaction.type === 'SAD'">😓</span>
            <span v-if="reaction.type === 'ANGRY'">😡</span>
          </template>
        </button>
      </div>

      <div
        v-if="!isMyMessage && message.status !== 'DELETED_FOR_ALL' && (message.status === null || (message.status === 'DELETED_FOR_ME' && message.deleteById !== userService.authenticatedUser.id))"
        class="relative">
        <button @click="showDropdown = !showDropdown" :class="showDropdown ? '!opacity-100' : ''"
          class="opacity-0 group-hover:opacity-100 transition-opacity bg-white shadow-sm ml-3 w-9 h-9 rounded-full flex justify-center items-center cursor-pointer hover:bg-white/60 dark:bg-[#192939] hover:dark:bg-[#1e3144] duration-150">
          <EllipsisVerticalIcon class="w-6 h-6 text-gray-500 dark:text-gray-400" />
        </button>
        <MessageDropdown v-if="showDropdown" class="left-3" :isMyMessage="isMyMessage" @delete-for-all="onDeleteForAll"
          @delete-for-me="onDeleteForMe" @react="onReact" @reply="onReply" :message="message" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { format } from 'date-fns';
import { EllipsisVerticalIcon, ArrowUturnLeftIcon } from '@heroicons/vue/24/outline';
import { useChatStore } from '@/stores/chatStore';
import { UserService } from '@/services/user/UserService';
import { sendMediaFile, sendMessage } from '@/services/api/messageApi';
import { useRoute } from 'vue-router';
import { calculateFileSize, server } from '@/services/util';
import MessageDropdown from './MessageDropdown.vue';
import ReplyMessageBubble from './ReplyMessageBubble.vue';
import UserPhoto from '@/components/UserPhoto.vue';

const emit = defineEmits(['mediaLoaded', 'cancelUpload', 'deleteForAll', 'deleteForMe', 'react', 'reply']);
const props = defineProps({
  message: {
    type: Object,
    required: true
  },
  chat: Object,
  send: {
    type: Boolean,
    default: false
  },
  renderAvatar: {
    type: Boolean,
    default: true
  }
});

const isForSend = ref(JSON.parse(JSON.stringify(props.send)));
const isUploading = ref(false);
const errorSending = ref(false);
const userService = new UserService();
const route = useRoute();
const chatStore = useChatStore();
const showDropdown = ref(false);

const isEditing = ref(false);
const uploadProgress = ref(10);

const isMyMessage = computed(() => props.message.senderId === userService.authenticatedUser.id);

const formattedTime = computed(() => {
  return format(new Date(props.message.createdAt), 'HH:mm');
});

const onUploadProgress = (event) => {
  uploadProgress.value = Math.round((event.loaded * 100) / event.total);
};

const computedDashoffset = computed(() => {
  const circumference = 113; // Circunferência do círculo SVG
  return circumference - (uploadProgress.value / 100) * circumference;
});

const downloadMedia = async () => {
  try {
    const response = await fetch(props.message.mediaUrl, {
      method: 'GET'
    })

    if (!response.ok) {
      alert('Erro ao baixar o arquivo. Por favor, tente novamente.');
    } else {
      const blob = await response.blob()
      const downloadUrl = URL.createObjectURL(blob)

      // Cria um link temporário para o download
      const a = document.createElement('a')
      a.href = downloadUrl
      a.target = '_blank'
      a.download = `${props.message.mediaName}` // Nome do arquivo a ser baixado
      document.body.appendChild(a)
      a.click()
      a.remove()

      // Libera o URL após o download
      URL.revokeObjectURL(downloadUrl)
    }
  } catch (error) {
    alert('Erro ao baixar o arquivo. Por favor, tente novamente.');
  }
}

const onSendMessage = () => {
  if (isForSend.value) {
    const chatId = route.params.chatId;

    if (props.message.type !== 'TEXT') {
      isUploading.value = true;
      errorSending.value = false;

      const formData = new FormData();
      formData.append('mediaFile', props.message.file);
      formData.append('content', props.message.content);
      formData.append('type', props.message.type);
      formData.append('senderId', userService.authenticatedUser.id);
      formData.append('receiverId', props.message.receiverId);
      formData.append('chatId', chatId);
      formData.append('receiverEmail', props.message.receiverEmail);
      formData.append('senderEmail', props.message.senderEmail);
      formData.append('isReplying', props.message.reply);
      formData.append('messageRepliedId', props.message.reply ? props.message.messageReplied.id : null);

      sendMediaFile(chatId, formData, onUploadProgress).then((response) => {
        if (response.status === 201) {
          isUploading.value = false;
          isForSend.value = false;
          errorSending.value = false;

          if (!chatStore.chatExists(chatId)) {
            const chat = chatStore.lastActiveChat
            chat.messages[chat.messages.length - 1].send = false
            chat.messages[chat.messages.length - 1].id = response.data
            chat.lastMessageId = response.data;
          }
          else {
            const chat = chatStore.lastActiveChat
            chat.messages[chat.messages.length - 1].send = false
            chatStore.chats.find(chat => chat.id === chatId).messages[chatStore.chats.find(chat => chat.id === chatId).messages.length - 1].send = false
            chatStore.chats.find(chat => chat.id === chatId).messages[chatStore.chats.find(chat => chat.id === chatId).messages.length - 1].id = response.data
            chatStore.chats.find(chat => chat.id === chatId).lastMessageId = response.data;
          }
        }
      }).catch(error => {
        errorSending.value = true;
        isUploading.value = false;
        uploadProgress.value = 0;
        console.error('Error sending message:', error);
      });
    }
    else {
      const message = {
        chatId: chatId,
        content: props.message.content,
        type: props.message.type,
        senderId: props.message.senderId,
        receiverId: props.message.receiverId,
        receiverEmail: props.message.receiverEmail,
        senderEmail: props.message.senderEmail,
        isReplying: props.message.reply,
        messageRepliedId: props.message.reply ? props.message.messageReplied.id : null
      };

      sendMessage(message).then((response) => {
        if (response.status === 201) {
          isForSend.value = false;
          errorSending.value = false;
          if (!chatStore.chatExists(chatId)) {
            const chat = chatStore.lastActiveChat
            chat.messages[chat.messages.length - 1].send = false
            chat.messages[chat.messages.length - 1].id = response.data
            chat.lastMessageId = response.data;
          }
          else {
            const chat = chatStore.lastActiveChat
            chat.messages[chat.messages.length - 1].send = false
            chatStore.chats.find(chat => chat.id === chatId).messages[chatStore.chats.find(chat => chat.id === chatId).messages.length - 1].send = false
            chatStore.chats.find(chat => chat.id === chatId).messages[chatStore.chats.find(chat => chat.id === chatId).messages.length - 1].id = response.data
            chatStore.chats.find(chat => chat.id === chatId).lastMessageId = response.data;
          }
        }
      }).catch(error => {
        errorSending.value = true;
        console.error('Error sending message:', error);
      });
    }
  }
};

const onDeleteForAll = () => {
  showDropdown.value = false;
  emit('deleteForAll', props.message.id);
};

const onReply = () => {
  showDropdown.value = false;
  emit('reply', props.message.id);
};

const onDeleteForMe = () => {
  showDropdown.value = false;
  emit('deleteForMe', props.message.id);
};

const onReact = (reactType) => {
  showDropdown.value = false;
  emit('react', {
    messageId: props.message.id,
    type: reactType
  });
};

onMounted(() => {
  window.addEventListener("click", (event) => {
    if (!event.target.closest(".relative")) {
      showDropdown.value = false;
    }
  })

  onSendMessage();
});

onUnmounted(() => {

});
</script>