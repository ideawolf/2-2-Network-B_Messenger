### Interface (Request and Response Format)

#### List of access-token for test
1. 00000000-0000-0000-0000-000000000001
2. 00000000-0000-0000-0000-000000000002
3. 00000000-0000-0000-0000-000000000003


#### Register
- request : {"command":"REGISTER", "id":"user id", "name":"NAME", "password":"PASSWORD", "nickname":"NICKNAME", "email":"
  Example@email.com"}
- response :
  - Success: {"body":"Register Success","status":200}
  - Fail: {"body":"Register Failed","status":400}


#### Login
- request : {"command":"LOGIN", "id":"user id", "password":"PASSWORD"}
- response :
  - Success: {"body":"Login Success","status":200, "access-token": "계정 토큰값"}
  - Fail: {"body":"Login Failed","status":400}
  - 로그인 이후 BufferedReader in 에 담긴 리더 그대로 사용해서 접속유지


#### Get Friends List
- request :  {"command":"GET_FRIENDS", "access-token":"access-token"}
- response
  - Success: { "body":[{"user_id":"test_user_2","nickname":"test_user_2","email":"test_user_2@email.com"},{"user_id":"test_user_3","nickname":"test_user_3","email":"test_user_3@email.com"}],"status":200 }
  - Fail: TODO

#### Get Chat List
- request :  {"command":"GET_USER_ROOM", "access-token":"access-token"}
- response : {"body":{"1":[{"user_id":"test_user_1","nickname":"test_user_1","email":"test_user_1@email.com"},{"user_id":"test_user_2","nickname":"test_user_2","email":"test_user_2@email.com"}],"2":[{"user_id":"test_user_1","nickname":"test_user_1","email":"test_user_1@email.com"},{"user_id":"test_user_3","nickname":"test_user_3","email":"test_user_3@email.com"}]},"status":200}

#### Get All USERID
- request : No need
- response: {"body":[null,null,null,"userId1","userId1","userId1","test_name","test_user_1","test_user_2","test_user_3","test_id","test_id","test_id","test_id","test_id","dds","sd","sd","as"],"status":200}

#### Create Room
- request : {"userlist":["test_user_2","test_user_3"],"access-token":"00000000-0000-0000-0000-000000000001","command":"CREATE_ROOM"}
- response :
  1. 만든 사람 : {"body":"Ok","status":200} 
  2. 초대 받은 사람 : {"room_id":0,"body":"you are invited in 0","command":"invited"}

#### Send a message
- request : {"room_id":8,"msg":"This is test message","access-token":"00000000-0000-0000-0000-000000000001","command":"SEND_MESSAGE"}
- response :
  1. 보낸 사람 : {"body":["test_user_1","test_user_3","as"],"status":200}
  2. 메시지를 받는 사람 : {"room_id":8,"sender":"test_user_1","sender_nickname":"test_user_1","sender_name":"name_test_1","time":"2022-12-13T19:53:26.474155","body":"This is test message","command":"recieve_message"}

#### Load My Room
- request : {"access-token":"00000000-0000-0000-0000-000000000003","command":"LOAD_MYROOM"}
- response: {"body":[{"last_time":"2022-12-12T17:47:07.419093","id":2},{"last_time":"2022-12-12T17:47:07.419093","id":8},{"last_time":"2022-12-12T17:47:07.419093","id":9}],"status":200}


#### Invite Room
- request : {"room_id":8,"userlist":["test_user_3","as"],"access-token":"00000000-0000-0000-0000-000000000001","command":"INVITE_ROOM"}
- response :
  1. 만든 사람 : {"body":"Ok","status":200}
  2. 초대 받은 사람 : {"room_id":0,"body":"you are invited in 0","command":"invited"}


#### Accept Invite
- request : {"room_id":8,"access-token":"00000000-0000-0000-0000-000000000001","command":"ACCEPT_INVITE"}
- response : {"body":"Ok","status":200}


#### Leave Room
- request: {"room_id":8,"time":"2022-12-13T19:12:48.746219","body":"test_user_3 leave room 8","command":"someone_leave","leave_user_id":"test_user_3"}
- response :
  1. 나간 사람 : {"body":"Ok","status":200}
  2. 해당 방에 존재하는 사람 : {"room_id":8,"remain":3,"time":"2022-12-13T21:02:01.9556","body":"test_user_3 leave room 8","command":"someone_leave","leave_user_id":"test_user_3"}

