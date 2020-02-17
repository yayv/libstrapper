
# 接口文件格式说明

## 文件格式

```
  # 文件说明
  # 其他内容
	~~test~~
  *从下行开始为接口描述的内容*
  # Interface Start 

  ## 模块编码 模块名

  ### --接口编号 接口名--
```
  NO: 同标题的<接口编号>
  NAME: 同标题的<接口名>
  URL: 必写
  METHOD: POST/GET...
  FORMAT: JSON/FORM
  NOTE:
  TODO:
  REQUEST: 统一用json格式表示, 必须有,值可以为空白
  RESPONSE: 必须有内容
  STATUS:

  ```

  其中, REQUEST 和 RESPONSE 的内容均需要用接口参数格式进行描述。如果 METHOD 为 GET 方法, 则 URL 里允许出现参数表的占位符, 如: {params}. 或者, 用 {key} {value} 的方式表示具体的 URL 拼接方式, 具体参数表的描述依然使用 REQUEST 部分的年日哦给你进行描述。

  接口描述格式中, URL,REQUEST,RESPONSE 3项为必填项; NO 和 NAME 项必须与标题行的内容一致。

  ```
