<?php

function createContents($apis)
{
	foreach($apis as $k=>$v)
	{
		/*
		echo trim($v['URL']),"\n";
		echo trim($v['NO']),"\n";
		echo trim($v['NAME']),"\n";
		echo trim($v['NOTE']),"\n";
		echo trim($v['STATUS']),"\n";
		echo trim($v['LASTUPDATE']),"\n";
		*/
		if(isset($v['STATUS']) && 'DONE'==trim($v['STATUS'])) 
			$done = 'X'; 
		else
			$done = ' ';
		echo "- [",$done,"] ",trim($v['NO']),":",trim($v['NAME']),"\n";
	}

	return ;
}

function listUpdate($newapis, $oldconfig)
{
	if(is_file($oldconfig))
		include_once($oldconfig);

	foreach($newapis as $k=>$v)
	{
	}

	return ;
}

function main()
{
	$argv = $_SERVER['argv'];
	$configfile = $argv[1];


	if(is_file($configfile))
		include_once($configfile);

	createContents($apis);
	
	#print_r($apis);

}

main();



