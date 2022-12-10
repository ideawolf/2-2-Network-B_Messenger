### Interface (Request and Response Format)

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
