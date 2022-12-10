### Interface (Request and Response Format)

#### Register

- request : {"command":"REGISTER", "name":"NAME", "password":"PASSWORD", "nickname":"NICKNAME", "email":"
  Example@email.com"}
- response :
  - Success: {"body":"Register Success","status":200}
  - Fail: {"body":"Register Failed","status":400}
