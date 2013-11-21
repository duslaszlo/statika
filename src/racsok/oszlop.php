<a href="show.php">Vissza</a>

<?php

$program='cmm';
if ($ip = getenv('REMOTE_ADDR')) {}
elseif ($ip = getenv('HTTP_CLIENT_IP')) {}
elseif ($ip = getenv('HTTP_X_FORWARDED_FOR')) {}
elseif ($ip = getenv('HTTP_X_FORWARDED')) {}
elseif ($ip = getenv('HTTP_FORWARDED_FOR')) {}
elseif ($ip = getenv('HTTP_FORWARDED')) {}
else { $ip = "0.0.0.0"; }

$con = mysql_connect("localhost","leslie","garfield");
if (!$con)   {  die('Could not connect: ' . mysql_error());  }
mysql_select_db("vir", $con);
$sql = "select id,max(belepes) as belepes from logs where ipszam='".$ip."' and program='' group by id;";
//echo $sql."<br />";
$result = mysql_query($sql);
$id = 0;
while ($row=mysql_fetch_array($result)) { $id=$row['id']; }
if ($id > 0) {
  $sql = "update logs set program = '".$program."' where id='".$id."';";
  if (!mysql_query($sql,$con)) { die('Error: ' . mysql_error()); }
  $sql = "select kilepes from logs where id='".$id."';";
  //echo $sql."<br />";
  $result = mysql_query($sql);
  while ($row=mysql_fetch_array($result)) { $kilepes=$row['kilepes']; }
}

if ($id == 0) {
    $sql = "select id,max(belepes) as belepes from logs where ipszam='".$ip."' and program='".$program."' group by id;";
    //echo $sql."<br />";
    $result = mysql_query($sql);
    while ($row=mysql_fetch_array($result)) { $id=$row['id']; }
    $sql = "select kilepes from logs where id='".$id."';";
    //echo $sql."<br />";
    $result = mysql_query($sql);
    while ($row=mysql_fetch_array($result)) { $kilepes=$row['kilepes']; }
}

//Ide van téve az idõbélyeg ellenõrzés...
$pontosido = substr(date(DATE_ATOM),0,10).' '.substr(date(DATE_ATOM),11,8);
//echo $pontosido.' -->> '.$kilepes."<br />";
if ($pontosido>$kilepes) { 
    //Lejárt az idõ...
    echo "<h1><a href='/vir/index.php'>A megadott idõkeret lejárt, újra be kell lépni.</a></h1>";
    exit();} 
  else
  {
  // Még belefér az idõbe
  if ((strtotime($kilepes) - strtotime($pontosido)) < 30 * 60) {
      $timestamp = strtotime($kilepes);
      $timestamp1 = $timestamp + 3600;  // ez egy óra ??  
      $kilepes = date('Y/m/d G:H:i',$timestamp1);
      $sql = "update logs set kilepes = '".$kilepes."' where id='".$id."';";
      if (!mysql_query($sql,$con)) { die('Error: ' . mysql_error()); }
    }
  } 
  
mysql_close($con); 

//echo " Mûködik...";

?>

<?php

function szog($x,$y)
{ $g = 0;
  if (($y==0) and ($x>0)) { $g = M_PI/2;}
  if (($y==0) and ($x<0)) { $g = (3/2)*M_PI;}
  if (($x==0) and ($y>0)) { $g = 0;}
  if (($x==0) and ($y<0)) { $g = M_PI;}
  if (($x>0) and ($y>0))  { $g = atan($x/$y);}
  if (($x>0) and ($y<0))  { $g = M_PI_2+atan(abs($y)/$x);}
  if (($x<0) and ($y>0))  { $g = ((3/2)*M_PI)+atan($y/abs($x));}
  if (($x<0) and ($y<0))  { $g = M_PI+atan(abs($x)/abs($y));}
  return $g;
}

$azonosito = $_POST[azonosito];
//echo $azonosito;

// A képméret beállítása
$con = mysql_connect("localhost","leslie","garfield");
if (!$con)  {  die('Could not connect: ' . mysql_error());  }
mysql_select_db("hitech", $con);
// Konstansok
$maxx = 0;$maxy = 0;$maxz = 0;
$minx = 50000;$miny = 50000;$minz = 50000;
$picx = 300; $picy = 700;$deltax = 10; $deltay = 10;
$ra   = 0.01745329252;
// A vizsgált csomópont
$cspvizsg = $_POST[csp];
//A vizsgált szint
$szint = 12345;
//Az elfordítás szöge (fok)
$alfa = $_POST[alfa] * $ra;$beta = $_POST[beta] * $ra;$gamma = $_POST[gamma] * $ra;
//echo 'Alfa:'.$_POST[alfa].'  beta:'.$_POST[beta].' Gamma:'.$_POST[gamma]."<br />";
//echo 'Alfa:'.$alfa.'  beta:'.$beta.' Gamma:'.$gamma."<br />";
// Adatbeolvasás
// A csomópontok x,y,z koordinátái
$sql = "SELECT x,y,z FROM csomopont WHERE azonosito = '$azonosito' order by csomopont";  
//echo $sql;  
$result = mysql_query($sql);
$cspindex = 0;
while ($row = mysql_fetch_array($result))  { 
  $cspindex++;$x[$cspindex] = $row['x'];
  if ($x[$cspindex]>$maxx) {$maxx = $x[$cspindex];}
  if ($x[$cspindex]<$minx) {$minx = $x[$cspindex];}
  $y[$cspindex] = $row['y'];
  if ($y[$cspindex]>$maxy) {$maxy = $y[$cspindex];}
  if ($y[$cspindex]<$miny) {$miny = $y[$cspindex];}
  $z[$cspindex] = $row['z'];
  if ($z[$cspindex]>$maxz) {$maxz = $z[$cspindex];}
  if ($z[$cspindex]<$minz) {$minz = $z[$cspindex];}       
  }

// A kép felbontási arányainak meghatározása
$aranyx = $maxx / ($picx - ($deltax * 2)) ;
$aranyy = $maxy / ($picy - ($deltay * 2)) ;
//$aranyx = $aranyx * 1.4;
if ($aranyx > $aranyy) {$aranyy =$aranyx;} else {$aranyx =$aranyy;}
if ($aranyx > $aranyy) {$arany =$aranyx;} else {$arany =$aranyy;}
//$aranyy = $aranyx ;   //???
//$aranyy = $maxy / ($picy - ($deltay * 2)) ;
//$deltay = ($picy - ($maxy - $miny))/2 ;
//echo '<p>deltay:'.$deltay.' maxy:'.$maxy.' miny:'.$miny.' aranyx:'.$aranyx.' maxx:'.$maxx.' picy:'.$picy.'</p>';
  
// A rudak kezdõ- és végkoordinátái, szine és a vastagsága
$sql = "SELECT kezdocsp,vegecsp,piros,zold,kek,vastagsag FROM rud WHERE azonosito = '$azonosito' ";  
//echo $sql;  
$result = mysql_query($sql);
$rudindex = 0;
while ($row = mysql_fetch_array($result))  { 
  $rudindex++;
  $kezdocsp[$rudindex] = $row['kezdocsp'];
  $vegecsp[$rudindex] = $row['vegecsp'];         
  $piros[$rudindex] = $row['piros'];
  $zold[$rudindex] = $row['zold'];
  $kek[$rudindex] = $row['kek'];
  $vastagsag[$rudindex] = $row['vastagsag'];
  }

//Súlypontszámítás
$sql = "SELECT avg(x) as sx ,avg(y) as sy,avg(z) as sz FROM `csomopont` WHERE `azonosito` = '$azonosito'";
//echo $sql; 
$result = mysql_query($sql);
while ($row = mysql_fetch_array($result))  {
    $sx = $row['sx'];$sy = $row['sy'];$sz = $row['sz'];
}
//echo '<p> sx:'.$sx.'  sy:'.$sy.'  sz:'.$sz.'</p>'; 

//A színek beállítása - a vizsgált csomópont/szint más színû
for ($i=1; $i<=$rudindex; $i++)  {
    $cx1[$i] = $x[$kezdocsp[$i]];$cy1[$i] = $maxy - $y[$kezdocsp[$i]];$cz1[$i] = $z[$kezdocsp[$i]];    
    $cx2[$i] = $x[$vegecsp[$i]];$cy2[$i] = $maxy - $y[$vegecsp[$i]];$cz2[$i] = $z[$vegecsp[$i]];    
    $szin[$i] = 'rgb('.$piros[$i].','.$zold[$i].','.$kek[$i].');';
    $szin[$i].= 'stroke-width:';
    if (($kezdocsp[$i]==$cspvizsg) or ($vegecsp[$i]==$cspvizsg) or 
		($y[$kezdocsp[$i]]== $szint) or ($y[$vegecsp[$i]]== $szint) ) {$szin[$i] = 'purple;stroke-width:';}
	// A rúd vastagsága...	
	if ($vastagsag[$i]==0) {$vast='0.5';} else {$vast = $vastagsag[$i] / $arany;}
	$szin[$i].=$vast.'"';
}

// Vázrajz X-Y sík (elölnézet)
$filenev1=$azonosito.'_xy.svg';
//echo "<p>$filenev1</p>";
$file = fopen($filenev1,"w");
$sor ='<?xml version="1.0" standalone="no"';$sor.='?';$sor.='>'. "\n";
fputs($file,$sor);$sor ='<';
$sor.='!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">'."\n";
fputs($file,$sor);
$sor ='<svg width="100%" height="100%" version="1.1" xmlns="http://www.w3.org/2000/svg">'."\n";
fputs($file,$sor);
// Keret
/*$x1 = 0;$y1 = 0; 
$sor ='<line x1="'. $x1 .'" y1="'. $y1 .'" x2="'. $picx .'" y2="'. $y1 .'" style="stroke:blue;stroke-width:1"/>'. "\n";
fputs($file,$sor);
$sor ='<line x1="'. $x1 .'" y1="'. $picy .'" x2="'. $picx .'" y2="'. $picy .'" style="stroke:blue;stroke-width:1"/>'. "\n";
fputs($file,$sor);
$sor ='<line x1="'. $x1 .'" y1="'. $y1 .'" x2="'. $x1 .'" y2="'. $picy .'" style="stroke:blue;stroke-width:1"/>'. "\n";
fputs($file,$sor);
$sor ='<line x1="'. $picx .'" y1="'. $y1 .'" x2="'. $picx .'" y2="'. $picy .'" style="stroke:blue;stroke-width:1"/>'. "\n";
fputs($file,$sor);*/
for ($i=1; $i<=$rudindex; $i++)  {
    $x1 = ($cx1[$i] / $aranyx) + $deltax;
    $x2 = ($cx2[$i] / $aranyx) + $deltax;
    $y1 = ($cy1[$i] / $aranyy) + $deltay;        
    $y2 = ($cy2[$i] / $aranyy) + $deltay;        
    $sor ='<line x1="'. $x1 .'" y1="'. $y1 .'" x2="'. $x2 .'" y2="'. $y2 .'" style="stroke:'.$szin[$i]. "/>\n";
    fputs($file,$sor);
}
$sor ='</svg>'. "\n";
fputs($file,$sor);
fclose($file);

// Vázrajz X-Z sík (oldalnézet)
$filenev2=$azonosito.'_xz.svg';
//echo "<p>$filenev2</p>";
$file = fopen($filenev2,"w");
$sor ='<?xml version="1.0" standalone="no"';$sor.='?';$sor.='>'. "\n";
fputs($file,$sor);$sor ='<';
$sor.='!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">'."\n";
fputs($file,$sor);
$sor ='<svg width="100%" height="100%" version="1.1" xmlns="http://www.w3.org/2000/svg">'."\n";
fputs($file,$sor);
// Keret
/*$x1 = 0;$y1 = 0; 
$sor ='<line x1="'. $x1 .'" y1="'. $y1 .'" x2="'. $picx .'" y2="'. $y1 .'" style="stroke:blue;stroke-width:1"/>'. "\n";
fputs($file,$sor);
$sor ='<line x1="'. $x1 .'" y1="'. $picy .'" x2="'. $picx .'" y2="'. $picy .'" style="stroke:blue;stroke-width:1"/>'. "\n";
fputs($file,$sor);
$sor ='<line x1="'. $x1 .'" y1="'. $y1 .'" x2="'. $x1 .'" y2="'. $picy .'" style="stroke:blue;stroke-width:1"/>'. "\n";
fputs($file,$sor);
$sor ='<line x1="'. $picx .'" y1="'. $y1 .'" x2="'. $picx .'" y2="'. $picy .'" style="stroke:blue;stroke-width:1"/>'. "\n";
fputs($file,$sor);*/
//$aranyx = $maxx / ($picx - ($deltax * 2)) ;
//$aranyy = $maxz / ($picy - ($deltay * 2)) ;
//$deltay = ($picy - ($maxz - $minz))/2 ;
for ($i=1; $i<=$rudindex; $i++)  {
    $x1 = ($cz1[$i] / $aranyx) + $deltax;
    $x2 = ($cz2[$i] / $aranyx) + $deltax;
    $y1 = ($cx1[$i] / $aranyy) + $deltay;        
    $y2 = ($cx2[$i] / $aranyy) + $deltay;        
    $sor ='<line x1="'. $x1 .'" y1="'. $y1 .'" x2="'. $x2 .'" y2="'. $y2 .'" style="stroke:'.$szin[$i]. "/>\n";
    fputs($file,$sor);
}
$sor ='</svg>'. "\n";
fputs($file,$sor);
fclose($file);

// Vázrajz Z-Y sík  (felülnézet)
$filenev3=$azonosito.'_zy.svg';
//echo "<p>$filenev3</p>";
$file = fopen($filenev3,"w");
$sor ='<?xml version="1.0" standalone="no"';$sor.='?';$sor.='>'. "\n";
fputs($file,$sor);$sor ='<';
$sor.='!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">'."\n";
fputs($file,$sor);
$sor ='<svg width="100%" height="100%" version="1.1" xmlns="http://www.w3.org/2000/svg">'."\n";
fputs($file,$sor);
// Keret
/*$x1 = 0;$y1 = 0; 
$sor ='<line x1="'. $x1 .'" y1="'. $y1 .'" x2="'. $picx .'" y2="'. $y1 .'" style="stroke:blue;stroke-width:1"/>'. "\n";
fputs($file,$sor);
$sor ='<line x1="'. $x1 .'" y1="'. $picy .'" x2="'. $picx .'" y2="'. $picy .'" style="stroke:blue;stroke-width:1"/>'. "\n";
fputs($file,$sor);
$sor ='<line x1="'. $x1 .'" y1="'. $y1 .'" x2="'. $x1 .'" y2="'. $picy .'" style="stroke:blue;stroke-width:1"/>'. "\n";
fputs($file,$sor);
$sor ='<line x1="'. $picx .'" y1="'. $y1 .'" x2="'. $picx .'" y2="'. $picy .'" style="stroke:blue;stroke-width:1"/>'. "\n";
fputs($file,$sor);*/
//$aranyx = $maxz / ($picx - ($deltax * 2)) ;
//$aranyy = $maxy / ($picy - ($deltay * 2)) ;
//$deltay = ($picy - ($maxy - $miny))/2 ;
for ($i=1; $i<=$rudindex; $i++)  {
    $x1 = ($cz1[$i] / $aranyx) + $deltax;
    $x2 = ($cz2[$i] / $aranyx) + $deltax;
    $y1 = ($cy1[$i] / $aranyy) + $deltay;        
    $y2 = ($cy2[$i] / $aranyy) + $deltay;            	
	// Az eredeti koordináták eltétele
	$dz1[$i] = $x1 ;$dz2[$i] = $x2 ;$dy1[$i] = $y1 ; $dy2[$i] = $y2 ;
	$sor ='<line x1="'. $x1 .'" y1="'. $y1 .'" x2="'. $x2 .'" y2="'. $y2 .'" style="stroke:'.$szin[$i]. "/>\n";
    fputs($file,$sor);
}
$sor ='</svg>'. "\n";
fputs($file,$sor);
fclose($file);

// Vázrajz térbeli nézet (-->> alfa,beta,gamma)

//$aranyx = $aranyx / 4;$aranyy = $aranyy / 4;
//$deltax = $deltax +100;$deltay = $deltay + 100;

$filenev4=$azonosito.'_iso.svg';
//echo "<p>$filenev4</p>";
$file = fopen($filenev4,"w");
$sor ='<?xml version="1.0" standalone="no"';$sor.='?';$sor.='>'. "\n";
fputs($file,$sor);$sor ='<';
$sor.='!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">'."\n";
fputs($file,$sor);
$sor ='<svg width="100%" height="100%" version="1.1" xmlns="http://www.w3.org/2000/svg">'."\n";
fputs($file,$sor);
// Keret
/*$x1 = 0;$y1 = 0; 
$sor ='<line x1="'. $x1 .'" y1="'. $y1 .'" x2="'. $picx .'" y2="'. $y1 .'" style="stroke:blue;stroke-width:1"/>'. "\n";
fputs($file,$sor);
$sor ='<line x1="'. $x1 .'" y1="'. $picy .'" x2="'. $picx .'" y2="'. $picy .'" style="stroke:blue;stroke-width:1"/>'. "\n";
fputs($file,$sor);
$sor ='<line x1="'. $x1 .'" y1="'. $y1 .'" x2="'. $x1 .'" y2="'. $picy .'" style="stroke:blue;stroke-width:1"/>'. "\n";
fputs($file,$sor);
$sor ='<line x1="'. $picx .'" y1="'. $y1 .'" x2="'. $picx .'" y2="'. $picy .'" style="stroke:blue;stroke-width:1"/>'. "\n";
fputs($file,$sor);*/
// A csomópontok elfordítása
for ($i=1; $i<=$cspindex; $i++)  {
    $vx[$i] = $x[$i] - $sx;
    $vy[$i] = $y[$i] - $sy;
    $vz[$i] = $z[$i] - $sz;
}

for ($i=1; $i<=$cspindex; $i++)  {
    $hossz = sqrt($vy[$i]*$vy[$i]+$vz[$i]*$vz[$i]);
	$szog1=szog($vy[$i],$vz[$i]);
    $vz[$i]=$hossz*cos($szog1+$alfa);
    $vy[$i]=$hossz*sin($szog1+$alfa);	
//	echo 'vz[i]:'.$vz[$i].'  vy[i]:'.$vy[$i].' Hossz:'.$hossz.' Alfa:'.$alfa." Szog:".$szog1."<br />";
}
for ($i=1; $i<=$cspindex; $i++)  {    
    $hossz = sqrt($vz[$i]*$vz[$i]+$vx[$i]*$vx[$i]);
	$szog1=szog($vz[$i],$vx[$i]);
    $vx[$i]=$hossz*cos($szog1+$beta);
    $vz[$i]=$hossz*sin($szog1+$beta);	
//	echo 'vx[i]:'.$vx[$i].'  vz[i]:'.$vz[$i].' Hossz:'.$hossz.' Beta:'.$beta." Szog:".$szog1."<br />";
}
for ($i=1; $i<=$cspindex; $i++)  {
    $hossz = sqrt($vy[$i]*$vy[$i]+$vx[$i]*$vx[$i]);
	$szog1=szog($vy[$i],$vx[$i]);
    $vx[$i]=$hossz*cos($szog1+$gamma);
    $vy[$i]=$hossz*sin($szog1+$gamma);	
//	echo 'vx[i]:'.$vx[$i].'  vy[i]:'.$vy[$i].' Hossz:'.$hossz.' Gamma:'.$gamma." Szog:".$szog1."<br />";
}
for ($i=1; $i<=$rudindex; $i++)  {
    $cx1[$i] = $vx[$kezdocsp[$i]];$cy1[$i] = $maxy - $vy[$kezdocsp[$i]];$cz1[$i] = $vz[$kezdocsp[$i]];    
    $cx2[$i] = $vx[$vegecsp[$i]];$cy2[$i] = $maxy - $vy[$vegecsp[$i]];$cz2[$i] = $vz[$vegecsp[$i]];     
    $szin[$i] = 'rgb('.$piros[$i].','.$zold[$i].','.$kek[$i].');';	
	$szin[$i].= 'stroke-width:';
    if (($kezdocsp[$i]==$cspvizsg) or ($vegecsp[$i]==$cspvizsg) or 
		($y[$kezdocsp[$i]]== $szint) or ($y[$vegecsp[$i]]== $szint) ) {$szin[$i] = 'purple;stroke-width:';}
	// A rúd vastagsága...	
	if ($vastagsag[$i]==0) {$vast='0.5';} else {$vast = $vastagsag[$i] / $arany;}
	$szin[$i].=$vast.'"';	    
}
for ($i=1; $i<=$rudindex; $i++)  {
    $x1 = (($cz1[$i] + $sx) / $aranyx) + $deltax ;
    $x2 = (($cz2[$i] + $sx) / $aranyx) + $deltax ;
    $y1 = (($cy1[$i] - $sy) / $aranyy) + $deltay;        
    $y2 = (($cy2[$i] - $sy) / $aranyy) + $deltay;        
    $sor ='<line x1="'. $x1 .'" y1="'. $y1 .'" x2="'. $x2 .'" y2="'. $y2 .'" style="stroke:'.$szin[$i]. "/>\n";
	$dz3[$i] = $x1 ;$dz4[$i] = $x2 ;$dy3[$i] = $y1 ; $dy4[$i] = $y2;
    fputs($file,$sor);
}
$sor ='</svg>'. "\n";
fputs($file,$sor);
fclose($file);

// Vázrajz animált térbeli nézet (-->> alfa,beta,gamma)
$filenev5=$azonosito.'_ani.svg';
//echo "<p>$filenev5</p>";
$file = fopen($filenev5,"w");

$sor ="<svg width='100%' height='100%' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' onload='Init(evt)'>". "\n";fputs($file,$sor);
$sor ="<title>Vazforgatas</title>"."\n";fputs($file,$sor);
$sor ="<script><![CDATA["."\n";fputs($file,$sor);
$sor ="   var SVGDocument = null;"."\n";fputs($file,$sor);
$sor ="   var SVGRoot = null;"."\n";fputs($file,$sor);
$kk =0;
for ($i=1; $i<=$rudindex; $i++)  {
	$kk++;$sor ="   var koordline".$kk." = null;"."\n";fputs($file,$sor);
	$kk++;$sor ="   var koordline".$kk." = null;"."\n";fputs($file,$sor);
}
for ($i=1; $i<=$rudindex; $i++)  {
	$sor ="   var flightPath".$i." = null;"."\n";fputs($file,$sor);
}
$sor ="   function Init(evt) {"."\n";fputs($file,$sor);
$sor ="      SVGDocument = evt.target.ownerDocument;"."\n";fputs($file,$sor);
$sor ="      SVGRoot = SVGDocument.documentElement;"."\n";fputs($file,$sor);
$koordindex = 0;
for ($i=1; $i<=$rudindex; $i++)  {	
	$koordindex ++; $koord[$koordindex]= $z[$i];
	$koordindex ++; $koord[$koordindex]= $y[$i];
}
$kk =0;
for ($i=1; $i<=$rudindex; $i++)  {	
	$kk++;$sor ="      koordline".$kk." = SVGDocument.getElementById('koordline".$kk."');"."\n";fputs($file,$sor);
	$kk++;$sor ="      koordline".$kk." = SVGDocument.getElementById('koordline".$kk."');"."\n";fputs($file,$sor);
	$sor ="      flightPath".$i." = SVGDocument.getElementById('flightPath".$i."');"."\n";fputs($file,$sor);
	$sor ="      window.setTimeout('SetEndpoint".$i."()', 20);"."\n";fputs($file,$sor);	
}
$sor ="}"."\n";fputs($file,$sor);
$kk =0;
for ($i=1; $i<=$rudindex; $i++)  {	
	$sor ="    function SetEndpoint".$i."(evt) {"."\n";fputs($file,$sor);
	$kk++;$sor ="        var matrixline".$kk." = koordline".$kk.".getCTM();"."\n";fputs($file,$sor);
	$kk1 = $kk + 1;
	$sor ="        var matrixline".$kk1." = koordline".$kk1.".getCTM();"."\n";fputs($file,$sor);
	$sor ="        var newPoints = matrixline".$kk.".e + ',' + matrixline".$kk.".f + ' ' + matrixline".$kk1.".e + ',' + matrixline".$kk1.".f;	"."\n";fputs($file,$sor);	
	$sor ="        flightPath".$i.".setAttributeNS(null, 'points', newPoints);"."\n";fputs($file,$sor);
	$sor ="        window.setTimeout('SetEndpoint".$i."()', 10); }"."\n";fputs($file,$sor);	
	$kk++;
}
$sor ="]]></script>"."\n";fputs($file,$sor);
$deltax = $deltax + 50;$deltay = $deltay + 50;

$kk =0;
for ($i=1; $i<=$rudindex; $i++)  {	
	$kk++;
	$x1 = (($z[$kezdocsp[$i]] + $sx) / $aranyx) + $deltax ;
	$y1 = (($maxy - $y[$kezdocsp[$i]] ) / $aranyy) + $deltay ;        
	$x2 = (($vz[$kezdocsp[$i]] + $sx) / $aranyx) + $deltax ;
	$y2 = (($maxy - $vy[$kezdocsp[$i]] -$sy ) / $aranyy) + $deltay;        
	$sor ="    <path id='line".$kk."' d='M".$x1.",".$y1." L".$x2.",".$y2."' fill='none' />"."\n";fputs($file,$sor);
	$kk++;
	$x1 = (($z[$vegecsp[$i]] + $sx) / $aranyx) + $deltax ;
	$y1 = (($maxy - $y[$vegecsp[$i]] ) / $aranyy) + $deltay ;        
	$x2 = (($vz[$vegecsp[$i]] + $sx) / $aranyx) + $deltax ;
	$y2 = (($maxy - $vy[$vegecsp[$i]] -$sy ) / $aranyy) + $deltay;        
	$sor ="    <path id='line".$kk."' d='M".$x1.",".$y1." L".$x2.",".$y2."' fill='none' />"."\n";fputs($file,$sor);
}

for ($i=1; $i<=$rudindex; $i++)  {	
	$sor ="    <polyline id='flightPath".$i."' style='stroke:".$szin[$i]."; fill:none; '/>"."\n";fputs($file,$sor);
}
$kk =0;
for ($i=1; $i<=$rudindex; $i++)  {
	$kk++;
	$sor ="    <circle id='koordline".$kk."' cx='0' cy='0' r='1' onclick='SetEndpoint".$i."(evt)'>"."\n";fputs($file,$sor);
	$sor ="      <animateMotion dur='10s' repeatCount='indefinite' rotate='auto' >"."\n";fputs($file,$sor);
	$sor ="         <mpath xlink:href='#line".$kk."'/>"."\n";fputs($file,$sor);
	$sor ="      </animateMotion>"."\n";fputs($file,$sor);
	$sor ="    </circle>"."\n";fputs($file,$sor);
	$kk++;
	$sor ="    <circle id='koordline".$kk."' cx='0' cy='0' r='1' onclick='SetEndpoint".$i."(evt)'>"."\n";fputs($file,$sor);
	$sor ="      <animateMotion dur='10s' repeatCount='indefinite' rotate='auto' >"."\n";fputs($file,$sor);
	$sor ="         <mpath xlink:href='#line".$kk."'/>"."\n";fputs($file,$sor);
	$sor ="      </animateMotion>"."\n";fputs($file,$sor);
	$sor ="    </circle>"."\n";fputs($file,$sor);
}
/*
// Ez a régi verzió. A vonalmozgatásnál a vonalvastagság is nõ...
$sor ='<svg xmlns="http://www.w3.org/2000/svg"'. "\n";fputs($file,$sor);
$sor ='     xmlns:xlink="http://www.w3.org/1999/xlink"'. "\n";fputs($file,$sor);
$sor ='	width="100%" height="100%"'. "\n";fputs($file,$sor);
$sor ='	onload="startup(evt)"'. "\n";fputs($file,$sor);
$sor ='>'. "\n";fputs($file,$sor);
$sor ='<script>'. "\n";fputs($file,$sor);
$sor ='<![CDATA['. "\n";fputs($file,$sor);
$sor ='xmlns="http://www.w3.org/2000/svg"'. "\n";fputs($file,$sor);
$sor ='var Doc;'. "\n";fputs($file,$sor);
$sor ='var O;'. "\n";fputs($file,$sor);
$sor ='var Boo=true'. "\n";fputs($file,$sor);
$sor ='function T(Boo){'. "\n";fputs($file,$sor);
$sor ='	if (Boo)O.pauseAnimations()'. "\n";fputs($file,$sor);
$sor ='	else O.unpauseAnimations()'. "\n";fputs($file,$sor);
$sor ='	return !Boo'. "\n";fputs($file,$sor);
$sor ='}'. "\n";fputs($file,$sor);
$sor ='function startup(evt){'. "\n";fputs($file,$sor);
$sor ='	O=evt.target'. "\n";fputs($file,$sor);
$sor ='	Doc=O.ownerDocument'. "\n";fputs($file,$sor);
$sor ='	O.setAttribute("onclick","Boo=T(Boo);")'. "\n";fputs($file,$sor);
$sor ='}'. "\n";fputs($file,$sor);
$sor ='//]]>'. "\n";fputs($file,$sor);
$sor ='</script>'. "\n";fputs($file,$sor);

// Keret
$x1 = 0;$y1 = 0; 
$sor ='<line x1="'. $x1 .'" y1="'. $y1 .'" x2="'. $picx .'" y2="'. $y1 .'" style="stroke:blue;stroke-width:1"/>'. "\n";
fputs($file,$sor);
$sor ='<line x1="'. $x1 .'" y1="'. $picy .'" x2="'. $picx .'" y2="'. $picy .'" style="stroke:blue;stroke-width:1"/>'. "\n";
fputs($file,$sor);
$sor ='<line x1="'. $x1 .'" y1="'. $y1 .'" x2="'. $x1 .'" y2="'. $picy .'" style="stroke:blue;stroke-width:1"/>'. "\n";
fputs($file,$sor);
$sor ='<line x1="'. $picx .'" y1="'. $y1 .'" x2="'. $picx .'" y2="'. $picy .'" style="stroke:blue;stroke-width:1"/>'. "\n";
fputs($file,$sor);
//$deltax = 300;$deltay = 100;
for ($i=1; $i<=$rudindex; $i++)  {
    //echo "<br />".'Koordináta:'.$i."<br />";
	if (($dz1[$i] == $dz2[$i]) and ($dy1[$i] == $dy2[$i])) {$dz2[$i] = $dz2[$i] + 1; $dy2[$i] = $dy2[$i] + 1; }
    $regix1 = $dz1[$i] + $deltax;
    $regix2 = $dz2[$i] + $deltax;
    $regiy1 = $dy1[$i] + $deltay;        
    $regiy2 = $dy2[$i] + $deltay;
	//echo 'Régix1:'.$regix1.'  Régiy1:'.$regiy1.'  Régix2:'.$regix2.'  Régiy2:'.$regiy2."<br />";
	if (($dz3[$i] == $dz4[$i]) and ($dy3[$i] == $dy4[$i])) {$dz4[$i] = $dz4[$i] + 1; $dy4[$i] = $dy4[$i] + 1; }
	$ujx1 = $dz3[$i] + $deltax;
    $ujx2 = $dz4[$i] + $deltax;
    $ujy1 = $dy3[$i] + $deltay;        
    $ujy2 = $dy4[$i] + $deltay;        
	//echo 'Újx1:'.$ujx1.'  Újy1:'.$ujy1.'  Újx2:'.$ujx2.'  Újy2:'.$ujy2."<br />";
		
	// Az elfordulási szög kiszámítása
	// A régi vonal
	if (($regiy1 == $regiy2) and ($regix2 > $regix1 )) { $szog_regi = 0; } 
	elseif (($regiy1 == $regiy2) and ($regix2 < $regix1 )) { $szog_regi = 180; } 
	elseif (($regix1 == $regix2) and ($regiy2 < $regiy1 )) { $szog_regi = 270; } 
	elseif (($regix1 == $regix2) and ($regiy2 > $regiy1 )) { $szog_regi = 90; } 
	elseif (($regix2 > $regix1) and ($regiy2 > $regiy1 )) 
		{ $szog_regi = ((M_PI/2)/(abs(atan($deltay_regi / $deltax_regi))))*90; }       //  0 - 90 
	elseif (($regix2 > $regix1) and ($regiy2 < $regiy1 )) 
		{ $szog_regi = 270 + ((M_PI/2)/(abs(atan($deltax_regi / $deltay_regi))))*90; } //  270 - 360  
	elseif (($regix2 < $regix1) and ($regiy2 > $regiy1 )) 
		{ $szog_regi = 90 + ((M_PI/2)/(abs(atan($deltax_regi / $deltay_regi))))*90; }  //  90 - 180 
	else 
		{ $szog_regi = 180 + ((M_PI/2)/(abs(atan($deltay_regi / $deltax_regi))))*90; } //  180 - 270 
	//echo 'szog_regi:'.$szog_regi."<br />";
	// Az új vonal
	if (($ujy1 == $ujy2) and ($ujx2 > $ujx1 )) { $szog_uj = 0; } 
	elseif (($ujy1 == $ujy2) and ($ujx2 < $ujx1 )) { $szog_uj = 180; } 
	elseif (($ujx1 == $ujx2) and ($ujy2 < $ujy1 )) { $szog_uj = 270; } 
	elseif (($ujx1 == $ujx2) and ($ujy2 > $ujy1 )) { $szog_uj = 90; } 
	elseif (($ujx2 > $ujx1) and ($ujy2 > $ujy1 )) 
		{ $szog_uj = ((abs(atan($deltay_uj / $deltax_uj)))/(M_PI/2))*90; }       //  0 - 90 
	elseif (($ujx2 > $ujx1) and ($ujy2 < $ujy1 )) 
		{ $szog_uj = 270 + ((abs(atan($deltax_uj / $deltay_uj)))/(M_PI/2))*90; } //  270 - 360  
	elseif (($ujx2 < $ujx1) and ($ujy2 > $ujy1 ))
		{ $szog_uj = 90 + ((abs(atan($deltax_uj / $deltay_uj)))/(M_PI/2))*90; }  //  90 - 180 
	else 
		{ $szog_uj = 180 + ((abs(atan($deltay_uj / $deltax_uj)))/(M_PI/2))*90; } //  180 - 270		
	//echo 'szog_uj:  '.$szog_uj."<br />";
	$szog = $szog_uj - $szog_regi;
	//echo 'Az elfordulási szög:'.$szog."<br />";
	
	// A nagyítási arány kiszámítása
	$deltax_regi = $regix2 - $regix1;
	$deltay_regi = $regiy2 - $regiy1;		
	// Az elfordulási szöggel vissza kell forgatni az új koordinátákat! (ujx2,ujy2)
	$deltax_uj = $ujx2 - $ujx1;
	$deltay_uj = $ujy2 - $ujy1;
	$arany_x = $deltax_uj / $deltax_regi;
	$arany_y = $deltay_uj / $deltay_regi;
	//echo 'Deltax_regi:'.$deltax_regi.'  Deltay_regi:'.$deltay_regi.'  arany_x:'.$arany_x."<br />";
	//echo 'Deltax_uj:'.$deltax_uj.'  Deltay_uj:'.$deltay_uj.'  arany_y:'.$arany_y."<br />";
	if ($hossz_regi == 0 ) { $arany = $hossz_uj;} else {$arany = $hossz_uj / $hossz_regi; }
	//echo 'A nagyítási arány:'.$arany."<br />";
	
	if ($szog > 180) { $szog = $szog - 360;}
	if (abs($szog) > 360) { $szog = (($szog / 360) - floor($szog / 360)) * 360;}
	// Az új eltolási koordináták
	$x3 = $ujx1 - $regix1 * $arany;
	$y3 = $ujy1 - $regiy1 * $arany;
	//echo $x3.'  '.$y3."<br />";	
    $sor ='<line x1="'. $regix1 .'" y1="'. $regiy1 .'" x2="'. $regix2 .'" y2="'. $regiy2 .'" style="stroke:'.$szin[$i]. ">\n";
    fputs($file,$sor);	
	// Eltolás
	$sor ='<animateTransform attributeName="transform" type="translate" from="0,0" to="'.$x3.','.$y3.'" begin="0" dur="10s" fill="freeze" additive="sum" repeatCount="indefinite"/>'. "\n";
	fputs($file,$sor);
	// méretnövelés / csökkentés
	$sor ='<animateTransform attributeName="transform" type="scale" from="1" to="'.$arany_x.' '.$arany_y.'" begin="0" dur="10s" fill="freeze" additive="sum" repeatCount="indefinite"/>'. "\n";
	fputs($file,$sor);
	// elforgatás
	$sor ='<animateTransform attributeName="transform" type="rotate" from="0,'.$regix1.','.$regiy1.'" to="'.$szog.','.$regix1.','.$regiy1.'" begin="0" dur="10s" fill="freeze" additive="sum" repeatCount="indefinite"/>'. "\n";
	fputs($file,$sor);					
	$sor ='</line>'. "\n";
  fputs($file,$sor);
}
*/

$sor ='</svg>'. "\n";
fputs($file,$sor);
fclose($file);

// A kiírandó vázszerkezet JAVA-s adatainak lerögzítése

$fileout = fopen("oszlop.obj","w");

//Csomóponti koordináták...
for ($i=1; $i<=$cspindex; $i++)  {
    $sor = 'v '.$x[$i]. ' ' . $y[$i] . ' ' . $z[$i] . "\n";
    fputs($fileout,$sor);
}
//Rúdkapcsolatok...
for ($i=1; $i<=$rudindex; $i++)  {
    $sor = 'l '.$kezdocsp[$i]. ' '.$vegecsp[$i]. "\n";
    fputs($fileout,$sor);
}

fclose($fileout);

echo '<TABLE border=1><tr>';
echo '<th>A rácsrúd koordináták</th><th>Elölnézet(XY)</th><th>Oldalnézet(ZY)</th><th>Felülnézet(XZ)</th></tr>';
echo '<tr>';
/*
echo '<td>';

echo '<table border=1>';
echo '<tr><th>No.</th><th>X</th><th>Y</th><th>Z</th></tr>';
for ($i=1; $i<=$cspindex; $i++)  {
    echo '<tr><th>'.$i.'</th><td>'.$x[$i].'</td><td>'.$y[$i].'</td><td>'.$z[$i].'</td></tr>';
}
echo '</table>';

echo '</td>';
 */

echo '<td><img src='.$filenev4.' width='.$picx.' height='.$picy.' />';

/*
echo '<table border=1>';
echo '<tr><th>No.</th><th>Kezdõcsp</th><th>Végecsp</th><th>KezdõX</th><th>KezdõY</th><th>KezdõZ</th><th>VégeX</th><th>VégeY</th><th>VégeZ</th></tr>';
for ($i=1; $i<=$rudindex; $i++)  {
    echo '<tr><th>'.$i.'</th><td>'.$kezdocsp[$i].'</td><td>'.$vegecsp[$i].'</td><td>'.$cx1[$i].'</td><td>'.$cy1[$i].'</td><td>'.$cz1[$i].'</td><td>'.$cx2[$i].'</td><td>'.$cy2[$i].'</td><td>'.$cz2[$i].'</td></tr>';
}
echo '</table>';
*/
echo '</td>';
echo '<td><img src='.$filenev1.' width='.$picx.' height='.$picy.' /></td>';
echo '<td><img src='.$filenev3.' width='.$picx.' height='.$picy.' /></td>';
echo '<td><img src='.$filenev2.' width='.$picx.' height='.$picy.' /></td>';
echo '</tr>';
echo '<tr><td colspan=4>';
echo '<applet code=ThreeD.class width=900 height=900>';
echo '<param name=model value=oszlop.obj>';
echo 'alt="Your browser understands the &lt;APPLET&gt; tag but is not running the applet, for some reason."';
echo 'Your browser is completely ignoring the &lt;APPLET&gt; tag!';
echo '</applet>';
echo '</td></tr>';
echo '</TABLE>';

/*
echo '<h3>A csomópont-koordináták</h3>';
echo '<table border=1>';
echo '<tr><th>No.</th><th>X</th><th>Y</th><th>Z</th></tr>';
for ($i=1; $i<=$cspindex; $i++)  {
    echo '<tr><th>'.$i.'</th><td>'.$x[$i].'</td><td>'.$y[$i].'</td><td>'.$z[$i].'</td></tr>';
}
echo '</table>';

echo '<h3>A rácsrúd koordináták</h3>';
echo '<table border=1>';
echo '<tr><th>No.</th><th>Kezdõcsp</th><th>Végecsp</th><th>KezdõX</th><th>KezdõY</th><th>KezdõZ</th><th>VégeX</th><th>VégeY</th><th>VégeZ</th></tr>';
for ($i=1; $i<=$rudindex; $i++)  {
    echo '<tr><th>'.$i.'</th><td>'.$kezdocsp[$i].'</td><td>'.$vegecsp[$i].'</td><td>'.$x1[$i].'</td><td>'.$y1[$i].'</td><td>'.$z1[$i].'</td><td>'.$x2[$i].'</td><td>'.$y2[$i].'</td><td>'.$z2[$i].'</td></tr>';
}
echo '</table>';
*/

?> 

<a href="show.php">Vissza</a>