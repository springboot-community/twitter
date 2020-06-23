# Twitter
## 使用Github的授权登录，聊天图片的存储使用 `jsdelivr` + `Github`

## 不会存储任何聊天记录（目前）

## 配置文件核心配置属性
```
# PID文件地址
spring.pid.file

# JWT的密钥
jwt.token

# Github授权登录配置
oauth2.github.client-id
oauth2.github.client-secret

# 存储图片文件的Github仓库配置
# 仓库所属用户名称（如果是组织的话，就是组织名称）
github.bucket.user
# 仓库名称
github.bucket.repository
# 个人账户的 access-token
github.bucket.access-token
# 图片访问的URL，一般不用修改
github.bucket.url
# Github上传API地址，一般不用修改
github.bucket.api
```

## Websocket
### 消息频率限制
默认，每秒只能发送一次消息。

### 消息大小限制
消息最大体积：20Kb

### 消息ACK机制
客户端生成消息ID，服务端广播后，给客户端响应消息确认

### Websocket消息格式
### 新的聊天消息
```json
{
	"code": "TWITTER_MESSAGE",
	"data": {
		"id": "11111",
		"content": "<span>Hello World!<span>",
		"dateTime": "2020-06-17 12:35:16",
		"user": {
			"id": 1,
			"name": "KevinBlandy",				// 昵称
			"avatar": "https://xxx.jpg",		// 头像
			"url": "https://github.com/xxx",	// 个人主页
			"banned": false						// 是否被禁言
		}
	}
}
```
### 新用户加入
```json
{
	"code": "TWITTER_JOIN",
	"data": {
		"user": {
			"id": 1,
			"name": "KevinBlandy",
			"avatar": "https://xxx.jpg",
			"url": "https://github.com/xxx",
			"banned": false
		}
	}
}
```

### 用户退出
```json
{
	"code": "TWITTER_QUIT",
	"data": {
		"user": {
			"id": 1,
			"name": "KevinBlandy",
			"avatar": "https://xxx.jpg",
			"url": "https://github.com/xxx",
			"banned": false
		}
	}
}
```

### 系统通知（弹窗提醒）
```json
{
	"code": "NOTIFY",
	"data": "我是通知消息"
}
```

### 消息ACK
```json
{
	"code": "TWITTER_MESSAGE_ACK",
	"data": "1"  // 消息ID
}
```
### 消息发送太快
```json
{
	"code": "MESSAGE_RATE_LIMIT",
	"data": 1 // 系统限制两次消息的间隔秒数
}
```

### 用户被禁言
```json
{
	"code": "BANNED",
	"data": {
		"id": 1,
		"name": "KevinBlandy",				// 昵称
		"avatar": "https://xxx.jpg",		// 头像
		"url": "https://github.com/xxx",	// 个人主页
	}
}
```
### 用户取消禁言
```json
{
	"code": "CANCEL_BANNED",
	"data": {
		"id": 1,
		"name": "KevinBlandy",				// 昵称
		"avatar": "https://xxx.jpg",		// 头像
		"url": "https://github.com/xxx",	// 个人主页
	}
}
```

## 业务接口
### 获取在线用户列表(按照加入时间排序)
- `/user/online`
- `GET`
- `response`
```json
{
    "success": true,
    "message": "ok",
    "code": 0,
    "data": [{
        "id": 1,
		"name": "KevinBlandy",				// 昵称
		"avatar": "https://xxx.jpg",		// 头像
		"url": "https://github.com/xxx",	// 个人主页
		"banned": false						// 是否被禁言
    }]
}
```
