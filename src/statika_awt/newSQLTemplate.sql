SELECT `blokkol`.munkaszam, `blokkol`.rajzszam, mid( min( `blokkol`.`kezdet` ) , 1, 10 ) AS kezdes, rendeles.elinditva, rendeles.elinditva - mid( min( `blokkol`.`kezdet` ) , 1, 10 ) AS diff
FROM `blokkol` 
LEFT JOIN rendeles ON rendeles.munkaszam = blokkol.munkaszam
AND rendeles.rajzszam = blokkol.rajzszam
WHERE `blokkol`.tip <>9
GROUP BY `blokkol`.`munkaszam` , `blokkol`.`rajzszam` 
ORDER BY `diff` ASC;