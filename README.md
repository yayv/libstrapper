### 这个项目是做什么的？
这个项目是对一套接口标准的格式检查

### 这个项目包含什么？

### 如何使用这个项目？

### 接口标准的说明
		格式语法 : "[*|#]<格式名>[数值范围][:长度][#默认值]//说明"
		开头的 *，#，或无，表示该参数项是否必须填写， * 为必填， #为
		格式名: 目前所支持的格式包括: email, phone, mobile, date, time, datetime, int, float, base64, MD5 ...
	  数值范围: 以{[(三个符号中的一个开始，由{开始则必须由}结束, 如{a,b,c}, 表示为枚举类型; [()] 的组合表示为 时间和数字可以用集合形式表示取值范围如 (1,100], 表示 大于1小于等于100的范围.
		长度: 表示为需要检查的变量里原始值的字符长度
		默认值: 当取值失败，或者取值超范围时，以默认值做为返回值，同时发出一个错误信息
		说明: 参数格式的表达过于技术化，需要有给产品或业务相关人员看得懂的说明，更好的表达这些设置的目的


### Interface Format 
    // Format Syntax: "[*]<format_name>[data range][:length][#default_value]//COMMENT"
		// data range syntax:
		// 	int float double: (1, 100), (1,100], [1,100),[1,100] 
		//  date time: 
		//  string : enum {papa,mama,grandpa,grandma,grandma-inlaw}
		// example: "role":"*string{papa, mama}:4#papa//role name 

