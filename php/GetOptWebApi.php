<?php
ini_set("display_errors","on");

class GetOptWebApi
{
	public function __construct()
	{
		$this->types = array(
			// 基础数据类型
			"int", "float", "double", "string","text","bool",

			// 扩展类型
			"year","month", "day","age","currency", // 数字
			"date",	"time", "datetime", "phone","mobile", // 带格式符号的数字
			"weekday", // 字母组合 
			"idcard", "plateNumber","verify","retCode", "MD5", // 字母数字组合
			"base64","email", "inlineImage",// 特定格式的字母数字符号的组合
			"username","password", // 有格式要求和一定顺序要求的字母数字符号的组合
			"lower","upper","letter", // 字母、数字的子集的组合
		);

		// DONE: int email
		// TODO: float", "double", "age",			"date",	"time", "datetime","year","phone","mobile", "base64","MD5","username","password","lower","upper","letter","string",

		$this->errors = array(
			"DATA_NOT_MATCHED" => "数据格式不匹配",
			"DATA_NOT_IN_VALID_RANGE" => "数据超合理范围",
			"DATA_NOT_IN_SET_RANGE" => "数据超出要求范围",
			"TYPE_NO_MATCHED" => "没有匹配的类型",
			"TYPE_WITHOUT_METHOD" => "没有匹配的解析方法",
		);

		$this->all_errors = array();
	}

	public function supportFormats()
	{
		// Format Syntax: "[*]<format_name>[data range][:length][#default_value]//COMMENT"
		// data range syntax:
		// 	int float double: (1, 100), (1,100], [1,100),[1,100] 
		//  date time: 
		//  string : enum {papa,mama,grandpa,grandma,grandma-inlaw}
		// example: "role":"*string{papa, mama}:4#papa//role name 
		// 格式语法 : "[*|#]<格式名>[数值范围][:长度][#默认值]//说明"
		// 开头的 *，#，或无，表示该参数项是否必须填写， * 为必填， #为
		// 格式名，目前所支持的格式包括: email, phone, mobile, date, time, datetime, int, float, base64, MD5 ...
		// 取值范围,以{[(三个符号中的一个开始，由{开始则必须由}结束, 如{a,b,c}, 表示为枚举类型; [()] 的组合表示为 时间和数字可以用集合形式表示取值范围如 (1,100], 表示 大于1小于等于100的范围.
		// :长度,表示为需要检查的变量里原始值的字符长度
		// 默认值，当取值失败，或者取值超范围时，以默认值做为返回值，同时发出一个错误信息
		// 说明，参数格式的表达过于技术化，需要有给产品或业务相关人员看得懂的说明，更好的表达这些设置的目的
		$formats = array(
			"int"  =>"[+-]?[0-9]*", 
			"float" =>"[+-]?[0-9]*\.[0-9]*", 
			"double"=>"[+-]?[0-9]*\.[0-9]*", 
			"string"=>".*", 
			"text"  =>".*", 
			"bool"  =>"", 

			// 扩展类型
			"year" => "[0-9]{4}",
			"month"=> "[12][0-9]",
			"date",	
			"time", 
			"datetime", 
			"day",
			"age",
			"currency", // 数字

			"phone","mobile", // 带格式符号的数字
			"weekday", // 字母组合 
			"idcard", "plateNumber","verify","retCode", "MD5", // 字母数字组合
			"base64","email", "inlineImage",// 特定格式的字母数字符号的组合
			"username","password", // 有格式要求和一定顺序要求的字母数字符号的组合
			"lower","upper","letter", // 字母、数字的子集的组合
		);
	}

	public function getAllErrors()
	{
		return $this->all_errors;
	}

	public function getLastError()
	{
		return $this->last_error;
	}

	public function cleanErrors()
	{
		unset($this->all_errors);
		$this->all_errors = array();
	}

	private function getFormat($format)
	{
		$ret = preg_match("/([\*|#])?([0-9a-zA-Z@]*)(([\{\[\(])(.*)([\)\]\}]))?(:([0-9]*))?(#([^\/]*))?(\/\/(.*))?/",$format, $matches);
		if($ret)
		{
			$format_result = array(
				"option"	=>$matches[1],
				"name"		=>$matches[2],
				"left"		=>$matches[4],
				"range"		=>$matches[5],	
				"right"		=>$matches[6],
				"length"	=>$matches[8],
				"default"	=>$matches[10],
				"comment"	=>$matches[12]);
		}

		return $format_result;
	}

	public function getValue($var, $format)
	{
		$f = $this->getFormat($format);

		if(!$f) {$this->last_error = "format can not be parse";return false;}

		if(!in_array($f['name'],$this->types))
		{
			$this->last_error = $this->errors['TYPE_NO_MATCHED'];
			$this->all_errors[] = $this->last_error ;
			return false; 
		}

		if(method_exists($this,'CHECK'.$f['name']))
		{
			$result = call_user_func(array("GetOptW", 'CHECK'.$f['name']), $f, $var);
			return $result;
		}
		else
		{
			$this->last_error = $this->errors['TYPE_WITHOUT_METHOD'];
			$this->all_errors[] = $this->last_error ;
			return false; 
		}
	}

	public function CHECKemail($format, $value)
	{
		$result = '';
		$ret = preg_match("/[a-zA-Z]+[0-9a-zA-Z\-\._]*@[a-zA-Z0-9\.]*.[a-zA-Z]*/",$value, $matches);

		//echo $value,'<pre>';print_r($matches);print_r($format);
		if($ret)
		{
			$result = $matches[0];

			// email类型，只支持range检查，不做数值范围检查
			if($format['left']=='{')
			{
				$enum = explode(",",$format['range']);

				if(in_array($result, $enum))
					return $result;
				else
				{
					$this->last_error = $format['name'].":格式正确; 但不在 枚举 范围内。";
					$this->all_errors[] = $this->last_error;
					return false;
				}
			}

			// 不做取值范围检查
			return $result;
		}
		else
		{
			$this->last_error = $format['name'].':'.$this->errors['DATA_NOT_MATCHED'];
			$this->all_errors[] = $this->last_error;
			return false;
		}
	}

	public function CHECKint($format, $value)
	{
		$result = '';
		$ret = preg_match("/(\+|\-)?[0-9]*/",$value, $matches);

		if($ret)
		{
			$result = $matches[0];

			if( $format['left']=='(' || $format['left']=='[' )
			{
				$range = explode(",",$format['range']);
				$min = intval($range[0]);
				$max = intval(array_pop($range));

				$outofrange = false;
				if($format['left']=='(' && intval($result) <= intval($min) )
				{
					$outofrange = true;
				}
				if($format['left']=='[' && intval($result) < intval($min) )
					$outofrange = true;
				if($format['right']==')' && intval($result) >= intval($max) )
					$outofrange = true;
				if($format['right']==']' && intval($result) > intval($max) )
					$outofrange = true;

				if($outofrange==true)
				{
					if($format['default']!='')
						$result = intval($format['default']);
					else
						$result = false;
				}
			}
			else
			{
				if($mathes[0]=='' && $format['default']!='')
					$result = intval($format['default']);
				else if($matches[0]=='' && $format['default']=='')
					$result = false;
				else
					$result = intval($matches[0]);
			}

			// 不做取值范围检查
			return $result;
		}
		else
		{
			$this->last_error = $format['name'].':'.$this->errors['DATA_NOT_MATCHED'];
			$this->all_errors[] = $this->last_error;
			return false;
		}
	}

	public function CHECKfloat($format, $value)
	{
		$result = '';
		$ret = preg_match("/(\+|\-)?[0-9\.]*/",$value, $matches);

		if($ret)
		{
			$result = $matches[0];

			if( $format['left']=='(' || $format['left']=='[' )
			{
				$range = explode(",",$format['range']);
				$min = floatval($range[0]);
				$max = floatval(array_pop($range));

				$outofrange = false;
				if($format['left']=='(' && floatval($result) <= $min )
					$outofrange = true;
				if($format['left']=='[' && floatval($result) < $min )
					$outofrange = true;
				if($format['right']==')' && floatval($result) >= $max )
					$outofrange = true;
				if($format['right']==']' && floatval($result) > $max )
					$outofrange = true;

				if($outofrange==true)
				{
					if($format['default']!='')
						$result = floatval($format['default']);
					else
						$result = false;
				}
			}			
			else
			{
				if($mathes[0]=='' && $format['default']!='')
					$result = floatval($format['default']);
				else if($matches[0]=='' && $format['default']=='')
					$result = false;
				else
					$result = floatval($matches[0]);
			}

			// 不做取值范围检查
			return $result;
		}
		else
		{
			$this->last_error = $format['name'].':'.$this->errors['DATA_NOT_MATCHED'];
			$this->all_errors[] = $this->last_error;
			return false;
		}
	}

	public function CHECKdouble($format, $value)
	{
		return $this->CHECKfloat($format, $value);
	}

	public function CHECKage($format, $value)
	{
		$result = '';
		$ret = preg_match("/(\+|\-)?[0-9]*/",$value, $matches);

		if($ret)
		{
			$result = $matches[0];

			if( intval($result)<0 && intval($result)>120 )
			{
				$this->last_error = $format['name'].':'.$this->errors['DATA_NOT_IN_VALID_RANGE'];
				$this->all_errors[] = $this->last_error;
				return false;
			}

			if( $format['left']=='(' || $format['left']=='[' )
			{
				$range = explode(",",$format['range']);
				$min = intval($range[0]);
				$max = intval(array_pop($range));

				$outofrange = false;
				if($format['left']=='(' && intval($result) <= intval($min) )
				{
					$outofrange = true;
				}
				if($format['left']=='[' && intval($result) < intval($min) )
					$outofrange = true;
				if($format['right']==')' && intval($result) >= intval($max) )
					$outofrange = true;
				if($format['right']==']' && intval($result) > intval($max) )
					$outofrange = true;

				if($outofrange==true)
				{
					if($format['default']!='')
						$result = intval($format['default']);
					else
						$result = false;
				}
			}
			else
			{
				if($mathes[0]=='' && $format['default']!='')
					$result = intval($format['default']);
				else if($matches[0]=='' && $format['default']=='')
					$result = false;
				else
					$result = intval($matches[0]);
			}

			// 不做取值范围检查
			return $result;
		}
		else
		{
			$this->last_error = $format['name'].':'.$this->errors['DATA_NOT_MATCHED'];
			$this->all_errors[] = $this->last_error;
			return false;
		}
	}

	public function CHECKphone($format, $value)
	{
		$result = '';
		$ret = preg_match("/(\+|\-)?[0-9 \-]*/",$value, $matches);

		if($ret)
		{
			$result = $matches[0];

			if( $format['left']=='(' || $format['left']=='[' )
			{
				// 电话号码不做范围检查，但可以做枚举检查
			}
			elseif($format['left']=='{')
			{
				$enum = explode(",",$format['range']);

				if(in_array($result, $enum))
					return $result;
				else
				{
					$this->last_error = $format['name'].":格式正确; 但不在 枚举 范围内。";
					$this->all_errors[] = $this->last_error;
					return false;
				}
			}			
			else
			{
				if($matches[0]=='' && $format['default']!='')
					$result = $format['default'];
				else if($matches[0]=='' && $format['default']=='')
					$result = false;
				else
					$result = $matches[0];
			}

			// 不做取值范围检查
			return $result;
		}
		else
		{
			$this->last_error = $format['name'].':'.$this->errors['DATA_NOT_MATCHED'];
			$this->all_errors[] = $this->last_error;
			return false;
		}
	}

	public function CHECKmobile($format, $value)
	{
		$result = '';
		$ret = preg_match("/(\+|\-)?[0-9 \+]*/",$value, $matches);

		if($ret)
		{
			$result = $matches[0];

			if( $format['left']=='(' || $format['left']=='[' )
			{
				// 手机号不需要检查范围
			}
			elseif($format['left']=='{')
			{
				$enum = explode(",",$format['range']);

				if(in_array($result, $enum))
					return $result;
				else
				{
					$this->last_error = $format['name'].":格式正确; 但不在 枚举 范围内。";
					$this->all_errors[] = $this->last_error;
					return false;
				}
			}	
			else
			{
				if($matches[0]=='' && $format['default']!='')
					$result = $format['default'];
				else if($matches[0]=='' && $format['default']=='')
					$result = false;
				else
					$result = $matches[0];
			}

			// 不做取值范围检查
			return $result;
		}
		else
		{
			$this->last_error = $format['name'].':'.$this->errors['DATA_NOT_MATCHED'];
			$this->all_errors[] = $this->last_error;
			return false;
		}
	}

	public function CHECKcurrency($format, $value)
	{
		$result = '';
		$ret = preg_match("/(\+|\-)?[0-9]*\.[0-9]{2}/",$value, $matches);

		return $this->CHECKfloat($format, $value);
	}

	public function CHECKinlineImage($format, $value)
	{
		// data:image/png;base64,		
	}

	/**
	 * format 是格式字符串, 可以单级可以多级，
	 */
	public function checkFormat($format, $obj)
	{
		$noerror = 'No error';

		$json = json_decode($format);

		$str = json_last_error_msg();

		if($noerror==$str)
		{
			$ret = $this->parseData($json, $obj);		
			return $ret;
		}
		else
			return false;
	}

	public function parseData($json, $obj)
	{
		if(is_array($json))
		{
			return $this->parseArray($json, $obj);
		}

		if(is_object($json))
		{
			return $this->parseObject($json, $obj);
		}

		// TODO: 能接受没有 [] {} 包裹的纯值吗?
		return 'false';
		return false;
	}

	public function dataType($format, $obj)
	{

	}

	private function parseArray($format, $obj)
	{
		if(!is_array($obj))
		{
			$this->error_msg = "DATA_NOT_MATCHED";
			return false;
		}

		if(count($format)>1)
		{
			// TODO: 
			$this->error_msg = "DATA_NOT_SUPPORT_MULTIFORMAT_ARRAY";
			return false;
		}

		foreach($obj as $v)
		{
			if(is_array($v))
			{

			}

			if(is_object($v))
			{

			}

			// is data
			// int float string ...
		}

		return "in array";
	}

	private function parseObject()
	{
		return "in object";
	}
}


