<?php

// TODO: 1. read interface file 
// TODO: 2. 

class apifile
{

	function __construct()
	{
		$this->status = [
			'not_start',
			'in_file',
			'in_menu',
			'in_model',
			'in_api',
			'in_body', 
			'end_file'
		];

		$this->pregs = [
			"menu"=>"/\#[ \t]*MENU.*/",
			"model"=>"/\#\#[ \t]*MODEL.*/",
			"api"=>"/\#\#\#[ \t]*(.*)/",
			"body"=>"/```/",
			"body_end"=>"/```/",
		];

		$this->INFOKEYS = ["No","URL","NAME","METHOD","FORMAT","REQUEST","RESPONSE","TEST","NOTE","TODO","STATUS"];
		
		$this->arrDatas = array(
			"NO"=>'',
			"NAME"=>'',
			"URL"=>'',
			"REQUEST"=>"",
			"FORMAT"=>"", // JSON/FORM
			"RESPONSE"=>"",
			"NOTE"=>"",
			"STATUS"=>"",
			"TODO"=>"",
		);

		$this->statusStack = [];
	}

	function parseBody($methodNo, $methodName, $lines)
	{

		$arrExtra = array();
		$iskey = false;
		$key = false;	

		if($methodNo=='') {print_r(array($methodNo,$methodName,$lines));die('kii');}
		echo "API:",$methodNo,"[",$methodName,"]\n";
		#print_r($lines);
		#echo "[------------]\n";

		foreach($lines as $k=>$v)
		{
			# echo $v,"\n";
			# continue;
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
		$status = 'in_file';// not_start in_file in_menu in_model in_interface in_body end_file
		$currentAPI = '';
		$currentNo  = '';
		$data = array();
		$apis = array();

		$pregs = ['menu'=>"/#[ \t]*MENU/","menu_end"=>"/#/"];
		$menu  = [];

		foreach($lines as $k=>$v)
		{
			$ret = false;
			foreach($this->pregs as $kk=>$vv)
			{
				$ret = preg_match($vv, $v, $matches);
				if($ret)
				{
					echo $vv,":\n";
					echo "\t",$kk,"\n";
				}
			}
			if($ret)
				echo "\n\n";
			/*
			switch($status)
			{
				case 'in_file':
					// todo: find model, find menu , find interface, find eof
					$this->in_file($v);
					break;
				case 'in_menu':
					// todo: clean
					$this->in_menu($v);
					break;
				case 'in_model':
					// todo: save model name
					$this->in_model($v);
					break;
				case 'in_api':
					// todo: get/set, find body
					$this->in_api($v);
					break;
				case 'in_body':
					// get/set status
					$this->in_body($v);
					break;
				default:
					echo '';
					die();
			}
			*/
		}
		/*
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
		*/

		return $apis;
	}

	function in_file($v)
	{
		#echo $v,"\n";

		// "menu"=>"/#[ \t]*MENU.*/",
		// "model"=>"/##[ \t]*MODEL.*/",
		// "api"=>"/###[ \t]*(.*)/",
		// "body"=>"/```/",
		#echo $this->pregs['menu'],"\n",$v,"\n";die();
		if( $ret = preg_match($this->pregs['menu'],$v,$matches) )
		{
			$this->status[] = 'in_menu';
		}

		#echo $this->pregs['model'],"\n";
		if( $ret = preg_match($this->pregs['menu'],$v,$matches) )
		{
			die('b');
		}

		#echo $this->pregs['api'],"\n";
		if( $ret = preg_match($this->pregs['menu'],$v,$matches) )
		{
			die('c');
		}

		#echo $this->pregs['body'],"\n";
		if( $ret = preg_match($this->pregs['menu'],$v,$matches) )
		{
			die('d');
		}

	}

	function in_menu($v)
	{
		#echo $this->pregs['model'],"\n";
		if( $ret = preg_match($this->pregs['menu'],$v,$matches) )
		{
			die('b');
		}

		#echo $this->pregs['api'],"\n";
		if( $ret = preg_match($this->pregs['menu'],$v,$matches) )
		{
			die('c');
		}

		#echo $this->pregs['body'],"\n";
		if( $ret = preg_match($this->pregs['menu'],$v,$matches) )
		{
			die('d');
		}

	}

	function in_model($v)
	{

	}

	function in_api($v)
	{

	}

	function in_body($v)
	{

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

}

function usage($prog)
{
	echo "usage: \n";
	echo "\t$prog <interface_file.md> \n";

	return ;
}

function main()
{
	/*
	$args = getopt("f:t:o:");
	*/
	$argv 		= $_SERVER['argv'];
	$formatFile = isset($argv[1])?$argv[1]:"";

	if( !is_file($formatFile) )
	{
		usage($_SERVER['argv'][0]);
		return ;
	}

	if($formatFile && is_file($formatFile))
	{
		$api = new apifile();
		$apis = $api->scanInterfaceFile($formatFile);

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

