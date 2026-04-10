# 毕业项目接口文档

> 版本：1.0.0 | 日期：2026-04-08

---

## 目录

- [概述](#概述)
- [通用规范](#通用规范)
- [认证说明](#认证说明)
- [枚举值说明](#枚举值说明)
- [Admin Service 接口](#admin-service-接口-端口9901)
  - [认证模块](#认证模块-adminauth)
  - [个人信息模块](#个人信息模块-admininfo)
  - [场地管理模块](#场地管理模块-admincourt)
  - [课程管理模块](#课程管理模块-admincourse)
  - [优惠券管理模块](#优惠券管理模块-admincoupon)
  - [文档管理模块](#文档管理模块-admindoc)
  - [日志模块](#日志模块-adminlog)
  - [在线监控模块](#在线监控模块-adminmonitor)
  - [公告模块](#公告模块-adminnotice)
  - [订单查看模块](#订单查看模块-adminorder)
  - [审批模块](#审批模块-adminrequest)
  - [门店管理模块](#门店管理模块-adminstore)
- [User Service 接口](#user-service-接口-端口9902)
  - [用户认证模块](#用户认证模块-userauth)
  - [用户信息模块](#用户信息模块-userinfo)
  - [孩子管理模块](#孩子管理模块-userchildren)
  - [优惠券模块](#优惠券模块-usercoupon)
  - [用户订单模块](#用户订单模块-userorder)
  - [课程模块](#课程模块-usercourse)
  - [文档模块](#文档模块-userdoc)
  - [申请模块](#申请模块-userrequest)
  - [门店信息模块](#门店信息模块-userstore)
- [Transaction Service 接口](#transaction-service-接口-端口9903)
  - [订单交易模块](#订单交易模块-transactionorder)
  - [课时管理模块](#课时管理模块-transactionclasshour)
- [错误码说明](#错误码说明)

---

## 概述

本项目为微服务架构，所有请求通过网关 **8080** 端口统一入口。

| 服务                | 端口 | 说明                    |
| ------------------- | ---- | ----------------------- |
| Gateway             | 8080 | 网关（统一入口）        |
| Admin Service       | 9901 | 管理端接口              |
| User Service        | 9902 | 用户端（教练/会员）接口 |
| Transaction Service | 9903 | 交易服务接口            |
| File Service        | 9900 | 文件服务（内部调用）    |

**BaseURL（开发环境）：** `http://127.0.0.1:8080`

---

## 通用规范

### 请求格式

- **Content-Type**：`application/json`（文件上传接口除外，使用 `multipart/form-data`）
- **字符编码**：UTF-8

### 响应格式

所有接口均返回统一 JSON 格式：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": { }
}
```

| 字段 | 类型    | 说明                         |
| ---- | ------- | ---------------------------- |
| code | Integer | 状态码，200 成功，其他为失败 |
| msg  | String  | 提示消息                     |
| data | Any     | 返回数据                     |

### 分页响应格式

分页接口返回：

```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 100,
  "rows": [ ]
}
```

### 分页请求参数

所有使用 PageHelper 的分页接口均支持以下查询参数（Query String）：

| 参数名        | 类型    | 默认值 | 说明                                    |
| ------------- | ------- | ------ | --------------------------------------- |
| pageNum       | Integer | 1      | 当前页码，从 1 开始                     |
| pageSize      | Integer | 10     | 每页条数                                |
| orderByColumn | String  | -      | 排序字段名（驼峰命名，如 `createTime`） |
| isAsc         | String  | desc   | 排序方式，`asc` 升序 / `desc` 降序      |

**示例：**
```
GET /admin/court/list?pageNum=1&pageSize=10&orderByColumn=createTime&isAsc=desc
```

---

## 认证说明

### Token 获取

登录成功后响应 `data` 字段即为 `access_token`（JWT 字符串）。

### Token 使用

所有需要认证的接口，在请求头中携带：

```
Authorization: Bearer <access_token>
```

### Token 有效期

- 有效时间：**12 小时**（720 分钟）
- 自动续签：在有效期内最后 60 分钟使用时，服务端自动刷新 token，新 token 在响应头 `Authorization` 字段中返回，前端应更新本地存储。

---

## 枚举值说明

### 用户类型（UserType）

| 值  | 说明                  |
| --- | --------------------- |
| 0   | 系统管理员（ADMIN）   |
| 1   | 门店管理员（MANAGER） |
| 2   | 教练（COACH）         |
| 3   | VIP 会员（VIP）       |

### 性别（sex）

| 值  | 说明 |
| --- | ---- |
| 0   | 男   |
| 1   | 女   |
| 2   | 未知 |

### 课程状态（course.status）

| 值  | 说明   |
| --- | ------ |
| 0   | 待开课 |
| 1   | 进行中 |
| 2   | 已完课 |
| 3   | 已取消 |

### 课程验证状态（course.verifyStatus）

| 值  | 说明   |
| --- | ------ |
| 0   | 待审核 |
| 1   | 已审核 |

### 考勤状态（verifyChild.status）

| 值  | 说明     |
| --- | -------- |
| 1   | 正常完课 |
| 2   | 迟到     |
| 3   | 早退     |
| 4   | 缺勤     |

### 优惠券类型（couponType）

| 值  | 说明   |
| --- | ------ |
| 0   | 满减券 |
| 1   | 折扣券 |

### 优惠券状态（coupon.status）

| 值  | 说明 |
| --- | ---- |
| 0   | 启用 |
| 1   | 禁用 |

### 订单产品类型（productType）

| 值  | 说明   |
| --- | ------ |
| 0   | 单课时 |
| 1   | 套餐   |

### 套餐类型（packageType）

| 值  | 说明        |
| --- | ----------- |
| p10 | 10 课时套餐 |
| p30 | 30 课时套餐 |
| p50 | 50 课时套餐 |

### 订单状态（order.status）

| 值  | 说明   |
| --- | ------ |
| 0   | 待支付 |
| 1   | 已支付 |
| 2   | 已取消 |
| 3   | 退款中 |
| 4   | 已退款 |

### 申请状态（request.status）

| 值  | 说明   |
| --- | ------ |
| 0   | 待审核 |
| 1   | 已通过 |
| 2   | 已拒绝 |

### 场地状态（court.status）

| 值  | 说明 |
| --- | ---- |
| 0   | 正常 |
| 1   | 停用 |

---

## Admin Service 接口（端口：9901）

---

### 认证模块 `/admin/auth`

#### 1. 管理员登录

```
POST /admin/auth/login
```

> 需要通过网关验证码校验（登录前需先获取验证码）

**请求体（application/json）：**

```json
{
  "username": "admin",
  "password": "123456"
}
```

| 字段     | 类型   | 必填 | 说明   |
| -------- | ------ | ---- | ------ |
| username | String | 是   | 用户名 |
| password | String | 是   | 密码   |

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": "eyJhbGciOiJIUzUxMiJ9.eyJ0b2tlbiI6IjEyMzQ1Ni..."
}
```

> `data` 即为 `access_token`，后续请求放入请求头 `Authorization: Bearer <token>`

---

#### 2. 管理员登出

```
DELETE /admin/auth/logout
```

**请求头：** `Authorization: Bearer <token>`

**响应示例：**

```json
{
  "code": 200,
  "msg": "退出成功",
  "data": null
}
```

---

#### 3. 注册门店管理员

```
POST /admin/auth/register
```

> 注册门店管理员需要邀请码（由系统管理员生成）

**请求体：**

```json
{
  "username": "manager01",
  "password": "Manager@123",
  "inviteCode": "INV-XXXXXXXX",
  "storeId": 1
}
```

| 字段       | 类型   | 必填 | 说明            |
| ---------- | ------ | ---- | --------------- |
| username   | String | 是   | 用户名          |
| password   | String | 是   | 密码            |
| inviteCode | String | 是   | 管理员邀请码    |
| storeId    | Long   | 否   | 门店 ID（可选） |

**响应示例：**

```json
{
  "code": 200,
  "msg": "注册成功",
  "data": null
}
```

---

#### 4. 生成门店管理员邀请码

```
POST /admin/auth/managerInvite?storeId=1
```

**权限：** ADMIN、MANAGER

**Query 参数：**

| 参数    | 类型 | 必填 | 说明        |
| ------- | ---- | ---- | ----------- |
| storeId | Long | 否   | 绑定门店 ID |

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": "INV-A1B2C3D4"
}
```

---

#### 5. 生成教练邀请码

```
POST /admin/auth/coachInvite
```

**权限：** MANAGER

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": "COACH-X9Y8Z7"
}
```

---

#### 6. 获取邮箱验证码

```
POST /admin/auth/emailCode?email=admin@example.com
```

**Query 参数：**

| 参数  | 类型   | 必填 | 说明     |
| ----- | ------ | ---- | -------- |
| email | String | 是   | 目标邮箱 |

**响应示例：**

```json
{
  "code": 200,
  "msg": "发送成功",
  "data": null
}
```

---

#### 7. 忘记密码（重置）

```
PUT /admin/auth/resetPassword
```

**请求体：**

```json
{
  "email": "admin@example.com",
  "emailCode": "123456",
  "newPassword": "NewPass@123"
}
```

| 字段        | 类型   | 必填 | 说明       |
| ----------- | ------ | ---- | ---------- |
| email       | String | 是   | 绑定邮箱   |
| emailCode   | String | 是   | 邮箱验证码 |
| newPassword | String | 是   | 新密码     |

**响应示例：**

```json
{
  "code": 200,
  "msg": "重置成功",
  "data": null
}
```

---

### 个人信息模块 `/admin/info`

**所有接口权限：** ADMIN、MANAGER

#### 1. 获取个人信息

```
GET /admin/info/
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "adminId": 1,
    "username": "admin",
    "nickName": "管理员",
    "email": "admin@example.com",
    "phone": "13800138000",
    "sex": "0",
    "avatar": "http://127.0.0.1:9900/pics/avatars/xxx.jpg",
    "userType": "0",
    "storeId": null,
    "createTime": "2026-01-01T00:00:00"
  }
}
```

---

#### 2. 更新个人信息

```
PUT /admin/info/updateInfo
```

**请求体：**

```json
{
  "nickName": "新昵称",
  "phone": "13900139000",
  "sex": "1"
}
```

| 字段     | 类型   | 必填 | 说明                  |
| -------- | ------ | ---- | --------------------- |
| nickName | String | 是   | 昵称                  |
| phone    | String | 是   | 手机号                |
| sex      | String | 是   | 性别（0男/1女/2未知） |

---

#### 3. 修改邮箱

```
PUT /admin/info/updateEmail
```

**请求体：**

```json
{
  "email": "newemail@example.com",
  "emailCode": "654321"
}
```

| 字段      | 类型   | 必填 | 说明       |
| --------- | ------ | ---- | ---------- |
| email     | String | 是   | 新邮箱     |
| emailCode | String | 是   | 邮箱验证码 |

---

#### 4. 修改密码

```
PUT /admin/info/updatePassword
```

**请求体：**

```json
{
  "oldPassword": "OldPass@123",
  "newPassword": "NewPass@456"
}
```

| 字段        | 类型   | 必填 | 说明   |
| ----------- | ------ | ---- | ------ |
| oldPassword | String | 是   | 旧密码 |
| newPassword | String | 是   | 新密码 |

---

#### 5. 上传头像

```
POST /admin/info/updateAvatar
Content-Type: multipart/form-data
```

**Form 参数：**

| 参数 | 类型 | 必填 | 说明                           |
| ---- | ---- | ---- | ------------------------------ |
| file | File | 是   | 图片文件（jpg/jpeg/png，≤5MB） |

**响应示例：**

```json
{
  "code": 200,
  "msg": "上传成功",
  "data": null
}
```

---

### 场地管理模块 `/admin/court`

#### 1. 添加场地

```
POST /admin/court/
```

**权限：** MANAGER

**请求体：**

```json
{
  "courtName": "1号球场"
}
```

| 字段      | 类型   | 必填 | 说明     |
| --------- | ------ | ---- | -------- |
| courtName | String | 是   | 场地名称 |

**响应示例：**

```json
{
  "code": 200,
  "msg": "添加成功",
  "data": 1
}
```

> `data` 为新建场地的 `courtId`

---

#### 2. 修改场地信息

```
PUT /admin/court/{courtId}
```

**权限：** MANAGER

**路径参数：** `courtId`（Long）场地ID

**请求体：**

```json
{
  "courtName": "修改后名称",
  "status": "0"
}
```

| 字段      | 类型   | 必填 | 说明                |
| --------- | ------ | ---- | ------------------- |
| courtName | String | 否   | 场地名称            |
| status    | String | 否   | 状态（0正常/1停用） |

---

#### 3. 删除场地

```
DELETE /admin/court/{courtId}
```

**权限：** MANAGER

**路径参数：** `courtId`（Long）

---

#### 4. 场地列表（分页）

```
GET /admin/court/list?pageNum=1&pageSize=10&orderByColumn=createTime&isAsc=desc&storeId=1
```

**权限：** ADMIN、MANAGER

**Query 参数：**

| 参数          | 类型    | 必填 | 说明                     |
| ------------- | ------- | ---- | ------------------------ |
| pageNum       | Integer | 否   | 页码，默认 1             |
| pageSize      | Integer | 否   | 每页条数，默认 10        |
| orderByColumn | String  | 否   | 排序字段                 |
| isAsc         | String  | 否   | asc/desc                 |
| storeId       | Long    | 否   | 按门店过滤（ADMIN 使用） |

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 5,
  "rows": [
    {
      "courtId": 1,
      "courtName": "1号球场",
      "storeId": 1,
      "status": "0",
      "createTime": "2026-01-01T10:00:00"
    }
  ]
}
```

---

#### 5. 获取场地详情

```
GET /admin/court/{courtId}
```

**权限：** ADMIN、MANAGER

---

### 课程管理模块 `/admin/course`

#### 1. 创建课程

```
POST /admin/course/
```

**权限：** MANAGER

**请求体：**

```json
{
  "courtId": 1,
  "courseDate": "2026-04-10",
  "startTime": "09:00:00",
  "totalHours": 2
}
```

| 字段       | 类型    | 必填 | 说明                        |
| ---------- | ------- | ---- | --------------------------- |
| courtId    | Long    | 是   | 场地 ID                     |
| courseDate | String  | 是   | 上课日期，格式 `yyyy-MM-dd` |
| startTime  | String  | 是   | 开始时间，格式 `HH:mm:ss`   |
| totalHours | Integer | 是   | 课时数，范围 1-3            |

**响应示例：**

```json
{
  "code": 200,
  "msg": "创建成功",
  "data": 101
}
```

> `data` 为新建课程的 `courseId`

---

#### 2. 取消课程

```
DELETE /admin/course/{courseId}
```

**权限：** MANAGER

---

#### 3. 分配/更换教练

```
PUT /admin/course/{courseId}/coach/{coachId}
```

**权限：** MANAGER

**路径参数：** `courseId`（Long）课程ID，`coachId`（Long）教练ID

---

#### 4. 批量安排孩子上课

```
PUT /admin/course/{courseId}/children
```

**权限：** MANAGER

**请求体：**

```json
[1, 2, 3, 4]
```

> 请求体为孩子 ID（Long）的数组

---

#### 5. 取消孩子的课程安排

```
DELETE /admin/course/{courseId}/child/{childId}
```

**权限：** MANAGER

---

#### 6. 课程列表（分页）

```
GET /admin/course/list?pageNum=1&pageSize=10&orderByColumn=courseDate&isAsc=asc&courseDate=2026-04-10&storeId=1
```

**权限：** ADMIN、MANAGER

**Query 参数：**

| 参数          | 类型    | 必填 | 说明                          |
| ------------- | ------- | ---- | ----------------------------- |
| pageNum       | Integer | 否   | 页码，默认 1                  |
| pageSize      | Integer | 否   | 每页条数，默认 10             |
| orderByColumn | String  | 否   | 排序字段                      |
| isAsc         | String  | 否   | asc/desc                      |
| courseDate    | String  | 否   | 按日期过滤，格式 `yyyy-MM-dd` |
| storeId       | Long    | 否   | 按门店过滤（ADMIN 使用）      |

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 20,
  "rows": [
    {
      "courseId": 101,
      "courtId": 1,
      "courtName": "1号球场",
      "courseDate": "2026-04-10",
      "startTime": "2026-04-10T09:00:00",
      "totalHours": 2,
      "coachId": 5,
      "coachName": "张教练",
      "status": "0",
      "verifyStatus": "0",
      "storeId": 1
    }
  ]
}
```

---

#### 7. 获取课程详情

```
GET /admin/course/{courseId}
```

**权限：** ADMIN、MANAGER

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "courseId": 101,
    "courtName": "1号球场",
    "courseDate": "2026-04-10",
    "startTime": "2026-04-10T09:00:00",
    "totalHours": 2,
    "status": "1",
    "verifyStatus": "0",
    "coach": {
      "coachId": 5,
      "coachName": "张教练",
      "avatar": "http://..."
    },
    "children": [
      {
        "childId": 1,
        "childName": "小明",
        "photo": "http://...",
        "attendanceStatus": null
      }
    ]
  }
}
```

---

#### 8. 获取课程考勤信息

```
GET /admin/course/{courseId}/attendanceInfo
```

**权限：** MANAGER

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "courseId": 101,
    "signInPhoto": "http://127.0.0.1:9900/pics/signs/signin.jpg",
    "signInTime": "2026-04-10T09:05:00",
    "signOutPhoto": "http://127.0.0.1:9900/pics/signs/signout.jpg",
    "signOutTime": "2026-04-10T11:10:00",
    "children": [
      {
        "childId": 1,
        "childName": "小明",
        "attendanceStatus": null
      }
    ]
  }
}
```

---

#### 9. 批量验证孩子考勤

```
POST /admin/course/{courseId}/verify
```

**权限：** MANAGER

**请求体：**

```json
[
  { "childId": 1, "status": "1" },
  { "childId": 2, "status": "4" }
]
```

| 字段    | 类型   | 必填 | 说明                              |
| ------- | ------ | ---- | --------------------------------- |
| childId | Long   | 是   | 孩子 ID                           |
| status  | String | 是   | 1正常完课 / 2迟到 / 3早退 / 4缺勤 |

---

### 优惠券管理模块 `/admin/coupon`

**权限：** MANAGER

#### 1. 创建优惠券

```
POST /admin/coupon/
```

**请求体：**

```json
{
  "couponName": "新人折扣券",
  "couponType": "1",
  "discountValue": 0.9,
  "minAmount": 100.00,
  "totalCount": 100,
  "claimLimit": 1,
  "startTime": "2026-04-01T00:00:00",
  "endTime": "2026-04-30T23:59:59",
  "linkToken": "optional-link-token"
}
```

| 字段          | 类型       | 必填 | 说明                                                              |
| ------------- | ---------- | ---- | ----------------------------------------------------------------- |
| couponName    | String     | 是   | 优惠券名称                                                        |
| couponType    | String     | 是   | 0满减券 / 1折扣券                                                 |
| discountValue | BigDecimal | 是   | 折扣值（满减券：减免金额；折扣券：折扣率如0.9代表九折），最小0.01 |
| minAmount     | BigDecimal | 是   | 使用门槛金额，最小0                                               |
| totalCount    | Integer    | 是   | 总发放数量，最小1                                                 |
| claimLimit    | Integer    | 是   | 每人领取上限，最小1                                               |
| startTime     | String     | 是   | 生效时间，格式 `yyyy-MM-ddTHH:mm:ss`                              |
| endTime       | String     | 是   | 过期时间，格式 `yyyy-MM-ddTHH:mm:ss`                              |
| linkToken     | String     | 否   | 活动链接 token（可通过链接领取）                                  |

**响应示例：**

```json
{
  "code": 200,
  "msg": "创建成功",
  "data": 10
}
```

---

#### 2. 切换优惠券状态（启用/禁用）

```
PUT /admin/coupon/{couponId}/status
```

**路径参数：** `couponId`（Long）

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "couponId": 10,
    "couponName": "新人折扣券",
    "status": "1"
  }
}
```

---

#### 3. 优惠券列表（分页）

```
GET /admin/coupon/list?pageNum=1&pageSize=10&orderByColumn=createTime&isAsc=desc
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 3,
  "rows": [
    {
      "couponId": 10,
      "couponName": "新人折扣券",
      "couponType": "1",
      "discountValue": 0.9,
      "minAmount": 100.00,
      "totalCount": 100,
      "remainingCount": 98,
      "claimLimit": 1,
      "startTime": "2026-04-01T00:00:00",
      "endTime": "2026-04-30T23:59:59",
      "status": "0"
    }
  ]
}
```

---

#### 4. 获取优惠券详情

```
GET /admin/coupon/{couponId}
```

---

### 文档管理模块 `/admin/doc`

**权限：** MANAGER

#### 1. 教学计划列表（分页）

```
GET /admin/doc/tp/list?pageNum=1&pageSize=10&orderByColumn=createTime&isAsc=desc
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 5,
  "rows": [
    {
      "tpId": 1,
      "title": "初级游泳教学计划",
      "description": "适合初学者",
      "coachId": 5,
      "coachName": "张教练",
      "createTime": "2026-03-01T10:00:00"
    }
  ]
}
```

---

#### 2. 获取教学计划详情

```
GET /admin/doc/tp/{tpId}
```

---

#### 3. 获取教学计划在线阅读链接

```
GET /admin/doc/tp/{tpId}/url
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": "http://127.0.0.1:9900/tps/xxx.pdf"
}
```

---

#### 4. 训练方法列表（分页）

```
GET /admin/doc/tm/list?pageNum=1&pageSize=10&orderByColumn=createTime&isAsc=desc
```

---

#### 5. 获取训练方法详情

```
GET /admin/doc/tm/{tmId}
```

---

#### 6. 获取训练方法在线阅读链接

```
GET /admin/doc/tm/{tmId}/url
```

---

### 日志模块 `/admin/log`

**权限：** ADMIN

#### 1. 操作日志列表（分页）

```
POST /admin/log/oper?pageNum=1&pageSize=10&orderByColumn=operTime&isAsc=desc
```

**请求体：**

```json
{
  "operatorType": null,
  "operatorName": "admin",
  "operIp": "127.0.0.1",
  "status": "0"
}
```

| 字段         | 类型    | 必填 | 说明                     |
| ------------ | ------- | ---- | ------------------------ |
| operatorType | Integer | 否   | 操作者类型               |
| operatorName | String  | 否   | 操作者用户名（模糊查询） |
| operIp       | String  | 否   | 操作 IP                  |
| status       | String  | 否   | 操作状态                 |

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 200,
  "rows": [
    {
      "operId": 1,
      "title": "用户登录",
      "method": "POST",
      "operatorName": "admin",
      "operUrl": "/admin/auth/login",
      "operIp": "127.0.0.1",
      "status": "0",
      "operTime": "2026-04-08T10:00:00",
      "costTime": 120
    }
  ]
}
```

---

#### 2. 操作日志详情

```
GET /admin/log/oper/{operId}
```

---

#### 3. 批量删除操作日志

```
DELETE /admin/log/oper?operIds=1,2,3
```

**Query 参数：**

| 参数    | 类型         | 必填 | 说明                   |
| ------- | ------------ | ---- | ---------------------- |
| operIds | List\<Long\> | 是   | 日志 ID 列表，逗号分隔 |

---

#### 4. 清空操作日志

```
DELETE /admin/log/oper/clean
```

---

#### 5. 登录日志列表（分页）

```
POST /admin/log/login?pageNum=1&pageSize=10&orderByColumn=accessTime&isAsc=desc
```

**请求体：**

```json
{
  "username": "admin",
  "status": "0",
  "ipAddr": "127.0.0.1"
}
```

| 字段     | 类型   | 必填 | 说明                    |
| -------- | ------ | ---- | ----------------------- |
| username | String | 否   | 用户名（模糊）          |
| status   | String | 否   | 登录状态（0成功/1失败） |
| ipAddr   | String | 否   | IP 地址                 |

---

#### 6. 登录日志详情

```
GET /admin/log/login/{loginLogId}
```

---

#### 7. 批量删除登录日志

```
DELETE /admin/log/login?logIds=1,2,3
```

---

#### 8. 清空登录日志

```
DELETE /admin/log/login/clean
```

---

### 在线监控模块 `/admin/monitor`

**权限：** ADMIN

#### 1. 在线管理员列表（分页）

```
GET /admin/monitor/online/list/admin?pageNum=1&pageSize=10&orderByColumn=userId&isAsc=desc
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 2,
    "records": [
      {
        "adminId": 1,
        "username": "admin",
        "nickName": "管理员",
        "userType": "0",
        "loginIp": "127.0.0.1",
        "loginTime": "2026-04-08T09:00:00"
      }
    ]
  }
}
```

---

#### 2. 在线用户列表（分页）

```
GET /admin/monitor/online/list/user?pageNum=1&pageSize=10&orderByColumn=userId&isAsc=desc
```

---

#### 3. 强制管理员下线

```
DELETE /admin/monitor/forceAdminLogout/{adminId}
```

---

#### 4. 强制用户下线

```
DELETE /admin/monitor/forceUserLogout/{userId}
```

---

#### 5. 封禁用户

```
PUT /admin/monitor/ban/{userId}
```

---

#### 6. 解封用户

```
PUT /admin/monitor/unban/{userId}
```

---

### 公告模块 `/admin/notice`

#### 1. 公告列表（分页）

```
GET /admin/notice/list?pageNum=1&pageSize=10&orderByColumn=createTime&isAsc=desc
```

**权限：** ADMIN、MANAGER

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 3,
  "rows": [
    {
      "noticeId": 1,
      "title": "系统维护通知",
      "content": "本系统将于2026-04-09进行维护...",
      "status": "0",
      "createBy": "admin",
      "createTime": "2026-04-08T08:00:00"
    }
  ]
}
```

---

#### 2. 公告详情

```
GET /admin/notice/{noticeId}
```

**权限：** ADMIN、MANAGER

---

#### 3. 发布公告

```
PUT /admin/notice/publish
```

**权限：** ADMIN

**请求体：**

```json
{
  "title": "重要通知",
  "content": "请各位会员注意..."
}
```

| 字段    | 类型   | 必填 | 说明     |
| ------- | ------ | ---- | -------- |
| title   | String | 是   | 公告标题 |
| content | String | 是   | 公告内容 |

---

#### 4. 编辑公告

```
PUT /admin/notice/edit/{noticeId}
```

**权限：** ADMIN

**请求体：**

```json
{
  "title": "修改后标题",
  "content": "修改后内容"
}
```

| 字段    | 类型   | 必填 | 说明     |
| ------- | ------ | ---- | -------- |
| title   | String | 否   | 公告标题 |
| content | String | 否   | 公告内容 |

---

#### 5. 删除公告

```
DELETE /admin/notice/{noticeId}
```

**权限：** ADMIN

---

### 订单查看模块 `/admin/order`

#### 1. 管理员查看所有订单（分页）

```
GET /admin/order/all?pageNum=1&pageSize=10&orderByColumn=createTime&isAsc=desc&storeId=1&status=1
```

**权限：** ADMIN

**Query 参数：**

| 参数          | 类型    | 必填 | 说明       |
| ------------- | ------- | ---- | ---------- |
| pageNum       | Integer | 否   | 页码       |
| pageSize      | Integer | 否   | 每页条数   |
| orderByColumn | String  | 否   | 排序字段   |
| isAsc         | String  | 否   | asc/desc   |
| storeId       | Long    | 否   | 按门店过滤 |
| status        | String  | 否   | 订单状态   |

---

#### 2. 门店管理员查看本店订单（分页）

```
GET /admin/order/store?pageNum=1&pageSize=10&orderByColumn=createTime&isAsc=desc&status=1
```

**权限：** MANAGER

---

#### 3. 管理员查看订单详情

```
GET /admin/order/{orderId}
```

**权限：** ADMIN

---

#### 4. 门店管理员查看订单详情

```
GET /admin/order/store/{orderId}
```

**权限：** MANAGER

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "orderId": 1001,
    "orderNo": "ORD2026040800001",
    "userId": 100,
    "storeId": 1,
    "productType": "0",
    "quantity": 5,
    "unitPrice": 50.00,
    "totalAmount": 250.00,
    "discountAmount": 25.00,
    "payAmount": 225.00,
    "couponId": 10,
    "status": "1",
    "payType": "wechat",
    "payTime": "2026-04-08T10:30:00",
    "createTime": "2026-04-08T10:25:00"
  }
}
```

---

### 审批模块 `/admin/request`

#### 1. 待审批列表（分页）

```
GET /admin/request/pending?pageNum=1&pageSize=10&orderByColumn=createTime&isAsc=asc
```

**权限：** ADMIN、MANAGER

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 5,
  "rows": [
    {
      "requestId": 1,
      "senderId": 100,
      "senderType": "3",
      "type": "leave",
      "payload": { "courseId": 101, "childId": 1 },
      "status": "0",
      "message": "孩子生病请假",
      "createTime": "2026-04-08T08:00:00"
    }
  ]
}
```

---

#### 2. 通过申请

```
POST /admin/request/{requestId}/approve
```

**权限：** ADMIN、MANAGER

---

#### 3. 拒绝申请

```
POST /admin/request/{requestId}/reject?rejectReason=不符合请假条件
```

**权限：** ADMIN、MANAGER

**Query 参数：**

| 参数         | 类型   | 必填 | 说明     |
| ------------ | ------ | ---- | -------- |
| rejectReason | String | 否   | 拒绝原因 |

---

### 门店管理模块 `/admin/store`

#### 1. 创建门店

```
POST /admin/store/
```

**权限：** ADMIN

**请求体：**

```json
{
  "storeName": "北京朝阳门店",
  "address": "北京市朝阳区xxx路xxx号"
}
```

| 字段      | 类型   | 必填 | 说明     |
| --------- | ------ | ---- | -------- |
| storeName | String | 是   | 门店名称 |
| address   | String | 否   | 门店地址 |

**响应示例：**

```json
{
  "code": 200,
  "msg": "创建成功",
  "data": 1
}
```

---

#### 2. 修改门店信息

```
PUT /admin/store/{storeId}
```

**权限：** ADMIN、MANAGER

**请求体：**

```json
{
  "storeName": "修改后门店名",
  "address": "修改后地址"
}
```

---

#### 3. 删除门店

```
DELETE /admin/store/{storeId}
```

**权限：** ADMIN

---

#### 4. 设置门店负责人

```
PUT /admin/store/{storeId}/owner/{ownerId}
```

**权限：** ADMIN

---

#### 5. 门店列表（分页）

```
GET /admin/store/list?pageNum=1&pageSize=10&orderByColumn=createTime&isAsc=desc&status=0
```

**权限：** ADMIN

**Query 参数：**

| 参数          | 类型    | 必填 | 说明     |
| ------------- | ------- | ---- | -------- |
| pageNum       | Integer | 否   | 页码     |
| pageSize      | Integer | 否   | 每页条数 |
| orderByColumn | String  | 否   | 排序字段 |
| isAsc         | String  | 否   | asc/desc |
| status        | String  | 否   | 门店状态 |

---

#### 6. 门店详情

```
GET /admin/store/{storeId}
```

**权限：** ADMIN、MANAGER

---

#### 7. 门店VIP会员列表（分页）

```
GET /admin/store/list/vip?pageNum=1&pageSize=10&orderByColumn=createTime&isAsc=desc
```

**权限：** MANAGER

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 30,
  "rows": [
    {
      "userId": 100,
      "nickName": "小王",
      "phone": "138...",
      "sex": "0",
      "status": "0"
    }
  ]
}
```

---

#### 8. VIP会员详情

```
GET /admin/store/vip/{vipId}
```

**权限：** MANAGER

---

#### 9. 教练列表（分页）

```
GET /admin/store/list/coach?pageNum=1&pageSize=10&orderByColumn=createTime&isAsc=desc
```

**权限：** MANAGER

---

#### 10. 教练详情

```
GET /admin/store/coach/{coachId}
```

**权限：** MANAGER

---

#### 11. 孩子列表（分页）

```
GET /admin/store/list/children?pageNum=1&pageSize=10&orderByColumn=createTime&isAsc=desc
```

**权限：** MANAGER

---

## User Service 接口（端口：9902）

---

### 用户认证模块 `/user/auth`

#### 1. 用户/教练登录（微信小程序）

```
POST /user/auth/login
```

> 使用微信小程序 `wx.login()` 获取的 code 进行登录

**请求体：**

```json
{
  "code": "wx_login_code_from_miniprogram"
}
```

| 字段 | 类型   | 必填 | 说明                                       |
| ---- | ------ | ---- | ------------------------------------------ |
| code | String | 是   | 微信小程序 `wx.login()` 返回的临时登录凭证 |

**响应示例（已注册）：**

```json
{
  "code": 200,
  "msg": "登录成功",
  "data": "eyJhbGciOiJIUzUxMiJ9..."
}
```

**响应示例（未注册）：**

```json
{
  "code": 201,
  "msg": "用户未注册",
  "data": null
}
```

> 返回 `code=201` 时，前端应跳转到注册页面并再次调用登录获取 code 传入注册接口

---

#### 2. 用户注册

```
POST /user/auth/register
```

**请求体：**

```json
{
  "code": "wx_login_code_from_miniprogram",
  "userType": "3",
  "inviteCode": "COACH-XXXXXX"
}
```

| 字段       | 类型   | 必填 | 说明                                       |
| ---------- | ------ | ---- | ------------------------------------------ |
| code       | String | 是   | 微信小程序 `wx.login()` 返回的临时登录凭证 |
| userType   | String | 是   | 注册身份：2=教练 / 3=VIP会员               |
| inviteCode | String | 否   | 教练注册时需要邀请码                       |

**响应示例：**

```json
{
  "code": 200,
  "msg": "注册成功",
  "data": "eyJhbGciOiJIUzUxMiJ9..."
}
```

---

### 用户信息模块 `/user/info`

**所有接口权限：** COACH、VIP

#### 1. 获取个人信息

```
GET /user/info/
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "userId": 100,
    "openId": "oXXXXXXXXXXX",
    "nickName": "小王",
    "avatar": "http://127.0.0.1:9900/pics/avatars/xxx.jpg",
    "phone": "13800138000",
    "email": "user@example.com",
    "sex": "0",
    "userType": "3",
    "storeId": 1,
    "status": "0",
    "createTime": "2026-01-01T00:00:00"
  }
}
```

---

#### 2. 更新个人信息

```
PUT /user/info/updateInfo
```

**请求体：**

```json
{
  "nickName": "新昵称",
  "phone": "13900139000",
  "sex": "1"
}
```

---

#### 3. 获取邮箱验证码

```
POST /user/info/emailCode?email=user@example.com
```

**Query 参数：**

| 参数  | 类型   | 必填 | 说明     |
| ----- | ------ | ---- | -------- |
| email | String | 是   | 目标邮箱 |

---

#### 4. 修改邮箱

```
PUT /user/info/updateEmail
```

**请求体：**

```json
{
  "email": "newemail@example.com",
  "emailCode": "123456"
}
```

---

#### 5. 上传头像

```
POST /user/info/updateAvatar
Content-Type: multipart/form-data
```

**Form 参数：**

| 参数 | 类型 | 必填 | 说明                           |
| ---- | ---- | ---- | ------------------------------ |
| file | File | 是   | 图片文件（jpg/jpeg/png，≤5MB） |

---

**响应示例：**

```json
{
  "code": 200,
  "msg": "上传成功",
  "data": null
}
```

#### 6. 上传教练照片（仅教练）

```
POST /user/info/updatePhoto
Content-Type: multipart/form-data
```

**权限：** COACH

**Form 参数：**

| 参数 | 类型 | 必填 | 说明                           |
| ---- | ---- | ---- | ------------------------------ |
| file | File | 是   | 图片文件（jpg/jpeg/png，≤5MB） |

**响应示例：**

```json
{
  "code": 200,
  "msg": "上传成功",
  "data": null
}
```

#### 7. 获取教练照片（仅教练）

```
GET /user/info/photo
```

**权限：** COACH

**响应示例：**

```json
{
  "code": 200,
  "msg": "上传成功",
  "data": "./photoPath"
}
```

---

#### 8. 获取课时信息（仅VIP）

```
GET /user/info/classHour
```

**权限：** VIP

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "totalHours": 30,
    "usedHours": 10,
    "remainingHours": 20
  }
}
```

---

### 孩子管理模块 `/user/children`

**所有接口权限：** VIP

#### 1. 孩子列表

```
GET /user/children/list
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "childId": 1,
      "childName": "小明",
      "birthday": "2018-05-20",
      "photo": "http://127.0.0.1:9900/pics/children/photos/xxx.jpg",
      "sex": "0",
      "status": "0"
    }
  ]
}
```

---

#### 2. 孩子详情

```
GET /user/children/{childId}
```

---

#### 3. 添加孩子信息

```
POST /user/children/
```

**请求体：**

```json
{
  "childName": "小明",
  "birthday": "2018-05-20",
  "sex": "0"
}
```

| 字段      | 类型   | 必填 | 说明                        |
| --------- | ------ | ---- | --------------------------- |
| childName | String | 是   | 孩子姓名                    |
| birthday  | String | 否   | 出生日期，格式 `yyyy-MM-dd` |
| sex       | String | 否   | 性别（0男/1女/2未知）       |

---

#### 4. 更新孩子信息

```
PUT /user/children/{childId}
```

**路径参数：** `childId`（Long）孩子ID

**请求体：**

```json
{
  "childName": "小明修改",
  "birthday": "2018-05-20",
  "sex": "0"
}
```

| 字段      | 类型   | 必填 | 说明     |
| --------- | ------ | ---- | -------- |
| childName | String | 否   | 孩子姓名 |
| birthday  | String | 否   | 出生日期 |
| sex       | String | 否   | 性别     |

---

#### 5. 上传孩子照片

```
POST /user/children/{childId}/photo
Content-Type: multipart/form-data
```

**Form 参数：**

| 参数       | 类型 | 必填 | 说明                            |
| ---------- | ---- | ---- | ------------------------------- |
| childPhoto | File | 是   | 图片文件（jpg/jpeg/png，≤10MB） |

---

#### 6. 删除孩子信息

```
DELETE /user/children/{childId}
```

---

### 优惠券模块 `/user/coupon`

**所有接口权限：** VIP

#### 1. 可领取优惠券列表（分页）

```
GET /user/coupon/available?pageNum=1&pageSize=10
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 3,
  "rows": [
    {
      "couponId": 10,
      "couponName": "新人折扣券",
      "couponType": "1",
      "discountValue": 0.9,
      "minAmount": 100.00,
      "remainingCount": 98,
      "startTime": "2026-04-01T00:00:00",
      "endTime": "2026-04-30T23:59:59"
    }
  ]
}
```

---

#### 2. 领取优惠券

```
POST /user/coupon/claim/{couponId}
```

**路径参数：** `couponId`（Long）优惠券ID

**响应示例：**

```json
{
  "code": 200,
  "msg": "领取成功",
  "data": 201
}
```

> `data` 为 `userCouponId`，下单时使用

---

#### 3. 通过活动链接领取优惠券

```
POST /user/coupon/claim/link/{token}
```

**路径参数：** `token`（String）活动链接 token（来自优惠券 `linkToken` 字段）

---

#### 4. 我的优惠券列表（分页）

```
GET /user/coupon/my?pageNum=1&pageSize=10&status=0
```

**Query 参数：**

| 参数     | 类型    | 必填 | 说明                                |
| -------- | ------- | ---- | ----------------------------------- |
| pageNum  | Integer | 否   | 页码                                |
| pageSize | Integer | 否   | 每页条数                            |
| status   | String  | 否   | 状态过滤（0未使用/1已使用/2已过期） |

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 2,
  "rows": [
    {
      "userCouponId": 201,
      "couponId": 10,
      "couponName": "新人折扣券",
      "couponType": "1",
      "discountValue": 0.9,
      "minAmount": 100.00,
      "status": "0",
      "claimTime": "2026-04-08T10:00:00",
      "endTime": "2026-04-30T23:59:59"
    }
  ]
}
```

---

### 用户订单模块 `/user/order`

**所有接口权限：** VIP

#### 1. 我的订单列表（分页）

```
GET /user/order/my?pageNum=1&pageSize=10&orderByColumn=createTime&isAsc=desc&status=1
```

**Query 参数：**

| 参数          | 类型    | 必填 | 说明         |
| ------------- | ------- | ---- | ------------ |
| pageNum       | Integer | 否   | 页码         |
| pageSize      | Integer | 否   | 每页条数     |
| orderByColumn | String  | 否   | 排序字段     |
| isAsc         | String  | 否   | asc/desc     |
| status        | String  | 否   | 订单状态过滤 |

---

#### 2. 订单详情

```
GET /user/order/{orderId}
```

---

### 课程模块 `/user/course`

#### 教练接口（权限：COACH）

##### 1. 签到（上传签到照片）

```
POST /user/course/{courseId}/sign-in
Content-Type: multipart/form-data
```

**Form 参数：**

| 参数 | 类型 | 必填 | 说明                            |
| ---- | ---- | ---- | ------------------------------- |
| file | File | 是   | 签到照片（jpg/jpeg/png，≤20MB） |

---

##### 2. 签退（上传签退照片）

```
POST /user/course/{courseId}/sign-out
Content-Type: multipart/form-data
```

**Form 参数：**

| 参数 | 类型 | 必填 | 说明                            |
| ---- | ---- | ---- | ------------------------------- |
| file | File | 是   | 签退照片（jpg/jpeg/png，≤20MB） |

---

##### 3. 教练课程列表（分页）

```
GET /user/course/coach/list?pageNum=1&pageSize=10&orderByColumn=courseDate&isAsc=asc&courseDate=2026-04-10
```

**Query 参数：**

| 参数          | 类型    | 必填 | 说明                          |
| ------------- | ------- | ---- | ----------------------------- |
| pageNum       | Integer | 否   | 页码                          |
| pageSize      | Integer | 否   | 每页条数                      |
| orderByColumn | String  | 否   | 排序字段                      |
| isAsc         | String  | 否   | asc/desc                      |
| courseDate    | String  | 否   | 按日期过滤，格式 `yyyy-MM-dd` |

---

##### 4. 教练查看课程详情

```
GET /user/course/coach/{courseId}
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "courseId": 101,
    "courtName": "1号球场",
    "courseDate": "2026-04-10",
    "startTime": "2026-04-10T09:00:00",
    "totalHours": 2,
    "status": "1",
    "signInPhoto": "http://...",
    "signInTime": "2026-04-10T09:05:00",
    "signOutPhoto": null,
    "signOutTime": null,
    "children": [
      {
        "childId": 1,
        "childName": "小明",
        "photo": "http://...",
        "attendanceStatus": null
      }
    ]
  }
}
```

---

#### VIP接口（权限：VIP）

##### 5. VIP查看孩子课程列表（分页）

```
GET /user/course/vip/list?pageNum=1&pageSize=10&orderByColumn=courseDate&isAsc=asc&courseDate=2026-04-10
```

---

##### 6. VIP查看课程详情

```
GET /user/course/vip/{courseId}
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "courseId": 101,
    "courtName": "1号球场",
    "courseDate": "2026-04-10",
    "startTime": "2026-04-10T09:00:00",
    "totalHours": 2,
    "status": "2",
    "coachName": "张教练",
    "myChildren": [
      {
        "childId": 1,
        "childName": "小明",
        "attendanceStatus": "1"
      }
    ]
  }
}
```

---

### 文档模块 `/user/doc`

**所有接口权限：** COACH

#### 1. 上传教学计划

```
POST /user/doc/tp/upload
Content-Type: multipart/form-data
```

**Form 参数：**

| 参数        | 类型   | 必填 | 说明                   |
| ----------- | ------ | ---- | ---------------------- |
| file        | File   | 是   | 文档（pdf/doc，≤30MB） |
| title       | String | 是   | 标题                   |
| description | String | 否   | 描述                   |

---

#### 2. 我的教学计划列表（分页）

```
GET /user/doc/tp/list?pageNum=1&pageSize=10&orderByColumn=createTime&isAsc=desc
```

---

#### 3. 获取教学计划阅读链接

```
GET /user/doc/tp/{tpId}/url
```

---

#### 4. 上传训练方法

```
POST /user/doc/tm/upload
Content-Type: multipart/form-data
```

**Form 参数：**

| 参数        | 类型   | 必填 | 说明                                 |
| ----------- | ------ | ---- | ------------------------------------ |
| file        | File   | 是   | 文档（pdf/doc/docx/ppt/pptx，≤30MB） |
| title       | String | 是   | 标题                                 |
| description | String | 否   | 描述                                 |

---

#### 5. 门店已审批训练方法列表（分页）

```
GET /user/doc/tm/store?pageNum=1&pageSize=10&orderByColumn=createTime&isAsc=desc
```

---

#### 6. 我的训练方法列表（分页）

```
GET /user/doc/tm/my?pageNum=1&pageSize=10&orderByColumn=createTime&isAsc=desc
```

---

#### 7. 获取训练方法阅读链接

```
GET /user/doc/tm/{tmId}/url
```

---

### 申请模块 `/user/request`

#### 1. 提交请假申请（仅VIP）

```
POST /user/request/leave
```

**权限：** VIP

**请求体：**

```json
{
  "courseId": 101,
  "childId": 1,
  "message": "孩子感冒发烧，需要请假"
}
```

| 字段     | 类型   | 必填 | 说明        |
| -------- | ------ | ---- | ----------- |
| courseId | Long   | 是   | 课程 ID     |
| childId  | Long   | 是   | 请假孩子 ID |
| message  | String | 否   | 请假说明    |

---

#### 2. 提交绑定门店申请

```
POST /user/request/bindStore
```

**权限：** COACH、VIP

**请求体：**

```json
{
  "inviteCode": "INVITE-XXXXXX",
  "message": "希望加入贵门店"
}
```

| 字段       | 类型   | 必填 | 说明       |
| ---------- | ------ | ---- | ---------- |
| inviteCode | String | 是   | 门店邀请码 |
| message    | String | 否   | 申请说明   |

---

#### 3. 我的申请列表（分页）

```
GET /user/request/my?pageNum=1&pageSize=10&orderByColumn=createTime&isAsc=desc
```

**权限：** COACH、VIP

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 2,
  "rows": [
    {
      "requestId": 1,
      "type": "leave",
      "payload": { "courseId": 101, "childId": 1 },
      "status": "0",
      "message": "孩子感冒请假",
      "rejectReason": null,
      "createTime": "2026-04-08T08:00:00"
    }
  ]
}
```

---

### 门店信息模块 `/user/store`

**所有接口权限：** COACH、VIP

#### 1. 获取当前绑定门店信息

```
GET /user/store/info
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "storeId": 1,
    "storeName": "北京朝阳门店",
    "address": "北京市朝阳区xxx路",
    "status": "0",
    "coaches": [
      {
        "userId": 5,
        "nickName": "张教练",
        "phone": "138...",
        "sex": "0",
        "photo": "http://...",
        "status": "0"
      }
    ]
  }
}
```

---

#### 2. 根据ID获取门店信息

```
GET /user/store/info/{storeId}
```

---

#### 3. 搜索门店

```
GET /user/store/search?keyword=北京&pageNum=1&pageSize=10
```

**Query 参数：**

| 参数     | 类型    | 必填 | 说明                           |
| -------- | ------- | ---- | ------------------------------ |
| keyword  | String  | 是   | 搜索关键词（门店名称模糊查询） |
| pageNum  | Integer | 否   | 页码                           |
| pageSize | Integer | 否   | 每页条数                       |

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 3,
  "rows": [
    {
      "storeId": 1,
      "storeName": "北京朝阳门店",
      "address": "北京市朝阳区xxx路"
    }
  ]
}
```

---

## Transaction Service 接口（端口：9903）

---

### 订单交易模块 `/transaction/order`

**所有接口权限：** VIP

#### 1. 创建订单

```
POST /transaction/order/create
```

**请求体（购买单课时）：**

```json
{
  "productType": "0",
  "quantity": 5,
  "userCouponId": 201
}
```

**请求体（购买套餐）：**

```json
{
  "productType": "1",
  "packageType": "p30",
  "userCouponId": null
}
```

| 字段         | 类型    | 必填     | 说明                                         |
| ------------ | ------- | -------- | -------------------------------------------- |
| productType  | String  | 是       | 0=单课时 / 1=套餐，默认"0"                   |
| quantity     | Integer | 条件必填 | productType="0" 时必填，购买课时数           |
| packageType  | String  | 条件必填 | productType="1" 时必填，p10/p30/p50          |
| userCouponId | Long    | 否       | 用户优惠券 ID（我的优惠券中的 userCouponId） |

**响应示例：**

```json
{
  "code": 200,
  "msg": "创建成功",
  "data": {
    "orderId": 1001,
    "orderNo": "ORD2026040800001",
    "productType": "0",
    "quantity": 5,
    "unitPrice": 50.00,
    "totalAmount": 250.00,
    "discountAmount": 25.00,
    "payAmount": 225.00,
    "status": "0",
    "createTime": "2026-04-08T10:25:00"
  }
}
```

---

#### 2. 取消订单

```
PUT /transaction/order/{orderId}/cancel
```

**路径参数：** `orderId`（Long）

**请求体（可选）：**

```json
{
  "cancelReason": "暂时不需要了"
}
```

| 字段         | 类型   | 必填 | 说明     |
| ------------ | ------ | ---- | -------- |
| cancelReason | String | 否   | 取消原因 |

> 只有状态为"待支付（0）"的订单可以取消

---

#### 3. 发起支付（获取微信支付参数）

```
POST /transaction/order/{orderId}/pay
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "appId": "wx_mock_appid",
    "timeStamp": "1712546700",
    "nonceStr": "abc123def456",
    "package": "prepay_id=wx_prepay_abc123",
    "signType": "RSA",
    "paySign": "mock_sign_xxx"
  }
}
```

> 将 `data` 中的字段传入小程序 `wx.requestPayment()` 发起支付

---

#### 4. 确认支付完成

```
POST /transaction/order/{orderId}/pay/confirm
```

> 小程序 `wx.requestPayment()` 成功回调后调用此接口确认支付

**响应示例：**

```json
{
  "code": 200,
  "msg": "支付成功",
  "data": null
}
```

---

### 课时管理模块 `/transaction/classHour`

#### 1. 门店会员课时列表（分页）

```
GET /transaction/classHour/list?pageNum=1&pageSize=10&orderByColumn=createTime&isAsc=desc
```

**权限：** MANAGER

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 30,
  "rows": [
    {
      "userId": 100,
      "nickName": "小王",
      "totalHours": 30,
      "usedHours": 10,
      "remainingHours": 20
    }
  ]
}
```

---

## 错误码说明

| 错误码 | 说明                          |
| ------ | ----------------------------- |
| 200    | 操作成功                      |
| 201    | 自定义状态（如用户未注册）    |
| 400    | 请求参数错误                  |
| 401    | 未登录或 Token 已过期         |
| 403    | 无权限访问                    |
| 404    | 资源不存在                    |
| 429    | 请求过于频繁（Sentinel 限流） |
| 500    | 服务器内部错误                |

---

## 前端接入示例

### 微信小程序登录流程

```javascript
// 1. 获取微信 code
wx.login({
  success(res) {
    // 2. 请求登录接口
    wx.request({
      url: 'http://127.0.0.1:8080/user/auth/login',
      method: 'POST',
      data: { code: res.code },
      success(loginRes) {
        if (loginRes.data.code === 200) {
          // 登录成功，保存 token
          wx.setStorageSync('token', loginRes.data.data)
        } else if (loginRes.data.code === 201) {
          // 未注册，跳转注册页面
          wx.navigateTo({ url: '/pages/register/register' })
        }
      }
    })
  }
})
```

### 携带 Token 发起请求

```javascript
wx.request({
  url: 'http://127.0.0.1:8080/user/info/',
  method: 'GET',
  header: {
    'Authorization': 'Bearer ' + wx.getStorageSync('token')
  },
  success(res) {
    // 检查响应头中是否有新 token（自动续签）
    const newToken = res.header['Authorization']
    if (newToken) {
      wx.setStorageSync('token', newToken.replace('Bearer ', ''))
    }
    console.log(res.data)
  }
})
```

### 发起支付流程

```javascript
// 1. 创建订单
const orderRes = await createOrder({ productType: '0', quantity: 5 })
const orderId = orderRes.data.orderId

// 2. 获取支付参数
const payRes = await initiatePayment(orderId)
const payParams = payRes.data

// 3. 调起微信支付
wx.requestPayment({
  ...payParams,
  success() {
    // 4. 确认支付
    confirmPayment(orderId)
  },
  fail(err) {
    console.error('支付失败', err)
  }
})
```

---

*文档由 Claude Code 自动生成，如有疑问请联系开发团队。*
