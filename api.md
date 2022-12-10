### Interface (Request and Response Format)

#### Register
- request : {"command":"REGISTER", "name":"NAME", "password":"PASSWORD", "nickname":"NICKNAME", "email":"
  Example@email.com"}
- response :
  - Success: {"body":"Register Success","status":200}
  - Fail: {"body":"Register Failed","status":400}


#### Register
- request : {"command":"LOGIN", "id":"ID", "password":"PASSWORD"}
- response :
  - Success: {"body":"Login Success","status":200}
  - Fail: {"body":"Login Failed","status":400}
