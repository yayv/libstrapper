


# Interface Start

## User 用户模块

### User01: 用户登录
```
NO: User01
NAME: 用户登录
URL: /user/login
NOTE: 这里的接口定义只是一个示范，登录信息完全用明文传输在安全上有点弱
FORMAT:JSON
METHOD:POST
REQUEST:
{
	"username":"*string//用户名",
	"password":"*string//密码",
	"verify":"*string//验证码"
}
RESPONSE:
{
	"code":"*retCode",
	"data":{
			"name":"*string//用户显示名",
			"score":"*int//用户积分"
		},
	"message":"!string//错误消息，如登录是失败原因之类"
}
```

### User02: 用户退出登录
```
NO: User02
NAME: 用户退出登录
URL:/user/logout
NOTE: 这里的接口定义只是一个示范，登录信息完全用明文传输在安全上有点弱
FORMAT:JSON
METHOD:GET/POST
REQUEST:{}
RESPONSE:
{
	"code":"*retCode",
	"data":{
			"name":"*string//用户显示名",
			"score":"*int//用户积分"
		},
	"message":"!string//错误消息，如登录是失败原因之类"
}
```

### User03: 获取图形验证码
```
NO: User03
NAME: 获取图形验证码
URL:/user/getVerify
NOTE: 这里的接口定义只是一个示范，登录信息完全用明文传输在安全上有点弱
FORMAT:JSON
METHOD:POST
REQUEST:{}
RESPONSE:
{
	"code":"*retCode",
	"data":{
			"image":"*inlineImage//数据流内的图片"
		},
	"message":"!string//错误消息，如登录是失败原因之类,此项为可选项，浏览器端读取接口时需注意"
}
```
