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


#### Get Friends List
- request :  {"command":"GET_FRIENDS", "access-token":"access-token"}
- response
  - Success: { "body":[{"user_id":"test_user_2","nickname":"test_user_2","email":"test_user_2@email.com"},{"user_id":"test_user_3","nickname":"test_user_3","email":"test_user_3@email.com"}],"status":200 }
  - Fail: TODO

#### Get Chat List
- request :  {"command":"GET_USER_ROOM", "access-token":"access-token"}
- reponse: {"body":{"1":[{"user_id":"test_user_1","nickname":"test_user_1","email":"test_user_1@email.com"},{"user_id":"test_user_2","nickname":"test_user_2","email":"test_user_2@email.com"}],"2":[{"user_id":"test_user_1","nickname":"test_user_1","email":"test_user_1@email.com"},{"user_id":"test_user_3","nickname":"test_user_3","email":"test_user_3@email.com"}]},"status":200}