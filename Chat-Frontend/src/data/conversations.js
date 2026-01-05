// Mock data for conversations
const conversationsData = [
  {
    id: '1',
    user: {
      id: 'user1',
      name: 'Dawn Sabrina',
      avatar: 'https://images.pexels.com/photos/415829/pexels-photo-415829.jpeg?auto=compress&cs=tinysrgb&w=600',
      lastSeen: 'Last seen Dec 16, 2019'
    },
    lastMessage: {
      content: 'Wow really cool',
      timestamp: new Date('2023-10-10T16:00:00')
    },
    unread: true,
    messages: [
      {
        id: 'msg1',
        content: 'So i found this great video online',
        timestamp: new Date('2023-10-10T13:00:00'),
        sender: 'user1',
        status: 'read'
      },
      {
        id: 'msg2',
        content: 'it\'s about stopping drugs',
        timestamp: new Date('2023-10-10T13:00:00'),
        sender: 'user1',
        status: 'read'
      },
      {
        id: 'msg3',
        content: 'lecture-10.mp3',
        timestamp: new Date('2023-10-10T13:00:00'),
        sender: 'user1',
        type: 'file',
        fileSize: '52.1 MB',
        status: 'read'
      },
      {
        id: 'msg4',
        content: 'can you send me a link',
        timestamp: new Date('2023-10-10T08:15:00'),
        sender: 'me',
        status: 'read'
      },
      {
        id: 'msg5',
        content: 'Here\'s the link you asked for',
        timestamp: new Date('2023-10-10T08:20:00'),
        sender: 'user1',
        status: 'read'
      },
      {
        id: 'msg6',
        content: 'Thanks! This is exactly what I needed',
        timestamp: new Date('2023-10-10T08:25:00'),
        sender: 'me',
        status: 'read',
        replyTo: {
          id: 'msg5',
          content: 'Here\'s the link you asked for'
        }
      }
    ]
  },
  {
    id: '2',
    user: {
      id: 'user2',
      name: 'Dillan Robbie',
      avatar: 'https://images.pexels.com/photos/2379004/pexels-photo-2379004.jpeg?auto=compress&cs=tinysrgb&w=600',
      lastSeen: 'Online'
    },
    lastMessage: {
      content: 'Wow really cool',
      timestamp: new Date('2023-10-10T16:00:00')
    },
    unread: false,
    messages: [
      {
        id: 'msg1',
        content: 'Hey there!',
        timestamp: new Date('2023-10-10T15:45:00'),
        sender: 'user2',
        status: 'read'
      },
      {
        id: 'msg2',
        content: 'Wow really cool',
        timestamp: new Date('2023-10-10T16:00:00'),
        sender: 'user2',
        status: 'read'
      }
    ]
  },
  {
    id: '3',
    user: {
      id: 'user3',
      name: 'Devon Felix',
      avatar: 'https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&w=600',
      lastSeen: 'Last seen 3h ago'
    },
    lastMessage: {
      content: 'typing...',
      timestamp: new Date('2023-10-10T16:00:00'),
      isTyping: true
    },
    unread: false,
    messages: [
      {
        id: 'msg1',
        content: 'Hello!',
        timestamp: new Date('2023-10-10T15:30:00'),
        sender: 'me',
        status: 'read'
      }
    ]
  },
  {
    id: '4',
    user: {
      id: 'user4',
      name: 'Dylan Billy',
      avatar: 'https://images.pexels.com/photos/1516680/pexels-photo-1516680.jpeg?auto=compress&cs=tinysrgb&w=600',
      lastSeen: 'Last seen 1h ago'
    },
    lastMessage: {
      content: 'Wow really cool',
      timestamp: new Date('2023-10-10T16:00:00')
    },
    unread: true,
    messages: [
      {
        id: 'msg1',
        content: 'Hey there!',
        timestamp: new Date('2023-10-10T15:45:00'),
        sender: 'user4',
        status: 'read'
      },
      {
        id: 'msg2',
        content: 'Wow really cool',
        timestamp: new Date('2023-10-10T16:00:00'),
        sender: 'user4',
        status: 'read'
      }
    ]
  },
  {
    id: '5',
    user: {
      id: 'user5',
      name: 'Lillian Lucia',
      avatar: 'https://images.pexels.com/photos/774909/pexels-photo-774909.jpeg?auto=compress&cs=tinysrgb&w=600',
      lastSeen: 'Online'
    },
    lastMessage: {
      content: 'Wow really cool',
      timestamp: new Date('2023-10-10T16:00:00')
    },
    unread: false,
    messages: [
      {
        id: 'msg1',
        content: 'Wow really cool',
        timestamp: new Date('2023-10-10T16:00:00'),
        sender: 'user5',
        status: 'read'
      }
    ]
  },
  {
    id: '6',
    user: {
      id: 'user6',
      name: 'Dylan Billy',
      avatar: 'https://images.pexels.com/photos/1516680/pexels-photo-1516680.jpeg?auto=compress&cs=tinysrgb&w=600',
      lastSeen: 'Last seen 5m ago'
    },
    lastMessage: {
      content: 'typing...',
      timestamp: new Date('2023-10-10T16:00:00'),
      isTyping: true
    },
    unread: false,
    messages: [
      {
        id: 'msg1',
        content: 'How are you doing?',
        timestamp: new Date('2023-10-10T15:45:00'),
        sender: 'me',
        status: 'read'
      }
    ]
  },
  {
    id: '7',
    user: {
      id: 'user7',
      name: 'Deanna Layla',
      avatar: 'https://images.pexels.com/photos/1239291/pexels-photo-1239291.jpeg?auto=compress&cs=tinysrgb&w=600',
      lastSeen: 'Last seen yesterday'
    },
    lastMessage: {
      content: 'Wow really cool',
      timestamp: new Date('2023-10-10T16:00:00')
    },
    unread: false,
    messages: [
      {
        id: 'msg1',
        content: 'Hey there!',
        timestamp: new Date('2023-10-10T15:45:00'),
        sender: 'user7',
        status: 'read'
      },
      {
        id: 'msg2',
        content: 'Wow really cool',
        timestamp: new Date('2023-10-10T16:00:00'),
        sender: 'user7',
        status: 'read'
      }
    ]
  }
];

export { conversationsData }