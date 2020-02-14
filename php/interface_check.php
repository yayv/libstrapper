<?php

// TODO: 1. read interface file 
// TODO: 2. 

$status = [
	'NOT_START',
	'API_START',
	'TITLE_LINE',
	'BODY_START',
	'BODY_END',
	'IN_DATA',
];

function usage($prog)
{
	echo "usage: $prog <interface_file.md>\n";
	return ;
}

function parseBody($methodNo, $methodName, $lines)
{
	$INFOKEYS = ["No","URL","NAME","METHOD","FORMAT","REQUEST","RESPONSE","TEST","NOTE","TODO","STATUS"];
	
	$arrDatas = array(
		"NO"=>$methodNo,
		"NAME"=>$methodName,
		"URL"=>'',
		"REQUEST"=>"",
		"FORMAT"=>"JSON", // JSON/FORM
		"RESPONSE"=>"",
		"NOTE"=>"",
		"STATUS"=>"",
		"TODO"=>"",
	);

	$arrExtra = array();
	$iskey = false;
	$key = false;

	echo "API:",$methodNo,"[",$methodName,"]\n";
	#print_r($lines);
	#echo "[------------]\n";

	foreach($lines as $k=>$v)
	{
		#echo $v,"\n";
		#continue;
		if($ret = preg_match("/```/",$v, $matches))
		{
			continue ;
		}

		$ret = preg_match("/(^[ a-zA-Z]*):(.*)/",$v, $matches);
		if($ret)
		{
			$key = strtoupper($matches[1]);

			// end last key, or start new key
			if( array_key_exists($key, $arrDatas) )
			{
				$iskey = true;
				$arrDatas[$key] = $matches[2];
			}
			else
			{
				$iskey = false;
				$arrExtra[$key] = $matches[2];
			}
		}
		else
		{
			if(!$key) continue ; // 尚未进入API

			if($iskey)
				$arrDatas[$key] .= $v;
			else
				$arrExtra[$key] .= $v;	
		}
	}

	#if($methodNo=='U18')
	{
		checkKeyValue($methodName, $arrDatas, $arrExtra);
	}

	if(count($arrDatas)==0)
		return false;
	else
		return $arrDatas;
}

function checkKeyValue($api, $arrDatas, $arrExtra)
{
	$MUSTKEYS = ["URL","REQUEST","RESPONSE"];
	foreach($MUSTKEYS as $v)
	{
		if(!isset($arrDatas[$v]) || ""==$arrDatas[$v])
		{
			// TODO: wrong
			echo "\tKEY:",$v, " 必须设置\n";
			#debug_print_backtrace();
			return false;
		}
	}

	if(''!=trim($arrDatas['REQUEST']))
	{
		$req = json_decode($arrDatas['REQUEST']);
		$msg = json_last_error_msg();
		if($msg!='No error') 
		{
			echo "  REQUEST: \t",$msg, "\n";
			print_r($req);
		}
	}
	else
	{
		// DO NOTHING
	}

	$res = json_decode($arrDatas['RESPONSE']);
	$msg = json_last_error_msg();
	if($msg!='No error') 
	{
		echo "  RESPONSE: \t",$msg, "\n";
		print_r($res);
	}

	if(isset($arrDatas['TODO']) && ''!=$arrDatas['TODO'])
	{
		echo '  待完成:',$arrDatas['TODO'],"\n";
	}
	#print_r(array($req, $res));
}

function scanInterfaceFile($filename)
{
	// ---------------
	$startLine = "# interface start";
	$modelTitleLine = "/##[ \t]*MODEL:[ ]*(.*)/";
	$apiTitleLine = "/###[ \t]*([a-zA-Z]*[0-9]*):[ ]*(.*)/";
	$apiBodyBorder = "/```/";
	// ---------------

	$lines = file($filename);

	$start  = false;
	$status = 'NOT_START';
	$currentAPI = '';
	$currentNo  = '';
	$data = array();

	// 从 # Interface Start 行开始
	foreach($lines as $k=>$v)
	{
		if(!$start)
		{
			if( 0==strpos( trim($v), $startLine) )
			{
				$start = true ;
				$status = 'API_START';
			}

			# not start, skip 
			continue;
		}

		# go on parse
		if($start)
		{
			$ret = preg_match($modelTitleLine, $v, $matches);
			if($ret)
			{
				echo "MODEL:",$matches[1],"\n";
				continue;
			}

			
			$ret = preg_match($apiTitleLine, $v, $matches);
			if($ret)
			{
				// BODY 没有正常结束				
				#parseBody($currentNo, $currentAPI, $data);

				$currentNo = $matches[1];
				$currentAPI = $matches[2];
				$data = array();
				continue;
			}

			if($status=='IN_DATA')
			{
				$ret = preg_match($apiBodyBorder, $v, $matches);
				if($ret)
				{
					// end data
					// call parse data
					$data[] = $v;
					$status = "BODY_END";
					parseBody($currentNo, $currentAPI, $data);
					continue ;
				}
				else
				{
					$data[]= $v;
				}
			}
			else //($status!='IN_DATA')
			{
				$ret = preg_match($apiBodyBorder, $v, $matches);
				if($ret)
				{
					$status = 'IN_DATA';
					$data = array();
					$data[] = $v;
					continue;
				}
			}
		}
	}
}

#$options = getopt("f:hp:");
#var_dump($options);
#die();

$infile = false;
$argv = $_SERVER['argv'];

if(count($argv)>1) {
	$infile = $argv[1];
}

if($infile)
	scanInterfaceFile($infile);
else
	showUsage($_SERVER['argv'][0]);

echo "\n";