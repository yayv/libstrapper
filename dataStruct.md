
本文件所描述的内容为 WEB API 对一组输入输出参数的描述格式进行的约定

# 接口参数格式说明

GET参数、POST FORM参数, 也都用 JSON 的表示进行表述, 只是 JSON 描述的信息里不能出现多级的描述。
POST JSON 的参数传递方式, 完全使用符合 JSON 格式的文本进行描述。

XML格式的参数传递方式，不在本格式的讨论范围之内。

## 格式约定 

### 基本数值类型
```
    所有值的表示必须用""包括格式,  具体如表示年份的必填参数如下: year=2012, 用描述值的格式描述为:
    "year":"*int[1990,2030]:4//必填参数年份,1990年起到2030年之间为有效值,本条为用基本数值进行描述的示例"
    用扩展格式描述可以为以下方式
    "year":"*year[1990,2030]//必填参数年份,1990年到2030年之间为有效数值,本条使用扩展类型year进行描述"
```

### 数组及数组中的对象
```
	出于对数组中多元素顺序的不确定性导致格式检查的复杂性上升的考虑, 目前不支持同一数组中出现不同格式的情况。监测程序对出现了同一个数组中存在多个格式描述的情况直接报错。
	示例如下:
	1. 数值数组:
	["int[0,99]//年龄"]

	2. 数值数组:
	["string{北京,上海,天津,重庆}//直辖市"]

	3. 对象数组:
	[		
		{
			"modelId":"*int//车型id",
			"modelName":"*string//车型名称",
			"price":"*int//租价"
		}
	]

```

### 对象
```
    对象是JSON格式的参数传递方式中使用最多的情况。每一个key为确定值, 其对应的 value 为用 "" 包括起来的一个值的格式说明。
    如果 key 为不确定时, key使用 !string 表示
    示例如下:
	{
		"userId":"*int",
		"retailId":"*int",
		"carId":"*int",
		"carData":{
			"plateNumber":"*string",
			"vehicleModelId":"*int",
			"carColor":"*string",
			"status":"*string",
			"mileage":"*float//里程表示数,保留小数点后2位",
	    "rechargeMileage":"500000000//车辆续航里程",
	    "batteryCapacity":"非必填 //电池容量",
	    "gasTankCapacity":"非必填//油箱容量",
	    "insuranceDate":"*date//保险到期日期",
	    "annualSurveyDate":"*date//年检到期日期",
	    "carCareDate":"*date//预估下一次保养日期",
	    "drivingLicenseNo":"*string//行驶证号",
	    "extraDatas":{
	    		"!string":"*string//扩展的 key-value 组合"
	    	}
		}
	}

	遗留问题: 如果 key-value 对中, 值的类型为对象, 且该对象整体为可选参数的时, 该如何表示。
```

## 示例 
```
	{
		"userId":"*string //当前登录用户ID",
	    "vehicleModelId":"*int",
		"price":{
			"basePrice":"*currency//每日租价",
			"overtimePrice":"*currency//超时价",
			"carDeposit":"*currency//用车押金",
			"trafficTicketDeposit":"*currency//违章押金",
			"depositReturnTime":"*int//违章押金退还周期(天)"
		}
	}

```

