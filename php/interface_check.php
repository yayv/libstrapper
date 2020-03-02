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
	echo "usage: \n";
	echo "\t$prog <interface_format_file> <template_file> <output_file>\n";
	#echo "\t$prog -f <interface_format_file> -t <template_file> -o <output_file>\n";
	echo "\t$prog <interface_file.md>\n";
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

	if($methodNo=='') {print_r(array($methodNo,$methodName,$lines));die('kii');}
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
	$startLine = "# Interface Start";
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
	$apis = array();

	// 从 # Interface Start 行开始
	foreach($lines as $k=>$v)
	{
		if(!$start)
		{
			if( 0===strpos( trim($v), $startLine) )
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
			#echo $k,$v,"\n";

			if($status=='IN_DATA')
			{
				$ret = preg_match($apiBodyBorder, $v, $matches);
				if($ret)
				{
					// end data
					// call parse data
					$data[] = $v;
					$status = "BODY_END";
					$apis[] = parseBody($currentNo, $currentAPI, $data);
					continue ;
				}
				else
				{
					$data[]= $v;
				}
			}
			else //($status!='IN_DATA')
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

	return $apis;
}

function printAPI($API, $template, $outfile=false)
{
	$f = false;
	if($outfile!=false)
	{
		$f = fopen($outfile);
	}

	if($f)
	{
		ob_start();
	}



	if($f)
	{
		ob_end_clean();
		$c = ob_get_contents();
		fwrite($f, $c);
	}

	if($f)
	{
		fclose($f);
	}
}

function main()
{
	/*
	$args = getopt("f:t:o:");
	*/
	$argv 		= $_SERVER['argv'];
	$formatFile = isset($argv[1])?$argv[1]:"";
	$templateFile = isset($argv[2])?$argv[2]:"";
	$outputFile = isset($argv[3])?$argv[3]:"";

	if( !is_file($formatFile) || !is_file($templateFile) )
	{
		usage($_SERVER['argv'][0]);
		return ;
	}

	if($formatFile && is_file($formatFile))
	{
		$apis = scanInterfaceFile($formatFile);

		$dump = array();
		foreach($apis as $k=>$v)
		{
			$dump[$v['URL']] = $v['REQUEST'];
		}

		print_r($dump);
		return ;
	}
	else if($formatFile)
	{
		echo 'Filename:',$formatFile," is not a validate file\n";
	}
	else
	{
		usage($_SERVER['argv'][0]);
	}

	echo "\n";
}

main();
